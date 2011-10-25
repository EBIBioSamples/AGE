package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeExternalObjectAttribute;
import uk.ac.ebi.age.model.AgeObject;

public interface AgeExternalObjectAttributeWritable extends AgeExternalObjectAttribute, AgeAttributeWritable
{
 void setTargetObject(AgeObject obj);
}
