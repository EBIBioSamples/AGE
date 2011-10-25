package uk.ac.ebi.age.authz;

import java.util.Collection;

import uk.ac.ebi.mg.collection.Named;

public interface Classifier extends Named<String>
{

 String getId();

 String getDescription();

 Tag getTag(String tagId);

 Collection<? extends Tag> getTags();
}
