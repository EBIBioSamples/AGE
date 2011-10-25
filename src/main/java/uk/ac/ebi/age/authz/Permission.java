package uk.ac.ebi.age.authz;

import uk.ac.ebi.age.ext.authz.SystemAction;

public interface Permission extends PermissionUnit
{
 SystemAction getAction();
 boolean isAllow();
 String getDescription();
}
