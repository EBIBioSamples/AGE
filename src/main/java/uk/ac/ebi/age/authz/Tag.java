package uk.ac.ebi.age.authz;

import java.util.Collection;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.ext.authz.SystemAction;

public interface Tag
{
 String getId();
 String getDescription();
 Tag getParent();
 
 boolean hasAccessRules();
 Permit checkPermission( SystemAction act, User user );
 
 Collection<? extends ProfileForGroupACR> getProfileForGroupACRs();
 Collection<? extends ProfileForUserACR> getProfileForUserACRs();
 Collection<? extends PermissionForUserACR> getPermissionForUserACRs();
 Collection<? extends PermissionForGroupACR> getPermissionForGroupACRs();

}
