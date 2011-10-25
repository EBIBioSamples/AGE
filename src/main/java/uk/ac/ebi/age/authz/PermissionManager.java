package uk.ac.ebi.age.authz;

import java.util.Collection;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.ext.entity.Entity;

public interface PermissionManager
{

 Permit checkSystemPermission(SystemAction act);

 Permit checkPermission(SystemAction act, Entity objId);

 Permit checkPermission(SystemAction act, String objOwner, Entity objId);

 Permit checkSystemPermission(SystemAction act, String user);

 Collection<TagRef> getEffectiveTags(Entity objId);

 Collection<TagRef> getAllowTags(SystemAction act, String user);
 Collection<TagRef> getDenyTags(SystemAction act, String user);

}