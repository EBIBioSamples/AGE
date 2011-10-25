package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.writable.PermissionProfileWritable;
import uk.ac.ebi.age.authz.writable.ProfileForUserACRWritable;
import uk.ac.ebi.age.authz.writable.UserWritable;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class ProfileForUserACRBean implements ProfileForUserACRWritable, Serializable
{

 private static final long serialVersionUID = 1L;


 private UserWritable subject;
 private PermissionProfileWritable profile;

 ProfileForUserACRBean()
 {}
 
 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! subject.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return profile.checkPermission(act);
 }

 @Override
 public UserWritable getSubject()
 {
  return subject;
 }

 @Override
 public PermissionProfileWritable getPermissionUnit()
 {
  return profile;
 }
 
 @Override
 public void setSubject( UserWritable ub )
 {
  subject=ub;
 }

 @Override
 public void setPermissionUnit( PermissionProfileWritable pb )
 {
  profile=pb;
 }


}
