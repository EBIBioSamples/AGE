package uk.ac.ebi.age.authz;

import java.util.Collection;

import uk.ac.ebi.mg.collection.Named;

public interface PermissionProfile extends PermissionUnit, Named<String>
{
 String getId();

 String getDescription();

 Collection<? extends Permission> getPermissions();
 Collection<? extends PermissionProfile> getProfiles();

 boolean isPartOf(PermissionProfile pb);

}
