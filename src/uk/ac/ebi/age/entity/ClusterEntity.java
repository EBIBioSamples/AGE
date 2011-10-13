package uk.ac.ebi.age.entity;

public class ClusterEntity implements Entity
{
 private String id;
 
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
