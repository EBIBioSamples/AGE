package uk.ac.ebi.age.authz.exception;

public class UserExistsException extends AuthDBException
{

 private static final long serialVersionUID = 1L;

 public UserExistsException()
 {
  super("User exists");
 }

 public UserExistsException(String string)
 {
  super( "User with ID '"+string+"' exists" );
 }
}
