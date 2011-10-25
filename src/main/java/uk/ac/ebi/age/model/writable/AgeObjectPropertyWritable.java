package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeObjectProperty;

public interface AgeObjectPropertyWritable extends AgeObjectProperty
{
 Collection<? extends AgeAttributeWritable> getAttributes();
 void addAttribute(AgeAttributeWritable attr);
}
