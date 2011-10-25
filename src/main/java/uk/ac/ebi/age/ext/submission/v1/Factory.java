package uk.ac.ebi.age.ext.submission.v1;

import uk.ac.ebi.age.ext.submission.AttachmentDiff;
import uk.ac.ebi.age.ext.submission.DataModuleDiff;
import uk.ac.ebi.age.ext.submission.DataModuleMeta;
import uk.ac.ebi.age.ext.submission.FileAttachmentMeta;
import uk.ac.ebi.age.ext.submission.HistoryEntry;
import uk.ac.ebi.age.ext.submission.SubmissionDiff;
import uk.ac.ebi.age.ext.submission.SubmissionMeta;

public class Factory
{
 public static AttachmentDiff createAttachmentDiff()
 {
  return new AttachmentDiffImpl();
 }
 
 public static DataModuleDiff createDataModuleDiff()
 {
  return new DataModuleDiffImpl();
 }
 
 public static SubmissionDiff createSubmissionDiff()
 {
  return new SubmissionDiffImpl();
 }

 
 public static DataModuleMeta createDataModuleMeta()
 {
  return new DataModuleMetaImpl();
 }

 public static SubmissionMeta createSubmissionMeta()
 {
  return new SubmissionMetaImpl();
 }

 public static FileAttachmentMeta createFileAttachmentMeta()
 {
  return new FileAttachmentMetaImpl();
 }
 
 public static HistoryEntry createHistoryEntry()
 {
  return new HistoryEntryImpl();
 }

}
