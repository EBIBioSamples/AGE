package uk.ac.ebi.age.authz.exception;

public class EmailNotUniqueException extends AuthDBException
{

 private static final long serialVersionUID = 1L;

 public EmailNotUniqueException()
 {
  super("Email address is not unique");
 }

 public EmailNotUniqueException(String string)
 {
  super( "User with ID '"+string+"' has an email address that is already taken" );
 }
}
