package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.AgeNotLogicRestriction;
import uk.ac.ebi.age.model.RestrictionException;

class NotLogicRestrictionImpl implements AgeNotLogicRestriction,Serializable
{
 private static final long serialVersionUID = 1L;
 
 private AgeRestriction operand;

 public NotLogicRestrictionImpl(AgeRestriction operand)
 {
  this.operand=operand;
 }

 public String toString()
 {
  return "NotLogic restriction: NOT ("+operand.toString()+")";
 }

 public void validate(AgeAbstractObject obj) throws RestrictionException
 {
  try
  {
   operand.validate(obj);
  }
  catch(RestrictionException e)
  {
   return;
  }
  
  throw new RestrictionException("The NOT expression failed");
 }

 @Override
 public AgeRestriction getOperand()
 {
  return operand;
 }

}
