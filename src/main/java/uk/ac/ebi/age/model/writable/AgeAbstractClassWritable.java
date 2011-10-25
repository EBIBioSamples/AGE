package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAnnotation;

public interface AgeAbstractClassWritable extends AgeAbstractClass
{

 void addAnnotation(AgeAnnotation ant);

}
