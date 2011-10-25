package uk.ac.ebi.age.log;

public class TooManyErrorsException extends RuntimeException
{

 private static final long serialVersionUID = 1L;
 
 private int errorCount;

 public TooManyErrorsException( int ec )
 {
  errorCount=ec;
 }

 public int getErrorCount()
 {
  return errorCount;
 }
 
}
