package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;
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
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.RelationClassRef;
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
import uk.ac.ebi.age.service.id.IdGenerator;

public class ContextSemanticModelImpl implements ContextSemanticModel, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private transient SemanticModel masterModel;
 
 private Map<String,AgeClassPlug> classPlugs;
 private Map<String,AgeAttributeClassPlug> attrClassPlugs;
 private Map<String,AgeRelationClassPlug> relClassPlugs;
 private Map<String,AgeRelationClassPlug> relImplicitClassPlugs;

 
 private Map<String,AgeClassWritable> customClassMap;
 private Map<String,AgeRelationClassWritable> customRelationClassMap;
 
 private Map<String,Map<String,AgeAttributeClassWritable>> definedClass2CustomAttrMap;
 private Map<String,Map<String,AgeAttributeClassWritable>> customClass2CustomAttrMap;
// private Map<AgeClass,Map<String,AgeRelationClass>> class2CustomRelationMap = new TreeMap<AgeClass,Map<String,AgeRelationClass>>();

 
 public ContextSemanticModelImpl( SemanticModel mm )
 {
  masterModel=mm;
 }

 public SemanticModel getMasterModel()
 {
  return masterModel;
 }
 
 @Override
 public AgeAttributeClassWritable getOrCreateCustomAgeAttributeClass(String name, DataType type, AgeClass cls, AgeAttributeClassWritable supCls )
 {
  AgeAttributeClassWritable acls = null;

  Map<String,AgeAttributeClassWritable> clsattr = null;
  Map<String,Map<String,AgeAttributeClassWritable>> map = null;
  
  if( cls.isCustom() )
  {
   if( customClass2CustomAttrMap == null )
    map=customClass2CustomAttrMap=new TreeMap<String, Map<String,AgeAttributeClassWritable>>();
   else
   {
    map=customClass2CustomAttrMap;
    clsattr = map.get(cls.getName());
   }
  }
  else
  {
   if( definedClass2CustomAttrMap == null )
    map=definedClass2CustomAttrMap=new TreeMap<String, Map<String,AgeAttributeClassWritable>>();
   else
   {
    map=definedClass2CustomAttrMap;
    clsattr = map.get(cls.getName());
   }
  }
  
  
  if( clsattr == null )
  {
   clsattr=new TreeMap<String,AgeAttributeClassWritable>();
   map.put(cls.getName(), clsattr);
   
//   if( cls.getAliases() != null )
//   {
//    for( String alias : cls.getAliases() )
//     map.put(alias, clsattr);
//   }
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
  AgeRelationClassWritable cls = null;
  
  if( customRelationClassMap == null )
   customRelationClassMap = new TreeMap<String, AgeRelationClassWritable>();
  else
   cls = customRelationClassMap.get(name);
  
  if( cls == null )
  {
   cls = masterModel.getModelFactory().createCustomAgeRelationClass(name, this, range, owner);
   customRelationClassMap.put(name, cls);
  }
  
  if( supCls != null )
   cls.addSuperClass( (AgeRelationClassWritable)supCls);
  
  
  AgeRelationClassWritable invRelCls = masterModel.getModelFactory().createAgeRelationClass( "!"+name,
    "InvImpRelClass-"+IdGenerator.getInstance().getStringId("classId"), null );
  
  invRelCls.setImplicit(true);
  
  cls.setInverseRelationClass( invRelCls );
  invRelCls.setInverseRelationClass(cls);

  
  return cls;
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, AgeAnnotationClass parent)
 {
  return masterModel.createAgeAnnotationClass(name, id, parent);
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, Collection<String> aliases, String id, AgeAnnotationClass parent)
 {
  return masterModel.createAgeAnnotationClass(name, aliases, id, parent);
 }

 public AgeClassWritable createAgeClass(String name, String id, String pfx, AgeClass parent)
 {
  return masterModel.createAgeClass(name, id, pfx, parent);
 }
 
 public AgeClassWritable createAgeClass(String name, Collection<String> aliases, String id, String pfx, AgeClass parent)
 {
  return masterModel.createAgeClass(name, aliases, id, pfx, parent);
 }

 
 public AgeClassWritable getOrCreateCustomAgeClass(String name, String pfx, AgeClass parent)
 {
  AgeClassWritable cls = null;
  
  if( customClassMap == null )
   customClassMap = new TreeMap<String, AgeClassWritable>();
  else
   cls = customClassMap.get(name);
  
  if( cls == null )
  {
   cls = masterModel.getModelFactory().createCustomAgeClass(name, pfx, this);
   customClassMap.put(name, cls);
  }
  
  cls.addSuperClass((AgeClassWritable)parent);
  
  return cls;
 }


 
 @Override
 public AgeExternalRelationWritable createExternalRelation(RelationClassRef clsRef, AgeObjectWritable sourceObj, String val )
 {
  return masterModel.getModelFactory().createExternalRelation(clsRef, sourceObj, val);
 }


 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, AttributedWritable host, String val )
 {
  return masterModel.getModelFactory().createExternalObjectAttribute(atCls, host, val );
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


 @Override
 public AgeClass getDefinedAgeClass(String name)
 {
  return masterModel.getDefinedAgeClass(name);
 }

 @Override
 public AgeRelationClass getDefinedAgeRelationClass(String name)
 {
  return masterModel.getDefinedAgeRelationClass(name);
 }

 
 @Override
 public AgeClass getCustomAgeClass(String name)
 {
  if( customClassMap == null )
   return null;
  
  return customClassMap.get(name);
 }

 /*
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
*/
 
 @Override
 public AgeRelationClass getCustomAgeRelationClass(String name)
 {
  if( customRelationClassMap == null )
   return null;
  
  return customRelationClassMap.get(name);
 }

 @Override
 public AgeAttributeClass getCustomAgeAttributeClass(String name, AgeClass cls)
 {
  Map<String,AgeAttributeClassWritable> atclMap = null;
  
  if( cls.isCustom() )
   atclMap = customClass2CustomAttrMap != null? customClass2CustomAttrMap.get(cls.getName()) : null;
  else
   atclMap = definedClass2CustomAttrMap != null? definedClass2CustomAttrMap.get(cls.getName()) : null;
  
  if( atclMap == null )
   return null;
  
  return atclMap.get(name);
 }

 @Override
 public AgeClassProperty getDefinedAgeClassProperty( String name )
 {
  return masterModel.getDefinedAgeClassProperty(name);
 }

// public boolean isValidProperty(AgeClassProperty prop, AgeClass ageClass)
// {
//  return masterModel.isValidProperty(prop, ageClass);
// }

 public DataModuleWritable createDataModule()
 {
  return masterModel.getModelFactory().createDataModule(this);
 }

 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, AgeAttributeClass parent)
 {
  return masterModel.createAgeAttributeClass(name, id, type, parent);
 }

 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, Collection<String> aliases, String id, DataType type, AgeAttributeClass parent)
 {
  return masterModel.createAgeAttributeClass(name, aliases, id, type, parent);
 }
 
 @Override
 public AgeObjectWritable createAgeObject(ClassRef cls, String id )
 {
  return masterModel.getModelFactory().createAgeObject( cls, id );
 }

 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, String id, AgeRelationClass parent)
 {
  return masterModel.createAgeRelationClass(name, id, parent);
 }

 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, Collection<String> aliases, String id, AgeRelationClass parent)
 {
  return masterModel.createAgeRelationClass(name, aliases, id, parent);
 }
 
 @Override
 public ModelFactory getModelFactory()
 {
  return masterModel.getModelFactory();
 }

