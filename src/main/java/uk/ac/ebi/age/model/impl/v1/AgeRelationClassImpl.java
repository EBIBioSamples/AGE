package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;

class AgeRelationClassImpl extends AgeAbstractClassImpl implements AgeRelationClassWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private String name;
 private String id;

 private boolean isAbstract;

 private Collection<AgeClass> domain = new HashSet<AgeClass>();
 private Collection<AgeClass> range = new HashSet<AgeClass>();
 private Collection<AgeRelationClass> subclasses = new HashSet<AgeRelationClass>();
 private Collection<AgeRelationClass> superclasses = new HashSet<AgeRelationClass>();

// private Collection<AgeRestriction> attributeRestrictions = new LinkedList<AgeRestriction>();
 
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
//  for( AgeClass exstDmCla : domain )
//   if( exstDmCla.equals(dmCls) )
//    return;

  domain.add(dmCls);
 }

 public void addRangeClass(AgeClass rgCls)
 {
//  for( AgeClass exstRgCla : range )
//   if( exstRgCla.equals(rgCls) )    //TODO should be class or subclass here?
//    return;
  
  range.add(rgCls);
 }

 @Override
 public void addSuperClass( AgeRelationClassWritable spCls )
 {
  if( superclasses.add(spCls) )
   spCls.addSubClass(this);
 }

 @Override
 public void addSubClass( AgeRelationClassWritable sbCls )
 {
  if( subclasses.add(sbCls) )
   sbCls.addSuperClass(this);
 }

 @Override
 public boolean isWithinRange(AgeClass key)
 {
  if( range.size() == 0 )
   return true;
  
  for( AgeClass rgCls : range )
   if( key.isClassOrSubclass(rgCls) )
    return true;
  
  return false;
 }
 
 @Override
 public boolean isWithinDomain(AgeClass key)
 {
  if( domain.size() == 0 )
   return true;
  
  for( AgeClass rgCls : domain )
   if( key.isClassOrSubclass(rgCls) )
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
 public AgeRelationClass getInverseRelationClass()
 {
  return inverse;
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
 public void setInverseRelationClass(AgeRelationClass ageEl)
 {
  inverse=ageEl;
 }

 @Override
 public void resetModel()
 {
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
//    Collection<AgeRestriction> restr = ((AgeRelationClassImpl)cls).getAttributeRestrictions();
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

 public void setFunctional(boolean functional)
 {
  this.functional = functional;
 }

 public void setInverseFunctional(boolean inverseFunctional)
 {
  this.inverseFunctional = inverseFunctional;
 }

 public void setSymmetric(boolean symmetric)
 {
  this.symmetric = symmetric;
 }

 public void setTransitive(boolean transitive)
 {
  this.transitive = transitive;
 }
 
}
