package uk.ac.ebi.age.ext.entity;

import java.io.Serializable;

public class ClusterEntity implements Entity, Serializable
{

 private static final long serialVersionUID = 1L;

 private String id;
 
 public ClusterEntity()
 {}
 
 public ClusterEntity( String id )
 {
  this.id=id;
 }
 
 @Override
 public String getEntityID()
 {
  return id;
 }
 
 public void setEntityID( String id )
 {
  this.id=id;
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.CLUSTER;
 }

 @Override
 public Entity getParentEntity()
 {
  return GraphEntity.getInstance();
 }

}
