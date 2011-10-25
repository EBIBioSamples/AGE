package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.util.Plug;

class AgeAttributeClassPlugFixed implements Plug, Serializable, AgeAttributeClassPlug
{
 private static final long serialVersionUID = 1L;

 private AgeAttributeClass ageAttributeClass;
 
 public AgeAttributeClassPlugFixed(AgeAttributeClass attrClass)
 {
  ageAttributeClass=attrClass;
 }

 public void unplug()
 {
 }
 
 public boolean plug()
 {
  return true;
 }
 
 public AgeAttributeClass getAgeAttributeClass()
 {
  return ageAttributeClass;
 }

 @Override
 public boolean isPlugged()
 {
  return true;
 }
}
