package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

abstract class AgeAttributeImpl extends AttributedObject implements AgeAttributeWritable, Serializable
{
 private static final long serialVersionUID = 3L;

 private AttributeClassRef classReference;
 private AttributedWritable hostObject;

 
 @Override
 public AttributeClassRef getClassRef()
 {
  return classReference;
 }
 
 public AgeAttributeImpl(AttributeClassRef attrClassR, AttributedWritable host)
 {
  classReference = attrClassR;
  hostObject = host;
 }


 public void finalizeValue()
 {
 }
 
 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return hostObject.getSemanticModel();
 }

 @Override
 public AgeAttributeClass getAgeElClass()
 {
  return classReference.getAttributeClass();
 }
 

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }
 
 
 public int getOrder()
 {
  return classReference.getOrder( );
 }
 
 @Override
 public AttributedWritable getHostObject()
 {
  return hostObject;
 }
}
