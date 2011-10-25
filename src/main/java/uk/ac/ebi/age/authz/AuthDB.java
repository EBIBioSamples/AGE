package uk.ac.ebi.age.authz;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.exception.AuthDBException;
import uk.ac.ebi.age.authz.exception.TagException;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionalDB;

import com.pri.util.collection.ListFragment;

public interface AuthDB extends TransactionalDB
{
 void addSecurityChangedListener( SecurityChangedListener lsnr );
 
 Permit checkSystemPermission(SystemAction act, User usr);

 User getUser( ReadLock lock, String id );
 User getUserByEmail(ReadLock lck, String email);
 List< ? extends User> getUsers( ReadLock lock, int begin, int end);
 ListFragment<User> getUsers( ReadLock lock, String idPat, String namePat, int begin, int end);

 int getUsersTotal( ReadLock lock );

 void updateUser( Transaction trn, String userId, String userName, String email) throws AuthDBException;
 void setUserPassword(Transaction trn, String userId, String userPass) throws AuthDBException;
 boolean checkUserPassword(ReadLock lck, String userId, String userPass) throws AuthDBException;

 void addUser( Transaction trn, String userId, String userName, String email, String userPass) throws AuthDBException;

 void deleteUser( Transaction trn, String userId) throws AuthDBException;

 
 UserGroup getUserGroup( ReadLock lock, String id );
 List< ? extends UserGroup> getGroups( ReadLock lock,int begin, int end);
 ListFragment<UserGroup> getGroups( ReadLock lock,String idPat, String namePat, int begin, int end);

 int getGroupsTotal( ReadLock lock);

 void deleteGroup( Transaction trn, String grpId) throws AuthDBException;

 void addGroup( Transaction trn, String grpId, String grpDesc) throws AuthDBException;

 void updateGroup( Transaction trn, String grpId, String grpDesc) throws AuthDBException;

 Collection< ? extends UserGroup> getGroupsOfUser( ReadLock lock, String userId) throws AuthDBException;

 void removeUserFromGroup( Transaction trn, String grpId, String userId) throws AuthDBException;
 void removeGroupFromGroup( Transaction trn, String grpId, String partId) throws AuthDBException;

 void addUserToGroup( Transaction trn, String userId, String grpId) throws AuthDBException;
 void addGroupToGroup( Transaction trn, String grpId, String partId) throws AuthDBException;

 Collection< ? extends User> getUsersOfGroup( ReadLock lock, String groupId) throws AuthDBException;
 Collection< ? extends UserGroup> getGroupsOfGroup( ReadLock lock, String groupId) throws AuthDBException;

 void addProfile( Transaction trn, String profId, String dsc) throws AuthDBException;
 void updateProfile( Transaction trn, String profId, String dsc) throws AuthDBException;
 void deleteProfile( Transaction trn, String profId) throws AuthDBException;
 
 PermissionProfile getProfile( ReadLock lock, String id );
 List< ? extends PermissionProfile> getProfiles( ReadLock lock, int begin, int end);
 ListFragment<PermissionProfile> getProfiles( ReadLock lock, String idPat, String namePat, int begin, int end);
 int getProfilesTotal( ReadLock lock );

 void addPermissionToProfile( Transaction trn, String profId, SystemAction actn, boolean allow) throws AuthDBException;
 void addProfileToProfile( Transaction trn, String profId, String string) throws AuthDBException;
 Collection< ? extends Permission> getPermissionsOfProfile( ReadLock lock, String profId) throws AuthDBException;
 Collection< ? extends PermissionProfile> getProfilesOfProfile( ReadLock lock, String profId) throws AuthDBException;
 void removePermissionFromProfile( Transaction trn, String profId, SystemAction actn, boolean allow) throws AuthDBException;
 void removeProfileFromProfile( Transaction trn, String profId, String toRemProf) throws AuthDBException;


 
 void deleteClassifier( Transaction trn,String csfId) throws TagException;

 void addClassifier( Transaction trn,String csfId, String csfDesc) throws TagException;

 void updateClassifier( Transaction trn,String csfId, String csfDesc) throws TagException;

 Classifier getClassifier( ReadLock lock,  String id );
 List< ? extends Classifier> getClassifiers( ReadLock lock, int begin, int end);

 int getClassifiersTotal( ReadLock lock );

 ListFragment<Classifier> getClassifiers( ReadLock lock, String string, String string2, int begin, int end);

 void removeTagFromClassifier( Transaction trn,String clsId, String tagId) throws TagException;

 void addTagToClassifier( Transaction trn,String clsId, String tagId, String description, String parentTagId) throws TagException;

 void updateTag( Transaction trn,String clsId, String tagId, String desc, String parentTagId) throws TagException;

 Tag getTag( ReadLock lock,  String clsfId, String tagId ) throws TagException;

 Collection< ? extends Tag> getTagsOfClassifier( ReadLock lock, String clsId, String parentTagId) throws TagException;
 Collection< ? extends Tag> getTagsOfClassifier( ReadLock lock, String clsId) throws TagException;

 boolean removeProfileForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException;
 boolean removeProfileForUserACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException;
 boolean removePermissionForUserACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException;
 boolean removePermissionForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException;

 void addProfileForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthDBException;
 void addProfileForUserACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthDBException;
 void addActionForUserACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthDBException;
 void addActionForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthDBException;

 Collection< ? extends ACR> getACL( ReadLock lock, String clsfId, String tagId) throws TagException;

 boolean removeSysProfileForGroupACR(Transaction trn, String subjId, String profileId) throws AuthDBException;
 boolean removeSysPermissionForGroupACR(Transaction trn, String subjId, SystemAction action, boolean allow) throws AuthDBException;
 boolean removeSysProfileForUserACR(Transaction trn, String subjId, String profileId) throws AuthDBException;
 boolean removeSysPermissionForUserACR(Transaction trn, String subjId, SystemAction action, boolean allow) throws AuthDBException;
 void addSysProfileForGroupACR(Transaction trn, String subjId, String profileId) throws AuthDBException;
 void addSysActionForGroupACR(Transaction trn, String subjId, SystemAction action, boolean allow) throws AuthDBException;
 void addSysProfileForUserACR(Transaction trn, String subjId, String profileId) throws AuthDBException;
 void addSysActionForUserACR(Transaction trn, String subjId, SystemAction action, boolean allow) throws AuthDBException;
 Collection< ? extends ACR> getSysACL(ReadLock rl) throws AuthDBException;

}
