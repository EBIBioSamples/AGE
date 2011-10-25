package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAnnotationWritable;

public class AgeAnnotationImpl extends AgeSemanticElementImpl implements AgeAnnotationWritable
{
 private static final long serialVersionUID = 1L;

 private AgeAnnotationClass antCls;
 private String text;
 private String id;

 public AgeAnnotationImpl(AgeAnnotationClass cls, SemanticModel sm )
 {
  super(sm);
  
  antCls = cls;
 }
 
 public void setText(String text)
 {
  this.text = text;
 }

 public String getText()
 {
  return text;
 }

 public AgeAnnotationClass getAgeElClass()
 {
  return antCls;
 }

 @Override
 public String getId()
 {
  return id;
 }
}
