package uk.ac.ebi.age.storage.index;

import uk.ac.ebi.age.model.AgeObject;

public interface SortedTextIndex<KeyT> extends TextIndex
{
 AgeObject getAgeObject(KeyT key);
}
