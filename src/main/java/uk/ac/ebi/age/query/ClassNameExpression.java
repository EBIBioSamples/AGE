package uk.ac.ebi.age.query;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;

public class ClassNameExpression implements QueryExpression
{
 public enum ClassType
 {
  DEFINED,
  CUSTOM,
  ANY
 }

 private String className;
 private ClassType classType;
 
 public String getClassName()
 {
  return className;
 }
 
 public void setClassName(String className)
 {
  this.className = className;
 }
 
 public ClassType getClassType()
 {
  return classType;
 }
 
 public void setClassType(ClassType classType)
 {
  this.classType = classType;
 }

 @Override
 public boolean test(AgeObject obj)
 {
  AgeClass cls = obj.getAgeElClass();
  
  if( classType == ClassType.DEFINED && cls.isCustom() )
   return false;

  if( classType == ClassType.CUSTOM &&  ! cls.isCustom() )
   return false;
  
  if( cls.getName().equals(className) )
   return true;
  
  return false;
 }

 @Override
 public boolean isTestingRelations()
 {
  return false;
 }
 
 

}
