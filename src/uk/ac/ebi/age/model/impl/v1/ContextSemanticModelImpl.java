package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.AgeAnnotation;
import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.SubmissionContext;
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
import uk.ac.ebi.age.model.writable.SubmissionWritable;

public class ContextSemanticModelImpl implements ContextSemanticModel, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private transient SemanticModel masterModel;
 
 private transient SubmissionContext context;
 
 private Map<String,AgeClassPlug> classPlugs = new TreeMap<String, AgeClassPlug>();
 private Map<String,AgeAttributeClassPlug> attrClassPlugs = new TreeMap<String, AgeAttributeClassPlug>();
 private Map<String,AgeRelationClassPlug> relClassPlugs = new TreeMap<String, AgeRelationClassPlug>();
 private Map<String,AgeRelationClassPlug> relImplicitClassPlugs = new TreeMap<String, AgeRelationClassPlug>();

 
 private Map<String,AgeClassWritable> customClassMap = new TreeMap<String, AgeClassWritable>();
 private Map<String,AgeRelationClassWritable> customRelationClassMap = new TreeMap<String, AgeRelationClassWritable>();
// private Map<String,AgeAttributeClass> customAttributeClassMap = new TreeMap<String, AgeAttributeClass>();
 
 private Map<AgeClass,Map<String,AgeAttributeClassWritable>> class2CustomAttrMap = new HashMap<AgeClass,Map<String,AgeAttributeClassWritable>>();
// private Map<AgeClass,Map<String,AgeRelationClass>> class2CustomRelationMap = new TreeMap<AgeClass,Map<String,AgeRelationClass>>();

 
 public ContextSemanticModelImpl( SemanticModel mm, SubmissionContext ctxt)
 {
  masterModel=mm;
  context=ctxt;
 }

 public SemanticModel getMasterModel()
 {
  return masterModel;
 }
 
 @Override
 public AgeAttributeClassWritable getOrCreateCustomAgeAttributeClass(String name, DataType type, AgeClass cls, AgeAttributeClassWritable supCls )
 {
  AgeAttributeClassWritable acls = null;

  Map<String,AgeAttributeClassWritable> clsattr = class2CustomAttrMap.get(cls);
  
  if( clsattr == null )
  {
   clsattr=new TreeMap<String,AgeAttributeClassWritable>();
   class2CustomAttrMap.put(cls, clsattr);
  }
  else
   acls = clsattr.get(name);

  if( acls == null )
  {
   acls = masterModel.getModelFactory().createCustomAgeAttributeClass(name, type, this, cls);
   clsattr.put(name,acls);
  }
  
  if( supCls != null )
   acls.addSuperClass(supCls);
  
  return acls;
 }
 
 @Override
 public AgeRelationClassWritable getOrCreateCustomAgeRelationClass(String name, AgeClass range, AgeClass owner, AgeRelationClass supCls)
 {
//  AgeRelationClassWritable rCls = masterModel.getModelFactory().createCustomAgeRelationClass(name, this, range, owner);
//  customRelationClassMap.put(name, rCls);
//  
//  return rCls;
  
  AgeRelationClassWritable cls = customRelationClassMap.get(name);
  
  if( cls == null )
  {
   cls = masterModel.getModelFactory().createCustomAgeRelationClass(name, this, range, owner);
   customRelationClassMap.put(name, cls);
  }
  
  if( supCls != null )
   cls.addSuperClass( (AgeRelationClassWritable)supCls);
  
  return cls;
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, AgeAnnotationClass parent)
 {
  return masterModel.createAgeAnnotationClass(name, id, parent);
 }
 
 public AgeClassWritable createAgeClass(String name, String id, String pfx, AgeClass parent)
 {
  return masterModel.createAgeClass(name, id, pfx, parent);
 }
 
 public AgeClassWritable getOrCreateCustomAgeClass(String name, String pfx, AgeClass parent)
 {
  AgeClassWritable cls = customClassMap.get(name);
  
  if( cls == null )
  {
   cls = masterModel.getModelFactory().createCustomAgeClass(name, pfx, this);
   customClassMap.put(name, cls);
  }
  
  cls.addSuperClass((AgeClassWritable)parent);
  
  return cls;
 }

 @Override
 public AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String val, AgeRelationClass targetClass )
 {
  return masterModel.createExternalRelation(sourceObj, val, targetClass);
 }


 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AgeAttributeClass atCls, String val)
 {
  return masterModel.createExternalObjectAttribute(atCls, val);
 }

