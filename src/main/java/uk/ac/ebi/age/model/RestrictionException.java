package uk.ac.ebi.age.model;

public class RestrictionException extends Exception
{

 public RestrictionException(String string)
 {
  super(string);
 }

 public RestrictionException(String string, RestrictionException e)
 {
  super(string,e);
 }

}
