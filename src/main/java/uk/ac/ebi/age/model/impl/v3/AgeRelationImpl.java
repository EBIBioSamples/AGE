package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

class AgeRelationImpl extends AttributedObject implements AgeRelationWritable, Serializable
{
 private static final long serialVersionUID = 3L;
 
 private RelationClassRef relClassRef;
 private AgeObjectWritable source;
 private AgeObjectWritable target;
 private AgeRelationWritable invRelation;
 private boolean inferred=false;
 
 public AgeRelationImpl(RelationClassRef cref, AgeObjectWritable sourceObj, AgeObjectWritable targetObj)
 {
  relClassRef= cref;
  source=sourceObj;
  target=targetObj;
 }

 @Override
 public AgeRelationClass getAgeElClass()
 {
  return relClassRef.getAgeRelationClass();
 }

 @Override
 public AgeObjectWritable getTargetObject()
 {
  return target;
 }

 @Override
 public AgeObjectWritable getSourceObject()
 {
  return source;
 }

 @Override
 public int getOrder()
 {
  return relClassRef.getOrder();
 }

 @Override
 public void setInferred( boolean inf )
 {
  inferred = inf;
 }

 
 @Override
 public boolean isInferred()
 {
  return inferred;
 }


 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }

 @Override
 public AgeRelationWritable createClone( AgeObjectWritable src )
 {
  AgeRelationImpl clone = new AgeRelationImpl(relClassRef, src, getTargetObject());
  
  cloneAttributes(clone);
  
  return clone;
 }

 @Override
 public AgeRelationWritable getInverseRelation()
 {
  return invRelation;
 }

 @Override
 public void setInverseRelation(AgeRelationWritable invRl)
 {
  invRelation = invRl;
 }

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return source.getSemanticModel();
 }
}
