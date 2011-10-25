package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.ext.submission.DataModuleMeta;
import uk.ac.ebi.age.ext.submission.FileAttachmentMeta;
import uk.ac.ebi.age.ext.submission.Status;
import uk.ac.ebi.age.ext.submission.SubmissionMeta;


public class SubmissionMetaImpl implements Serializable, SubmissionMeta
{
 private static final long serialVersionUID = 1L;

 private String id;
 
 private String description;
 private String submitter;
 private String modifier;
 
 private List<TagRef> tags;
 
 private long submissionTime;
 private long modificationTime;
 
 private List<DataModuleMeta> mods = new ArrayList<DataModuleMeta>(3);
 private List<FileAttachmentMeta> atts ;
 
 private boolean removed;

 SubmissionMetaImpl()
 {}

 private Status status;
 
 @Override
 public String getSubmitter()
 {
  return submitter;
 }

 @Override
 public void setSubmitter(String submitter)
 {
  this.submitter = submitter;
 }

 @Override
 public String getModifier()
 {
  return modifier;
 }

 @Override
 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }

 @Override
 public long getSubmissionTime()
 {
  return submissionTime;
 }

 @Override
 public void setSubmissionTime(long submissionTime)
 {
  this.submissionTime = submissionTime;
 }

 @Override
 public long getModificationTime()
 {
  return modificationTime;
 }

 @Override
 public void setModificationTime(long modificationTime)
 {
  this.modificationTime = modificationTime;
 }


 @Override
 public String getDescription()
 {
  return description;
 }

 @Override
 public void setDescription(String string)
 {
  description = string;
 }

 @Override
 public String getId()
 {
  return id;
 }

 @Override
 public void setId(String id)
 {
  this.id = id;
 }

 @Override
 public void addDataModule(DataModuleMeta dmMeta)
 {
  mods.add(dmMeta);
 }

 @Override
 public List<DataModuleMeta> getDataModules()
 {
  return mods;
 }

 @Override
 public void addAttachment(FileAttachmentMeta fAtMeta)
 {
  if( atts == null )
   atts = new ArrayList<FileAttachmentMeta>(3);

  atts.add( fAtMeta );
 }

 @Override
 public List<FileAttachmentMeta> getAttachments()
 {
  return atts;
 }

 @Override
 public void setStatus(Status st)
 {
  status = st;
 }

 @Override
 public Status getStatus()
 {
  return status;
 }

 @Override
 public boolean isRemoved()
 {
  return removed;
 }

 @Override
 public void setRemoved(boolean removed)
 {
  this.removed = removed;
 }

 @Override
 public List<TagRef> getTags()
 {
  return tags;
 }

 @Override
 public void setTags(List<TagRef> tgs)
 {
  tags=tgs;
 }

}
