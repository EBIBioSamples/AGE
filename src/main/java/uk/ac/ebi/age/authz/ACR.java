package uk.ac.ebi.age.authz;

import uk.ac.ebi.age.ext.authz.SystemAction;

public interface ACR
{
 public enum Permit
 {
  ALLOW,
  DENY,
  UNDEFINED
 }
 
 Permit checkPermission( SystemAction act, User user );

 Subject getSubject();
 PermissionUnit getPermissionUnit();
}
