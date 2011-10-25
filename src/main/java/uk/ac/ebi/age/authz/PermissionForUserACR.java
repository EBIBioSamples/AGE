package uk.ac.ebi.age.authz;

public interface PermissionForUserACR extends ACR
{
 User getSubject();
 Permission getPermissionUnit();
}
