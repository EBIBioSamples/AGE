package uk.ac.ebi.age.model;

public class ModelException extends Exception
{
 public ModelException( String msg )
 {
  super( msg );
 }
 
 public ModelException( String msg, Throwable t )
 {
  super( msg, t );
 }

}
