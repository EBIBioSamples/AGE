package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.writable.PermissionForUserACRWritable;
import uk.ac.ebi.age.authz.writable.PermissionWritable;
import uk.ac.ebi.age.authz.writable.UserWritable;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class PermissionForUserACRBean implements PermissionForUserACRWritable, Serializable
{

 private static final long serialVersionUID = 1L;


 private UserWritable subject;
 private PermissionWritable perm;

 PermissionForUserACRBean()
 {}
 
 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! subject.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return perm.checkPermission(act);
 }

 @Override
 public UserWritable getSubject()
 {
  return subject;
 }

 @Override
 public PermissionWritable getPermissionUnit()
 {
  return perm;
 }
 
 @Override
 public void setSubject( UserWritable ub )
 {
  subject=ub;
 }

 @Override
 public void setPermissionUnit( PermissionWritable pb )
 {
  perm=pb;
 }


}
