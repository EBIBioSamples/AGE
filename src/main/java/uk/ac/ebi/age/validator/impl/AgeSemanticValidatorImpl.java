package uk.ac.ebi.age.validator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.validator.AgeSemanticValidator;

public class AgeSemanticValidatorImpl implements AgeSemanticValidator
{

 @Override
 public boolean validate(DataModule subm, LogNode log)
 {

  return validate(subm, Resolver.getIntsance(), log);
 }

 @Override
 public boolean validate(DataModule subm, SemanticModel mod, LogNode log)
 {
  return validate(subm, new ExtModelResolver(subm.getContextSemanticModel().getMasterModel(), mod), log);
 }

 private boolean validate(DataModule subm, Resolver rslv, LogNode log)
 {
  boolean res = true;
  
  for( AgeObject obj : subm.getObjects() )
  {
   LogNode ln = log.branch("Validating object ID="+obj.getId()+" (Scope: "+obj.getIdScope()+") Class: '"+obj.getAgeElClass()+"' Order: "+obj.getOrder());
   
   if( !validateObject( obj, rslv, ln ) )
     res = false;
   else
    ln.success();
  }
  
  return res;
 }


 private boolean validateObject(AgeObject obj, Resolver mod, LogNode log)
 {
  boolean valid = true;

  boolean res;

  LogNode ln = log.branch("Validating object's attributes");
  res = validateAttributed(obj, 0, mod, ln);
  valid = res && valid;

  if(res)
   ln.success();

  ln = log.branch("Validating object's relations");
  res = validateRelations(obj, null, null, mod, ln);

  valid = res && valid;

  if(res)
   ln.success();

  return valid;
 }

 public boolean validateRelations(AgeObject obj, Set<? extends AgeRelation> auxRels, Set<? extends AgeRelation> remRels, LogNode log)
 {
  return validateRelations(obj, auxRels, remRels, Resolver.getIntsance(), log);
 }
 
