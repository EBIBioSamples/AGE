package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.authz.writable.UserGroupWritable;
import uk.ac.ebi.age.authz.writable.UserWritable;
import uk.ac.ebi.mg.collection.IndexList;
import uk.ac.ebi.mg.collection.Named;

import com.pri.util.NaturalStringComparator;

public class GroupBean implements Named<String>, Serializable, UserGroupWritable
{

 private static final long serialVersionUID = 1L;

 private String id;
 private String description;
 private IndexList<String,UserWritable> users = new IndexList<String,UserWritable>( NaturalStringComparator.getInstance() );
 private IndexList<String,UserGroupWritable> groups = new IndexList<String,UserGroupWritable>( NaturalStringComparator.getInstance() );

 GroupBean()
 {}
 
 @Override
 public String getId()
 {
  return id;
 }

 @Override
 public void setId(String id)
 {
  this.id = id;
 }

 @Override
 public String getDescription()
 {
  return description;
 }

 @Override
 public void setDescription(String description)
 {
  this.description = description;
 }

 @Override
 public void addUser(UserWritable u)
 {
  users.add(u);
 }

 @Override
 public void addGroup(UserGroupWritable g)
 {
  groups.add(g);
 }

 @Override
 public Collection<UserWritable> getUsers()
 {
  return users;
 }

 @Override
 public void removeUser(UserWritable ub)
 {
  users.remove(ub);
 }
 
 @Override
 public void removeGroup(UserGroup gp)
 {
  groups.remove(gp);
 }

 @Override
 public Collection< ? extends UserGroupWritable> getGroups()
 {
  return groups;
 }

 @Override
 public boolean isPartOf(UserGroup pb)
 {
  if( pb.getGroups() == null )
   return false;
  
  for( UserGroup gb : pb.getGroups() )
  {
   if( equals(gb) )
    return true;
   
   if( isPartOf(gb) )
    return true;
  }
  
  return false;
 }

 @Override
 public boolean isUserCompatible(User u)
 {
  for( User mu : users )
   if( u == mu )
    return true;
  
  for( UserGroupWritable gb : groups )
   if( gb.isUserCompatible(u) )
    return true;
  
  return false;
 }

 @Override
 public UserWritable getUser(String userId)
 {
  return users.getByKey(userId);
 }

 @Override
 public UserGroup getGroup(String partId)
 {
  return groups.getByKey(partId);
 }



}
