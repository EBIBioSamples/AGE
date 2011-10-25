package uk.ac.ebi.age.model;

public interface AgeRestriction
{

 void validate( AgeAbstractObject obj ) throws RestrictionException;

}
