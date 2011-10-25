package uk.ac.ebi.age.model.impl.v3;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

class AgeRealAttributeImpl extends AgeAttributeImpl implements AgeAttributeWritable
{

 private static final long serialVersionUID = 3L;

 private double value; 

 public AgeRealAttributeImpl(AttributeClassRef attrClass, AttributedWritable host )
 {
  super(attrClass, host);
 }

 public Object getValue()
 {
  return value;
 }

 public void updateValue(String val) throws FormatException
 {
  val=val.trim();
  
  if( val.length() == 0 )
   return;

  try
  { 
   value=Double.parseDouble(val);
  }
  catch (NumberFormatException e) 
  {
   throw new FormatException("Invalid real format",e);
  }
 }

 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public boolean getValueAsBoolean()
 {
  return value!=0;
 }

 @Override
 public double getValueAsDouble()
 {
  return value;
 }

 @Override
 public int getValueAsInteger()
 {
  return (int)Math.round(value);
 }

 @Override
 public void setBooleanValue(boolean boolValue)
 {
  value=boolValue?1:0;
 }

 @Override
 public void setDoubleValue(double doubleValue)
 {
  value=doubleValue;
 }

 @Override
 public void setIntValue(int intValue)
 {
  value=intValue;
 }

 @Override
 public void setValue(Object val)
 {
  if( val instanceof Number )
   value=((Number)val).doubleValue();
  else try
  {
   value = Double.parseDouble(val.toString());
  }
  catch (Exception e)
  {
  }
 }
 
 @Override
 public AgeAttributeWritable createClone( AttributedWritable host )
 {
  AgeRealAttributeImpl clone  = new AgeRealAttributeImpl(getClassRef(), host);
  clone.value=this.value;
  
  cloneAttributes( clone );

  return clone;
 }
 
 public boolean equals( Object ob )
 {
  if( ! (ob instanceof AgeAttribute) )
   return false;
  
   return value == ((AgeAttribute)ob).getValueAsDouble();
 }
 
 @Override
 public int compareTo(AgeAttribute o)
 {
  return value==o.getValueAsDouble()? 0 : value-o.getValueAsDouble() > 0 ? 1 : -1;
 }
}
