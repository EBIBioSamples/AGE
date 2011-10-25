package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.AgeAnnotation;
import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAnnotationClassWritable;
import uk.ac.ebi.age.model.writable.AgeAnnotationWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AttributeAttachmentRuleWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;
import uk.ac.ebi.age.service.id.IdGenerator;

public class SemanticModelImpl implements SemanticModel, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private Map<String,AgeClass> classMap = new TreeMap<String,AgeClass>();
 private Map<String,AgeAttributeClass> attributeMap = new TreeMap<String, AgeAttributeClass>();
 private Map<String,AgeRelationClass> relationMap = new TreeMap<String, AgeRelationClass>();
 private Map<String,AgeAnnotationClass> annotationMap = new TreeMap<String, AgeAnnotationClass>();

 private Map<String,AgeClass> classIdMap = new TreeMap<String,AgeClass>();
 private Map<String,AgeAttributeClass> attributeIdMap = new TreeMap<String, AgeAttributeClass>();
 private Map<String,AgeRelationClass> relationIdMap = new TreeMap<String, AgeRelationClass>();
 private Map<String,AgeAnnotationClass> annotationIdMap = new TreeMap<String, AgeAnnotationClass>();

 private Collection<AgeAnnotation> annotation = new ArrayList<AgeAnnotation>();
 
 private transient ModelFactory modelFactory;

 private AgeAttributeClassWritable attrClassRoot;
 private AgeClassWritable classRoot;
 private AgeRelationClassWritable relationRoot;
 private AgeAnnotationClassWritable annotationRoot;

 private int idGen;
 
 public SemanticModelImpl(ModelFactory modelFactory)
 {
  this.modelFactory=modelFactory;
  
  classRoot = modelFactory.createAgeClass(ROOT_CLASS_NAME, ROOT_CLASS_ID, "CL", this);
  attrClassRoot = modelFactory.createAgeAttributeClass(ROOT_ATTRIBUTE_CLASS_NAME, ROOT_ATTRIBUTE_CLASS_ID, null, this);
  relationRoot = modelFactory.createAgeRelationClass(ROOT_RELATION_CLASS_NAME, ROOT_RELATION_CLASS_ID, this);
  annotationRoot = modelFactory.createAgeAnnotationClass(ROOT_ANNOTATION_CLASS_NAME, ROOT_ANNOTATION_CLASS_ID, this);
 
  classRoot.setAbstract(true);
  attrClassRoot.setAbstract(true);
  relationRoot.setAbstract(true);
  annotationRoot.setAbstract(true);
  
  classMap.put(ROOT_CLASS_NAME, classRoot);
  classIdMap.put(ROOT_CLASS_ID, classRoot);

  attributeMap.put(ROOT_ATTRIBUTE_CLASS_NAME, attrClassRoot);
  attributeMap.put(ROOT_ATTRIBUTE_CLASS_ID, attrClassRoot);

  relationMap.put(ROOT_RELATION_CLASS_NAME, relationRoot);
  relationIdMap.put(ROOT_RELATION_CLASS_ID, relationRoot);

  annotationMap.put(ROOT_ANNOTATION_CLASS_NAME, annotationRoot);
  annotationIdMap.put(ROOT_ANNOTATION_CLASS_ID, annotationRoot);
 }
 
