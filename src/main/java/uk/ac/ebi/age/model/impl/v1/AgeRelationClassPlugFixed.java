package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.util.Plug;

public class AgeRelationClassPlugFixed implements Serializable, Plug, AgeRelationClassPlug
{
 private static final long serialVersionUID = 1L;
 
 private AgeRelationClass ageRelationClass;
 
 public AgeRelationClassPlugFixed(AgeRelationClass relClass)
 {
  ageRelationClass=relClass;
 }

 public void unplug()
 {
 }
 
 public boolean plug()
 {
  return true;
 }
 
 public AgeRelationClass getAgeRelationClass()
 {
  return ageRelationClass;
 }

 @Override
 public boolean isPlugged()
 {
  return true;
 }
}
