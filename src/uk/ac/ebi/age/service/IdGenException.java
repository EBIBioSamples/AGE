package uk.ac.ebi.age.service;

public class IdGenException extends RuntimeException
{

 private static final long serialVersionUID = 1L;

 public IdGenException( String msg, Throwable t )
 {
  super(msg,t);
 }
}
