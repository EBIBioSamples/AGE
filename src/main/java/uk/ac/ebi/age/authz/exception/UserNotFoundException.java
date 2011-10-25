package uk.ac.ebi.age.authz.exception;

public class UserNotFoundException extends AuthDBException
{

 private static final long serialVersionUID = 1L;

 public UserNotFoundException()
 {
  super("User not found");
 }

 public UserNotFoundException(String userId)
 {
  super("User with ID '"+userId+"' doesn't exist");
 }
}
