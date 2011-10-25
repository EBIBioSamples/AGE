package uk.ac.ebi.age.storage.index;

import java.util.List;

import uk.ac.ebi.age.model.AgeObject;

public interface AgeIndex
{
 List<? extends AgeObject> getObjectList();
}
