package uk.ac.ebi.age.query;

import uk.ac.ebi.age.model.AgeObject;

public class TrueExpression implements QueryExpression
{

 @Override
 public boolean test(AgeObject obj)
 {
  return true;
 }

 @Override
 public boolean isTestingRelations()
 {
  return false;
 }

}