// @Override
// public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass, AttributedWritable host)
// {
//  AttributeClassRef ref = masterModel.getModelFactory().createAttributeClassRef( getAgeAttributeClassPlug(attrClass), 0, attrClass.getName());
//  
//  return masterModel.getModelFactory().createAgeAttribute(ref,host);
// }
 
 @Override
 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return masterModel.getModelFactory().createAgeAttribute(attrClass, host);
 }

 
 @Override
 public AgeRelationWritable createAgeRelation(RelationClassRef rClsR, AgeObjectWritable sourceObj, AgeObjectWritable targetObj)
 {
  return masterModel.getModelFactory().createRelation(rClsR,sourceObj, targetObj);
 }


 @Override
 public AgeAttributeClass getDefinedAgeAttributeClass(String attrClass)
 {
  return masterModel.getDefinedAgeAttributeClass( attrClass );
 }

 @Override
 public void setMasterModel(SemanticModel newModel)
 {
  masterModel = newModel;

  if(classPlugs != null)
  {
   for(AgeClassPlug plg : classPlugs.values())
    plg.unplug();
  }

  if(attrClassPlugs != null)
  {
   for(AgeAttributeClassPlug plg : attrClassPlugs.values())
    plg.unplug();
  }

  if(relClassPlugs != null)
  {
   for(AgeRelationClassPlug plg : relClassPlugs.values())
    plg.unplug();
  }

  if(relImplicitClassPlugs != null)
  {
   for(AgeRelationClassPlug plg : relImplicitClassPlugs.values())
    plg.unplug();
  }
 }

 @Override
 public AgeClassPlug getAgeClassPlug(AgeClass cls)
 {
  AgeClassPlug plug = null;
  
  if( classPlugs == null )
   classPlugs = new TreeMap<String, AgeClassPlug>();
  else
  {
   plug = classPlugs.get(cls.getId());
   
   if( plug != null )
    return plug;
  }
  
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
   AgeRelationClassPlug plug = null;
   
   if( relImplicitClassPlugs == null )
    relImplicitClassPlugs = new TreeMap<String, AgeRelationClassPlug>();
   else
   {
    plug = relImplicitClassPlugs.get(cls.getInverseRelationClass().getId());
    
    if( plug != null )
     return plug;
   }
   

   if( cls.getInverseRelationClass().isCustom() )
    plug = new AgeRelationClassPlugFixed(cls);
   else
    plug = masterModel.getModelFactory().createAgeRelationInverseClassPlug(cls,this);
  
   relImplicitClassPlugs.put(cls.getInverseRelationClass().getId(), plug);
   
   return plug;
  }
  else
  {
   AgeRelationClassPlug plug = null;

   if(relClassPlugs == null)
    relClassPlugs = new TreeMap<String, AgeRelationClassPlug>();
   else
   {
    plug = relClassPlugs.get(cls.getId());

    if(plug != null)
     return plug;
   }

   if(cls.isCustom())
    plug = new AgeRelationClassPlugFixed(cls);
   else
    plug = masterModel.getModelFactory().createAgeRelationClassPlug(cls, this);

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
  if( cls.isCustom() )
   return cls;
  
  AgeAttributeClassPlug plug = null;
  
  if( attrClassPlugs == null )
   attrClassPlugs = new TreeMap<String, AgeAttributeClassPlug>();
  else
  {
   plug = attrClassPlugs.get(cls.getId());
  
   if( plug != null )
    return plug;
  }
  
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
  if( customClassMap == null )
   return null;
  
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

 @Override
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

 @Override
 public void setModelFactory(ModelFactory mf)
 {
  getMasterModel().setModelFactory(mf);
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
