package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;

public class AttrClassRef implements AttributeClassRef, Serializable
{
 private static final long serialVersionUID = 3L;

 private int order;
 private String heading;
 private AgeAttributeClassPlug attrClassPlug;

 public AttrClassRef(AgeAttributeClassPlug attrClassPlug, int order, String heading )
 {
  super();
  this.order = order;
  this.heading = heading;
  this.attrClassPlug = attrClassPlug;
 }

 @Override
 public AgeAttributeClass getAttributeClass()
 {
  return attrClassPlug.getAgeAttributeClass();
 }

 @Override
 public int getOrder()
 {
  return order;
 }

 @Override
 public String getHeading()
 {
  return heading;
 }

}
