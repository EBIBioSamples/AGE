package uk.ac.ebi.age.model;

public interface AgeMinCardinalityRestriction extends AgeRestriction
{
 AgeRelationClass getAgeRelationClass();
 AgeRestriction getFiller();
 
 int getCardinality();
}
