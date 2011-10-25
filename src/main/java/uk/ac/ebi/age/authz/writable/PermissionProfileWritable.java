package uk.ac.ebi.age.authz.writable;

import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.authz.PermissionProfile;

public interface PermissionProfileWritable extends PermissionProfile
{

 void setId(String id);

 void setDescription(String description);

 void removePermission(Permission perm);

 void addPermission(PermissionWritable perm);

 void addProfile(PermissionProfileWritable npb);

 void removeProfile(PermissionProfile p);

}