package uk.ac.ebi.age.authz;

public interface PermissionForGroupACR extends ACR
{
 UserGroup getSubject();
 Permission getPermissionUnit();
}
