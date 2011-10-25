package uk.ac.ebi.age.transaction;


public class InconsistentStateException extends TransactionException
{

 public InconsistentStateException(String string, Exception e)
 {
  super(string, e);
 }

}
