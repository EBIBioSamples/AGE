package uk.ac.ebi.age.authz.exception;

public class GroupCycleException extends AuthDBException
{
 public GroupCycleException()
 {
  super("Group participation loop");
 }
}
