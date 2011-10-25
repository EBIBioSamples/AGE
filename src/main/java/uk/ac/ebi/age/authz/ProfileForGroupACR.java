package uk.ac.ebi.age.authz;

public interface ProfileForGroupACR extends ACR
{
 UserGroup getSubject();
 PermissionProfile getPermissionUnit();
}
