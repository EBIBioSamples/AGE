package uk.ac.ebi.age.model;


public interface AgeExternalRelation extends AgeRelation
{
 String getTargetObjectId();
 
 AgeObject getSourceObject();
}