package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.QualifierRule;

public interface QualifierRuleWritable extends QualifierRule
{

 void setAttributeClass(AgeAttributeClass ageAttributeClass);

 void setUnique(boolean unique);

 void setRuleId(int id);

}
