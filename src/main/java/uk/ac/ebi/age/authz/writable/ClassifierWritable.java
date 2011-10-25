package uk.ac.ebi.age.authz.writable;

import java.util.Collection;

import uk.ac.ebi.age.authz.Classifier;

public interface ClassifierWritable extends Classifier
{

 void setId(String id);

 void setDescription(String description);

 void addTag(TagWritable tb);

 TagWritable getTag(String tagId);

 Collection<? extends TagWritable> getTags();

}