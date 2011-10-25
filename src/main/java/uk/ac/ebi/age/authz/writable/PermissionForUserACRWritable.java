package uk.ac.ebi.age.authz.writable;

import uk.ac.ebi.age.authz.PermissionForUserACR;

public interface PermissionForUserACRWritable extends PermissionForUserACR
{

 void setSubject(UserWritable ub);

 void setPermissionUnit(PermissionWritable pb);

}