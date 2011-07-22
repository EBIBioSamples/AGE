package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

class AgeBooleanAttributeImpl extends AgeAttributeImpl 
{
 private static final long serialVersionUID = 1L;

 private boolean value; 

 protected AgeBooleanAttributeImpl()
 {}
 
 public AgeBooleanAttributeImpl(AgeAttributeClass attrClass, AttributedObject host)
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

  value = "yes".equalsIgnoreCase(val) || "1".equals(val) || "true".equalsIgnoreCase(val);

 }

 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public boolean getValueAsBoolean()
 {
  return value;
 }

 @Override
 public double getValueAsDouble()
 {
  return value?1:0;
 }

 @Override
 public int getValueAsInteger()
 {
  return value?1:0;
 }

 @Override
 public void setBooleanValue(boolean boolValue)
 {
  value=boolValue;
 }

 @Override
 public void setDoubleValue(double doubleValue)
 {
  value = doubleValue != 0;
 }

 @Override
 public void setIntValue(int intValue)
 {
  value = intValue != 0;
 }

 @Override
 public void setValue(Object val)
 {
  if( val instanceof Boolean )
   value=((Boolean)val).booleanValue();
  else if( val instanceof Number )
   value=((Number)val).intValue() != 0;
  else
   try
   {
    value=Boolean.parseBoolean(val.toString());
   }
   catch(Exception e)
   {
   }
 }

 @Override
 public AgeAttributeWritable createClone()
 {
  AgeBooleanAttributeImpl clone  = new AgeBooleanAttributeImpl(getAgeAttributeClass(), getSemanticModel());
  clone.value=this.value;
  
  clone.setOrder( getOrder() );
  
  cloneAttributes( clone );

  return clone;
 }

 public boolean equals( Object ob )
 {
  if( ! (ob instanceof AgeAttribute) )
   return false;
  
   return value == ((AgeAttribute)ob).getValueAsBoolean();
 }

 @Override
 public int compareTo(AgeAttribute o)
 {
  return value==o.getValueAsBoolean()? 0 : value?1:-1;
 }


}
