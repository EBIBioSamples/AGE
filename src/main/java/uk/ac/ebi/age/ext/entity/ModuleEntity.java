package uk.ac.ebi.age.ext.entity;

import java.io.Serializable;

public class ModuleEntity implements Entity, Serializable
{

 private static final long serialVersionUID = 1L;

 private String id;
 private ClusterEntity clusterEntity;

 ModuleEntity()
 {}

 public ModuleEntity( ClusterEntity ce, String id )
 {
  this.id=id;
  clusterEntity = ce;
 }
 
 @Override
 public String getEntityID()
 {
  return id;
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.ATTACHMENT;
 }

 @Override
 public Entity getParentEntity()
 {
  return clusterEntity;
 }

 public void setEntityId(String id)
 {
  this.id = id;
 }

}
