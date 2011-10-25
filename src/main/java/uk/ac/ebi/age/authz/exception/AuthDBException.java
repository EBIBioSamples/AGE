package uk.ac.ebi.age.authz.exception;

public class AuthDBException extends Exception
{

 public AuthDBException()
 {
  super("Authorization exception");
 }

 public AuthDBException(String string)
 {
  super( string );
 }

 public AuthDBException(String string, Exception e)
 {
  super( string, e );
 }

}
