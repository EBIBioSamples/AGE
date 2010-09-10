package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.util.Collector;

import com.pri.util.collection.CollectionsUnion;

class AgeRelationClassImpl extends AgeAbstractClassImpl implements AgeRelationClassWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private String name;
 private String id;

 private boolean isAbstract;

 private Collection<AgeClass> domain = new LinkedList<AgeClass>();
 private Collection<AgeClass> range = new LinkedList<AgeClass>();
 private Collection<AgeRelationClass> subclasses = new LinkedList<AgeRelationClass>();
 private Collection<AgeRelationClass> superclasses = new LinkedList<AgeRelationClass>();

 private Collection<AgeRestriction> attributeRestrictions = new LinkedList<AgeRestriction>();
 
 private Collection<String> aliases;
 
 private boolean implicit=false;
 private AgeRelationClass inverse;

 private boolean functional;
 private boolean inverseFunctional;
 private boolean symmetric;
 private boolean transitive;

 public AgeRelationClassImpl(String name, String id, SemanticModel sm)
 {
  super(sm);
  this.name=name;
  this.id=id;
 }

 public String getName()
 {
  return name;
 }
 
 public String getId()
 {
  return id;
 }
 

 public void addDomainClass(AgeClass dmCls)
 {
  for( AgeClass exstDmCla : domain )
   if( exstDmCla.equals(dmCls) )
    return;

  domain.add(dmCls);
 }

 public void addRangeClass(AgeClass rgCls)
 {
  for( AgeClass exstRgCla : range )
   if( exstRgCla.equals(rgCls) )    //TODO should be class or subclass here?
    return;
  
  range.add(rgCls);
 }

 public void addSubClass(AgeRelationClass sbcls)
 {
  subclasses.add(sbcls);
 }

 public void addSuperClass(AgeRelationClass sbcls)
 {
  superclasses.add(sbcls);
 }


 public boolean isWithinRange(AgeClass key)
 {
  if( range.size() == 0 )
   return true;
  
  for( AgeClass rgCls : range )
   if( key.equals(rgCls) )
    return true;
  
  return false;
 }

 public Collection<AgeClass> getRange()
 {
  return range;
 }
 
 public Collection<AgeClass> getDomain()
 {
  return domain;
 }

 public Collection<AgeRelationClass> getSubClasses()
 {
  return subclasses;
 }

 public Collection<AgeRelationClass> getSuperClasses()
 {
  return superclasses;
 }

 public boolean isCustom()
 {
  return false;
 }


 @Override
 public AgeRelationClass getInverseClass()
 {
  return inverse;
 }


 @Override
 public boolean isWithinDomain(AgeClass key)
 {
  if( domain.size() == 0 )
   return true;
  
  for( AgeClass rgCls : domain )
   if( key.equals(rgCls) )   //TODO should be class or subclass here?
    return true;
  
  return false;
 }

 @Override
 public boolean isImplicit()
 {
  return implicit;
 }

 @Override
 public void setImplicit(boolean b)
 {
  implicit = b;
 }

 @Override
 public void setInverseClass(AgeRelationClass ageEl)
 {
  inverse=ageEl;
 }

 @Override
 public void resetModel()
 {
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
    Collection<AgeRestriction> restr = ((AgeRelationClassImpl)cls).getAttributeRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }
  });
  
  return new CollectionsUnion<AgeRestriction>(allRest);
 }

 public boolean isAbstract()
 {
  return isAbstract;
 }

 public void setAbstract(boolean isAbstract)
 {
  this.isAbstract = isAbstract;
 }

 @Override
 public boolean isFunctional()
 {
  return functional;
 }

 @Override
 public boolean isInverseFunctional()
 {
  return inverseFunctional;
 }

 @Override
 public boolean isSymmetric()
 {
  return symmetric;
 }

 @Override
 public boolean isTransitive()
 {
  return transitive;
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
 
}
