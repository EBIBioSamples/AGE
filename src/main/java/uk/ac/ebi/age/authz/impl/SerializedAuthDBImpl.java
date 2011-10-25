package uk.ac.ebi.age.authz.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;

import uk.ac.ebi.age.authz.ACR;
import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.AuthDB;
import uk.ac.ebi.age.authz.BuiltInGroups;
import uk.ac.ebi.age.authz.BuiltInUsers;
import uk.ac.ebi.age.authz.Classifier;
import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.authz.PermissionForGroupACR;
import uk.ac.ebi.age.authz.PermissionForUserACR;
import uk.ac.ebi.age.authz.PermissionProfile;
import uk.ac.ebi.age.authz.ProfileForGroupACR;
import uk.ac.ebi.age.authz.ProfileForUserACR;
import uk.ac.ebi.age.authz.SecurityChangedListener;
import uk.ac.ebi.age.authz.Tag;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.authz.exception.AuthDBException;
import uk.ac.ebi.age.authz.exception.BuiltInChangeException;
import uk.ac.ebi.age.authz.exception.ClassifierExistsException;
import uk.ac.ebi.age.authz.exception.ClassifierNotFoundException;
import uk.ac.ebi.age.authz.exception.DBInitException;
import uk.ac.ebi.age.authz.exception.EmailNotUniqueException;
import uk.ac.ebi.age.authz.exception.GroupCycleException;
import uk.ac.ebi.age.authz.exception.GroupExistsException;
import uk.ac.ebi.age.authz.exception.GroupNotFoundException;
import uk.ac.ebi.age.authz.exception.PermissionNotFound;
import uk.ac.ebi.age.authz.exception.ProfileCycleException;
import uk.ac.ebi.age.authz.exception.ProfileExistsException;
import uk.ac.ebi.age.authz.exception.ProfileNotFoundException;
import uk.ac.ebi.age.authz.exception.TagException;
import uk.ac.ebi.age.authz.exception.TagExistsException;
import uk.ac.ebi.age.authz.exception.TagNotFoundException;
import uk.ac.ebi.age.authz.exception.UserExistsException;
import uk.ac.ebi.age.authz.exception.UserNotFoundException;
import uk.ac.ebi.age.authz.writable.ClassifierWritable;
import uk.ac.ebi.age.authz.writable.PermissionForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.PermissionForUserACRWritable;
import uk.ac.ebi.age.authz.writable.PermissionProfileWritable;
import uk.ac.ebi.age.authz.writable.PermissionWritable;
import uk.ac.ebi.age.authz.writable.ProfileForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.ProfileForUserACRWritable;
import uk.ac.ebi.age.authz.writable.TagWritable;
import uk.ac.ebi.age.authz.writable.UserGroupWritable;
import uk.ac.ebi.age.authz.writable.UserWritable;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.transaction.InconsistentStateException;
import uk.ac.ebi.age.transaction.InvalidStateException;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;
import uk.ac.ebi.age.transaction.TransactionalDB;
import uk.ac.ebi.mg.collection.IndexList;

import com.pri.util.NaturalStringComparator;
import com.pri.util.StringUtils;
import com.pri.util.collection.CollectionsUnion;
import com.pri.util.collection.ListFragment;

public class SerializedAuthDBImpl implements AuthDB
{
 private static final String serialFileName = "authdb.ser";
 private static final String defaultSupervisorPassword = BuiltInUsers.SUPERVISOR.getName();
 
 private class RLock implements ReadLock
 {
  boolean active = true;
  
  RLock( )
  {
  }
  
  void setActive( boolean a )
  {
   active = a;
  }

  public boolean isActive()
  {
   return active;
  }
 }
 
 private class Tran extends RLock implements Transaction
 {
  Tran( TransactionalDB db )
  {
   super();
  }

 }
 
 private IndexList<String, UserWritable> userList;
 private IndexList<String, UserGroupWritable> groupList;
 private IndexList<String, PermissionProfileWritable> profileList;
 private IndexList<String, ClassifierWritable> classifierList;
 private TagWritable sysTag;
 
// private List<UserWritable> userList;
// private List<UserGroupWritable> groupList;
// private List<PermissionProfileWritable> profileList;
// private List<ClassifierWritable> classifierList;
// 
// private Map<String, UserWritable> userMap;
// private Map<String,UserGroupWritable> groupMap;
// private Map<String,PermissionProfileWritable> profileMap;
// private Map<String,ClassifierWritable> classifierMap;

 private static class DataPacket implements Serializable
 {
  private static final long serialVersionUID = 1L;
  
  IndexList<String, UserWritable> userList;
  IndexList<String, UserGroupWritable> groupList;
  IndexList<String, PermissionProfileWritable> profileList;
  IndexList<String, ClassifierWritable> classifierList;
  TagWritable sysTag;
 }
 
 private DataPacket dataPacket;
 
 private ReadWriteLock lock = new ReentrantReadWriteLock();
 
 private FileResourceManager txManager;
 private String serialFileRelPath;
 private File serialFile;
 
 private ArrayList<SecurityChangedListener> listeners = new ArrayList<SecurityChangedListener>();
 
