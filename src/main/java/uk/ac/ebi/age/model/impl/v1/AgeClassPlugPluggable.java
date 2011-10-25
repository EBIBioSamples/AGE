package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.util.Plug;

class AgeClassPlugPluggable implements Serializable, Plug, AgeClassPlug
{
 private static final long serialVersionUID = 1L;

 private String className;
 private transient AgeClass ageClass;
 private SemanticModel model;
 
 public AgeClassPlugPluggable(AgeClass cls, SemanticModel mdl)
 {
  className = cls.getName();
  ageClass = cls;
  
  model = mdl;
 }

 public void unplug()
 {
  ageClass = null;
 }
 
 public boolean plug()
 {
  ageClass = model.getDefinedAgeClass(className);
  
  if( ageClass != null )
   return true;
  
  return false;
 }
 
 public AgeClass getAgeClass()
 {
  if( ageClass == null )
   plug();
  
  return ageClass;
 }

 @Override
 public boolean isPlugged()
 {
  return ageClass!=null;
 }
}
