package uk.ac.ebi.age.model;

import java.util.Collection;

import uk.ac.ebi.age.model.writable.AgeAnnotationClassWritable;
import uk.ac.ebi.age.model.writable.AgeAnnotationWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AttributeAttachmentRuleWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;

public interface SemanticModel
{
 static final String ROOT_CLASS_NAME = "Class";
 static final String ROOT_ATTRIBUTE_CLASS_NAME = "Attribute";
 static final String ROOT_RELATION_CLASS_NAME = "Relation";
 static final String ROOT_ANNOTATION_CLASS_NAME = "Annotation";
 static final String ROOT_CLASS_ID = "__RootAgeClass";
 static final String ROOT_ATTRIBUTE_CLASS_ID = "__RootAgeAttributeClass";
 static final String ROOT_RELATION_CLASS_ID = "__RootAgeRelationClass";
 static final String ROOT_ANNOTATION_CLASS_ID = "__RootAgeAnnotationClass";

 int getIdGen();
 void setIdGen( int id );
 
 AgeClassWritable createAgeClass(String name, String id, String pfx, AgeClass parent);
 AgeClassWritable createAgeClass(String name, Collection<String> aliases, String id, String pfx, AgeClass parent);

 AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, AgeAttributeClass parent);
 AgeAttributeClassWritable createAgeAttributeClass(String name, Collection<String> aliases, String id, DataType type, AgeAttributeClass parent);

 AgeRelationClassWritable createAgeRelationClass(String name, String id, AgeRelationClass parent);
 AgeRelationClassWritable createAgeRelationClass(String name, Collection<String> aliases, String id, AgeRelationClass parent);

 AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, AgeAnnotationClass parent);
 AgeAnnotationClassWritable createAgeAnnotationClass(String name, Collection<String> aliases, String id, AgeAnnotationClass parent);

 
 
 AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls);
// AgeAttributeWritable createAgeAttribute(AgeAttributeClass attr);


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
 void setModelFactory( ModelFactory mf );

 
// AgeAttributeClassPlug getAgeAttributeClassPlug(AgeAttributeClass attrClass);
//
// AgeClassPlug getAgeClassPlug(AgeClass cls);
//
// AgeRelationClassPlug getAgeRelationClassPlug(AgeRelationClass relClass);

 Collection< ? extends AgeClass> getAgeClasses();
 
 
 AgeClass getRootAgeClass();

 AgeAttributeClass getRootAgeAttributeClass();

 AgeRelationClass getRootAgeRelationClass();

 AgeAnnotationClass getRootAgeAnnotationClass();

 void addAnnotation(AgeAnnotation ant);
 Collection<AgeAnnotation> getAnnotations();


// void setRootAgeClass( AgeClass cls );
// void setRootAgeAttributeClass( AgeAttributeClass cls );
// void setRootAgeRelationClass( AgeRelationClass cls );
// void setRootAgeAnnotationClass( AgeAnnotationClass cls );



// AgeClass getAgeClass(String clsName);
//
// AgeRelationClass getAgeRelationClass(String relClsClass);
//
// AgeAttributeClass getAgeAttributeClass(String attrClsName);


}
