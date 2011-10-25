package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeOrLogicRestriction extends AgeRestriction
{
 Collection<AgeRestriction> getOperands();
}
