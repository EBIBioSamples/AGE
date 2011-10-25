package uk.ac.ebi.age.storage.exeption;

public class StorageInstantiationException extends Exception
{
 public StorageInstantiationException( String msg )
 {
  super(msg);
 }

 public StorageInstantiationException(String string, Exception e)
 {
  super(string,e);
 }
}
