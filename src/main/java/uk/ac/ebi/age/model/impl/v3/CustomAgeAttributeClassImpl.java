package uk.ac.ebi.age.model.impl.v3;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;

class CustomAgeAttributeClassImpl extends AbstractCustomAgeAttributeClassImpl
{
 private static final long serialVersionUID = 3L;
 
 
 private DataType dataType;
 
 public CustomAgeAttributeClassImpl(String name2, DataType type, ContextSemanticModel sm, AgeClass owner2)
 {
  super(name2, sm, owner2);
  dataType=type;
 }

 public DataType getDataType()
 {
  return dataType;
 }


 @Override
 public void setDataType(DataType typ)
 {
  dataType=typ;
 }

}

