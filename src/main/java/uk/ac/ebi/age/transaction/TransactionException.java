package uk.ac.ebi.age.transaction;

public class TransactionException extends Exception
{

 private static final long serialVersionUID = 1L;

 public TransactionException(String string, Exception e)
 {
  super(string, e);
 }

}
