package uk.ac.ebi.age.model.impl.v3;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;

class CustomObjectAgeAttributeClassImpl extends AbstractCustomAgeAttributeClassImpl
{
 private static final long serialVersionUID = 3L;

 private AgeClassPlug targetClass;

 public CustomObjectAgeAttributeClassImpl(String name2, ContextSemanticModel sm, AgeClass owner2)
 {
  super(name2, sm, owner2);
 }

 
 public DataType getDataType()
 {
  return DataType.OBJECT;
 }


 @Override
 public void setDataType(DataType typ)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public void setTargetClass(AgeClass cls)
 {
  targetClass=getSemanticModel().getAgeClassPlug(cls);
 }

 public AgeClass getTargetClass()
 {
  return targetClass!=null?targetClass.getAgeClass():null;
 }

}

