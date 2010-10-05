package uk.ac.ebi.age.model;

import java.util.Collection;

import uk.ac.ebi.age.model.writable.AgeAnnotationClassWritable;
import uk.ac.ebi.age.model.writable.AgeAnnotationWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributeAttachmentRuleWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;

public interface SemanticModel
{
 int getIdGen();
 void setIdGen( int id );
 
 AgeClassWritable createAgeClass(String name, String id, String pfx);

 AgeRelationClassWritable createAgeRelationClass(String name, String id);

 AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type);
 AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id);
 AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls);

 
 AgeAttributeWritable createAgeAttribute(AgeAttributeClass attr);

 AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String val, AgeRelationClass relClass);

 AgeRelationWritable createAgeRelation(AgeObjectWritable targetObj, AgeRelationClass relClass);

 AgeObjectWritable createAgeObject(String id, AgeClass cls);

 AttributeAttachmentRuleWritable createAttributeAttachmentRule(RestrictionType type);

 RelationRuleWritable createRelationRule(RestrictionType type);
 QualifierRuleWritable createQualifierRule();

 AgeClass getDefinedAgeClass(String name);
 AgeClass getDefinedAgeClassById(String name);

 AgeRelationClass getDefinedAgeRelationClass(String name);
 AgeRelationClass getDefinedAgeRelationClassById(String name);

 AgeClassProperty getDefinedAgeClassProperty(String name);

// AgeRelationClass getAttributeAttachmentClass();
 
 AgeAttributeClass getDefinedAgeAttributeClass(String attrClass);
 AgeAttributeClass getDefinedAgeAttributeClassById(String classId);

 
 ModelFactory getModelFactory();

 
 AgeAttributeClassPlug getAgeAttributeClassPlug(AgeAttributeClass attrClass);

 AgeClassPlug getAgeClassPlug(AgeClass cls);

 AgeRelationClassPlug getAgeRelationClassPlug(AgeRelationClass relClass);

 Collection<AgeClass> getAgeClasses();
 
 
 AgeClass getRootAgeClass();

 AgeAttributeClass getRootAgeAttributeClass();

 AgeRelationClass getRootAgeRelationClass();

 AgeAnnotationClass getRootAgeAnnotationClass();

 void addAnnotation(AgeAnnotation ant);
 Collection<AgeAnnotation> getAnnotations();







// AgeClass getAgeClass(String clsName);
//
// AgeRelationClass getAgeRelationClass(String relClsClass);
//
// AgeAttributeClass getAgeAttributeClass(String attrClsName);


}
