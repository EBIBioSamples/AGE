package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;

public interface AgeRelationClassWritable extends AgeRelationClass, AgeAbstractClassWritable, AttributedClassWritable
{
 void addSubClass(AgeRelationClassWritable makeRelationsBranch);
 void addSuperClass(AgeRelationClassWritable ageRelCls);

 void addDomainClass(AgeClass dmCls);
 void addRangeClass(AgeClass dmCls);

 void setInverseRelationClass(AgeRelationClass ageEl);
 
 void setImplicit(boolean b);
 
 void setAbstract(boolean b);
 
 void addAlias(String ali);
 
 void setFunctional(boolean functional);
 void setInverseFunctional(boolean inverseFunctional);
 void setSymmetric(boolean symmetric);
 void setTransitive(boolean transitive);
 
}
