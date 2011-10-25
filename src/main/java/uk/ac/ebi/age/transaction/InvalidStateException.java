package uk.ac.ebi.age.transaction;

public class InvalidStateException extends RuntimeException
{
 public InvalidStateException()
 {
  super("Transaction/lock is inactive");
 }

 public InvalidStateException(String string)
 {
  super(string);
 }
}

