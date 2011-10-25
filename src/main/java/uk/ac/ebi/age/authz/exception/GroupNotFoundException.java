package uk.ac.ebi.age.authz.exception;

public class GroupNotFoundException extends AuthDBException
{
 private static final long serialVersionUID = 1L;

 public GroupNotFoundException()
 {
  super("Group not found");
 }

 public GroupNotFoundException(String grpId)
 {
  super("Group with ID '"+grpId+"' doesn't exist");
 }
}
