package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.RestrictionType;

public interface RelationRuleWritable extends RelationRule
{

 void setCardinality(int cardinality);

 void setCardinalityType(Cardinality cardinalityType);

 void setRelationSubclassesIncluded(boolean relationSubclassesIncluded);

 void setType(RestrictionType type);

 void setSubclassesIncluded(boolean subclassesIncluded);

 void setRelationClass(AgeRelationClass ageRelationClass);

 void setTargetClass(AgeClass ageClass);

 void addQualifier(QualifierRule qrul);

 void setRuleId( int id );
}
