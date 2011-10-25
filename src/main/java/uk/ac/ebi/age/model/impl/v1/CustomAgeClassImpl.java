package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.service.id.IdGenerator;

class CustomAgeClassImpl extends AgeAbstractClassImpl implements AgeClassWritable, Serializable 
{
 private static final long serialVersionUID = 1L;
 

 private String name;
 private String id;
 
 private String idPrefix;

 private Collection<AgeClass> superClasses;

 public CustomAgeClassImpl(String name, String pfx, SemanticModel sm)
 {
  super( sm );
  this.name=name;
  
  if( pfx == null )
   idPrefix = name.substring(0,1);
  else
   idPrefix=pfx;
  
  id = "AgeClass"+IdGenerator.getInstance().getStringId("classId");
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }
 
  public Collection<AgeRestriction> getRestrictions()
 {
  return null;
 }
 

 public Collection<AgeRestriction> getAllRestrictions()
 {
  return null;
 }

 
 public Collection<AgeClass> getSuperClasses()
 {
  return superClasses;
 }
 
 public Collection<AgeClass> getSubClasses()
 {
  return null;
 }

 public String getName()
 {
  return name;
 }

 public boolean isCustom()
 {
  return true;
 }


 public String getIdPrefix()
 {
  return idPrefix;
 }

 @Override
 public boolean isAbstract()
 {
  return false;
 }

 @Override
 public Collection<RelationRule> getRelationRules()
 {
  return null;
 }

 @Override
 public Collection<RelationRule> getAllRelationRules()
 {
  return null;
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
 public void addSubClass(AgeClassWritable cls)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addSuperClass(AgeClassWritable cls)
 {
  if( superClasses == null )
   superClasses = new ArrayList<AgeClass>(4);
  
  superClasses.add(cls);
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
 public void addRelationRule(RelationRule mrr)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public Collection<String> getAliases()
 {
  return null;
 }

}
