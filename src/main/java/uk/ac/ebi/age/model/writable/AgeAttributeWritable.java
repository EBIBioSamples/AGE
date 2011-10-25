package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.FormatException;

public interface AgeAttributeWritable extends AgeAttribute, AttributedWritable, AgeObjectPropertyWritable
{
 void updateValue(String value) throws FormatException;

 void finalizeValue();

 void setValue( Object val );
 
 void setBooleanValue(boolean boolValue);
 void setIntValue(int intValue);
 void setDoubleValue(double doubleValue);

 AgeAttributeWritable createClone(AttributedWritable hst);

// void setHostObject( AttributedObject hst );
}
