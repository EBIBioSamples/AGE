package uk.ac.ebi.age.authz.exception;

public class PermissionNotFound extends AuthDBException
{
 public PermissionNotFound()
 {
  super("Permission not found");
 }
}
