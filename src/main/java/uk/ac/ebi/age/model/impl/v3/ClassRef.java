package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.ContextSemanticModel;

public class ClassRef implements uk.ac.ebi.age.model.ClassRef, Serializable
{
 private static final long serialVersionUID = 3L;

 private int order;
 private String heading;
 private AgeClassPlug classPlug;
 private boolean horizontal;
 private ContextSemanticModel model;

 public ClassRef(AgeClassPlug cp, int order, String headingl, boolean horiz, ContextSemanticModel md )
 {
  super();
  this.order = order;
  this.heading = headingl;
  this.classPlug = cp;
  horizontal = horiz;
  model = md;
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

 @Override
 public AgeClass getAgeClass()
 {
  return classPlug.getAgeClass();
 }

 @Override
 public boolean isHorizontal()
 {
  return horizontal;
 }


 public ContextSemanticModel getSemanticModel()
 {
  return model;
 }

}
