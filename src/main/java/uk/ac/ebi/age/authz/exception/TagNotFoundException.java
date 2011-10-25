package uk.ac.ebi.age.authz.exception;

public class TagNotFoundException extends TagException
{
 public TagNotFoundException()
 {
  super("Tag not found");
 }
}
