package uk.ac.ebi.age.model;

public interface ClassRef
{
 AgeClass getAgeClass();
 int getOrder();
 String getHeading();
 boolean isHorizontal();
 ContextSemanticModel getSemanticModel();
}
