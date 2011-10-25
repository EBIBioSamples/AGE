package uk.ac.ebi.age.authz;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.ext.authz.SystemAction;

public interface PermissionUnit
{
 Permit checkPermission( SystemAction act );
}
