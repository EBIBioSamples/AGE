package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;

class AgeAttributeClassImpl extends AgeAbstractClassImpl implements AgeAttributeClassWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private DataType dataType;
 private String name;
 private String id;

 private boolean isAbstract;

 private Collection<String> aliases;
 
 private AgeClass targetClass;

 private Collection<AgeAttributeClass> subClasses = new HashSet<AgeAttributeClass>();
 private Collection<AgeAttributeClass> superClasses = new HashSet<AgeAttributeClass>();
// private Collection<AgeRestriction> attributeRestrictions = new LinkedList<AgeRestriction>();

 protected AgeAttributeClassImpl()
 {
  super(null);
 }
 
 public AgeAttributeClassImpl(String name, String id, DataType type, SemanticModel sm)
 {
  super(sm);
  dataType=type;
  this.name=name;
  this.id=id;
 }

// public AgeAttribute createAttribute()
// {
//  getSemanticModel().
//  throw new dev.NotImplementedYetException();
// }

// public boolean validateValue(String val)
// {
//  // TODO Auto-generated method stub
//  throw new dev.NotImplementedYetException();
// }

 public DataType getDataType()
 {
  return dataType;
 }

 public void setDataType(DataType dataType)
 {
  this.dataType = dataType;
 }

 public String getName()
 {
  return name;
 }
 
 @Override
 public void addSuperClass( AgeAttributeClassWritable cl )
 {
  if( superClasses.add(cl) )
   cl.addSubClass(this);
 }

 @Override
 public void addSubClass( AgeAttributeClassWritable cl )
 {
  if( subClasses.add(cl) )
   cl.addSuperClass(this);
 }

 
 public Collection<AgeAttributeClass> getSuperClasses()
 {
  return superClasses;
 }
 
 
 public Collection<AgeAttributeClass> getSubClasses()
 {
  return subClasses;
 }

 public AgeClass getOwningClass()
 {
  return null;
 }

 public boolean isCustom()
 {
  return false;
 }

 public String getId()
 {
  return id;
 }

// public void addAttributeRestriction(AgeRestriction rest)
// {
//  attributeRestrictions.add(rest);
// }
//
// public Collection<AgeRestriction> getAttributeRestrictions()
// {
//  return attributeRestrictions;
// }
//
// public Collection<AgeRestriction> getAttributeAllRestrictions()
// {
//  Collection<Collection<AgeRestriction>> allRest = new ArrayList<Collection<AgeRestriction>>(10);
//  
//  Collector.collectFromHierarchy(this, allRest, new Collector<Collection<AgeRestriction>>()
//  {
//   public Collection<AgeRestriction> get(AgeAbstractClass cls)
//   {
//    Collection<AgeRestriction> restr = ((AgeAttributeClassImpl)cls).getAttributeRestrictions();
//    return restr==null||restr.size()==0?null:restr;
//   }
//  });
//  
//  return new CollectionsUnion<AgeRestriction>(allRest);
// }

 public boolean isAbstract()
 {
  return isAbstract;
 }

 public void setAbstract(boolean isAbstract)
 {
  this.isAbstract = isAbstract;
 }

 @Override
 public void addAlias(String ali)
 {
  if( aliases == null )
   aliases = new ArrayList<String>( 5 );
  
  aliases.add(ali);
 }

 public Collection<String> getAliases()
 {
  return aliases;
 }

 public AgeClass getTargetClass()
 {
  return targetClass;
 }

 public void setTargetClass(AgeClass targetClass)
 {
  this.targetClass = targetClass;
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

