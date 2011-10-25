package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.authz.ACR;
import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.writable.PermissionForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.PermissionForUserACRWritable;
import uk.ac.ebi.age.authz.writable.ProfileForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.ProfileForUserACRWritable;
import uk.ac.ebi.age.authz.writable.TagWritable;
import uk.ac.ebi.age.authz.Tag;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class TagBean implements TagWritable, Serializable
{

 private static final long serialVersionUID = 1L;


 private String id;
 private String description;
 private Tag    parent;
 
 private Collection<ProfileForGroupACRWritable> acrPf4G = null;
 private Collection<ProfileForUserACRWritable> acrPf4U = null;
 private Collection<PermissionForGroupACRWritable> acrPm4G = null;
 private Collection<PermissionForUserACRWritable> acrPm4U = null;

 TagBean()
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
 public boolean hasAccessRules()
 {
  return   ( acrPf4G != null && acrPf4G.size() > 0 )
        || ( acrPf4U != null && acrPf4U.size() > 0 )
        || ( acrPm4U != null && acrPm4U.size() > 0 )
        || ( acrPm4G != null && acrPm4G.size() > 0 );
 }
 
 @Override
 public Tag getParent()
 {
  return parent;
 }

 @Override
 public void setParent(Tag parent)
 {
  this.parent = parent;
 }

 @Override
 public Collection<ProfileForGroupACRWritable> getProfileForGroupACRs()
 {
  return acrPf4G;
 }

 @Override
 public Collection<ProfileForUserACRWritable> getProfileForUserACRs()
 {
  return acrPf4U;
 }

 @Override
 public Collection<PermissionForUserACRWritable> getPermissionForUserACRs()
 {
  return acrPm4U;
 }

 @Override
 public Collection<PermissionForGroupACRWritable> getPermissionForGroupACRs()
 {
  return acrPm4G;
 }

 @Override
 public void addProfileForGroupACR(ProfileForGroupACRWritable acr)
 {
  if(acrPf4G == null)
   acrPf4G = new ArrayList<ProfileForGroupACRWritable>();
 
  acrPf4G.add(acr);
 }

 @Override
 public void addProfileForUserACR(ProfileForUserACRWritable acr)
 {
  if(acrPf4U == null)
   acrPf4U = new ArrayList<ProfileForUserACRWritable>();
  
  acrPf4U.add(acr);
 }

 @Override
 public void addPermissionForUserACR(PermissionForUserACRWritable acr)
 {
  if(acrPm4U == null)
   acrPm4U = new ArrayList<PermissionForUserACRWritable>();
  
  acrPm4U.add(acr);
 }

 @Override
 public void addPermissionForGroupACR(PermissionForGroupACRWritable acr)
 {
  if(acrPm4G == null)
   acrPm4G = new ArrayList<PermissionForGroupACRWritable>();
  
  acrPm4G.add(acr);
 }

 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  boolean allow = false;

  if(acrPf4G != null)
  {
   for(ProfileForGroupACRWritable b : acrPf4G)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  if(acrPf4U != null)
  {
   for(ACR b : acrPf4U)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  if(acrPm4U != null)
  {
   for(ACR b : acrPm4U)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  if(acrPm4G != null)
  {
   for(ACR b : acrPm4G)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  return allow ? Permit.ALLOW : Permit.UNDEFINED;
 }

}
