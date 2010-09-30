package uk.ac.ebi.age.validator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.validator.AgeSemanticValidator;

public class AgeSemanticValidatorImpl implements AgeSemanticValidator
{

 @Override
 public void validate(Submission subm)
 {
  for( AgeObject obj : subm.getObjects() )
   validateObject( obj, obj.getAgeElClass() );
 }

 private boolean validateObject(AgeObject obj, AgeClass cls)
 {
  boolean valid = true;
  
  if( cls.getSuperClasses() != null )
  {
   for( AgeClass supCls : cls.getSuperClasses() )
    valid = valid?validateObject(obj,supCls):false;
  }
  
  validateAttributed( obj );
  
  return valid;
 }

 private boolean validateRelations(AgeObject obj)
 {
  AgeClass cls = obj.getAgeElClass();

  Collection<RelationRule> rlRules = cls.getRelationRules() != null ? cls.getRelationRules() : Collections.<RelationRule> emptyList();

  Collection< ? extends AgeRelationClass> rlClasses = obj.getRelationClasses();

  boolean objectOk = true;

  for(AgeRelationClass rlCls : rlClasses)
  {
   if(rlCls.isCustom())
    continue;

   Collection< ? extends AgeRelation> rels = obj.getRelationsByClass(rlCls, true);

   if(!isRelationAllowed(rlCls, rels, rlRules))
   {
    objectOk = false;
    break;
   }

   for(AgeRelation rl : rels)
   {
    if(!validateAttributed(rl))
    {
     objectOk = false;
     break;
    }
   }
  }

  if(cls.getRelationRules() != null)
  {
   for(RelationRule rlRl : cls.getRelationRules())
    isRelationRuleSatisfied(rlRl, obj);
  }

  return objectOk;

 }
 
 
 private boolean validateAttributed( Attributed obj )
 {
  boolean valid = true;
  
  AttributedClass cls = obj.getAttributedClass();

  Collection<AttributeAttachmentRule> atRules = cls.getAttributeAttachmentRules() != null?
    cls.getAttributeAttachmentRules() : Collections.<AttributeAttachmentRule>emptyList();
  
  Collection<? extends AgeAttributeClass> atClasses = obj.getAttributeClasses();
  
  boolean objectOk=true;
  
  for( AgeAttributeClass atCls : atClasses )
  {
   if( atCls.isCustom() )
    continue;
   
   Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atCls, true);
   
   if( ! isAttributeAllowed(atCls, attrs, atRules) )
   {
    objectOk=false;
    break;
   }
 
   for( AgeAttribute attr : attrs )
   {
    if( ! validateAttributed(attr) )
    {
     objectOk=false;
     break;
    }
   }
  }
  
  if( cls.getAttributeAttachmentRules() != null )
  {
   for( AttributeAttachmentRule atRl : cls.getAttributeAttachmentRules() )
    isAttributeRuleSatisfied(atRl,obj);
  }
  
  

