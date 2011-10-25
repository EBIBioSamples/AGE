package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.authz.BuiltInGroups;
import uk.ac.ebi.age.authz.BuiltInUsers;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.authz.writable.UserGroupWritable;
import uk.ac.ebi.age.authz.writable.UserWritable;
import uk.ac.ebi.mg.collection.Named;

import com.pri.util.collection.Collections;

public class AuthenticatedGroupBean extends GroupBean implements UserGroup, Named<String>, Serializable
{

 private static final long serialVersionUID = 1L;

 AuthenticatedGroupBean()
 {}

 public boolean isPartOf(UserGroup pb)
 {
  return false;
 }

 @Override
 public boolean isUserCompatible(User u)
 {
  return ! u.getId().equals(BuiltInUsers.ANONYMOUS.getName());
 }

 @Override
 public String getId()
 {
  return BuiltInGroups.AUTHENTICATED.getName();
 }

 @Override
 public String getDescription()
 {
  return BuiltInGroups.AUTHENTICATED.getDescription();
 }

 @Override
 public Collection<UserWritable> getUsers()
 {
  return Collections.emptyList();
 }

 @Override
 public Collection< ? extends UserGroupWritable> getGroups()
 {
  return Collections.emptyList();
 }

 @Override
 public UserWritable getUser(String userId)
 {
  return null;
 }

 @Override
 public UserGroup getGroup(String partId)
 {
  return null;
 }
}
