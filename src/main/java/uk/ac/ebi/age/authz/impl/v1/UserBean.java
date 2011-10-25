package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.writable.UserGroupWritable;
import uk.ac.ebi.age.authz.writable.UserWritable;
import uk.ac.ebi.mg.collection.Named;

public class UserBean implements Named<String>, Serializable, UserWritable
{

 private static final long serialVersionUID = 1L;

 private String id;
 private String name;
 private String pass;
 private String email;
 private Set<UserGroupWritable> groups = new HashSet<UserGroupWritable>();

 UserBean()
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
 public String getName()
 {
  return name;
 }

 @Override
 public void setName(String name)
 {
  this.name = name;
 }

 @Override
 public String getPass()
 {
  return pass;
 }

 @Override
 public void setPass(String pass)
 {
  this.pass = pass;
 }

 @Override
 public Collection<UserGroupWritable> getGroups()
 {
  return groups;
 }

 @Override
 public void addGroup( UserGroupWritable grp )
 {
  groups.add(grp);
 }

 @Override
 public void removeGroup(UserGroupWritable gb)
 {
  groups.remove(gb);
 }

 @Override
 public boolean isUserCompatible(User u)
 {
  return u == this;
 }

 @Override
 public String getEmail()
 {
  return email;
 }

 @Override
 public void setEmail(String email)
 {
  this.email = email;
 }
 

}
