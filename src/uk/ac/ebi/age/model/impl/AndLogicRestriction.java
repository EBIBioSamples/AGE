package uk.ac.ebi.age.model.impl;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.RestrictionException;

public class AndLogicRestriction implements AgeRestriction
{
 private Collection<AgeRestriction> operands;

 public AndLogicRestriction(Collection<AgeRestriction> operands)
 {
  this.operands=operands;
 }

 public String toString()
 {
  StringBuilder sb = new StringBuilder(1000);
  
  sb.append("AndLogic restriction:\n");
  
  boolean first=true;
  
  for( AgeRestriction rst : operands )
  {
   if( first )
   {
    first=false;
    sb.append("  ");
   }
   else
    sb.append("AND\n  ");
   
   sb.append(rst.toString()).append("\n");
  }
  
  sb.append("End of AndLogic restriction");
  
  return sb.toString();
 }

 public void validate(AgeAbstractObject obj) throws RestrictionException
 {
  try
  {
   for(AgeRestriction rstr : operands)
   {
    rstr.validate(obj);
   }
  }
  catch(RestrictionException e)
  {
   throw new RestrictionException("The AND expression failed: " + e.getMessage(), e);
  }
 }

}
