package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.SemanticModel;
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
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;

public class ModelFactoryImpl extends ModelFactory implements Serializable
{
 private static final long serialVersionUID = 1L;

 private static ModelFactoryImpl instance = new ModelFactoryImpl();

 private ModelFactoryImpl()
 {}
 
 public static ModelFactory getInstance()
 {
  return instance;
 }

 
 private ModelFactory v1factory = uk.ac.ebi.age.model.impl.v1.ModelFactoryImpl.getInstance();
 private ModelFactory v3factory = uk.ac.ebi.age.model.impl.v3.ModelFactoryImpl.getInstance();
 private ModelFactory v4factory = uk.ac.ebi.age.model.impl.v4.ModelFactoryImpl.getInstance();
 
 @Override
 public SemanticModel createModelInstance()
 {
  SemanticModel mod = v1factory.createModelInstance();
  
  mod.setModelFactory( this );
  
  return mod;
 }
 
 @Override
 public DataModuleWritable createDataModule( ContextSemanticModel sm )
 {
  return v4factory.createDataModule( sm );
 }

 @Override
 public AgeObjectWritable createAgeObject( ClassRef clsR, String id)
 {
  return v3factory.createAgeObject(clsR, id);
 }

 @Override
 public AgeClassWritable createAgeClass(String name, String id, String pfx, SemanticModel sm)
 {
  return v1factory.createAgeClass(name, id, pfx, sm);
 }

 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, String id, SemanticModel sm)
 {
  return v1factory.createAgeRelationClass(name, id, sm);
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, SemanticModel sm)
 {
  return v1factory.createAgeAnnotationClass(name, id, sm);
 }

 
 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, SemanticModel sm)
 {
  return v1factory.createAgeAttributeClass(name, id, type, sm);
 }


 @Override
 public AgeExternalRelationWritable createExternalRelation(RelationClassRef rClsR, AgeObjectWritable sourceObj, String id, ResolveScope glb )
 {
  return v4factory.createExternalRelation(rClsR, sourceObj, id, glb);
 }
 

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, AttributedWritable host, String id, ResolveScope glb )
 {
  return v4factory.createExternalObjectAttribute(atCls, host, id, glb);
 }

 @Override
 public AgeRelationWritable createRelation(RelationClassRef rClsR, AgeObjectWritable targetObj)
 {
  return v3factory.createRelation(rClsR, targetObj);
 }


 @Override
 public AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, ContextSemanticModel sm, AgeClass owner)
 {
  return v3factory.createCustomAgeAttributeClass(name, type, sm, owner);
 }

 @Override
 public AgeClassWritable createCustomAgeClass(String name, String pfx, ContextSemanticModel sm)
 {
  return v3factory.createCustomAgeClass(name, pfx, sm);
 }

 @Override
 public AgeRelationClassWritable createCustomAgeRelationClass(String name, ContextSemanticModel sm, AgeClass range, AgeClass owner)
 {
  return v3factory.createCustomAgeRelationClass(name, sm, range, owner);
 }

 @Override
 public AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, ContextSemanticModel sm)
 {
  return v1factory.createAgeAttributeClassPlug(attrClass, sm);
 }

 @Override
 public AgeClassPlug createAgeClassPlug(AgeClass cls, ContextSemanticModel sm)
 {
  return v1factory.createAgeClassPlug(cls, sm);
 }

 @Override
 public AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass relClass, ContextSemanticModel sm)
 {
  return v1factory.createAgeRelationClassPlug(relClass, sm);
 }

 @Override
 public AgeRelationClassPlug createAgeRelationInverseClassPlug(AgeRelationClass relClass, ContextSemanticModel sm)
 {
  return v1factory.createAgeRelationInverseClassPlug(relClass, sm);
 }

 @Override
 public AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls, SemanticModel sm)
 {
  return v1factory.createAgeAnnotation(cls, sm);
 }

 @Override
 public AttributeAttachmentRuleWritable createAgeAttributeAttachmentRule(RestrictionType type, SemanticModel sm)
 {
  return v1factory.createAgeAttributeAttachmentRule(type,sm);
 }

 @Override
 public RelationRuleWritable createAgeRelationRule(RestrictionType type, SemanticModel sm)
 {
  return v1factory.createAgeRelationRule(type, sm);
 }

 @Override
 public QualifierRuleWritable createAgeQualifierRule(SemanticModel sm)
 {
  return v1factory.createAgeQualifierRule( sm );
 }

 @Override
 public AttributeClassRef createAttributeClassRef(AgeAttributeClassPlug plug, int order, String heading)
 {
  return v3factory.createAttributeClassRef(plug, order, heading);
 }

 @Override
 public RelationClassRef createRelationClassRef(AgeRelationClassPlug plug, int order, String heading)
 {
  return v3factory.createRelationClassRef(plug, order, heading);
 }

 @Override
 public ClassRef createClassRef(AgeClassPlug plug, int order, String heading, boolean hrz, ContextSemanticModel modl )
 {
  return v3factory.createClassRef(plug, order, heading, hrz, modl );
 }

 @Override
 public ContextSemanticModel createContextSemanticModel(SemanticModel mm)
 {
  return v1factory.createContextSemanticModel(mm);
 }

 @Override
 public AgeAttributeWritable createAgeStringAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return v3factory.createAgeStringAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeIntegerAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return v3factory.createAgeIntegerAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeRealAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return v3factory.createAgeRealAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeBooleanAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return v3factory.createAgeBooleanAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeFileAttribute(AttributeClassRef attrClass, AttributedWritable host, ResolveScope scope)
 {
  return v4factory.createAgeFileAttribute(attrClass, host, scope);
 }

 @Override
 public AgeAttributeWritable createAgeObjectAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return v3factory.createAgeObjectAttribute(attrClass, host);
 }

}