 private boolean validateRelations(AgeObject obj, Set<? extends AgeRelation> auxRels, Set<? extends AgeRelation> remRels, Resolver mod, LogNode log)
 {
  AgeClass cls = mod.getAgeClass(obj.getAgeElClass());
  
  if(cls == null)
  {
   log.log(Level.ERROR, "Class '" + obj.getAgeElClass() + "' doesn't exist in the new model");
   return false;
  }

  
  Collection<RelationRule> rlRules = cls.getAllRelationRules();

  if( rlRules == null )
   rlRules = Collections.<RelationRule> emptyList();
  
  Collection< ? extends AgeRelationClass> rlClasses = obj.getRelationClasses();

  Collection<AgeRelation> byClassRels = null;

  if( auxRels != null )
  {
   Set<AgeRelationClass> clsSet = new HashSet<AgeRelationClass>();
   
   clsSet.addAll(rlClasses);
   
   for( AgeRelation r : auxRels )
    clsSet.add(r.getAgeElClass());
    
   byClassRels = new ArrayList<AgeRelation>(10);
   
   rlClasses=clsSet;
  }
  
  boolean objectOk = true;


  for(AgeRelationClass rlCls : rlClasses)
  {
   if(rlCls.isCustom() || rlCls.isImplicit() )
    continue;

   AgeRelationClass rslvRlCls = mod.getAgeRelationClass( rlCls );
   
   if( rslvRlCls == null )
   {
    log.log(Level.ERROR, "Relation class '"+rlCls+"' doesn't exist in the new model");
    objectOk = false;
    
    continue;
   }
   
   Collection< ? extends AgeRelation> rels = obj.getRelationsByClass(rlCls, true);

   if( auxRels != null )
   {
    byClassRels.clear();
    
    for( AgeRelation r : auxRels )
    {
     if( r.getAgeElClass().isClassOrSubclass(rlCls) || remRels == null || ! remRels.contains( r ) )
      byClassRels.add(r);
    }
 
    for( AgeRelation r : rels )
    {
     if( remRels == null || ! remRels.contains( r ) )
      byClassRels.add(r);
    }

    rels = byClassRels;
   }
   
   LogNode ln = log.branch("Validating relations of class '"+rlCls+"' Relations number: "+rels.size());

   
   boolean res = checkTargetsUnique( rels, log ); 

   if( ! res )
    ln.log(Level.ERROR, "Relation targets are not unique");
   
   objectOk = res && objectOk; 

   res = isRelationAllowed(rslvRlCls, rels, rlRules, mod, ln);
   
   if( res )
    ln.success();
   else
    ln.log(Level.ERROR, "There is no rule that allows this relation");
   
   objectOk = res && objectOk; 
  }

  if(cls.getRelationRules() != null && cls.getRelationRules().size() > 0 )
  {
   LogNode ln = log.branch("Validating relation rules");

   boolean rrulOk = true;
   for(RelationRule rlRl : cls.getRelationRules())
   {
    LogNode rlln = ln.branch("Validating rule: "+rlRl.getRuleId()+" of class: '"+cls.getName()
      +"' Relation class: '"+rlRl.getRelationClass().getName()+"' Target class: '"+rlRl.getTargetClass().getName()+"'");

    boolean res = isRelationRuleSatisfied(rlRl, obj, auxRels, remRels, mod, rlln);
    
    if( res )
     rlln.success();
    
    rrulOk = res && rrulOk;
   }
   
   if( rrulOk )
    ln.success();
   
   objectOk = objectOk && rrulOk;
  }

  boolean hasQualifiers=false;

  for(AgeRelation rl : obj.getRelations())
  {
   if( rl.getAttributes() != null && rl.getAttributes().size() > 0 )
   {
    hasQualifiers = true;
    break;
   }
  }
  
  if(hasQualifiers)
  {
   LogNode ln = log.branch("Validating relation qualifiers");
   boolean qres = true;

   for(AgeRelation rl : obj.getRelations())
   {
    if( rl.isInferred() || rl.getAttributes() == null || rl.getAttributes().size() == 0 )
     continue;
    
    LogNode atln = ln.branch("Validating relation's qualifiers. Relation class: '" + rl.getAgeElClass()
      + "' Target object: " + rl.getTargetObject().getId() + " Order: " + rl.getOrder());

    boolean res = validateAttributed(rl, 1, mod, atln);

    if(res)
     atln.success();

    qres = res && qres;
   }

   if( qres )
    ln.success();

   objectOk = objectOk && qres;
  }
  

  
  return objectOk;
 }
 
 
 private boolean validateAttributed( Attributed obj, int level, Resolver rslv, LogNode log )
 {
  boolean valid = true;
  
//  log.log(Level.INFO, "Validation")
  
  AttributedClass cls = rslv.getAttributedClass(obj);
  
  if( cls == null )
  {
   log.log(Level.ERROR, "Class '"+obj.getAttributedClass()+"' doesn't exist in the new model");
   return false;
  }

  Collection<AttributeAttachmentRule> atRules = cls.getAttributeAttachmentRules() != null?
    cls.getAttributeAttachmentRules() : Collections.<AttributeAttachmentRule>emptyList();
  
  Collection<? extends AgeAttributeClass> atClasses = obj.getAttributeClasses();
  
  for( AgeAttributeClass atCls : atClasses )
  {
   AgeAttributeClass rslvAtCls = rslv.getAttributeClass(atCls);
   
   if( atCls.isCustom() )
    continue;

   if( rslvAtCls == null )
   {
    log.log(Level.ERROR, "Class '"+atCls+"' doesn't exist in the new model");
    valid = false;
    continue;
   }
   
   
   Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atCls, true);
   
   LogNode ln = log.branch("Validating attributes of class '"+atCls+"' Attributes: "+attrs.size());

   boolean res = isAttributeAllowed(rslvAtCls, obj, atCls, atRules, rslv, ln);
   
   if( res )
    ln.success();
   else
    ln.log(Level.ERROR, "Validation failed. No correspondent rule that allows this attribute.");
   
   valid = res && valid;
  }
  
  if( cls.getAttributeAttachmentRules() != null )
  {
   LogNode ln = log.branch("Validating attribute attachment rules");

   boolean rlres = true;
   for( AttributeAttachmentRule atRl : cls.getAttributeAttachmentRules() )
   {
    LogNode sln = ln.branch("Validating rule "+atRl.getRuleId()+". Type: "+atRl.getType().name()+". Target class: '"+atRl.getAttributeClass().getName()+"'");

    boolean res = isAttributeRuleSatisfied( atRl, obj, rslv, sln );
    
    if( res )
     sln.success();
    
    rlres = res && rlres;
   }
   
   if( rlres )
    ln.success();
  
   valid = rlres && valid;
  }
  
