package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeContextSemanticElement;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.service.id.IdGenerator;

class CustomAgeAttributeClassImpl extends AgeAbstractClassImpl implements AgeContextSemanticElement, AgeAttributeClassWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private DataType dataType;
 private String name;
 private String id;

 private AgeClass owner;
 
 private AgeClass targetClass;

 private Collection<AgeAttributeClassPlug> superClassPlugs;
 private transient Collection<AgeAttributeClass> superClasses;
 
 public CustomAgeAttributeClassImpl(String name2, DataType type, ContextSemanticModel sm, AgeClass owner2)
 {
  super(sm);
  dataType=type;
  this.name=name2;
  owner = owner2;
  
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
 
 public DataType getDataType()
 {
  return dataType;
 }

 public String getName()
 {
  return name;
 }
 
 
 public Collection<AgeAttributeClass> getSuperClasses()
 {
  buildSuperClassCache();
  
  return superClasses;
 }
 
 private void buildSuperClassCache()
 {
  if( superClasses != null || superClassPlugs == null )
   return;
   
  superClasses = new ArrayList<AgeAttributeClass>( superClassPlugs.size() );
  
  for( AgeAttributeClassPlug plg : superClassPlugs )
   superClasses.add(plg.getAgeAttributeClass());
 }
 
 public Collection<AgeAttributeClass> getSubClasses()
 {
  return null;
 }

 public AgeClass getOwningClass()
 {
  return owner;
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
  
  if( superClasses != null )
   superClasses.add(supcls);
 }

 @Override
 public void setDataType(DataType typ)
 {
  dataType=typ;
 }

 @Override
 public void setAbstract(boolean b)
 {
 }

 @Override
 public void addAlias(String ali)
 {
 }

 @Override
 public void setTargetClass(AgeClass cls)
 {
  targetClass=cls;
 }

 public AgeClass getTargetClass()
 {
  return targetClass;
 }

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return (ContextSemanticModel)super.getSemanticModel();
 }
 
 public Collection<String> getAliases()
 {
  return null;
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

