package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;
import java.util.List;

import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.ext.submission.FileAttachmentMeta;


public class FileAttachmentMetaImpl implements Serializable, FileAttachmentMeta
{
 private static final long serialVersionUID = 1L;

 private String id;
// private String originalId;
 private String description;
 private boolean global;
 private transient Object aux;

 private long submissionTime;
 private long modificationTime;
 
 private String submitter;
 private String modifier;

 private List<TagRef> tags;

 private long fileVersion;
 
 FileAttachmentMetaImpl()
 {}
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getId()
  */
 @Override
 public String getId()
 {
  return id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setId(java.lang.String)
  */
 @Override
 public void setId(String id)
 {
  this.id = id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getDescription()
  */
 @Override
 public String getDescription()
 {
  return description;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setDescription(java.lang.String)
  */
 @Override
 public void setDescription(String description)
 {
  this.description = description;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setGlobal(boolean)
  */
 @Override
 public void setGlobal(boolean b)
 {
  global=b;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#isGlobal()
  */
 @Override
 public boolean isGlobal()
 {
  return global;
 }

// public String getOriginalId()
// {
//  return originalId;
// }
//
// public void setOriginalId(String originalId)
// {
//  this.originalId = originalId;
// }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getAux()
  */
 @Override
 public Object getAux()
 {
  return aux;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setAux(java.lang.Object)
  */
 @Override
 public void setAux(Object aux)
 {
  this.aux = aux;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getSubmissionTime()
  */
 @Override
 public long getSubmissionTime()
 {
  return submissionTime;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setSubmissionTime(long)
  */
 @Override
 public void setSubmissionTime(long submissionTime)
 {
  this.submissionTime = submissionTime;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getModificationTime()
  */
 @Override
 public long getModificationTime()
 {
  return modificationTime;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setModificationTime(long)
  */
 @Override
 public void setModificationTime(long modificationTime)
 {
  this.modificationTime = modificationTime;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getSubmitter()
  */
 @Override
 public String getSubmitter()
 {
  return submitter;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setSubmitter(java.lang.String)
  */
 @Override
 public void setSubmitter(String submitter)
 {
  this.submitter = submitter;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getModifier()
  */
 @Override
 public String getModifier()
 {
  return modifier;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setModifier(java.lang.String)
  */
 @Override
 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }


 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#getFileVersion()
  */
 @Override
 public long getFileVersion()
 {
  return fileVersion;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.FileAttachmentMeta#setFileVersion(long)
  */
 @Override
 public void setFileVersion(long fileVersion)
 {
  this.fileVersion = fileVersion;
 }

 @Override
 public List<TagRef> getTags()
 {
  return tags;
 }

 @Override
 public void setTags(List<TagRef> tags)
 {
  this.tags = tags;
 }

}
