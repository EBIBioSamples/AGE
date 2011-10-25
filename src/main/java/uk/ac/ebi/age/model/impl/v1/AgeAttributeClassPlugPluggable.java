package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.util.Plug;

class AgeAttributeClassPlugPluggable implements Plug, Serializable, AgeAttributeClassPlug
{
 private static final long serialVersionUID = 1L;

 private String className;
 private transient AgeAttributeClass ageAttributeClass = null;
 private SemanticModel model;
 
 public AgeAttributeClassPlugPluggable(AgeAttributeClass attrClass, SemanticModel mdl)
 {
  ageAttributeClass=attrClass;
  className = attrClass.getName();
  model=mdl;
 }

 public void unplug()
 {
  ageAttributeClass = null;
 }
 
 public boolean plug()
 {
  ageAttributeClass = model.getDefinedAgeAttributeClass(className);
  
  if( ageAttributeClass != null )
   return true;
  
  return false;
 }
 
 public AgeAttributeClass getAgeAttributeClass()
 {
  if( ageAttributeClass == null )
   if( !plug() )
    System.err.println("Can't plug attribute class: "+className);
  
  return ageAttributeClass;
 }

 @Override
 public boolean isPlugged()
 {
  return ageAttributeClass!=null;
 }
}
