package uk.ac.ebi.age.query;

import uk.ac.ebi.age.model.AgeObject;

public interface QueryExpression
{

 boolean test(AgeObject obj);

 boolean isTestingRelations();

}
