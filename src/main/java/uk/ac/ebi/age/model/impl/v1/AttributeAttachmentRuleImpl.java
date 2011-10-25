package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeSemanticElement;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AttributeAttachmentRuleWritable;


public class AttributeAttachmentRuleImpl implements Serializable, AttributeAttachmentRuleWritable, AgeSemanticElement
{
 private static final long serialVersionUID = 1L;

 private RestrictionType type = RestrictionType.MAY;
 private Cardinality cardType = Cardinality.ANY;
 private AgeAttributeClass attributeClass;
 private int cardinality=1;
 private Collection<QualifierRule> qualifiers;
 private boolean valueUnique;
 private boolean subclassesIncluded=true;
 private boolean subclassesCountedSeparately=true;
 
 
 private SemanticModel model;

 private int id;

 public AttributeAttachmentRuleImpl( RestrictionType typ, SemanticModel mod )
 {
  type=typ;
  model = mod;
 }
 
 @Override
 public AgeAttributeClass getAttributeClass()
 {
  return attributeClass;
 }

 public RestrictionType getType()
 {
  return type;
 }

 public void setType(RestrictionType type)
 {
  this.type = type;
 }

 public Cardinality getCardinalityType()
 {
  return cardType;
 }

 public void setCardinalityType(Cardinality cardType)
 {
  this.cardType = cardType;
 }

 public int getCardinality()
 {
  return cardinality;
 }

 public void setCardinality(int cardinality)
 {
  this.cardinality = cardinality;
 }

 public Collection<QualifierRule> getQualifiers()
 {
  return qualifiers;
 }

 public void addQualifier( QualifierRule qr )
 {
  if( qualifiers == null )
   qualifiers=new ArrayList<QualifierRule>();

  qualifiers.add(qr);
 
 }
 
 public void setAttributeClass(AgeAttributeClass attributeClass)
 {
  this.attributeClass = attributeClass;
 }

 /*
 public String toString()
 {
  StringBuilder sb = new StringBuilder();
  
  sb.append("object ").append(type.getTitle()).append(" have ");
  sb.append("attributes of class <b>").append(attributeClass.getName()).append("</b> (");

  if( subclassesIncluded )
   sb.append("including subclasses)");
  else
   sb.append("excluding subclasses)");
   
  
  if( type != RestrictionType.MUSTNOT )
  {
//   sb.append(" that have ").append(cardType.name());
   
   if( cardType == Cardinality.ANY)
    sb.append(" that may have any number of values ");
   else
    sb.append(" that must have ").append(cardType.getTitle()).append(" ").append(cardinality).append(" value").append(cardinality>1?"s ":" ");
   
   if( ( cardinality > 1 || cardType == Cardinality.MIN ) && (valueUnique || qualifiersUnique) )
   {
    sb.append("(all ");
    
    if( valueUnique )
    {
     sb.append("values");
     
     if(qualifiersUnique)
      sb.append(" and ");
    }

    if(qualifiersUnique)
     sb.append("qualifiers");
    
    sb.append(" must be unique) ");
    
   }
   
   if( qualifiers != null )
   {
    boolean hasQ=false;
  
    sb.append("and ");
    
    for( RestrictionType rt : RestrictionType.values() )
    {
     Collection<QualifierRule> qcoll = qualifiers.get(rt);
     
     if( qcoll != null && qcoll.size() > 0 )
     {
      hasQ=true;
      sb.append(rt.getTitle()).append(" have qualifiers of class");
      
      if( qcoll.size() > 1 )
       sb.append("es");
      
      sb.append(" ");
      
      for( QualifierRule qr : qcoll )
       sb.append("<b>").append(qr.getAttributeClass().getName()).append("</b>, ");
      
      sb.setLength( sb.length()-2 );
      
      sb.append(" and ");
     }
    }

    if( hasQ )
     sb.setLength( sb.length()-5 );
    else
     sb.setLength( sb.length()-6 );
   }
  }
  else
  {
   if( cardType != Cardinality.ANY )
   {
    sb.append(" if they have ");
    
    switch(cardType)
    {
     case EXACT:
      sb.append(cardinality).append(" values");
      break;
 
     case MIN:
      sb.append(cardinality).append(" or more values");
      break;

     case MAX:
      sb.append(cardinality).append(" or less values");
      break;

     default:
      break;
    }
   }
   
   if( qualifiersCondition != QualifiersCondition.ANY )
   {
    if( cardType != Cardinality.ANY )
     sb.append(" and");
    
    if( qualifiersCondition == QualifiersCondition.NONE )
     sb.append(" if they have no qualifiers");
    else if( qualifiers != null && qualifiers.size() > 0 )
    {
     sb.append(" if they have the following qualifier");
     
     sb.append( qualifiers.size() > 1?"s ":" ");
     
     for( QualifierRule qr : qualifiers.get(RestrictionType.MUST) )
      sb.append("<b>").append(qr.getAttributeClass().getName()).append("</b>, ");

     sb.setLength(sb.length()-2);
    }
   }
  }
  
  return sb.toString();
 }
 */

 public void clearQualifiers()
 {
  if( qualifiers != null )
   qualifiers.clear();
 }

 public boolean isValueUnique()
 {
  return valueUnique;
 }
 

 public void setValueUnique(boolean valueUnique)
 {
  this.valueUnique = valueUnique;
 }


 public boolean isSubclassesIncluded()
 {
  return subclassesIncluded;
 }

 public void setSubclassesIncluded(boolean subclassesIncluded)
 {
  this.subclassesIncluded = subclassesIncluded;
 }

 @Override
 public RestrictionType getRestrictionType()
 {
  return type;
 }

 @Override
 public SemanticModel getSemanticModel()
 {
  return model;
 }

 @Override
 public int getRuleId()
 {
  return id;
 }
 
 @Override
 public void setRuleId( int id )
 {
  this.id=id;
 }

 @Override
 public String getId()
 {
  return null;
 }

 public boolean isSubclassesCountedSeparately()
 {
  return subclassesCountedSeparately;
 }

 public void setSubclassesCountedSeparately(boolean subclassesCountedSeparately)
 {
  this.subclassesCountedSeparately = subclassesCountedSeparately;
 }



}
