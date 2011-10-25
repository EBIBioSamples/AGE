package uk.ac.ebi.age.storage.exeption;

public class ModelStoreException extends Exception
{
 public ModelStoreException( String msg )
 {
  super( msg );
 }
 
 public ModelStoreException( String msg, Throwable t )
 {
  super( msg, t );
 }

}
