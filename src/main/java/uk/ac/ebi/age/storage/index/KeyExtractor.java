package uk.ac.ebi.age.storage.index;

import uk.ac.ebi.age.model.AgeObject;

public interface KeyExtractor<KeyT>
{
 KeyT extractKey(AgeObject o1);
 void recycleKey( KeyT k );
}
