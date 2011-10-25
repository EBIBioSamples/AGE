package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeClass;
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

 private Collection<AgeClass> subClasses = new HashSet<AgeClass>();
 private Collection<AgeClass> superClasses = new HashSet<AgeClass>();

 private Collection<RelationRule> relationRules;
 
 public AgeClassImpl(String name, String id, String pfx, SemanticModel sm)
 {
  super( sm );
  this.name=name;
  this.id=id;
 
  if( pfx == null )
   idPrefix = name.substring(0,1);
  else
   idPrefix=pfx;
  
//  Collection< Collection<AgeRestriction> > un = new ArrayList<Collection<AgeRestriction>>(2);
//  un.add(restrictions);
//  un.add(attributeRestrictions);
//  
//  unionRestrictions = new CollectionsUnion<AgeRestriction>( un );
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }
 
 @Override
 public void addSuperClass( AgeClassWritable spCls )
 {
  if( superClasses.add(spCls) )
   spCls.addSubClass(this);
 }

 @Override
 public void addSubClass( AgeClassWritable sbCls )
 {
  if( subClasses.add(sbCls) )
   sbCls.addSuperClass(this);
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


}
