package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeObjectAttribute;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectAttributeWritable;

class AgeObjectAttributeImpl extends AgeAttributeImpl implements AgeObjectAttributeWritable
{
 private static final long serialVersionUID = 1L;
 
 private AgeObject value; 

 public AgeObjectAttributeImpl(AgeAttributeClass attrClass, SemanticModel sm)
 {
  super(attrClass, sm);
 }

 public AgeObject getValue()
 {
  return value;
 }

 public void updateValue(String val) throws FormatException
 {
  throw new UnsupportedOperationException();
 }

 public void finalizeValue()
 {
 }
 

 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public boolean getValueAsBoolean()
 {
  return false;
 }

 @Override
 public double getValueAsDouble()
 {
  return 0;
 }

 @Override
 public int getValueAsInteger()
 {
  return 0;
 }

 @Override
 public void setBooleanValue(boolean boolValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setDoubleValue(double doubleValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setIntValue(int intValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setValue(Object val)
 {
  if( val instanceof AgeObject )
   value=(AgeObject)val;
 }
 
 @Override
 public void setValue( AgeObject val)
 {
  value=val;
 }
 
 @Override
 public AgeAttributeWritable createClone()
 {
  AgeObjectAttributeImpl clone  = new AgeObjectAttributeImpl(getAgeAttributeClass(), getSemanticModel());
  clone.value=this.value;
  
  return clone;
 }
 
 public boolean equals( Object ob )
 {
  if( ! (ob instanceof AgeAttribute) )
   return false;
  
   return value.equals( ((AgeAttribute)ob).getValue() );
 }

 @Override
 public int compareTo( AgeAttribute ob )
 {
  if( ! (ob instanceof AgeObjectAttribute) )
   return 1;

  
  return value.getId().compareTo( ((AgeObjectAttribute)ob).getValue().getId() );
 }


}