  boolean hasQualifiers=false;

  
  for(AgeAttribute attr : obj.getAttributes())
  {
   if( attr.getAttributes() != null && attr.getAttributes().size() > 0 )
   {
    hasQualifiers = true;
    break;
   }
  }
  
  
  if( hasQualifiers )
  {
   LogNode ln = log.branch("Validating attribute qualifiers");
   boolean qres = true;
   
   for( AgeAttribute attr : obj.getAttributes() )
   {
    if( attr.getAgeElClass().isCustom() || attr.getAttributes() == null )
     continue;
    
    LogNode sln = ln.branch("Validating qualifier. Class: '"+attr.getAgeElClass()+"' Column: "+attr.getOrder() );
    
    boolean res = validateAttributed(attr, level+1, rslv, sln);

    if( res )
     sln.success();
    
    valid = res && valid;
    qres = res && qres;
   }
   
   if( qres )
    ln.success();
  }
  

  return valid;
 }
 
 //  Collection<? extends AgeAttribute> attrs,
 private boolean isAttributeAllowed(AgeAttributeClass rslvAtCls, Attributed obj, AgeAttributeClass atClass,  Collection<AttributeAttachmentRule> atRules, Resolver rslv, LogNode log)
 {
  if( atRules == null )
   return false;
  
  for(AttributeAttachmentRule rul : atRules)
  {
   LogNode ln = log.branch("Validating rule "+rul.getRuleId()+". Target class: '"+rul.getAttributeClass().getName()+"'");

   if(rul.getType() == RestrictionType.MUSTNOT)
   {
    ln.log(Level.DEBUG, "MUSTNOT rule. Skiping rule.");
    continue;
   }
   
   if(!((rul.isSubclassesIncluded() && rslvAtCls.isClassOrSubclass(rul.getAttributeClass())) || rul.getAttributeClass().equals(rslvAtCls)))
   {
    ln.log(Level.DEBUG, "Rule doesn't match attribute class ("+rslvAtCls.getName()+"). Skiping rule.");
    continue;
   }
   
   Collection<? extends AgeAttribute> attrs = null;
   
   if( rul.isSubclassesCountedSeparately() )
    attrs = obj.getAttributesByClass(atClass, false);
   else
    attrs = obj.getAttributesByClass(rslv.getAttributeClassOriginal(rul.getAttributeClass()), true);

   if( ! matchCardinality( rul, attrs.size() ) )
   {
    ln.log(Level.INFO,"Rule "+rul.getRuleId()+" cardinality requirement failed. Cardinality: "
      +rul.getCardinalityType().name()+":"+rul.getCardinality()+" Attributes: "+attrs.size()+". Skiping rule.");
    continue;
   }
   else
    ln.success();
   
   if( rul.isValueUnique() )
   {
    if( ! checkValuesUnique( rul, attrs ) )
    {
     ln.log(Level.INFO, "Value uniqueness check failed. Skiping rule");
     continue;
    }
    else
     ln.success();
   }
   
   if( rul.getQualifiers() != null  )
   {
    if( ! matchQualifiers( rul.getQualifiers(), attrs, rslv, ln ) )
    {
     ln.log(Level.INFO, "Qualifiers don't match. Skiping rule");
     continue;
    }
    else
     ln.log(Level.DEBUG, "Qualifiers matched");
   }
  
  
   ln.log(Level.SUCCESS, "Attribute(s) allowed by this rule");
   return true;
  }

  return false;
 }
 
 private boolean isRelationAllowed(AgeRelationClass rslvRlCls, Collection<? extends AgeRelation> rels, Collection<RelationRule> relRules, Resolver rslv, LogNode log)
 {
  if( relRules == null )
   return false;
  
//  log.log(Level.INFO, "Checking whether relations are allowed by some rule");
  
  for(RelationRule rul : relRules)
  {
   LogNode ln = log.branch("Validating rule "+rul.getRuleId()+" Relation class: '"+rul.getRelationClass().getName()+"' Target class: '"+rul.getTargetClass().getName()+"'");

   if(!((rul.isSubclassesIncluded() && rslvRlCls.isClassOrSubclass(rul.getRelationClass())) || rul.getRelationClass().equals(rslvRlCls)))
   {
    ln.log(Level.DEBUG, "Rule "+rul.getRuleId()+" doesn't match class '"+rslvRlCls+"'. Skiping.");
    continue;
   }
   
   if(rul.getType() == RestrictionType.MUSTNOT)
   {
    ln.log(Level.DEBUG, "MUSTNOT rule. Skiping rule.");
    continue;
   }

   if( ! matchCardinality( rul, rels.size() ) )
   {
    ln.log(Level.INFO,"Rule "+rul.getRuleId()+" cardinality requirement failed. Cardinality: "
      +rul.getCardinalityType().name()+":"+rul.getCardinality()+" Relations: "+rels.size()+". Skiping rule.");
    continue;
   }
   else
    ln.success();

  
   if( rul.getQualifiers() != null  )
   {
    if( ! matchQualifiers( rul.getQualifiers(), rels, rslv, ln ) )
    {
     ln.log(Level.INFO, "Qualifiers don't match. Skiping rule");
     continue;
    }
    else
     ln.success();
   }

  
   ln.log(Level.SUCCESS,"Relations of class '"+rslvRlCls+"' are allowed by rule "+rul.getRuleId());

   
   return true;
  }

  return false;
 }

