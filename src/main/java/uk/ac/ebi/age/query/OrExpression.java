package uk.ac.ebi.age.query;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeObject;

public class OrExpression implements QueryExpression
{
 private Collection<QueryExpression> exprs = new ArrayList<QueryExpression>(5);
 
 public void addExpression(QueryExpression expr)
 {
  exprs.add(expr);
 }

 public boolean test(AgeObject obj)
 {
  for( QueryExpression expr : exprs )
  {
   if( expr.test(obj) )
    return true;
  }
  
  return false;
 }


 @Override
 public boolean isTestingRelations()
 {
  for( QueryExpression expr : exprs )
  {
   if( expr.isTestingRelations() )
    return true;
  }
  
  return false;
 }

}
