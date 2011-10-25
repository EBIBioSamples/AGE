package uk.ac.ebi.age.validator;

import java.util.Set;

import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.DataModule;

public interface AgeSemanticValidator
{
 boolean validate(DataModule s, LogNode log);
 boolean validate(DataModule subm, SemanticModel mod, LogNode log);

 boolean validateRelations(AgeObject obj, Set<? extends AgeRelation> newRels, Set<? extends AgeRelation> remRels, LogNode log);

}
