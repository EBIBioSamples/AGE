package uk.ac.ebi.age.authz.writable;

import uk.ac.ebi.age.authz.ProfileForUserACR;

public interface ProfileForUserACRWritable extends ProfileForUserACR
{

 void setSubject(UserWritable ub);

 void setPermissionUnit(PermissionProfileWritable pb);

}