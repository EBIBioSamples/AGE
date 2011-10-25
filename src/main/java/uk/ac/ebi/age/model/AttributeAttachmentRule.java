package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AttributeAttachmentRule
{
 RestrictionType getRestrictionType();

 AgeAttributeClass getAttributeClass();

 int getCardinality();
 
 boolean isSubclassesCountedSeparately();

 Cardinality getCardinalityType();

 boolean isSubclassesIncluded();

 RestrictionType getType();

 boolean isValueUnique();

 Collection<QualifierRule> getQualifiers();

 int getRuleId();


}