 public SerializedAuthDBImpl(FileResourceManager frm, String authRelPath) throws DBInitException
 {
  txManager=frm;
 
  serialFileRelPath = authRelPath+"/"+serialFileName;
  
  serialFile = new File(frm.getStoreDir(),serialFileRelPath);
  
  if( serialFile.exists() )
  {
   try
   {
    readData();
   }
   catch(IOException e)
   {
    throw new DBInitException(e);
   }
  }
  else
  {
   dataPacket = new DataPacket();

   userList = dataPacket.userList = new IndexList<String, UserWritable>( NaturalStringComparator.getInstance() );
   groupList = dataPacket.groupList = new IndexList<String, UserGroupWritable>( NaturalStringComparator.getInstance() );
   profileList = dataPacket.profileList = new IndexList<String, PermissionProfileWritable>( NaturalStringComparator.getInstance() );
   classifierList = dataPacket.classifierList = new IndexList<String, ClassifierWritable>( NaturalStringComparator.getInstance() );
   sysTag=dataPacket.sysTag = AuthBeanFactory.getInstance().createTagBean();

   for( BuiltInUsers usr: BuiltInUsers.values() )
   {
    UserWritable ub = AuthBeanFactory.getInstance().createUserBean();
    ub.setId(usr.getName());
    ub.setName(usr.getDescription());
    
    userList.add(ub);
    
    if( usr.getName().equals(BuiltInUsers.SUPERVISOR.getName()) )
     ub.setPass(StringUtils.hashStringSHA1(defaultSupervisorPassword));
   }
   
   UserGroupWritable gb = AuthBeanFactory.getInstance().createEveryoneGroupBean();
   gb.setId(BuiltInGroups.EVERYONE.getName());
   gb.setDescription(BuiltInGroups.EVERYONE.getDescription());
   
   groupList.add(gb);

   groupList.add(AuthBeanFactory.getInstance().createAuthenticatedGroupBean());

   
   
//   for( BuiltInGroups grp: BuiltInGroups.values() )
//   {
//    UserGroupWritable gb = new UserGroupWritable();
//    gb.setId(grp.getName());
//    gb.setDescription(grp.getDescription());
//    
//    groupList.add(gb);
//   }

   String txId;

   try
   {
    txId = txManager.generatedUniqueTxId();
    txManager.startTransaction(txId);
    OutputStream outputStream = txManager.writeResource(txId, serialFileRelPath);
    
    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
    
    oos.writeObject(dataPacket);
    
    oos.close();
    
    txManager.commitTransaction(txId);
   }
   catch(Exception e)
   {
    throw new DBInitException(e);
   }

  }
 }
 
 private void readData() throws IOException
 {
  FileInputStream fis = new FileInputStream(serialFile);
  ObjectInputStream ois = new ObjectInputStream( fis );
  
  try
  {
   dataPacket = (DataPacket)ois.readObject();
  }
  catch(ClassNotFoundException e)
  {
   e.printStackTrace();
  }
  
  userList=dataPacket.userList;
  groupList=dataPacket.groupList;
  profileList=dataPacket.profileList;
  classifierList=dataPacket.classifierList;
  sysTag=dataPacket.sysTag;
  
  fis.close();
  
 }
 
/* 
 public SerializedAuthDBImpl(FileResourceManager frm, String authRelPath)
 {
  txManager=frm;
  relPath=authRelPath;
  
  groupList = new HashMap<String,UserGroupWritable>(20);
  
  for( int i=1; i <= 13; i++ )
  {
   UserGroupWritable u = new UserGroupWritable();
   
   u.setId("Group"+i);
   u.setDescription("Test Group №"+i);
   
   groupList.put(u.getId(), u);

  }

  userList = new HashMap<String,UserWritable>(200);
  for( int i=1; i <= 13; i++ )
  {
   UserWritable u = new UserWritable();
   
   u.setId("User"+i);
   u.setName("Test User №"+i);
   
   int n = Random.randInt(1, 5);
   for( int j=0; j < n; j++ )
   {
    UserGroupWritable grp = groupList.get( Random.randInt(0, groupList.size()-1) );
    u.addGroup( grp );
    grp.addUser( u );
   }
   
   userList.put(u.getId(),u);
  }

  profileList = new HashMap<String,PermissionProfileWritable>();
  
  for( int i=1; i <=3; i++ )
  {
   PermissionProfileWritable pb = new PermissionProfileWritable();
   
   pb.setId("P"+i);
   pb.setDescription("Profile No."+i);
   
   PermissionWritable prmb = new PermissionWritable();
   prmb.setAction(SystemAction.READ);
   prmb.setAllow(true);

   pb.addPermission( prmb );
   
   prmb = new PermissionWritable();
   prmb.setAction(SystemAction.CHANGE);
   prmb.setAllow(true);

   pb.addPermission( prmb );
   
   prmb = new PermissionWritable();
   prmb.setAction(SystemAction.DELETE);
   prmb.setAllow(false);

   pb.addPermission( prmb );

   profileList.put(pb.getId(),pb);
  }

  
  ClassifierWritable cb = new ClassifierWritable();
  
  cb.setId("Test clssf");
  cb.setDescription("Test clssifier No1");
  
  TagWritable a = new TagWritable();
  a.setId("A");
  a.setDescription("Test tag A");
  
  TagWritable b = new TagWritable();
  b.setId("B");
  b.setDescription("Test tag B");
  
  TagWritable a1 = new TagWritable();
  a1.setId("A1");
  a1.setDescription("Test tag A1");
  a1.setParent(a);
  
  TagWritable a2 = new TagWritable();
  a2.setId("A2");
  a2.setDescription("Test tag A2");
  a2.setParent(a);
  
  cb.addTag(a);
  cb.addTag(a1);
  cb.addTag(a2);
  cb.addTag(b);
  
  classifierList = new HashMap<String, ClassifierWritable>();
  
  classifierList.put(cb.getId(),cb);
 }
*/

