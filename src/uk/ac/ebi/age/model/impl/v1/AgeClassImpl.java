package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.util.Collector;

import com.pri.util.collection.CollectionsUnion;

class AgeClassImpl extends AgeAbstractClassImpl implements AgeClassWritable, Serializable 
{
 private static final long serialVersionUID = 1L;

 private String name;
 private String id;
 private String idPrefix;

 private boolean isAbstract;
 
 private Collection<String> aliases;

 private Collection<AgeClass> subClasses = new LinkedList<AgeClass>();
 private Collection<AgeClass> superClasses = new LinkedList<AgeClass>();

 @Deprecated private Collection<AgeRestriction> restrictions = new LinkedList<AgeRestriction>();
 @Deprecated private Collection<AgeRestriction> attributeRestrictions = new LinkedList<AgeRestriction>();

 @Deprecated private Collection<AgeRestriction> unionRestrictions;

 private Collection<RelationRule> relationRules;
 private Collection<AttributeAttachmentRule> atatRules;
 
 public AgeClassImpl(String name, String id, String pfx, SemanticModel sm)
 {
  super( sm );
  this.name=name;
  this.id=id;
 
  if( pfx == null )
   idPrefix = name.substring(0,1);
  else
   idPrefix=pfx;
  
  Collection< Collection<AgeRestriction> > un = new ArrayList<Collection<AgeRestriction>>(2);
  un.add(restrictions);
  un.add(attributeRestrictions);
  
  unionRestrictions = new CollectionsUnion<AgeRestriction>( un );
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }
 
 public void addSubClass(AgeClass sbCls)
 {
  subClasses.add(sbCls);
 }

 public void addSuperClass(AgeClass sbCls)
 {
  superClasses.add(sbCls);
 }
 
 public Collection<AgeRestriction> getRestrictions()
 {
  return unionRestrictions;
 }
 

 public Collection<AgeRestriction> getAllRestrictions()
 {
  Collection<Collection<AgeRestriction>> allRest = new ArrayList<Collection<AgeRestriction>>(10);
  
  Collector.collectFromHierarchy(this,allRest, new Collector<Collection<AgeRestriction>>(){

   public Collection<AgeRestriction> get(AgeAbstractClass cls)
   {
    Collection<AgeRestriction> restr = ((AgeClass)cls).getRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }} );
  
  return new CollectionsUnion<AgeRestriction>(allRest);
 }

 
 public Collection<AgeClass> getSuperClasses()
 {
  return superClasses;
 }
 
 public Collection<AgeClass> getSubClasses()
 {
  return subClasses;
 }

 public String getName()
 {
  return name;
 }

 public boolean isCustom()
 {
  return false;
 }

 public Collection<AgeRestriction> getObjectRestrictions()
 {
  return restrictions;
 }

 public void addObjectRestriction(AgeRestriction rest)
 {
  restrictions.add(rest);
 }

 public Collection<AgeRestriction> getAllObjectRestrictions()
 {
  Collection<Collection<AgeRestriction>> allRest = new ArrayList<Collection<AgeRestriction>>(10);
  
  Collector.collectFromHierarchy(this, allRest, new Collector<Collection<AgeRestriction>>()
  {
   public Collection<AgeRestriction> get(AgeAbstractClass cls)
   {
    Collection<AgeRestriction> restr = ((AgeClass)cls).getObjectRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }
  });
  
  return new CollectionsUnion<AgeRestriction>(allRest);
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
    Collection<AgeRestriction> restr = ((AgeClassImpl)cls).getAttributeRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }
  });
  
  return new CollectionsUnion<AgeRestriction>(allRest);
 }

 public String getIdPrefix()
 {
  return idPrefix;
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
 public Collection<RelationRule> getRelationRules()
 {
  return relationRules;
 }

 @Override
 public Collection<AttributeAttachmentRule> getAttributeAttachmentRules()
 {
  return atatRules;
 }

 @Override
 public Collection<RelationRule> getAllRelationRules()
 {
  Collection<Collection<RelationRule>> allRest = new ArrayList<Collection<RelationRule>>(10);
  
  Collector.collectFromHierarchy(this,allRest, new Collector<Collection<RelationRule>>(){

   public Collection<RelationRule> get(AgeAbstractClass cls)
   {
    Collection<RelationRule> restr = ((AgeClass)cls).getRelationRules();
    return restr==null||restr.size()==0?null:restr;
   }} );
  
  return new CollectionsUnion<RelationRule>(allRest);
 }

 @Override
 public Collection<AttributeAttachmentRule> getAllAttributeAttachmentRules()
 {
  Collection<Collection<AttributeAttachmentRule>> allRest = new ArrayList<Collection<AttributeAttachmentRule>>(10);
  
  Collector.collectFromHierarchy(this,allRest, new Collector<Collection<AttributeAttachmentRule>>(){

   public Collection<AttributeAttachmentRule> get(AgeAbstractClass cls)
   {
    Collection<AttributeAttachmentRule> restr = ((AgeClass)cls).getAttributeAttachmentRules();
    return restr==null||restr.size()==0?null:restr;
   }} );
  
  return new CollectionsUnion<AttributeAttachmentRule>(allRest);
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

 @Override
 public void addRelationRule(RelationRule mrr)
 {
  if( relationRules == null )
   relationRules = new ArrayList<RelationRule>();
  
  relationRules.add(mrr);
 }

 @Override
 public void addAttributeAttachmentRule(AttributeAttachmentRule atatRule)
 {
  if( atatRules == null )
   atatRules = new ArrayList<AttributeAttachmentRule>();
  
  atatRules.add(atatRule);
 }


}
