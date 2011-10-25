package uk.ac.ebi.age.authz.exception;

public class ProfileExistsException extends AuthDBException
{
 public ProfileExistsException()
 {
  super("Profile exists");
 }
}
