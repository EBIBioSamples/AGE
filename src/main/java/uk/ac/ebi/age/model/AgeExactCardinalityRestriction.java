package uk.ac.ebi.age.model;

public interface AgeExactCardinalityRestriction extends AgeRestriction
{
 AgeRelationClass getAgeRelationClass();
 AgeRestriction getFiller();
 
 int getCardinality();
}
