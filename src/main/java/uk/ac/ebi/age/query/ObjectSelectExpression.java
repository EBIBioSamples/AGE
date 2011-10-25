package uk.ac.ebi.age.query;

import uk.ac.ebi.age.model.AgeObject;

public class ObjectSelectExpression implements QueryExpression
{

 public void setAgeClassName(String string)
 {
  // TODO Auto-generated method stub
 }

 public void setCondition(QueryExpression selExpr)
 {
  // TODO Auto-generated method stub
 }

 public boolean test(AgeObject obj)
 {
  // TODO Auto-generated method stub
  return false;
 }

 @Override
 public boolean isTestingRelations()
 {
  // TODO Auto-generated method stub
  return false;
 }

}
