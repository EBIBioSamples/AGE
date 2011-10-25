package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.authz.writable.PermissionForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.PermissionWritable;
import uk.ac.ebi.age.authz.writable.UserGroupWritable;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class PermissionForGroupACRBean implements Serializable, PermissionForGroupACRWritable
{
 private static final long serialVersionUID = 1L;


 private UserGroupWritable subject;
 private PermissionWritable perm;

 PermissionForGroupACRBean()
 {}
 
 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! subject.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return perm.checkPermission(act);
 }

 @Override
 public UserGroup getSubject()
 {
  return subject;
 }

 @Override
 public PermissionWritable getPermissionUnit()
 {
  return perm;
 }
 
 @Override
 public void setSubject( UserGroupWritable gb )
 {
  subject=gb;
 }

 @Override
 public void setPermissionUnit( PermissionWritable pb )
 {
  perm=pb;
 }

}
