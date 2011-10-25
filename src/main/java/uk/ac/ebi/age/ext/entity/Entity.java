package uk.ac.ebi.age.ext.entity;

public interface Entity
{
 String getEntityID();
 EntityDomain getEntityDomain();
 Entity getParentEntity();
}
