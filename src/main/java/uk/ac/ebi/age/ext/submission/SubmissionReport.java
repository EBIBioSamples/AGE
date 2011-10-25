package uk.ac.ebi.age.ext.submission;

import java.io.Serializable;
import java.util.List;


public class SubmissionReport implements Serializable
{

 private static final long serialVersionUID = 1L;

 private List<SubmissionMeta> objects;
 private int totalRecords;
 private int totalSamples;
 private int totalMatchedSamples=-1;
 
 public List<SubmissionMeta> getSubmissions()
 {
  return objects;
 }
 
 public void setSubmissions(List<SubmissionMeta> objects)
 {
  this.objects = objects;
 }
 
 public int getTotalSubmissions()
 {
  return totalRecords;
 }
 
 public void setTotalSubmissions(int totalRecords)
 {
  this.totalRecords = totalRecords;
 }

 public int getTotalModules()
 {
  return totalSamples;
 }

 public void setTotalModules(int totalSamples)
 {
  this.totalSamples = totalSamples;
 }

 public int getTotalMatchedModules()
 {
  return totalMatchedSamples;
 }

 public void setTotalMatchedModules(int totalMatchedSamples)
 {
  this.totalMatchedSamples = totalMatchedSamples;
 }

}
