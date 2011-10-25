package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.writable.PermissionProfileWritable;
import uk.ac.ebi.age.authz.writable.ProfileForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.UserGroupWritable;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class ProfileForGroupACRBean implements ProfileForGroupACRWritable, Serializable
{

 private static final long serialVersionUID = 1L;


 private UserGroupWritable group; 
 private PermissionProfileWritable profile; 
 
 ProfileForGroupACRBean()
 {}
 
 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! group.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return profile.checkPermission(act);
 }

 @Override
 public UserGroupWritable getSubject()
 {
  return group;
 }

 @Override
 public PermissionProfileWritable getPermissionUnit()
 {
  return profile;
 }

 @Override
 public void setSubject( UserGroupWritable gb )
 {
  group=gb;
 }

 @Override
 public void setPermissionUnit( PermissionProfileWritable pb )
 {
  profile=pb;
 }
}