 @Override
 public ReadLock getReadLock()
 {
  lock.readLock().lock();
  return new RLock();
 }

 @Override
 public Transaction startTransaction()
 {
  lock.writeLock().lock();
  return new Tran( this );
 }

 @Override
 public void commitTransaction(Transaction t) throws TransactionException
 {
  ((RLock)t).setActive(false);
  
  try
  {
   sync();
  }
  catch(Exception e)
  {
   try
   {
    readData();
   }
   catch(IOException e1)
   {
    throw new InconsistentStateException("System is in undefined state due to IO error",e1);
   }
   
   throw new TransactionException("Transaction IO exception", e);
  }
  finally
  {
   lock.writeLock().unlock();
  }

  fireSecurityChanged();
 }

 private void sync() throws ResourceManagerException, IOException
 {
  String txId;

  txId = txManager.generatedUniqueTxId();

  txManager.startTransaction(txId);

  txManager.moveResource(txId, serialFileRelPath, serialFileRelPath + "." + System.currentTimeMillis(), true);

  OutputStream outputStream = txManager.writeResource(txId, serialFileRelPath);

  ObjectOutputStream oos = new ObjectOutputStream(outputStream);

  oos.writeObject(dataPacket);
  
  oos.close();

  txManager.commitTransaction(txId);
 }
 
 @Override
 public void rollbackTransaction(Transaction t) throws TransactionException
 {
  ((RLock)t).setActive(false);

  try
  {
   readData();
  }
  catch(IOException e)
  {
   throw new InconsistentStateException("System is in undefined state due to IO error",e);
  }
  finally
  {
   lock.writeLock().unlock();
  }
  
 }

 @Override
 public void releaseLock(ReadLock lck )
 {
  lock.readLock().unlock();
  ((RLock)lck).setActive(false);
 }
 
 private void checkState( ReadLock lck )
 {
  if( ! ((RLock)lck).isActive() )
   throw new InvalidStateException();
 }
 
 private ClassifierWritable getClassifier( String id )
 {
  return classifierList.getByKey(id);
 }

 @Override
 public Classifier getClassifier( ReadLock lck, String id)
 {
  checkState(lck);

  return getClassifier(id);
 }


 private TagWritable getTag( String clsfId, String tagId) throws TagException
 {
  ClassifierWritable c = getClassifier(clsfId);
  
  if( c == null )
   throw new ClassifierNotFoundException();
   
  return c.getTag(tagId);
 }

 @Override
 public Tag getTag( ReadLock lck, String clsfId, String tagId) throws TagException
 {
  checkState(lck);
   
  return getTag(clsfId, tagId);
 }

 private UserWritable getUser( String id)
 {
  return userList.getByKey(id);
 }
 
 private UserWritable getUserByEmail( String email )
 {
  for( UserWritable u : userList )
   if( email.equals(u.getEmail()) )
    return u;
  
  return null;
 }

 
 @Override
 public User getUser( ReadLock lck, String id)
 {
  checkState(lck);
  
  return getUser( id );
 }

 @Override
 public User getUserByEmail( ReadLock lck, String email)
 {
  checkState(lck);
  
  return getUserByEmail(email);
 }

 
 private UserGroupWritable getUserGroup( String id )
 {
  return groupList.getByKey(id);
 }
 
 @Override
 public UserGroup getUserGroup( ReadLock lck, String id)
 {
  return getUserGroup(id);
 }

 private PermissionProfileWritable getProfile( String id)
 {
  return profileList.getByKey(id);
 }
 
 @Override
 public PermissionProfile getProfile( ReadLock lck, String id)
 {
  checkState(lck);

  return getProfile(id);
 }
 
 @Override
 public List<? extends User> getUsers( ReadLock lck, int begin, int end)
 {
  checkState(lck);

  int to = end!=-1 && end <= userList.size() ?end:userList.size();
  
  return userList.subList(begin, to);
 }

