package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.AgeAndLogicRestriction;
import uk.ac.ebi.age.model.RestrictionException;

class AndLogicRestrictionImpl implements AgeAndLogicRestriction,Serializable
{
 private static final long serialVersionUID = 1L;
 
 private Collection<AgeRestriction> operands;

 public AndLogicRestrictionImpl(Collection<AgeRestriction> operands)
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

 @Override
 public Collection<AgeRestriction> getOperands()
 {
  return operands;
 }

}
