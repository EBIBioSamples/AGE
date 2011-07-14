package uk.ac.ebi.age.authz.impl;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.annotation.AnnotationManager;
import uk.ac.ebi.age.annotation.Topic;
import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.AuthDB;
import uk.ac.ebi.age.authz.BuiltInUsers;
import uk.ac.ebi.age.authz.Classifier;
import uk.ac.ebi.age.authz.PermissionManager;
import uk.ac.ebi.age.authz.Session;
import uk.ac.ebi.age.authz.SessionManager;
import uk.ac.ebi.age.authz.Tag;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.exception.TagException;
import uk.ac.ebi.age.entity.ID;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.transaction.ReadLock;

public class PermissionManagerImpl implements PermissionManager
{
 private SessionManager sessMgr;
 private AuthDB authDB;
 private AnnotationManager annotMngr;
 
 public PermissionManagerImpl(SessionManager sessMgr, AuthDB authDB, AnnotationManager annotMngr)
 {
  this.sessMgr = sessMgr;
  this.authDB = authDB;
  this.annotMngr = annotMngr;
 }
 
 @Override
 public Collection<TagRef> getAllowTags( SystemAction act, String user )
 {
  return getTags(act, user, true);
 }

 @Override
 public Collection<TagRef> getDenyTags( SystemAction act, String user )
 {
  return getTags(act, user, false);
 }
 
 private Collection<TagRef> getTags( SystemAction act, String user, boolean allow )
 {
  ReadLock lck = authDB.getReadLock();
  
  ArrayList<TagRef> res = new ArrayList<TagRef>();
  
  Permit p = allow?Permit.ALLOW:Permit.DENY;
  
  try
  {
   User u = authDB.getUser(lck, user);
   
   if( u == null )
    return res;
   
   Collection<? extends Classifier> clasif = authDB.getClassifiers(lck, 0, Integer.MAX_VALUE);
   
   for( Classifier c : clasif )
   {
    for( Tag t : c.getTags() )
    {
     if( t.checkPermission(act, u) == p )
      res.add(new TagRef(c.getId(), t.getId()));
    }
   }
   
   return res;
  }
  finally
  {
   lck.release();
  }

 }

 
 @Override
 public Permit checkSystemPermission( SystemAction act )
 {
  Session sess = sessMgr.getSession();
  
  String user = sess!=null?sess.getUser():BuiltInUsers.ANONYMOUS.getName();
  
  return checkSystemPermission(act, user);
 }

 @Override
 public Permit checkSystemPermission( SystemAction act, String user )
 {
  
  ReadLock lck = authDB.getReadLock();
  
  try
  {
   User usr = authDB.getUser(lck, user);

   if(usr == null)
    return Permit.UNDEFINED;
   
   if( user.equals(BuiltInUsers.SUPERVISOR.getName()) )
    return Permit.ALLOW;
   
   return authDB.checkSystemPermission( act, usr );
  }
  finally
  {
   lck.release();
  }
 }
 
 @Override
 public Permit checkPermission( SystemAction act, ID objId )
 {
  return checkPermission(act, null, objId);
 }
 
 @Override
 @SuppressWarnings("unchecked")
 public Permit checkPermission( SystemAction act, String objOwner, ID objId )
 {
  Session sess = sessMgr.getSession();
  
  String user = sess!=null?sess.getUser():BuiltInUsers.ANONYMOUS.getName();
  
  if( user.equals(BuiltInUsers.SUPERVISOR.getName()) )
   return Permit.ALLOW;
  
  if( objOwner == null )
  {
   ID oid = objId;
   
   while( oid != null )
   {
    objOwner = (String) annotMngr.getAnnotation(Topic.OWNER, objId );
   
    if( objOwner != null )
     break;
    
    oid = oid.getParentObjectID();
   }
  }
  
  if( objOwner != null && objOwner.equals(user) )
   return Permit.ALLOW;
  
  Collection<TagRef> tags = null;
  ID oid = objId;

   
  ReadLock lck = authDB.getReadLock();
  
  try
  {
   User usr = authDB.getUser(lck, user);

   if(usr == null)
    return Permit.UNDEFINED;
   
   boolean gotRes = false;
   
   while( oid != null )
   {
    tags = (Collection<TagRef>) annotMngr.getAnnotation(Topic.TAG, objId );
   
    if( tags != null )
    {
     boolean allow = true;
     
     for( TagRef tagrf : tags )
     {
      try
      {
       Tag t = authDB.getTag(lck, tagrf.getClassiferName(), tagrf.getTagName());
       
       if( t == null || ! t.hasAccessRules() )
        continue;
       
       gotRes = true;
       
       Permit tp = t.checkPermission(act, usr);
       
       if( tp == Permit.DENY )
        return Permit.DENY;
       else if( tp == Permit.ALLOW )
        allow = true;
      }
      catch(TagException e)
      {
      }
     }     

     if( gotRes )
      return allow?Permit.ALLOW:Permit.UNDEFINED;
    }
    
    
    oid = oid.getParentObjectID();
   }

   return Permit.UNDEFINED;
  }
  finally
  {
   lck.release();
  }
 }

 @SuppressWarnings("unchecked")
 @Override
 public Collection<TagRef> getEffectiveTags( ID objId )
 {
  Collection<TagRef> tags = null;
  ID oid = objId;

   
  ReadLock lck = authDB.getReadLock();
  
  try
  {
   while( oid != null )
   {
    tags = (Collection<TagRef>) annotMngr.getAnnotation(Topic.TAG, objId );
   
    if( tags != null )
    {
     boolean allow = true;
     
     for( TagRef tagrf : tags )
     {
      try
      {
       Tag t = authDB.getTag(lck, tagrf.getClassiferName(), tagrf.getTagName());
       
       if( t == null || ! t.hasAccessRules() )
        continue;
       
       return tags;
      }
      catch(TagException e)
      {
      }
     }     
    }
    
    
    oid = oid.getParentObjectID();
   }

   return null;
  }
  finally
  {
   lck.release();
  }
 }

}
