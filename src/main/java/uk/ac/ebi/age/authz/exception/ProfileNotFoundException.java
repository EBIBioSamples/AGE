package uk.ac.ebi.age.authz.exception;

public class ProfileNotFoundException extends AuthDBException
{
 private static final long serialVersionUID = 1L;

 public ProfileNotFoundException()
 {
  super("Profile not found");
 }

 public ProfileNotFoundException(String profileId)
 {
  super("Profile with ID '"+profileId+"' doesn't exist");
 }
}
