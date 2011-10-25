package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.writable.PermissionProfileWritable;
import uk.ac.ebi.age.authz.writable.PermissionWritable;
import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.authz.PermissionProfile;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class ProfileBean implements Serializable, PermissionProfileWritable
{

 private static final long serialVersionUID = 1L;


 private String id;
 private String description;
 private Collection<PermissionWritable> permissions = new HashSet<PermissionWritable>();
 private Set<PermissionProfileWritable> profiles = new HashSet<PermissionProfileWritable>();

 ProfileBean()
 {}
 
 @Override
 public String getId()
 {
  return id;
 }

 @Override
 public void setId(String id)
 {
  this.id = id;
 }

 @Override
 public String getDescription()
 {
  return description;
 }

 @Override
 public void setDescription(String description)
 {
  this.description = description;
 }

 @Override
 public Collection<PermissionWritable> getPermissions()
 {
  return permissions;
 }

 @Override
 public void removePermission(Permission perm)
 {
  permissions.remove(perm);
 }
 
 @Override
 public void addPermission(PermissionWritable perm)
 {
  permissions.add(perm);
 }

 @Override
 public void addProfile(PermissionProfileWritable npb)
 {
  profiles.add(npb);
 }
 
 public Collection<? extends PermissionProfile> getProfiles()
 {
  return profiles;
 }

 @Override
 public void removeProfile(PermissionProfile p)
 {
  profiles.remove(p);
 }

 @Override
 public boolean isPartOf(PermissionProfile pb)
 {
  if( pb.getProfiles() == null )
   return false;
  
  for( PermissionProfile gb : pb.getProfiles() )
  {
   if( equals(gb) )
    return true;
   
   if( isPartOf(gb) )
    return true;
  }
  
  return false;
 }

 @Override
 public Permit checkPermission( SystemAction act )
 {
  boolean allw = false;
  
  for( Permission p : permissions )
  {
   if( act == p.getAction() )
   {
    if( p.isAllow() )
     allw = true;
    else
     return Permit.DENY;
   }
  }
   
  for( PermissionProfileWritable pp : profiles )
  {
   Permit r = pp.checkPermission(act);
   
   if( r == Permit.DENY )
    return Permit.DENY;
   else if( r == Permit.ALLOW )
    allw = true;
  }
  
  return allw?Permit.ALLOW:Permit.UNDEFINED;
  
 }

}
