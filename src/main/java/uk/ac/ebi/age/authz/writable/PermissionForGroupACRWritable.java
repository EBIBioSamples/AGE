package uk.ac.ebi.age.authz.writable;

import uk.ac.ebi.age.authz.PermissionForGroupACR;

public interface PermissionForGroupACRWritable extends PermissionForGroupACR
{

 void setSubject(UserGroupWritable gb);

 void setPermissionUnit(PermissionWritable pb);

}