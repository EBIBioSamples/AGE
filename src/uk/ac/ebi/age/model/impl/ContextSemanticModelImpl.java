package uk.ac.ebi.age.model.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;

public class ContextSemanticModelImpl implements ContextSemanticModel, Serializable
{
 private transient SemanticModel masterModel;
 
 private SubmissionContext context;
 
 private Map<String,AgeClass> customClassMap = new TreeMap<String, AgeClass>();
 private Map<String,AgeRelationClass> customRelationClassMap = new TreeMap<String, AgeRelationClass>();
// private Map<String,AgeAttributeClass> customAttributeClassMap = new TreeMap<String, AgeAttributeClass>();
 
 private Map<AgeClass,Map<String,AgeAttributeClass>> class2AttrMap = new TreeMap<AgeClass,Map<String,AgeAttributeClass>>();

 
 public ContextSemanticModelImpl( SemanticModel mm, SubmissionContext ctxt)
 {
  masterModel=mm;
  context=ctxt;
 }

 public AgeAttributeClass createCustomAgeAttributeClass(String name, DataType type, AgeClass cls)
 {
  AgeAttributeClass acls = createAgeAttributeClass(name,type);
  
  acls.setCustom( true );
  acls.setOwningClass( cls );
  
  Map<String,AgeAttributeClass> clsattr = class2AttrMap.get(cls);
  
  if( clsattr == null )
  {
   clsattr=new TreeMap<String,AgeAttributeClass>();
   class2AttrMap.put(cls, clsattr);
  }
  
  clsattr.put(name,acls);
  
  return acls;
 }

 public AgeClass createAgeClass(String name, String pfx)
 {
  AgeClass cls = masterModel.createAgeClass(name, pfx);
  customClassMap.put(name, cls);
  
  return cls;
 }

 public AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String val, AgeRelationClass targetClass )
 {
  return masterModel.createExternalRelation(sourceObj, val, targetClass);
 }

 public AgeRelationClass createRelationClass(String name, AgeClass cls, AgeClass rangeCls)
 {
  AgeRelationClass rcls = masterModel.createAgeRelationClass(name);
  
  rcls.addDomainClass(cls);
  rcls.addRangeClass(rangeCls);
  
  rcls.setCustom( true );
 
  return rcls;
 }

 public SubmissionContext getContext()
 {
  return context;
 }

 public AgeClass getDefinedAgeClass(String name)
 {
  return masterModel.getDefinedAgeClass(name);
 }

 public AgeRelationClass getDefinedAgeRelationClass(String name)
 {
  return masterModel.getDefinedAgeRelationClass(name);
 }

 
 public AgeClass getCustomAgeClass(String name)
 {
  return customClassMap.get(name);
 }
 
 public AgeClass getAgeClass(String name)
 {
  AgeClass cls = getCustomAgeClass(name);
  
  if( cls != null )
   return cls;
  
  return getDefinedAgeClass(name);
 }

 public AgeRelationClass getAgeRelationClass(String name)
 {
  AgeRelationClass cls = getCustomAgeRelationClass(name);
  
  if( cls != null )
   return cls;
  
  return getDefinedAgeRelationClass(name);
 }

 
 public AgeRelationClass getCustomAgeRelationClass(String name)
 {
  return customRelationClassMap.get(name);
 }

 public AgeAttributeClass getCustomAgeAttributeClass(String name, AgeClass cls)
 {
  Map<String,AgeAttributeClass> atclMap = class2AttrMap.get(cls);
  
  if( atclMap == null )
   return null;
  
  return atclMap.get(name);
 }

 public AgeClassProperty getDefinedAgeClassProperty( String name )
 {
  return masterModel.getDefinedAgeClassProperty(name);
 }

// public boolean isValidProperty(AgeClassProperty prop, AgeClass ageClass)
// {
//  return masterModel.isValidProperty(prop, ageClass);
// }

 public SubmissionWritable createSubmission()
 {
  return masterModel.getModelFactory().createSubmission(this);
 }

 public AgeAttributeClass createAgeAttributeClass(String name, DataType type)
 {
  return masterModel.createAgeAttributeClass(name, type);
 }

 public AgeObjectWritable createAgeObject(String id, AgeClass cls)
 {
  return masterModel.createAgeObject(id, cls);
 }

 public AgeRelationClass createAgeRelationClass(String name)
 {
  return masterModel.createAgeRelationClass(name);
 }

 public ModelFactory getModelFactory()
 {
  return masterModel.getModelFactory();
 }

 public AgeAttributeWritable createAgeAttribute(AgeObject obj, AgeAttributeClass attrClass)
 {
  return masterModel.createAgeAttribute(obj,attrClass);
 }

 public AgeRelationWritable createAgeRelation(AgeObjectWritable targetObj, AgeRelationClass relClass)
 {
  return masterModel.createAgeRelation(targetObj, relClass);
 }

 public AgeRelationClass getAttributeAttachmentClass()
 {
  return masterModel.getAttributeAttachmentClass();
 }

// public AgeAttributeClass getAgeAttributeClass(String name)
// {
//  AgeAttributeClass cls = getCustomAgeAttributeClass(name);
//  
//  if( cls != null )
//   return cls;
//  
//  return getDefinedAgeAttributeClass(name); 
// }

 public AgeAttributeClass getDefinedAgeAttributeClass(String attrClass)
 {
  return masterModel.getDefinedAgeAttributeClass( attrClass );
 }

 @Override
 public void setMasterModel(SemanticModel newModel)
 {
  masterModel = newModel;
 }

}