// public SemanticModelImpl( String sourceURI, ModelFactory modelFactory ) throws ModelException
// {
//  this.modelFactory=modelFactory;
//  
//  parseOWL(sourceURI);
// }
 
 

 public ModelFactory getModelFactory()
 {
  return modelFactory;
 }

 public void setModelFactory( ModelFactory mf )
 {
  modelFactory=mf;
 }

 
 public AgeClass getDefinedAgeClass(String name)
 {
  return classMap.get(name);
 }

 
 public AgeClassProperty getDefinedAgeClassProperty(String name)
 {
  AgeClassProperty prop = attributeMap.get(name);
  
  if( prop != null )
   return prop;
  
  return relationMap.get(name);
 }

 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, AgeAttributeClass parent)
 {
  return createAgeAttributeClass(name, null, id, type, parent);
 }

 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, Collection<String> aliases, String id, DataType type, AgeAttributeClass parent)
 {
  if( id == null )
   id = "AgeAttributeClass"+IdGenerator.getInstance().getStringId("classId");

  AgeAttributeClassWritable cls = modelFactory.createAgeAttributeClass(name, id, type, this);
  
  ((AgeAttributeClassWritable)parent).addSubClass(cls);
  cls.addSuperClass((AgeAttributeClassWritable)parent);
  
  attributeMap.put(name, cls);
  attributeIdMap.put(id, cls);
  
  if( aliases != null )
  {
   for( String al : aliases )
   {
    attributeMap.put(al, cls); 
    cls.addAlias(al);
   }
  }
  
  return cls;
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, AgeAnnotationClass parent)
 {
  return createAgeAnnotationClass(name, null, id, parent);
 }
 
 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, Collection<String> aliases, String id, AgeAnnotationClass parent)
 {
  if( id == null )
   id = "AgeAnnotationClass"+IdGenerator.getInstance().getStringId("classId");

  AgeAnnotationClassWritable cls = modelFactory.createAgeAnnotationClass(name, id, this);
  
  ((AgeAnnotationClassWritable)parent).addSubClass(cls);
  cls.addSuperClass((AgeAnnotationClassWritable)parent);
  
  annotationMap.put(name, cls);
  annotationIdMap.put(id, cls);
  
  if( aliases != null )
  {
   for( String al : aliases )
   {
    annotationMap.put(al, cls); 
    cls.addAlias(al);
   }
  }
  
  return cls;

 }
 
 @Override 
 public AgeClassWritable createAgeClass(String name, String id, String pfx, AgeClass parent )
 {
  return createAgeClass(name, null, id, pfx, parent);
 }
 
 @Override 
 public AgeClassWritable createAgeClass(String name, Collection<String> aliases, String id, String pfx, AgeClass parent )
 {
  if( id == null )
   id = "AgeClass"+IdGenerator.getInstance().getStringId("classId");
  
  AgeClassWritable cls = modelFactory.createAgeClass(name, id, pfx, this);
  
  ((AgeClassWritable)parent).addSubClass(cls);
  cls.addSuperClass((AgeClassWritable)parent);
  
  classMap.put(name, cls);
  classIdMap.put(id, cls);
  
  if( aliases != null )
  {
   for( String al : aliases )
   {
    classMap.put(al, cls); 
    cls.addAlias(al);
   }
  }
     
  return cls;
 }

 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, String id, AgeRelationClass parent)
 {
  return createAgeRelationClass(name, null, id, parent);
 }
 
 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, Collection<String> aliases, String id, AgeRelationClass parent)
 {
  if( id == null )
   id = "AgeRelationClass"+IdGenerator.getInstance().getStringId("classId");

  
  AgeRelationClassWritable cls = getModelFactory().createAgeRelationClass(name, id, this);
  
  if( parent != null )
  {
   ((AgeRelationClassWritable) parent).addSubClass(cls);
   cls.addSuperClass((AgeRelationClassWritable) parent);

   relationMap.put(name, cls);
   relationIdMap.put(id, cls);
  }
  
  if( aliases != null )
  {
   for( String al : aliases )
   {
    relationMap.put(al, cls); 
    cls.addAlias(al);
   }
  }
  
  return cls;
 }

 @Override
 public AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls)
 {
  return getModelFactory().createAgeAnnotation(cls, this);
 }


 
 @Override
 public AttributeAttachmentRuleWritable createAttributeAttachmentRule(RestrictionType type)
 {
  return modelFactory.createAgeAttributeAttachmentRule(type, this);
 }

 @Override
 public RelationRuleWritable createRelationRule(RestrictionType type)
 {
  return modelFactory.createAgeRelationRule(type, this);
 }

 @Override
 public QualifierRuleWritable createQualifierRule()
 {
  return modelFactory.createAgeQualifierRule(this);
 }
 

 @Override
 public AgeRelationClass getDefinedAgeRelationClass(String name)
 {
  return relationMap.get(name);
 }

// public AgeRelationClass getAttributeAttachmentClass()
// {
//  return attributeAttachmentRelation;
// }

 public AgeClass getAgeClass(String name)
 {
  return getDefinedAgeClass(name);
 }

 public AgeRelationClass getAgeRelationClass(String relClassName)
 {
  return getDefinedAgeRelationClass(relClassName);
 }


 public AgeAttributeClass getAgeAttributeClass(String attClsName)
 {
  return getDefinedAgeAttributeClass(attClsName);
 }



 @Override
 public AgeAttributeClass getDefinedAgeAttributeClass(String attClsName)
 {
  return attributeMap.get(attClsName);
 }

 protected void addAttributeClass(AgeAttributeClassWritable cls)
 {
  attributeMap.put(cls.getName(), cls);
  attributeIdMap.put(cls.getId(), cls);
  
  if( cls.getAliases() != null )
   for( String als : cls.getAliases() )
    attributeMap.put( als, cls );
 }

 protected void addClass(AgeClassWritable cls)
 {
  classMap.put(cls.getName(), cls);
  classIdMap.put(cls.getId(), cls);

  if( cls.getAliases() != null )
   for( String als : cls.getAliases() )
    classMap.put( als, cls );
 }

 protected void addRelationClass(AgeRelationClassWritable cls)
 {
  relationMap.put(cls.getName(), cls);
  relationIdMap.put(cls.getId(), cls);

  if( cls.getAliases() != null )
   for( String als : cls.getAliases() )
    relationMap.put( als, cls );
 }


 @Override
 public AgeClass getDefinedAgeClassById(String classId)
 {
  return classIdMap.get(classId);
 }


 @Override
 public AgeRelationClass getDefinedAgeRelationClassById(String classId)
 {
  return relationIdMap.get(classId);
 }


 @Override
 public AgeAttributeClass getDefinedAgeAttributeClassById(String classId)
 {
  return attributeIdMap.get(classId);
 }

 @Override
 public Collection<AgeClass> getAgeClasses()
 {
  return classMap.values();
 }

 @Override
 public AgeClass getRootAgeClass()
 {
  return classRoot;
 }

 @Override
 public AgeAttributeClass getRootAgeAttributeClass()
 {
  return attrClassRoot;
 }


 @Override
 public AgeRelationClass getRootAgeRelationClass()
 {
  return relationRoot;
 }

 @Override
 public AgeAnnotationClass getRootAgeAnnotationClass()
 {
  return annotationRoot;
 }

 @Override
 public Collection<AgeAnnotation> getAnnotations()
 {
  return annotation;
 }

 @Override
 public void addAnnotation(AgeAnnotation ant)
 {
  annotation.add(ant);
 }

 @Override
 public int getIdGen()
 {
  return idGen;
 }

 @Override
 public void setIdGen(int id)
 {
  idGen = id;
 }


}
