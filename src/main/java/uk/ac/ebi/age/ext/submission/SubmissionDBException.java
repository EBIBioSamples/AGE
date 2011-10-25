package uk.ac.ebi.age.ext.submission;

public class SubmissionDBException extends Exception
{
 private static final long serialVersionUID = 1L;

 public SubmissionDBException( )
 {}

 public SubmissionDBException( String msg )
 {
  super(msg);
 }
 
 public SubmissionDBException( String msg, Throwable cause )
 {
  super(msg,cause);
 }
}
