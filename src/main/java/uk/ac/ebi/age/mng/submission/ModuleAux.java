package uk.ac.ebi.age.mng.submission;

import uk.ac.ebi.age.ext.submission.Status;

public class ModuleAux
{
 private int order;
 private Status status;
 
 public int getOrder()
 {
  return order;
 }
 
 public void setOrder(int order)
 {
  this.order = order;
 }
 
 public Status getStatus()
 {
  return status;
 }
 
 public void setStatus(Status status)
 {
  this.status = status;
 }
}
