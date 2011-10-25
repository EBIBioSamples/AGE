package uk.ac.ebi.age.authz.writable;

import java.util.Collection;

import uk.ac.ebi.age.authz.Tag;

public interface TagWritable extends Tag
{
 Collection<? extends ProfileForGroupACRWritable> getProfileForGroupACRs();
 Collection<? extends ProfileForUserACRWritable> getProfileForUserACRs();
 Collection<? extends PermissionForUserACRWritable> getPermissionForUserACRs();
 Collection<? extends PermissionForGroupACRWritable> getPermissionForGroupACRs();


 void setId(String id);

 void setDescription(String description);

 void setParent(Tag parent);

 void addProfileForGroupACR(ProfileForGroupACRWritable acr);

 void addProfileForUserACR(ProfileForUserACRWritable acr);

 void addPermissionForUserACR(PermissionForUserACRWritable acr);

 void addPermissionForGroupACR(PermissionForGroupACRWritable acr);

}