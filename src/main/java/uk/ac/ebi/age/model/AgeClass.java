package uk.ac.ebi.age.model;

import java.util.Collection;

/**
@model
*/

public interface AgeClass extends AgeSemanticElement, AgeAbstractClass, AttributedClass
{

 boolean isCustom();

 String getName();

 Collection<AgeClass> getSuperClasses();
 Collection<AgeClass> getSubClasses();

 String getIdPrefix();

 Collection<RelationRule> getRelationRules();
 Collection<RelationRule> getAllRelationRules();
 
}

