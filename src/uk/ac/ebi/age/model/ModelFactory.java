package uk.ac.ebi.age.model;

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
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;

public abstract class ModelFactory
{
 public abstract DataModuleWritable createDataModule( ContextSemanticModel sm );

 public abstract AgeClassWritable createAgeClass(String name, String id, String pfx, SemanticModel sm);
 public abstract AgeClassWritable createCustomAgeClass(String name, String pfx, SemanticModel sm);

 public abstract AgeObjectWritable createAgeObject(String id, AgeClass ageClass, SemanticModel sm);

 public abstract AgeRelationClassWritable  createAgeRelationClass(String name, String id, SemanticModel sm);
 public abstract AgeRelationClassWritable createCustomAgeRelationClass(String name, SemanticModel sm, AgeClass range, AgeClass owner);

 public abstract AgeAttributeClassWritable  createAgeAttributeClass( String name, String id, DataType type, SemanticModel sm );
 public abstract AgeAttributeClassWritable createCustomAgeAttributeClass( String name, DataType type, SemanticModel sm, AgeClass owner );

 public abstract AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String id, AgeRelationClass targetClass,  SemanticModel sm);
 public abstract AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, String id, SemanticModel sm);

 public abstract AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass, SemanticModel sm);


 public abstract AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass, SemanticModel semanticModel);

 
 public abstract AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, SemanticModel sm);

 public abstract AgeClassPlug createAgeClassPlug(AgeClass attrClass, SemanticModel sm);
 
 public abstract AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass attrClass, SemanticModel sm);
 public abstract AgeRelationClassPlug createAgeRelationInverseClassPlug(AgeRelationClass cls, SemanticModel sm);

 public abstract AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, SemanticModel semanticModelImpl);
 public abstract AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls, SemanticModel semanticModelImpl);

 public abstract AttributeAttachmentRuleWritable createAgeAttributeAttachmentRule(RestrictionType type, SemanticModel sm);
 public abstract RelationRuleWritable createAgeRelationRule(RestrictionType type, SemanticModel sm);
 public abstract QualifierRuleWritable createAgeQualifierRule( SemanticModel sm);

 public abstract AttributeClassRef createAttributeClassRef( AgeAttributeClassPlug plug, int order, String heading);


}
