package uk.ac.ebi.age.model.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;

class AgeRelationClassImpl extends AgeAbstractClassImpl implements AgeRelationClassWritable, Serializable
{
 private String name;
 private Collection<AgeClass> domain = new LinkedList<AgeClass>();
 private Collection<AgeClass> range = new LinkedList<AgeClass>();
 private Collection<AgeRelationClass> subclasses = new LinkedList<AgeRelationClass>();
 private Collection<AgeRelationClass> superclasses = new LinkedList<AgeRelationClass>();

 private boolean implicit=false;
 private AgeRelationClass inverse;

 public String getName()
 {
  return name;
 }
 
 public AgeRelationClassImpl(String name, SemanticModel sm)
 {
  super(sm);
  this.name=name;
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

}
