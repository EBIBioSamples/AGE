package uk.ac.ebi.age.ext.annotation;

public class AnnotationDBException extends Exception
{
 private static final long serialVersionUID = 1L;

 public AnnotationDBException( )
 {}

 public AnnotationDBException( String msg )
 {
  super(msg);
 }
 
 public AnnotationDBException( String msg, Throwable cause )
 {
  super(msg,cause);
 }
}
