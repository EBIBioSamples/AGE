package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAnnotationClass;

public interface AgeAnnotationClassWritable extends AgeAnnotationClass, AgeAbstractClassWritable
{

 void setAbstract(boolean b);

 void addSuperClass(AgeAnnotationClassWritable cl);

 void addSubClass(AgeAnnotationClassWritable cl);

 void addAlias(String ali);

}