  return valid;
 }
 
 private boolean isAttributeAllowed(AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs, Collection<AttributeAttachmentRule> atRules)
 {
  if( atRules == null )
   return false;
  
  boolean satisf = false;

  for(AttributeAttachmentRule rul : atRules)
  {
   if(!((rul.isSubclassesIncluded() && atCls.isClassOrSubclass(rul.getAttributeClass())) || rul.getAttributeClass().equals(atCls)))
    continue;

   if(rul.getType() == RestrictionType.MUSTNOT)
    continue;

   if( ! matchCardinality( rul, attrs.size() ) )
    continue;
   
   if( ! checkValuesUnique( rul, attrs ) )
    continue;
  
   if( ! matchQualifiers(rul.getQualifiers(), attrs))
    continue;
   
  
   satisf = true;
   break;
   
  }

  return satisf;
 }
 
 private boolean isRelationAllowed(AgeRelationClass atCls, Collection<? extends AgeRelation> rels, Collection<RelationRule> relRules)
 {
  if( relRules == null )
   return false;
  
  boolean satisf = false;

  for(RelationRule rul : relRules)
  {
   if(!((rul.isSubclassesIncluded() && atCls.isClassOrSubclass(rul.getRelationClass())) || rul.getRelationClass().equals(atCls)))
    continue;

   if(rul.getType() == RestrictionType.MUSTNOT)
    continue;

   if( ! matchCardinality( rul, rels.size() ) )
    continue;
   
   if( ! checkTargetsUnique( rels ) )
    continue;
  
   if( ! matchQualifiers(rul.getQualifiers(), rels))
    continue;
   
  
   satisf = true;
   break;
   
  }

  return satisf;
 }

 private boolean checkUniq( Collection<? extends AgeAttribute> attrs )
 {
  if( attrs.size() <= 1 )
   return true;
  
  List<AgeAttribute> atList = new ArrayList<AgeAttribute>( attrs.size() );
  atList.addAll(attrs);
  
  for( int i=0; i < attrs.size()-1; i++ )
  {
   for( int j=i+1; j < attrs.size(); j++ )
   {
    if( atList.get(i).equals( atList.get(j) ) )
     return false;
   }
  }
  
  return true;
 }
 
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
 
 private boolean isAttributeRuleSatisfied(AttributeAttachmentRule atRl, Attributed obj)
 {
  if( atRl.getType() == RestrictionType.MAY )
   return true;
  
  Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atRl.getAttributeClass(), true);

  if( attrs == null || attrs.size() == 0 )
   return atRl.getType() == RestrictionType.MUSTNOT;

  
  if( ! matchCardinality( atRl, attrs.size() ) )
   return atRl.getType() == RestrictionType.MUSTNOT;

  if( atRl.isValueUnique() && ! checkValuesUnique(atRl, attrs) )
   return atRl.getType() == RestrictionType.MUSTNOT;
   
  if( ! matchQualifiers(atRl.getQualifiers(), attrs) )
   return atRl.getType() == RestrictionType.MUSTNOT;
  
  
  return true;
 }

 private boolean isRelationRuleSatisfied(RelationRule rlRl, AgeObject obj)
 {
  if( rlRl.getType() == RestrictionType.MAY )
   return true;
  
  Collection<? extends AgeRelation> rels = obj.getRelationsByClass(rlRl.getRelationClass(), true);

  if( rels == null || rels.size() == 0 )
   return rlRl.getType() == RestrictionType.MUSTNOT;

  
  if( ! matchCardinality( rlRl, rels.size() ) )
   return rlRl.getType() == RestrictionType.MUSTNOT;

  if( ! checkTargetsUnique(rels) )
   return rlRl.getType() == RestrictionType.MUSTNOT;
   
  if( ! matchQualifiers(rlRl.getQualifiers(), rels) )
   return rlRl.getType() == RestrictionType.MUSTNOT;
  
  
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
  }
 
  return true;
 }
 
 private boolean matchCardinality( RelationRule rul, int nAttr )
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
  }
 
  return true;
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
 
 private boolean checkTargetsUnique( Collection<? extends AgeRelation> rels )
 {
  if(rels.size() > 1 )
  {
   ArrayList<AgeRelation> rlList = new ArrayList<AgeRelation>(rels.size());
   rlList.addAll(rels);

   for(int i = 0; i < rels.size() - 1; i++)
   {
    for(int j = i + 1; j < rels.size(); j++)
    {
     if(rlList.get(i).equals(rlList.get(j)))
      return false;
    }
   }
  }
  
  return true;
 }
 
 private boolean matchQualifiers( Collection<QualifierRule> qRules, Collection<? extends Attributed> attrs )
 {
  if(qRules != null)
  {
   for(QualifierRule qr : qRules)
   {
    for(Attributed attr : attrs)
    {
     boolean found = false;

     Collection<? extends AgeAttributeClass> clss = attr.getAttributeClasses();
     
     if( clss != null )
     {
      for(AgeAttributeClass atc : clss)
      {
       if(atc.isClassOrSubclass(qr.getAttributeClass()))
       {
        found = true;
        break;
       }
      }
      
     }
     

     if(!found)
      return false;
    }

    if(qr.isUnique())
    {
     ArrayList<Attributed> atList = new ArrayList<Attributed>(attrs.size());
     atList.addAll(attrs);

     for(int i = 0; i < attrs.size() - 1; i++)
     {
      for(int j = i + 1; j < attrs.size(); j++)
      {
       if( isEqual(atList.get(i).getAttributesByClass(qr.getAttributeClass(), true), atList.get(j).getAttributesByClass(qr.getAttributeClass(),true)) )
        return false;
      }
     }

    }
   }
  }
  
  return true;
 }
 
 private boolean checkAllQualifiers( AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs )
 {
  for( AgeAttribute attr : attrs )
  {
   Collection<? extends AgeAttributeClass> qClss = attr.getAttributeClasses();
   
   if( qClss != null )
   {
    for( AgeAttributeClass qCls : qClss )
    {
     if( ! isAttributeAllowed(qCls, attr.getAttributesByClass(qCls, true), atCls.getAttributeAttachmentRules() ) )
      return false;
    }
   }
  }
  
  return true;
 }
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
