package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeObjectAttribute;

public interface AgeObjectAttributeWritable extends AgeObjectAttribute, AgeAttributeWritable
{
 void setValue( AgeObject val );
}
