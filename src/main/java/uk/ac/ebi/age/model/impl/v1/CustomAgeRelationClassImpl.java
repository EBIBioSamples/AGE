package uk.ac.ebi.age.model.impl.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAnnotation;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.service.id.IdGenerator;

class CustomAgeRelationClassImpl extends AgeContextSemanticElementImpl implements AgeRelationClassWritable
{
 private static final long serialVersionUID = 1L;
 
 private String name;
 private AgeClassPlug ownerClass;
 private AgeClassPlug rangeClass;
 private CustomAgeRelationClassImpl inverse;
 private boolean implicit = false;
 private String id;
 
 private Collection<AgeRelationClass> superClasses;

 public CustomAgeRelationClassImpl(String name, ContextSemanticModel sm, AgeClass range, AgeClass owner)
 {
  this( name, sm, range, owner, null);
 }

 private CustomAgeRelationClassImpl(String name, ContextSemanticModel sm, AgeClass range, AgeClass owner, CustomAgeRelationClassImpl inv)
 {
  super(sm);
  
  this.name=name;
  
  rangeClass = sm.getAgeClassPlug(range);

  ownerClass = sm.getAgeClassPlug(owner);
  
  if( inv == null )
  {
   inverse = new CustomAgeRelationClassImpl( "!"+name, sm, range, owner, this );
   inverse.setImplicit(true);
  }
  else
   inverse = inv;
  
  id="AgeRelationClass"+IdGenerator.getInstance().getStringId("classId");
 }

 protected void setInverseClass( CustomAgeRelationClassImpl inv )
 {
  inverse = inv;
 }
 
 @Override
 public AgeRelationClass getInverseRelationClass()
 {
  return inverse;
 }
 
 @Override
 public String getName()
 {
  return name;
 }

 @Override
 public Collection<AgeClass> getDomain()
 {
  return Collections.singletonList(ownerClass.getAgeClass());
 }


 @Override
 public Collection<AgeClass> getRange()
 {
  return Collections.singletonList(rangeClass.getAgeClass());
 }
 
 public void setId(String id)
 {
  this.id = id;
 }


 public String getId()
 {
  return id;
 }

 @Override
 public boolean isCustom()
 {
  return true;
 }

 @Override
 public boolean isWithinDomain(AgeClass key)
 {
  return key.equals(ownerClass.getAgeClass());
 }

 @Override
 public boolean isWithinRange(AgeClass key)
 {
  return key.isClassOrSubclass(rangeClass.getAgeClass());
 }

 @Override
 public Collection<AgeRelationClass> getSubClasses()
 {
  return null;
 }

 @Override
 public Collection<AgeRelationClass> getSuperClasses()
 {
  return superClasses;
 }

 @Override
 public boolean isClassOrSubclass(AgeAbstractClass cl)
 {
  return cl.equals(this);
 }

 @Override
 public boolean isImplicit()
 {
  return implicit;
 }
 
 public void setImplicit( boolean b )
 {
  implicit=b;
 }

 public void resetModel()
 {
  ownerClass.unplug();
  rangeClass.unplug();
  inverse.resetModel();
 }


 @Override
 public boolean isAbstract()
 {
  return false;
 }

 @Override
 public Collection<AgeAnnotation> getAnnotations()
 {
  return Collections.emptyList();
 }

 @Override
 public boolean isFunctional()
 {
  return false;
 }

 @Override
 public boolean isInverseFunctional()
 {
  return false;
 }

 @Override
 public boolean isSymmetric()
 {
  return false;
 }

 @Override
 public boolean isTransitive()
 {
  return false;
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
 public void addAnnotation(AgeAnnotation ant)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addAttributeAttachmentRule(AttributeAttachmentRule atatRule)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addSubClass(AgeRelationClassWritable makeRelationsBranch)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addSuperClass(AgeRelationClassWritable ageRelCls)
 {
  if(superClasses == null)
   superClasses = new ArrayList<AgeRelationClass>(4);
  
  superClasses.add(ageRelCls);
 }

 @Override
 public void addDomainClass(AgeClass dmCls)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addRangeClass(AgeClass dmCls)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setInverseRelationClass(AgeRelationClass ageEl)
 {
  throw new UnsupportedOperationException();
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
 public void setFunctional(boolean functional)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setInverseFunctional(boolean inverseFunctional)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setSymmetric(boolean symmetric)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setTransitive(boolean transitive)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public Collection<String> getAliases()
 {
  return null;
 }

}
