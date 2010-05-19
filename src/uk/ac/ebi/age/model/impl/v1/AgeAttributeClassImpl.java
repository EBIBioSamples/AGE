package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.util.Collector;

import com.pri.util.collection.CollectionsUnion;

class AgeAttributeClassImpl extends AgeAbstractClassImpl implements AgeAttributeClassWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private DataType dataType;
 private String name;
 private String id;


 private Collection<AgeAttributeClass> subClasses = new LinkedList<AgeAttributeClass>();
 private Collection<AgeAttributeClass> superClasses = new LinkedList<AgeAttributeClass>();
 private Collection<AgeRestriction> attributeRestrictions = new LinkedList<AgeRestriction>();

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
 
 public void addSuperClass( AgeAttributeClass cl )
 {
  superClasses.add(cl);
 }

 public void addSubClass( AgeAttributeClass cl )
 {
  subClasses.add(cl);
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

 public void addAttributeRestriction(AgeRestriction rest)
 {
  attributeRestrictions.add(rest);
 }

 public Collection<AgeRestriction> getAttributeRestrictions()
 {
  return attributeRestrictions;
 }

 public Collection<AgeRestriction> getAttributeAllRestrictions()
 {
  Collection<Collection<AgeRestriction>> allRest = new ArrayList<Collection<AgeRestriction>>(10);
  
  Collector.collectFromHierarchy(this, allRest, new Collector<Collection<AgeRestriction>>()
  {
   public Collection<AgeRestriction> get(AgeAbstractClass cls)
   {
    Collection<AgeRestriction> restr = cls.getAttributeRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }
  });
  
  return new CollectionsUnion<AgeRestriction>(allRest);
 }

}

