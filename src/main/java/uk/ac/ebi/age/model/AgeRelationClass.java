package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeRelationClass extends AgeClassProperty, AgeAbstractClass, AgeSemanticElement, AttributedClass
{
 Collection<AgeRelationClass> getSuperClasses();
 Collection<AgeRelationClass> getSubClasses();



 String getId();
 String getName();

 boolean isWithinRange(AgeClass key);
 boolean isWithinDomain(AgeClass key);

 Collection<AgeClass> getRange();
 Collection<AgeClass> getDomain();

 boolean isCustom();

 AgeRelationClass getInverseRelationClass();
 
 boolean isImplicit();
 
 public void resetModel();
 
 boolean isFunctional();
 boolean isInverseFunctional();
 boolean isSymmetric();
 boolean isTransitive();

}
