package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAnnotation;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeContextSemanticElement;
import uk.ac.ebi.age.model.AgeSemanticElement;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.service.id.IdGenerator;

import com.pri.util.Extractor;
import com.pri.util.collection.ExtractorCollection;

abstract class AbstractCustomAgeAttributeClassImpl implements AgeContextSemanticElement, AgeAttributeClassWritable, Serializable, AgeSemanticElement
{
 private static final long serialVersionUID = 3L;
 
 private static Extractor<AgeAttributeClassPlug, AgeAttributeClass> clsExtr = new Extractor<AgeAttributeClassPlug, AgeAttributeClass>()
   {

    @Override
    public AgeAttributeClass extract(AgeAttributeClassPlug obj)
    {
     return obj.getAgeAttributeClass();
    }
   };

 private ContextSemanticModel model;
 
 private String name;
 private String id;

 private AgeClassPlug owner;
 
 private Collection<AgeAttributeClassPlug> superClassPlugs;
 
 private Collection<AgeAnnotation> annotations;

 
 public AbstractCustomAgeAttributeClassImpl(String name2, ContextSemanticModel sm, AgeClass owner2)
 {
  model = sm;
  this.name=name2;
  owner = sm.getAgeClassPlug(owner2);
  
  id = "AgeAttributeClass"+IdGenerator.getInstance().getStringId("classId");
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }
 
 public String getName()
 {
  return name;
 }
 
 
 public Collection<AgeAttributeClass> getSuperClasses()
 {
  if( superClassPlugs == null )
   return null;
  
  return new ExtractorCollection<AgeAttributeClassPlug, AgeAttributeClass>(superClassPlugs, clsExtr );
 }
 
 
 public Collection<AgeAttributeClass> getSubClasses()
 {
  return null;
 }

 public AgeClass getOwningClass()
 {
  return owner.getAgeClass();
 }

 public boolean isCustom()
 {
  return true;
 }


 @Override
 public boolean isAbstract()
 {
  return false;
 }

 @Override
 public void addSubClass(AgeAttributeClassWritable sbcls)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addSuperClass(AgeAttributeClassWritable supcls)
 {
  if( superClassPlugs == null )
   superClassPlugs = new ArrayList<AgeAttributeClassPlug>(4);
  
  superClassPlugs.add( getSemanticModel().getAgeAttributeClassPlug(supcls) );
 }


 @Override
 public void setAbstract(boolean b)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addAlias(String ali)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setTargetClass(AgeClass cls)
 {
  throw new UnsupportedOperationException();
 }

 public AgeClass getTargetClass()
 {
  return null;
 }

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return model;
 }
 
 public Collection<String> getAliases()
 {
  return null;
 }

 @Override
 public boolean isClassOrSubclass(AgeAbstractClass cl)
 {
  if( cl.equals(this) && cl.isCustom() == isCustom() )
   return true;
  
  if( superClassPlugs == null )
   return false;
  
  for( AgeAttributeClassPlug supclsp : superClassPlugs )
   if( supclsp.getAgeAttributeClass().isClassOrSubclass(cl) )
    return true;
  
  return false;
 }

 @Override
 public Collection<AgeAnnotation> getAnnotations()
 {
  return annotations;
 }

 @Override
 public Collection<AttributeAttachmentRule> getAttributeAttachmentRules()
 {
  return null;
 }

 @Override
 public Collection<AttributeAttachmentRule> getAllAttributeAttachmentRules()
 {
  return null;
 }

 @Override
 public void addAnnotation(AgeAnnotation annt)
 {
  if( annotations == null )
   annotations = new ArrayList<AgeAnnotation>(8);
  
  annotations.add(annt);
 }

 @Override
 public void addAttributeAttachmentRule(AttributeAttachmentRule atatRule)
 {
  throw new UnsupportedOperationException();
 }
 
 public void unplug()
 {
 }
 
 public boolean plug()
 {
  return true;
 }
 
 public AgeAttributeClass getAgeAttributeClass()
 {
  return this;
 }

 @Override
 public boolean isPlugged()
 {
  return true;
 }
}

