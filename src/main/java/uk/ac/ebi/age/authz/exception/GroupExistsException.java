package uk.ac.ebi.age.authz.exception;

public class GroupExistsException extends AuthDBException
{
 public GroupExistsException()
 {
  super("Group already exists");
 }
}
