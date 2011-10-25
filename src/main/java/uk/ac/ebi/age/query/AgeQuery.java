package uk.ac.ebi.age.query;

public class AgeQuery
{
 private QueryExpression expr;
 
 private AgeQuery()
 {}
 
 public void setQueryExpression(QueryExpression exp)
 {
  expr=exp;
 }

 public QueryExpression getExpression()
 {
  return expr;
 }

 public static AgeQuery create( QueryExpression expr )
 {
  AgeQuery q = new AgeQuery();
  q.setQueryExpression(expr);
  
  return q;
 }

}