// private boolean checkUniq( Collection<? extends AgeAttribute> attrs )
// {
//  if( attrs.size() <= 1 )
//   return true;
//  
//  List<AgeAttribute> atList = new ArrayList<AgeAttribute>( attrs.size() );
//  atList.addAll(attrs);
//  
//  for( int i=0; i < attrs.size()-1; i++ )
//  {
//   for( int j=i+1; j < attrs.size(); j++ )
//   {
//    if( atList.get(i).equals( atList.get(j) ) )
//     return false;
//   }
//  }
//  
//  return true;
// }
 
 private boolean isEqual(Collection<? extends AgeAttribute> set1, Collection<? extends AgeAttribute> set2 )
 {
  if( set1 == null )
   return set2 == null || set2.size() == 0 ;

  if( set2 == null )
   return set1.size() == 0 ;
  
  if( set1.size() != set2.size() )
   return false;
  
  if( set1.size() == 1 )
   return set1.iterator().next().equals(set2.iterator().next());
  
  List<AgeAttribute> lst1 = new ArrayList<AgeAttribute>( set1.size() );
  List<AgeAttribute> lst2 = new ArrayList<AgeAttribute>( set2.size() );
  
  lst1.addAll(set1);
  lst2.addAll(set2);
  
  Collections.sort(lst1);
  Collections.sort(lst2);
  
  for( int i=0; i < set1.size(); i++ )
  {
   if( ! lst1.get(i).equals(lst2.get(i)) )
    return false;
  }
  
  return true;
 }
 
 private boolean isAttributeRuleSatisfied(AttributeAttachmentRule atRl, Attributed obj, Resolver rslv, LogNode log)
 {
  if( atRl.getType() == RestrictionType.MAY )
  {
   log.log(Level.DEBUG, "MAY rule. Skiping rule.");
   return true;
  }
  
  AgeAttributeClass reslInvRuleCls = rslv.getAttributeClassOriginal(atRl.getAttributeClass());
  
  if( reslInvRuleCls == null )
  {
   log.log(Level.INFO,"Class '"+atRl.getAttributeClass()+"' doen't exist in the current model");
   return atRl.getType() == RestrictionType.MUSTNOT;
  }
  
  Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(reslInvRuleCls, true);

  if( attrs == null || attrs.size() == 0 )
  {
   log.log(Level.DEBUG, "No attributes of rule's class ("+reslInvRuleCls.getName()+")");

   if( atRl.getType() == RestrictionType.MUSTNOT )
    return true;

   attrs = Collections.emptyList();
  }
  
  LogNode ln = log.branch("Validating cardinality");
  if( ! matchCardinality( atRl, attrs.size() ) )
  {
   ln.log(atRl.getType() == RestrictionType.MUSTNOT?Level.INFO:Level.ERROR,
     "Rule "+atRl.getRuleId()+" cardinality requirement failed. Cardinality: "+atRl.getCardinalityType().name()+":"+atRl.getCardinality()
     +" Attributes: "+attrs.size());
   
   return atRl.getType() == RestrictionType.MUSTNOT;
  }
  else
   ln.success();
  
  if( atRl.isValueUnique() )
  {
   ln = log.branch("Validating attribute values uniquness");
   if( ! checkValuesUnique(atRl, attrs) )
   {
    ln.log(Level.ERROR, "Uniqueness validation failed");    
    return atRl.getType() == RestrictionType.MUSTNOT;
   }
   else
    ln.success();
  }
  
  
  if( atRl.getQualifiers() != null )
  {
   ln = log.branch("Matching qualifiers");
   if( ! matchQualifiers(atRl.getQualifiers(), attrs, rslv, ln) )
   {
    ln.log(Level.ERROR, "Qualifiers match failed");
    
    return atRl.getType() == RestrictionType.MUSTNOT;
   }
   else
    ln.success();
  }
  
  return true;
 }

 private boolean isRelationRuleSatisfied(RelationRule rlRl, AgeObject obj, Set< ? extends AgeRelation> auxRels, Set< ? extends AgeRelation> remRels, Resolver rslv, LogNode log)
 {
  if( rlRl.getType() == RestrictionType.MAY )
  {
   log.log(Level.DEBUG, "MAY rule. Skiping.");
   return true;
  }

  AgeRelationClass origRelCls = rslv.getAgeRelationClassOriginal(rlRl.getRelationClass()); //If the rule came from the new model we have to get correspondent class of the older model
  
  if( origRelCls == null )
  {
   log.log(rlRl.getType() == RestrictionType.MUSTNOT?Level.INFO:Level.ERROR,"Class '"+rlRl.getRelationClass()+"' doen't exist in the current model");
   
   return rlRl.getType() == RestrictionType.MUSTNOT;
  }
  
  Collection<? extends AgeRelation> rels = obj.getRelationsByClass(origRelCls, true);

  if( rels == null || rels.size() == 0 )
  {
   log.log(Level.DEBUG, "No relations of rule's class ("+origRelCls.getName()+")");

   if( rlRl.getType() == RestrictionType.MUSTNOT )
    return true;

   if( rels == null )
    rels = Collections.emptyList();
  }
  
  if( ( auxRels != null && auxRels.size() > 0 ) || ( remRels != null && remRels.size() > 0 ) )
  {
   ArrayList<AgeRelation> nRls = new ArrayList<AgeRelation>( rels.size() + auxRels.size() );
   
   for( AgeRelation r : rels )
   {
    if( ! auxRels.contains(r) || ! remRels.contains(r) )
     nRls.add(r);
   }
   
   for( AgeRelation r : auxRels )
    nRls.add(r);

   rels = nRls;
  }
   
   
  LogNode ln = log.branch("Validating cardinality");
  if( ! matchCardinality( rlRl, rels.size() ) )
  {
   ln.log(Level.ERROR,"Rule "+rlRl.getRuleId()+" cardinality requirement failed. Cardinality: "+rlRl.getCardinalityType().name()+":"+rlRl.getCardinality()
     +" Relations: "+rels.size());

   return rlRl.getType() == RestrictionType.MUSTNOT;
  }
  else
   ln.success();
  
//  if( ! checkTargetsUnique(rels) )
//   return rlRl.getType() == RestrictionType.MUSTNOT;
   
  
  if( rlRl.getQualifiers() != null )
  {
   ln = log.branch("Matching qualifiers");
   if( ! matchQualifiers(rlRl.getQualifiers(), rels, rslv, ln) )
   {
    ln.log(Level.ERROR, "Qualifiers match failed");
    
    return rlRl.getType() == RestrictionType.MUSTNOT;
   }
   else
    ln.success();
  }
  
  
  return true;
 }
 
 private boolean matchCardinality( AttributeAttachmentRule rul, int nAttr )
 {
  switch(rul.getCardinalityType())
  {
   case EXACT:
    if(rul.getCardinality() != nAttr)
     return false;

    break;

   case MAX:
    if(rul.getCardinality() < nAttr)
     return false;

    break;

   case MIN:
    if(rul.getCardinality() > nAttr)
     return false;

    break;
    
   default:
  }
 
  return true;
 }
 
 private boolean matchCardinality( RelationRule rul, int nRel)
 {
  boolean res=true;
  
  switch(rul.getCardinalityType())
  {
   case EXACT:
    if(rul.getCardinality() != nRel)
     res=false;

    break;

   case MAX:
    if(rul.getCardinality() < nRel)
     res=false;

    break;

   case MIN:
    if(rul.getCardinality() > nRel)
     res=false;

    break;

   default:
  }
 
  return res;
 }
 
 private boolean checkValuesUnique( AttributeAttachmentRule rul, Collection<? extends AgeAttribute> attrs )
 {
  if(rul.isValueUnique() && attrs.size() > 1)
  {
   ArrayList<AgeAttribute> atList = new ArrayList<AgeAttribute>(attrs.size());
   atList.addAll(attrs);

   for(int i = 0; i < attrs.size() - 1; i++)
   {
    for(int j = i + 1; j < attrs.size(); j++)
    {
     if(atList.get(i).equals(atList.get(j)))
      return false;
    }
   }
  }
  
  return true;
 }
 
 private boolean checkTargetsUnique( Collection<? extends AgeRelation> rels, LogNode log )
 {
  AgeObject dupObj=null;
  
  if(rels.size() > 1 )
  {
   ArrayList<AgeRelation> rlList = new ArrayList<AgeRelation>(rels.size());
   rlList.addAll(rels);

   cyc: for(int i = 0; i < rels.size() - 1; i++)
   {
    for(int j = i + 1; j < rels.size(); j++)
    {
     if(rlList.get(i).getTargetObject().equals(rlList.get(j).getTargetObject()))
     {
      dupObj = rlList.get(i).getTargetObject();
      break cyc;
     }
    }
   }
  }
  
  if( dupObj != null )
  {
   log.log(Level.ERROR, "All target objects must be unique. Object ID: '"+dupObj.getId()+"' (Scope: "+dupObj.getIdScope()+") is duplicated");
   return false;
  }
  else
   return true;
 }
 
 private boolean matchQualifiers( Collection<QualifierRule> qRules, Collection<? extends Attributed> attrs, Resolver resolv,  LogNode log )
 {
  boolean matched = true;

  if(qRules != null)
  {
   rules: for(QualifierRule qr : qRules)
   {
    LogNode ln = log.branch("Validating qualifier rule " + qr.getRuleId());

    AgeAttributeClass invReslRuleAttrCls = resolv.getAttributeClassOriginal(qr.getAttributeClass());
    
    if( invReslRuleAttrCls == null )
    {
     log.log(Level.ERROR, "Attribute class '"+qr.getAttributeClass()+"' doesn't exist in current model");
     matched = false;
     continue;
    }
   
    for(Attributed attr : attrs)
    {
     boolean found = false;

     Collection< ? extends AgeAttributeClass> clss = attr.getAttributeClasses();

     if(clss != null)
     {
      for(AgeAttributeClass atc : clss)
      {
       AgeAttributeClass rslvAtc = resolv.getAttributeClass(atc);
       
       if( rslvAtc == null )
       {
        log.log(Level.ERROR, "Attribute class '"+atc+"' doesn't exist in current model");
        return false;
       }
       
       if(rslvAtc.isClassOrSubclass(qr.getAttributeClass()))
       {
        found = true;
        break;
       }
      }
     }

     if(!found)
     {
      ln.log(Level.ERROR, "Rule validation failed. No qualifiers with rule's class");

      matched = false;
      continue;
     }
    }

    if(qr.isUnique())
    {
     ln.log(Level.INFO, "Validating qualifiers' uniqueness");

     ArrayList<Attributed> atList = new ArrayList<Attributed>(attrs.size());
     atList.addAll(attrs);

     for(int i = 0; i < attrs.size() - 1; i++)
     {
      for(int j = i + 1; j < attrs.size(); j++)
      {
       if(isEqual(atList.get(i).getAttributesByClass(invReslRuleAttrCls, true), atList.get(j).getAttributesByClass(invReslRuleAttrCls, true)))
       {
        ln.log(Level.ERROR, "Rule validation failed. Qualifiers are not unique");
        matched = false;
        continue rules;
       }
      }
     }
    }

    ln.success();
   }
  }

  return matched;
 }
 
 private static class Resolver
 {
  private static Resolver instance = new Resolver();
  
  static Resolver getIntsance()
  {
   return instance;
  }

  public AgeRelationClass getAgeRelationClassOriginal(AgeRelationClass relationClass)
  {
   return relationClass;
  }

  public AgeRelationClass getAgeRelationClass(AgeRelationClass rlCls)
  {
   return rlCls;
  }

  public AgeClass getAgeClass(AgeClass ageElClass)
  {
   return ageElClass;
  }

  public AgeAttributeClass getAttributeClassOriginal(AgeAttributeClass attributeClass)
  {
   return attributeClass;
  }

  public AgeAttributeClass getAttributeClass(AgeAttributeClass atCls)
  {
   return atCls;
  }

  public AttributedClass getAttributedClass(Attributed obj)
  {
   return obj.getAttributedClass();
  }
 }
 
 private static class ExtModelResolver extends Resolver
 {
  private SemanticModel origModel;
  private SemanticModel newModel;
  
  ExtModelResolver(SemanticModel oldM,SemanticModel newM)
  {
   origModel = oldM;
   newModel = newM;
  }
  
  @Override
  public AgeRelationClass getAgeRelationClassOriginal(AgeRelationClass rlCls)
  {
   if( rlCls.isCustom() )
    return rlCls;
   
   if( rlCls.isImplicit() )
    return null;
   
   return origModel.getDefinedAgeRelationClass(rlCls.getName());
  }

  @Override
  public AgeRelationClass getAgeRelationClass(AgeRelationClass rlCls)
  {
   if( rlCls.isCustom() )
    return rlCls;
   
   if( rlCls.isImplicit() )
    return null;
   
   return newModel.getDefinedAgeRelationClass(rlCls.getName());
  }

  @Override
  public AgeClass getAgeClass(AgeClass ageElClass)
  {
   if( ageElClass.isCustom() )
    return ageElClass;
   
   return newModel.getDefinedAgeClass(ageElClass.getName());
  }

  @Override
  public AgeAttributeClass getAttributeClassOriginal(AgeAttributeClass atCls)
  {
   if( atCls.isCustom() )
    return atCls;
   
   return origModel.getDefinedAgeAttributeClass(atCls.getName());
  }

  @Override
  public AgeAttributeClass getAttributeClass(AgeAttributeClass atCls)
  {
   if( atCls.isCustom() )
    return atCls;
   
   return newModel.getDefinedAgeAttributeClass(atCls.getName());
  }

  @Override
  public AttributedClass getAttributedClass(Attributed obj)
  {
   AttributedClass cls = obj.getAttributedClass();
   
   if( cls.isCustom() )
    return cls;
   
   if( cls instanceof AgeClass )
    return newModel.getDefinedAgeClass(cls.getName());
   else if ( cls instanceof AgeAttributeClass )
    return newModel.getDefinedAgeAttributeClass(cls.getName());
   else if ( cls instanceof AgeRelationClass )
    return newModel.getDefinedAgeRelationClass(cls.getName());
   
   return null;
  }

 }

// private boolean checkAllQualifiers( AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs )
// {
//  for( AgeAttribute attr : attrs )
//  {
//   Collection<? extends AgeAttributeClass> qClss = attr.getAttributeClasses();
//   
//   if( qClss != null )
//   {
//    for( AgeAttributeClass qCls : qClss )
//    {
//     if( ! isAttributeAllowed(qCls, attr.getAttributesByClass(qCls, true), atCls.getAttributeAttachmentRules() ) )
//      return false;
//    }
//   }
//  }
//  
//  return true;
// }

 // private boolean validateAttributeByAttributeRule(AttributeAttachmentRule atRl, AgeAttribute at)
// {
//  if( !atRl.isSubclassesIncluded() )
//  {
//   if( ! at.getAgeElClass().equals(atRl.getAttributeClass()) )
//    return false;
//  }
//  else if( ! at.getAgeElClass().isClassOrSubclass(atRl.getAttributeClass()) )
//   return false;
//  
//  int nVals = at.
//  
//  if( atRl.getCardinalityType() != Cardinality.ANY )
//  {}
// }
 
}
