package uk.ac.ebi.age.storage.index;

import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.query.AgeQuery;

public interface AgeIndexWritable extends AgeIndex
{

 AgeQuery getQuery();

 void index(List<AgeObject> res, boolean append);

// void reset();
 
}
