package uk.ac.ebi.age.authz.writable;

import uk.ac.ebi.age.authz.ProfileForGroupACR;

public interface ProfileForGroupACRWritable extends ProfileForGroupACR
{

 void setSubject(UserGroupWritable gb);

 void setPermissionUnit(PermissionProfileWritable pb);

}