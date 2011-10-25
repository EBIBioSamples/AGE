package uk.ac.ebi.age.authz.exception;

public class TagExistsException extends TagException
{
 public TagExistsException()
 {
  super("Tag exists");
 }
}
