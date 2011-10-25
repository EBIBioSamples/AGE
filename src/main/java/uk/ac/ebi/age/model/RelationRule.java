package uk.ac.ebi.age.model;

import java.util.Collection;

public interface RelationRule
{
 RestrictionType getRestrictionType();

 int getCardinality();

 Cardinality getCardinalityType();

 boolean isRelationSubclassesIncluded();

 RestrictionType getType();

 boolean isSubclassesIncluded();

 AgeRelationClass getRelationClass();

 AgeClass getTargetClass();

 Collection<QualifierRule> getQualifiers();

 int getRuleId();
}
