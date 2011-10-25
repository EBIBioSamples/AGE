package uk.ac.ebi.age.storage.index;

import java.util.List;

import uk.ac.ebi.age.model.AgeObject;

public interface TextIndex extends AgeIndex
{
 List<AgeObject> select(String query);

 int count(String query);


}
