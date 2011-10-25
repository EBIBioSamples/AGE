package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.writable.PermissionWritable;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class PermissionBean implements Serializable, PermissionWritable
{

 private static final long serialVersionUID = 1L;


 private SystemAction action;
 private boolean allow;
 
 PermissionBean()
 {}

 @Override
 public String getDescription()
 {
  return action.getDescription();
 }


 @Override
 public SystemAction getAction()
 {
  return action;
 }



 @Override
 public void setAction(SystemAction action)
 {
  this.action = action;
 }



 @Override
 public boolean isAllow()
 {
  return allow;
 }



 @Override
 public void setAllow(boolean allow)
 {
  this.allow = allow;
 }

 @Override
 public Permit checkPermission(SystemAction act)
 {
  if( act != action )
   return Permit.UNDEFINED;
  
  return allow?Permit.ALLOW:Permit.DENY;
 }

}
