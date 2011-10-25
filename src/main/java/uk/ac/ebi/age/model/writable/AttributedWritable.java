package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.Attributed;


public interface AttributedWritable extends Attributed
{
// AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass);
 AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass);
// AgeAttributeWritable createExternalObjectAttribute(String val, AgeAttributeClass attrClass);
 AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef attrClass, String val );

 
 Collection<? extends AgeAttributeWritable> getAttributes();

// Collection< ? extends AgeAttributeWritable> getAttributes(AgeAttributeClass cls);

 Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls);


 void addAttribute(AgeAttributeWritable attr);

 void removeAttribute(AgeAttributeWritable attr);

 void reset();
 void sortAttributes();
}