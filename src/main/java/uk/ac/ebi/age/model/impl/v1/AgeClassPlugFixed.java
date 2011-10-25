package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.util.Plug;

public class AgeClassPlugFixed implements Serializable, Plug, uk.ac.ebi.age.model.AgeClassPlug
{
 private static final long serialVersionUID = 1L;

 private AgeClass ageClass;
 
 public AgeClassPlugFixed(AgeClass cls)
 {
  ageClass = cls;
 }

 public void unplug()
 {
 }
 
 public boolean plug()
 {
  return true;
 }
 
 public AgeClass getAgeClass()
 {
  return ageClass;
 }

 @Override
 public boolean isPlugged()
 {
  return true;
 }
}
