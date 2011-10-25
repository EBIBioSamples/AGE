package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeAndLogicRestriction extends AgeRestriction
{
 Collection<AgeRestriction> getOperands();
}
