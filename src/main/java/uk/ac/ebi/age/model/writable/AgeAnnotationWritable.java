package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAnnotation;

public interface AgeAnnotationWritable extends AgeAnnotation
{
 public abstract void setText(String text);
}
