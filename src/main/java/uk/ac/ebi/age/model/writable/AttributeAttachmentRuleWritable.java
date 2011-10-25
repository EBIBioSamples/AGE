package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RestrictionType;

public interface AttributeAttachmentRuleWritable extends AttributeAttachmentRule
{
 void setAttributeClass(AgeAttributeClass ageAttributeClass);

 void setCardinality(int cardinality);

 void setCardinalityType(Cardinality cardinalityType);

 void setSubclassesIncluded(boolean subclassesIncluded);
 
 void setSubclassesCountedSeparately(boolean sbClsSep);
 
 void setType(RestrictionType type);

 void setValueUnique(boolean valueUnique);

 void addQualifier(QualifierRule qrul);

 void setRuleId( int id );
}
