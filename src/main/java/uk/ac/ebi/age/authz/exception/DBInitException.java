package uk.ac.ebi.age.authz.exception;


public class DBInitException extends AuthDBException
{
 public DBInitException()
 {
  super("Auth database initialization exception");
 }

 public DBInitException(Exception e)
 {
  super("Auth database initialization exception", e);
 }
}
