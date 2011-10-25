package uk.ac.ebi.age.ext.entity;

import java.io.Serializable;

public class GraphEntity implements Entity, Serializable
{

 private static final long serialVersionUID = 1L;

 private static GraphEntity instance = new GraphEntity(); 
 
 private GraphEntity()
 {}

 public static Entity getInstance()
 {
  return instance;
 }
 
 @Override
 public String getEntityID()
 {
  return "";
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.GRAPH;
 }

 @Override
 public Entity getParentEntity()
 {
  return null;
 }

}
