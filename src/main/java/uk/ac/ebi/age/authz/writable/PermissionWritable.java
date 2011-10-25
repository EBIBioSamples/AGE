package uk.ac.ebi.age.authz.writable;

import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.ext.authz.SystemAction;

public interface PermissionWritable extends Permission
{

 void setAction(SystemAction action);

 void setAllow(boolean allow);

}