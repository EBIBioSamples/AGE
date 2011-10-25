package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeSemanticElement;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;

public class QualifierRuleImpl implements QualifierRuleWritable, AgeSemanticElement, Serializable
{

 private static final long serialVersionUID = 1L;

 private SemanticModel model;
 private int id;
 private boolean uniq;
 private AgeAttributeClass attrClass;

 
 public QualifierRuleImpl(SemanticModel sm)
 {
  model = sm;
 }

 @Override
 public AgeAttributeClass getAttributeClass()
 {
  return attrClass;
 }

 @Override
 public boolean isUnique()
 {
  return uniq;
 }

 @Override
 public int getRuleId()
 {
  return id;
 }

 @Override
 public void setAttributeClass(AgeAttributeClass ageAttributeClass)
 {
  attrClass = ageAttributeClass;
 }

 @Override
 public void setUnique(boolean unique)
 {
  uniq=unique;
 }

 @Override
 public void setRuleId(int id)
 {
  this.id = id;
 }

 @Override
 public SemanticModel getSemanticModel()
 {
  return model;
 }

 @Override
 public String getId()
 {
  return String.valueOf(id);
 }

}
