package uk.ac.ebi.age.authz.exception;

public class ProfileCycleException extends AuthDBException
{
 public ProfileCycleException()
 {
  super("Profiles loop");
 }
}
