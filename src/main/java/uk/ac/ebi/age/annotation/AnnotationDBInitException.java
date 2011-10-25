package uk.ac.ebi.age.annotation;


public class AnnotationDBInitException extends Exception
{

 private static final long serialVersionUID = 1L;

 public AnnotationDBInitException()
 {
  super("Annotation database initialization exception");
 }

 public AnnotationDBInitException(Exception e)
 {
  super("Annotation database initialization exception", e);
 }
}
