package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAnnotation;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.RelationRule;

/**
@model
*/

public interface AgeClassWritable extends AgeClass, AgeAbstractClassWritable
{
 void addObjectRestriction(AgeRestriction rest);

 void addAttributeRestriction(AgeRestriction rest);

 void addSubClass(AgeClass cls);
 void addSuperClass(AgeClass cls);

 void setId(String string);
 
 void addAnnotation( AgeAnnotation annt );

 void setAbstract(boolean b);

 void addAlias(String ali);

 void addRelationRule(RelationRule mrr);

 void addAttributeAttachmentRule(AttributeAttachmentRule atatRule);
}