// public AgeRelationClass createRelationClass(String name, AgeClass cls, AgeClass rangeCls)
// {
//  AgeRelationClass rcls = masterModel.createAgeRelationClass(name);
//  
//  rcls.addDomainClass(cls);
//  rcls.addRangeClass(rangeCls);
//  
//  rcls.setCustom( true );
// 
//  return rcls;
// }

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
  Map<String,AgeAttributeClassWritable> atclMap = class2CustomAttrMap.get(cls);
  
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

 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, AgeAttributeClass parent)
 {
  return masterModel.createAgeAttributeClass(name, id, type, parent);
 }

 public AgeObjectWritable createAgeObject(String id, AgeClass cls)
 {
  return masterModel.getModelFactory().createAgeObject(id, cls, this);
 }

 public AgeRelationClassWritable createAgeRelationClass(String name, String id, AgeRelationClass parent)
 {
  return masterModel.createAgeRelationClass(name, id, parent);
 }

 public ModelFactory getModelFactory()
 {
  return masterModel.getModelFactory();
 }

 public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass)
 {
  return masterModel.getModelFactory().createAgeAttribute(attrClass,this);
 }
 
 public AgeRelationWritable createAgeRelation(AgeObjectWritable targetObj, AgeRelationClass relClass)
 {
  return masterModel.getModelFactory().createRelation(targetObj, relClass, this);
 }



 public AgeAttributeClass getDefinedAgeAttributeClass(String attrClass)
 {
  return masterModel.getDefinedAgeAttributeClass( attrClass );
 }

 @Override
 public void setMasterModel(SemanticModel newModel)
 {
  masterModel = newModel;
  
  for( AgeClassPlug plg: classPlugs.values() )
   plg.unplug();

  for( AgeAttributeClassPlug plg: attrClassPlugs.values() )
   plg.unplug();
  
  for( AgeRelationClassPlug plg: relClassPlugs.values() )
   plg.unplug();

  for( AgeRelationClassPlug plg: relImplicitClassPlugs.values() )
   plg.unplug();
 }

 @Override
 public AgeClassPlug getAgeClassPlug(AgeClass cls)
 {
  AgeClassPlug plug = classPlugs.get(cls.getId());
  
  if( plug != null )
   return plug;
  
  if( cls.isCustom() )
   plug = new AgeClassPlugFixed(cls);
  else
   plug = masterModel.getModelFactory().createAgeClassPlug(cls,this);
  
  classPlugs.put(cls.getId(), plug);
  
  return plug;
 }

 @Override
 public AgeClass getDefinedAgeClassById(String classId)
 {
  return masterModel.getDefinedAgeClassById(classId);
 }

 @Override
 public AgeRelationClassPlug getAgeRelationClassPlug(AgeRelationClass cls)
 {
  if( cls.isImplicit() )
  {
   AgeRelationClassPlug plug = relImplicitClassPlugs.get(cls.getInverseRelationClass().getId());

   if( plug != null )
    return plug;

   if( cls.getInverseRelationClass().isCustom() )
    plug = new AgeRelationClassPlugFixed(cls);
   else
    plug = masterModel.getModelFactory().createAgeRelationInverseClassPlug(cls,this);
  
   relImplicitClassPlugs.put(cls.getInverseRelationClass().getId(), plug);
   
   return plug;
  }
  else
  {
   AgeRelationClassPlug plug = relClassPlugs.get(cls.getId());
   
   if( plug != null )
    return plug;
   
   if( cls.isCustom() )
    plug = new AgeRelationClassPlugFixed(cls);
   else
    plug = masterModel.getModelFactory().createAgeRelationClassPlug(cls,this);
   
   relClassPlugs.put(cls.getId(), plug);
   
   return plug;
  }
 }

 @Override
 public AgeRelationClass getDefinedAgeRelationClassById(String classId)
 {
  return masterModel.getDefinedAgeRelationClassById(classId);
 }

 
 @Override
 public AgeAttributeClassPlug getAgeAttributeClassPlug(AgeAttributeClass cls)
 {
  AgeAttributeClassPlug plug = attrClassPlugs.get(cls.getId());
  
  if( plug != null )
   return plug;
  
  if( cls.isCustom() )
   plug = new AgeAttributeClassPlugFixed(cls);
  else
   plug = masterModel.getModelFactory().createAgeAttributeClassPlug(cls,this);
  
  attrClassPlugs.put(cls.getId(), plug);
  
  return plug;
 }

 @Override
 public AgeAttributeClass getDefinedAgeAttributeClassById(String classId)
 {
  return masterModel.getDefinedAgeAttributeClassById(classId);
 }

 @Override
 public Collection<? extends AgeClass> getAgeClasses()
 {
  return customClassMap.values();
 }

 @Override
 public AgeClass getRootAgeClass()
 {
  return null;
 }

 @Override
 public AgeAttributeClass getRootAgeAttributeClass()
 {
  return null;
 }
 
 @Override
 public AgeRelationClass getRootAgeRelationClass()
 {
  return null;
 }

 @Override
 public AgeAnnotationClass getRootAgeAnnotationClass()
 {
  return null;
 }

 public Collection<AgeAnnotation> getAnnotations()
 {
  return null;
 }

 @Override
 public AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls)
 {
  return masterModel.createAgeAnnotation(cls);
 }

 @Override
 public void addAnnotation(AgeAnnotation ant)
 {
 }

 @Override
 public AttributeAttachmentRuleWritable createAttributeAttachmentRule(RestrictionType type)
 {
  return masterModel.createAttributeAttachmentRule(type);
 }

 @Override
 public RelationRuleWritable createRelationRule(RestrictionType type)
 {
  return masterModel.createRelationRule(type);
 }

 @Override
 public QualifierRuleWritable createQualifierRule()
 {
  return masterModel.createQualifierRule();
 }

 @Override
 public int getIdGen()
 {
  return masterModel.getIdGen();
 }

 @Override
 public void setIdGen(int id)
 {
  masterModel.setIdGen( id );
 }

// @Override
// public void setRootAgeClass(AgeClass cls)
// {
//  masterModel.setRootAgeClass(cls);
// }
//
// @Override
// public void setRootAgeAttributeClass(AgeAttributeClass cls)
// {
//  masterModel.setRootAgeAttributeClass(cls);
// }
//
// @Override
// public void setRootAgeRelationClass(AgeRelationClass cls)
// {
//  masterModel.setRootAgeRelationClass(cls);
// }
//
// @Override
// public void setRootAgeAnnotationClass(AgeAnnotationClass cls)
// {
//  masterModel.setRootAgeAnnotationClass(cls);
// }

}
