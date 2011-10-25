package uk.ac.ebi.age.ext.submission;

public class Factory
{
 public static AttachmentDiff createAttachmentDiff()
 {
  return uk.ac.ebi.age.ext.submission.v1.Factory.createAttachmentDiff();
 }
 
 public static DataModuleDiff createDataModuleDiff()
 {
  return uk.ac.ebi.age.ext.submission.v1.Factory.createDataModuleDiff();
 }
 
 public static SubmissionDiff createSubmissionDiff()
 {
  return uk.ac.ebi.age.ext.submission.v1.Factory.createSubmissionDiff();
 }

 
 public static DataModuleMeta createDataModuleMeta()
 {
  return uk.ac.ebi.age.ext.submission.v1.Factory.createDataModuleMeta();
 }

 public static SubmissionMeta createSubmissionMeta()
 {
  return uk.ac.ebi.age.ext.submission.v1.Factory.createSubmissionMeta();
 }

 public static FileAttachmentMeta createFileAttachmentMeta()
 {
  return uk.ac.ebi.age.ext.submission.v1.Factory.createFileAttachmentMeta();
 }
 
 public static HistoryEntry createHistoryEntry()
 {
  return uk.ac.ebi.age.ext.submission.v1.Factory.createHistoryEntry();
 }
}
