package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.RelationClassRef;

public class RelClassRef implements RelationClassRef, Serializable
{
 private static final long serialVersionUID = 3L;

 private int order;
 private String heading;
 private AgeRelationClassPlug relClassPlug;

 public RelClassRef(AgeRelationClassPlug attrClassPlug, int order, String heading )
 {
  super();
  this.order = order;
  this.heading = heading;
  this.relClassPlug = attrClassPlug;
 }

 @Override
 public AgeRelationClass getAgeRelationClass()
 {
  return relClassPlug.getAgeRelationClass();
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
