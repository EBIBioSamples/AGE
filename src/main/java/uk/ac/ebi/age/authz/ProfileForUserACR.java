package uk.ac.ebi.age.authz;

public interface ProfileForUserACR extends ACR
{
 User getSubject();
 PermissionProfile getPermissionUnit();
}
