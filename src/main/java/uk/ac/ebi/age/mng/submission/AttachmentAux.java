package uk.ac.ebi.age.mng.submission;

import java.io.File;

import uk.ac.ebi.age.ext.submission.Status;

public class AttachmentAux
{
 private int order;
 private Status status;
 private File file;
 private String newId;
 
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
 
 public File getFile()
 {
  return file;
 }
 
 public void setFile(File file)
 {
  this.file = file;
 }
 

 public void setNewId(String nid)
 {
  newId = nid;
 }

 public String getNewId()
 {
  return newId;
 }
}