 @Override
 public ListFragment<User> getUsers( ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= userList.size() ?end:userList.size();

  
  ListFragment<User> res = new ListFragment<User>();
  
  List<User> sel = new ArrayList<User>();
  
  res.setList(sel);
  
  for( User u : userList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getName().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public int getUsersTotal(ReadLock lck)
 {
  checkState(lck);

  return userList.size();
 }

 @Override
 public void updateUser( Transaction lck, String userId, String userName, String email) throws AuthDBException
 {
  checkState(lck);

  UserWritable u = userList.getByKey(userId);
  
  if( u == null )
   throw new UserNotFoundException( userId );
  
  for( BuiltInUsers usr : BuiltInUsers.values() )
   if( usr.getName().equals(userId) )
    throw new BuiltInChangeException("Built-in user can't be modified");
  
  if( email!=null )
  {
   email = email.trim();
   
   if( email.length() != 0 )
    if( getUserByEmail(email) != null )
     throw new EmailNotUniqueException( userId );
  }
  
  u.setName(userName);
  u.setEmail(email);
 
 }
 
 @Override
 public boolean checkUserPassword( ReadLock lck, String userId, String userPass) throws AuthDBException
 {
  checkState(lck);

  UserWritable u = userList.getByKey(userId);
  
  if( u == null )
   throw new UserNotFoundException( userId );
  
  return StringUtils.hashStringSHA1(userPass).equals(u.getPass()) ;
 }

 
 @Override
 public void setUserPassword( Transaction lck, String userId, String userPass) throws AuthDBException
 {
  checkState(lck);

  UserWritable u = userList.getByKey(userId);
  
  if( u == null )
   throw new UserNotFoundException( userId );
  
  u.setPass( StringUtils.hashStringSHA1(userPass) );  
 }

 @Override
 public void addUser( Transaction lck, String userId, String userName, String email, String userPass) throws AuthDBException
 {
  checkState(lck);

  if( getUser(userId) != null )
   throw new UserExistsException( userId );
  
  if( email!=null )
  {
   email = email.trim();
   
   if( email.length() != 0 )
    if( getUserByEmail(email) != null )
     throw new EmailNotUniqueException( userId );
  }
  
  UserWritable u = AuthBeanFactory.getInstance().createUserBean();
  
  u.setId(userId);
  u.setName(userName);
  u.setEmail(email);
  u.setPass(StringUtils.hashStringSHA1(userPass));
  
  userList.add( u );
 }

 @Override
 public void deleteUser( Transaction lck, String userId) throws AuthDBException
 {
  checkState(lck);

  for( BuiltInUsers usr : BuiltInUsers.values() )
   if( usr.getName().equals(userId) )
    throw new BuiltInChangeException("Built-in user can't be deleted");
  
  UserWritable rmUsr = userList.removeKey(userId);
  
  if( rmUsr == null )
   throw new UserNotFoundException(userId);

  for( UserGroupWritable gb : rmUsr.getGroups() )
   gb.removeUser(rmUsr);
  
  for( ClassifierWritable clsb: classifierList )
  {
   for( TagWritable tb : clsb.getTags() )
   {
    Iterator<? extends PermissionForUserACRWritable> pmgIter = tb.getPermissionForUserACRs().iterator();
    
    while( pmgIter.hasNext() )
    {
     PermissionForUserACRWritable p = pmgIter.next();
     
     if( p.getSubject() == rmUsr )
      pmgIter.remove();
    }
    
    Iterator<? extends ProfileForUserACRWritable> pfgIter = tb.getProfileForUserACRs().iterator();
    
    while( pfgIter.hasNext() )
    {
     ProfileForUserACRWritable p = pfgIter.next();
     
     if( p.getSubject() == rmUsr )
      pfgIter.remove();
    }
   }
  }
 }

 @Override
 public List< ? extends UserGroup> getGroups(ReadLock lck, int begin, int end)
 {
  checkState(lck);

  int to = end!=-1 && end <= groupList.size() ? end:groupList.size();
  
  return groupList.subList(begin, to);
 }

 @Override
 public ListFragment<UserGroup> getGroups(ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= groupList.size() ?end:groupList.size();

  
  ListFragment<UserGroup> res = new ListFragment<UserGroup>();
  
  List<UserGroup> sel = new ArrayList<UserGroup>();
  
  res.setList(sel);
  
  for( UserGroup u : groupList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getDescription().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public int getGroupsTotal( ReadLock lck )
 {
  checkState(lck);
  return groupList.size();
 }

 @Override
 public void deleteGroup(Transaction lck, String grpId) throws AuthDBException
 {
  checkState(lck);

  for( BuiltInGroups grp : BuiltInGroups.values() )
   if( grp.getName().equals(grpId) )
    throw new BuiltInChangeException("Built-in group can't be deleted");

  UserGroupWritable rmGrp = groupList.removeKey(grpId);
  
  if( rmGrp == null )
   throw new GroupNotFoundException();

  for( UserWritable ub : rmGrp.getUsers() )
   ub.removeGroup(rmGrp);
  
  for( ClassifierWritable clsb: classifierList )
  {
   for( TagWritable tb : clsb.getTags() )
   {
    Iterator<? extends PermissionForGroupACRWritable> pmgIter = tb.getPermissionForGroupACRs().iterator();
    
    while( pmgIter.hasNext() )
    {
     PermissionForGroupACRWritable p = pmgIter.next();
     
     if( p.getSubject() == rmGrp )
      pmgIter.remove();
    }
    
    Iterator<? extends ProfileForGroupACRWritable> pfgIter = tb.getProfileForGroupACRs().iterator();
    
    while( pfgIter.hasNext() )
    {
     ProfileForGroupACRWritable p = pfgIter.next();
     
     if( p.getSubject() == rmGrp )
      pfgIter.remove();
    }
   }
  }

 }

 @Override
 public void addGroup(Transaction lck, String grpId, String grpDesc) throws AuthDBException
 {
  checkState(lck);

  if( getUserGroup(grpId) != null  )
   throw new GroupExistsException();
  
  UserGroupWritable u = AuthBeanFactory.getInstance().createGroupBean();
  
  u.setId(grpId);
  u.setDescription(grpDesc);
  
  groupList.add( u );
 }

 @Override
 public void updateGroup(Transaction lck, String grpId, String grpDesc) throws AuthDBException
 {
  checkState(lck);

  UserGroupWritable u = getUserGroup(grpId);
  
  if( u == null  )
   throw new GroupNotFoundException();

  for( BuiltInGroups grp : BuiltInGroups.values() )
   if( grp.getName().equals(grpId) )
    throw new BuiltInChangeException("Built-in group can't be modified");

  
  u.setDescription(grpDesc);
  
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfUser(ReadLock lck, String userId) throws AuthDBException
 {
  checkState(lck);

  User u = getUser(userId);
  
  if( u == null  )
   throw new UserNotFoundException();
  
  return u.getGroups();
 }

 @Override
 public void removeUserFromGroup(Transaction lck, String grpId, String userId) throws AuthDBException
 {
  checkState(lck);

  UserGroupWritable gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException();
  
  UserWritable ub = gb.getUser(userId);
  
  if( ub == null )
   throw new UserNotFoundException();
  
  gb.removeUser( ub );
  ub.removeGroup( gb );
 }

 @Override
 public void removeGroupFromGroup(Transaction lck, String grpId, String partId) throws AuthDBException
 {
  checkState(lck);

  UserGroupWritable gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException();
  
  UserGroup gp = gb.getGroup(partId);
  
  if( gp == null )
   throw new GroupNotFoundException();
  
  gb.removeGroup( gp );
 }

 
 @Override
 public void addUserToGroup(Transaction lck, String grpId, String userId) throws AuthDBException
 {
  checkState(lck);

  UserGroupWritable gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException(grpId);
  
  for( BuiltInGroups grp : BuiltInGroups.values() )
   if( grp.getName().equals(grpId) )
    throw new BuiltInChangeException("Built-in group can't be modified");

  UserWritable ub = getUser(userId);
 
  if( ub == null )
   throw new UserNotFoundException(userId);
  
  gb.addUser( ub );
  ub.addGroup( gb );
 }

 @Override
 public Collection< ? extends User> getUsersOfGroup(ReadLock lck, String groupId) throws AuthDBException
 {
  checkState(lck);

  UserGroupWritable gb = getUserGroup(groupId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  return gb.getUsers();
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfGroup(ReadLock lck, String groupId) throws AuthDBException
 {
  checkState(lck);

  UserGroupWritable gb = getUserGroup(groupId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  return gb.getGroups();
 }

 @Override
 public void addGroupToGroup(Transaction lck, String grpId, String partId) throws AuthDBException
 {
  checkState(lck);

  UserGroupWritable gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException(grpId);

  for( BuiltInGroups grp : BuiltInGroups.values() )
   if( grp.getName().equals(grpId) )
    throw new BuiltInChangeException("Built-in group can't be modified");

  
  UserGroupWritable pb = getUserGroup(partId);
  
  if( pb == null )
   throw new GroupNotFoundException(partId);
 
  if( grpId.equals(partId) || gb.isPartOf(pb) )
   throw new GroupCycleException();
  
  
  gb.addGroup( pb );
 }

 @Override
 public void addProfile(Transaction lck, String profId, String dsc) throws AuthDBException
 {
  checkState(lck);

  if( getProfile(profId) != null )
   throw new ProfileExistsException();
  
  PermissionProfileWritable pb = AuthBeanFactory.getInstance().createProfileBean();
  
  pb.setId(profId);
  pb.setDescription(dsc);
  
  profileList.add( pb );
 }

 @Override
 public void updateProfile(Transaction lck, String profId, String dsc) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable pf = getProfile(profId);
  
  if( pf == null )
   throw new ProfileNotFoundException();
  
  pf.setDescription(dsc);
 }

 @Override
 public void deleteProfile(Transaction lck, String profId) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable rmPb = profileList.removeKey(profId);
  
  if( rmPb == null )
   throw new ProfileNotFoundException();
 
  for( ClassifierWritable clsb: classifierList )
  {
   for( TagWritable tb : clsb.getTags() )
   {
    Iterator<? extends ProfileForGroupACRWritable> pfuIter = tb.getProfileForGroupACRs().iterator();
    
    while( pfuIter.hasNext() )
    {
     ProfileForGroupACRWritable p = pfuIter.next();
     
     if( p.getPermissionUnit() == rmPb )
      pfuIter.remove();
    }
    
    Iterator<? extends ProfileForUserACRWritable> pfgIter = tb.getProfileForUserACRs().iterator();
    
    while( pfgIter.hasNext() )
    {
     ProfileForUserACRWritable p = pfgIter.next();
     
     if( p.getPermissionUnit() == rmPb )
      pfgIter.remove();
    }
   }
  }
 }

 @Override
 public List< ? extends PermissionProfile> getProfiles(ReadLock lck, int begin, int end)
 {
  checkState(lck);

  int to = end!=-1 && end <= profileList.size() ?end:profileList.size();
  
  return profileList.subList(begin, to);
 }

 @Override
 public ListFragment<PermissionProfile> getProfiles(ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= userList.size() ?end:userList.size();

  
  ListFragment<PermissionProfile> res = new ListFragment<PermissionProfile>();
  
  List<PermissionProfile> sel = new ArrayList<PermissionProfile>();
  
  res.setList(sel);
  
  for( PermissionProfile u : profileList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getDescription().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public int getProfilesTotal(ReadLock lck)
 {
  checkState(lck);

  return profileList.size();
 }

 @Override
 public void addPermissionToProfile(Transaction lck, String profId, SystemAction actn, boolean allow) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();

  
  if( prof.getPermissions() != null )
  {
   for( Permission p : prof.getPermissions() )
   {
    if( p.getAction() == actn && p.isAllow() == allow )
     return;
   }
  }
  
  PermissionWritable pb = AuthBeanFactory.getInstance().createPermissionBean();
  pb.setAction(actn);
  pb.setAllow(allow);
  
  prof.addPermission(pb);
 }

 @Override
 public Collection< ? extends Permission> getPermissionsOfProfile(ReadLock lck, String profId) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  return prof.getPermissions();
 }

 @Override
 public Collection< ? extends PermissionProfile> getProfilesOfProfile(ReadLock lck, String profId) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  return prof.getProfiles();
 }

 
 @Override
 public void removePermissionFromProfile(Transaction lck, String profId, SystemAction actn, boolean allow) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  Permission perm = null;
  for( Permission pm : prof.getPermissions() )
  {
   if( pm.getAction() == actn && pm.isAllow() == allow )
   {
    perm=pm;
    break;
   }
  }
  
  if( perm == null )
   throw new PermissionNotFound();
 
  prof.removePermission( perm );
 }
 
 @Override
 public void removeProfileFromProfile(Transaction lck, String profId, String toRemProf) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();

  PermissionProfile rmProf = getProfile(toRemProf);
  
  if( rmProf == null )
   throw new ProfileNotFoundException();
 
  prof.removeProfile( rmProf );
 }


 @Override
 public void addProfileToProfile(Transaction lck, String profId, String toAdd) throws AuthDBException
 {
  checkState(lck);

  PermissionProfileWritable prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();

 
  PermissionProfileWritable npb = getProfile(toAdd);
  
  if( npb == null )
   throw new ProfileNotFoundException();

  if( profId.equals(toAdd) || prof.isPartOf(npb) )
   throw new ProfileCycleException();
  
  
  prof.addProfile( npb );
 }

 @Override
 public void deleteClassifier(Transaction lck, String csfId) throws TagException
 {
  checkState(lck);

  if( classifierList.removeKey(csfId) == null )
   throw new ClassifierNotFoundException();
  
 }

 @Override
 public void addClassifier(Transaction lck, String csfId, String csfDesc) throws TagException
 {
  checkState(lck);

  if( getClassifier(csfId) != null )
   throw new ClassifierExistsException();
  
  ClassifierWritable cb = AuthBeanFactory.getInstance().createClassifierBean();
  
  cb.setId( csfId );
  cb.setDescription(csfDesc);
  
  classifierList.add(cb);
 }

 @Override
 public void updateClassifier(Transaction lck, String csfId, String csfDesc) throws TagException
 {
  checkState(lck);

  ClassifierWritable cb = getClassifier(csfId);
  
  if( cb == null )
   throw new ClassifierNotFoundException();

  cb.setDescription(csfDesc);
  
 }

 @Override
 public List< ? extends Classifier> getClassifiers( ReadLock lck, int begin, int end)
 {
  checkState(lck);

  return classifierList;
 }

 @Override
 public int getClassifiersTotal( ReadLock lck )
 {
  checkState(lck);

  return classifierList.size();
 }

 @Override
 public ListFragment<Classifier> getClassifiers( ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= classifierList.size() ?end:classifierList.size();

  
  ListFragment<Classifier> res = new ListFragment<Classifier>();
  
  List<Classifier> sel = new ArrayList<Classifier>();
  
  res.setList(sel);
  
  for( Classifier u : classifierList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getDescription().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public void removeTagFromClassifier(Transaction lck, String clsId, String tagId) throws TagException
 {
  checkState(lck);

  ClassifierWritable cb = getClassifier(clsId);
  
  if( cb == null )
   throw new ClassifierNotFoundException(); 
  
  Iterator<? extends Tag> tgItr = cb.getTags().iterator();
  
  while( tgItr.hasNext() )
  {
   Tag t = tgItr.next();
   
   if( t.getId().equals(tagId) )
   {
    tgItr.remove();
    continue;
   }
   
   Tag pt = t.getParent();
   
   while( pt != null )
   {
    if( pt.getId().equals(tagId) )
    {
     tgItr.remove();
     break;
    }
    
    pt = pt.getParent();
   }
   
  }
 }

 @Override
 public void addTagToClassifier(Transaction lck, String clsId, String tagId, String description, String parentTagId) throws TagException
 {
  checkState(lck);

  ClassifierWritable clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  Tag pTag = null;
  
  if( parentTagId != null )
  {
   pTag = clsb.getTag(parentTagId);
   
   if( pTag == null )
    throw new TagNotFoundException();
  }
  

  if( clsb.getTag(tagId) != null )
   throw new TagExistsException();
  
  TagWritable tb = AuthBeanFactory.getInstance().createTagBean();
  
  tb.setId(tagId);
  tb.setDescription(description);
  tb.setParent(pTag);
 
  clsb.addTag( tb );
 }

 @Override
 public void updateTag(Transaction lck, String clsId, String tagId, String desc, String parentTagId) throws TagException
 {
  checkState(lck);

  ClassifierWritable clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  Tag pTag = null;
  
  if( parentTagId != null )
  {
   pTag = clsb.getTag(parentTagId);
   
   if( pTag == null )
    throw new TagNotFoundException();
  }
  
  TagWritable tb = clsb.getTag(tagId);

  if( tb == null )
   throw new ClassifierNotFoundException();
  
  tb.setDescription(desc);
  tb.setParent(pTag);
 }

 @Override
 public Collection< ? extends Tag> getTagsOfClassifier( ReadLock lck, String clsId) throws TagException
 {
  checkState(lck);

  ClassifierWritable clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  return clsb.getTags();
 }

 @Override
 public Collection< ? extends Tag> getTagsOfClassifier( ReadLock lck, String clsId, final String parentTagId) throws TagException
 {
  checkState(lck);

  final ClassifierWritable clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  return new AbstractCollection<TagWritable>()
  {

   @Override
   public Iterator<TagWritable> iterator()
   {
    return new Iterator<TagWritable>()
    {
     private TagWritable next;
     private Iterator<? extends TagWritable> iter = clsb.getTags().iterator();

     @Override
     public boolean hasNext()
     {
      if( next != null )
       return true;
      
      if( ! iter.hasNext() )
       return false;
      
      do
      {
       next = iter.next();
       
       if( parentTagId == null )
       {
        if( next.getParent() == null )
         return true;
       }
       else if( parentTagId.equals(next.getParent()) )
        return true;
       
      }while( iter.hasNext() );
      
      return false;
     }

     @Override
     public TagWritable next()
     {
      if( ! hasNext() )
       return null;
      
      TagWritable nxt = next;
      next = null;
      
      return nxt;
     }

     @Override
     public void remove()
     {
     }
    };
   }

   @Override
   public int size()
   {
    return 0;
   }
  };
 }

 @Override
 public boolean removeProfileForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends ProfileForGroupACR> acrs = tb.getProfileForGroupACRs();
  
  if( acrs == null )
   return false;
  
  Iterator<? extends ProfileForGroupACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   ProfileForGroupACR acr = iter.next();
   
   if(acr.getPermissionUnit().getId().equals(profileId) && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }
  
  return false;
 }

 @Override
 public boolean removeProfileForUserACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends ProfileForUserACR> acrs = tb.getProfileForUserACRs();
  
  if( acrs == null )
   return false;
  
  Iterator<? extends ProfileForUserACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   ProfileForUserACR acr = iter.next();
   
   if(acr.getPermissionUnit().getId().equals(profileId) && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public boolean removePermissionForUserACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends PermissionForUserACR> acrs = tb.getPermissionForUserACRs();
  
  if( acrs == null )
   return false;
  
  Iterator<? extends PermissionForUserACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   PermissionForUserACR acr = iter.next();
   
   if(acr.getPermissionUnit().getAction() == act && acr.getPermissionUnit().isAllow() == allow && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public boolean removePermissionForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends PermissionForGroupACR> acrs = tb.getPermissionForGroupACRs();
  
  if( acrs == null )
   return false;
  
  Iterator<? extends PermissionForGroupACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   PermissionForGroupACR acr = iter.next();
   
   if(acr.getPermissionUnit().getAction() == act && acr.getPermissionUnit().isAllow() == allow && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public void addProfileForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthDBException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  ProfileForGroupACRWritable acr = AuthBeanFactory.getInstance().createProfileForGroupACRBean();
  
  PermissionProfileWritable pb = getProfile(profileId);
  
  if( pb == null )
   throw new ProfileNotFoundException();
  
  UserGroupWritable gb = getUserGroup(subjId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addProfileForGroupACR( acr );
 }

 @Override
 public void addProfileForUserACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthDBException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  ProfileForUserACRWritable acr = AuthBeanFactory.getInstance().createProfileForUserACRBean();
  
  PermissionProfileWritable pb = getProfile(profileId);
  
  if( pb == null )
   throw new ProfileNotFoundException();
  
  UserWritable gb = getUser(subjId);
  
  if( gb == null )
   throw new UserNotFoundException();

  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addProfileForUserACR( acr );
 }

 @Override
 public void addActionForUserACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthDBException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  PermissionForUserACRWritable acr = AuthBeanFactory.getInstance().createPermissionForUserACRBean();
  
  UserWritable gb = getUser(subjId);
  
  if( gb == null )
   throw new UserNotFoundException();

  PermissionWritable pb = AuthBeanFactory.getInstance().createPermissionBean();
  pb.setAction(act);
  pb.setAllow(allow);
  
  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addPermissionForUserACR( acr );
 }

 @Override
 public void addActionForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthDBException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  PermissionForGroupACRWritable acr = AuthBeanFactory.getInstance().createPermissionForGroupACRBean();
  
  UserGroupWritable gb = getUserGroup(subjId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  PermissionWritable pb = AuthBeanFactory.getInstance().createPermissionBean();
  pb.setAction(act);
  pb.setAllow(allow);
  
  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addPermissionForGroupACR( acr );
 }

 @SuppressWarnings("unchecked")
 @Override
 public Collection<? extends ACR> getACL( ReadLock lck, String clsfId, String tagId) throws TagException
 {
  checkState(lck);

  TagWritable tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  return new CollectionsUnion<ACR>( new Collection[] {
    tb.getPermissionForUserACRs(),
    tb.getPermissionForGroupACRs(),
    tb.getProfileForUserACRs(),
    tb.getProfileForGroupACRs()});
 }

 @Override
 public boolean removeSysProfileForGroupACR(Transaction lck, String subjId, String profileId) throws AuthDBException
 {
  checkState(lck);

  
  Collection<? extends ProfileForGroupACR> acrs = sysTag.getProfileForGroupACRs();
  
  Iterator<? extends ProfileForGroupACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   ProfileForGroupACR acr = iter.next();
   
   if(acr.getPermissionUnit().getId().equals(profileId) && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }
  
  return false; }

 @Override
 public boolean removeSysPermissionForGroupACR(Transaction lck, String subjId, SystemAction action, boolean allow)
   throws AuthDBException
 {
  checkState(lck);

  
  Collection<? extends PermissionForGroupACR> acrs = sysTag.getPermissionForGroupACRs();
  
  Iterator<? extends PermissionForGroupACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   PermissionForGroupACR acr = iter.next();
   
   if(acr.getPermissionUnit().getAction() == action && acr.getPermissionUnit().isAllow() == allow && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public boolean removeSysProfileForUserACR(Transaction trn, String subjId, String profileId) throws AuthDBException
 {
  checkState(trn);
  
  Collection<? extends ProfileForUserACR> acrs = sysTag.getProfileForUserACRs();
  
  Iterator<? extends ProfileForUserACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   ProfileForUserACR acr = iter.next();
   
   if(acr.getPermissionUnit().getId().equals(profileId) && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public boolean removeSysPermissionForUserACR(Transaction trn, String subjId, SystemAction action, boolean allow) throws AuthDBException
 {
  checkState(trn);

  
  Collection<? extends PermissionForUserACR> acrs = sysTag.getPermissionForUserACRs();
  
  Iterator<? extends PermissionForUserACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   PermissionForUserACR acr = iter.next();
   
   if(acr.getPermissionUnit().getAction() == action && acr.getPermissionUnit().isAllow() == allow && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
  
 }

 @Override
 public void addSysProfileForGroupACR(Transaction trn, String subjId, String profileId) throws AuthDBException
 {
  checkState(trn);


  ProfileForGroupACRWritable acr = AuthBeanFactory.getInstance().createProfileForGroupACRBean();
  
  PermissionProfileWritable pb = getProfile(profileId);
  
  if( pb == null )
   throw new ProfileNotFoundException(profileId);
  
  UserGroupWritable gb = getUserGroup(subjId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  sysTag.addProfileForGroupACR( acr );
 }

 @Override
 public void addSysActionForGroupACR(Transaction trn, String subjId, SystemAction action, boolean allow) throws AuthDBException
 {
  checkState(trn);

  PermissionForGroupACRWritable acr = AuthBeanFactory.getInstance().createPermissionForGroupACRBean();
  
  UserGroupWritable gb = getUserGroup(subjId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  PermissionWritable pb = AuthBeanFactory.getInstance().createPermissionBean();
  pb.setAction(action);
  pb.setAllow(allow);
  
  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  sysTag.addPermissionForGroupACR( acr );
  
 }

 @Override
 public void addSysProfileForUserACR(Transaction trn, String subjId, String profileId) throws AuthDBException
 {
  checkState(trn);

  ProfileForUserACRWritable acr = AuthBeanFactory.getInstance().createProfileForUserACRBean();
  
  PermissionProfileWritable pb = getProfile(profileId);
  
  if( pb == null )
   throw new ProfileNotFoundException(profileId);
  
  UserWritable gb = getUser(subjId);
  
  if( gb == null )
   throw new UserNotFoundException(subjId);

  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  sysTag.addProfileForUserACR( acr );  
 }

 @Override
 public void addSysActionForUserACR(Transaction trn, String subjId, SystemAction action, boolean allow)  throws AuthDBException
 {
  checkState(trn);

  PermissionForUserACRWritable acr = AuthBeanFactory.getInstance().createPermissionForUserACRBean();
  
  UserWritable gb = getUser(subjId);
  
  if( gb == null )
   throw new UserNotFoundException(subjId);

  PermissionWritable pb = AuthBeanFactory.getInstance().createPermissionBean();
  pb.setAction(action);
  pb.setAllow(allow);
  
  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  sysTag.addPermissionForUserACR( acr );  
 }

 @SuppressWarnings("unchecked")
 @Override
 public Collection< ? extends ACR> getSysACL(ReadLock lck) throws AuthDBException
 {
  checkState(lck);

  
  return new CollectionsUnion<ACR>( new Collection[] {
    sysTag.getPermissionForUserACRs(),
    sysTag.getPermissionForGroupACRs(),
    sysTag.getProfileForUserACRs(),
    sysTag.getProfileForGroupACRs()}); }

 @Override
 public Permit checkSystemPermission(SystemAction act, User usr)
 {
  return sysTag.checkPermission(act, usr);
 }

 @Override
 public void addSecurityChangedListener(SecurityChangedListener lsnr)
 {
  synchronized(listeners)
  {
   listeners.add(lsnr);
  }
 }

 private synchronized void fireSecurityChanged()
 {
  synchronized(listeners)
  {
   for( SecurityChangedListener l : listeners )
    l.securityChanged();
  }
 }

 @Override
 public void prepareTransaction(Transaction t) throws TransactionException
 {
 }
 
}
