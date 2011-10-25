package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;
import java.util.List;

import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.ext.submission.DataModuleMeta;

public class DataModuleMetaImpl implements Serializable, DataModuleMeta
{
 private static final long serialVersionUID = 1L;

 private String id;
 private String description;
 private long ctime;
 private long mtime;
 private String submitter;
 private String modifier;
 private long docVersion;
 private List<TagRef> tags;
 
 private transient String text;
 private transient Object aux;
 
 
 
 DataModuleMetaImpl()
 {}
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setId(java.lang.String)
  */
 @Override
 public void setId(String stringId)
 {
  id = stringId;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getDescription()
  */
 @Override
 public String getDescription()
 {
  return description;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setDescription(java.lang.String)
  */
 @Override
 public void setDescription(String description)
 {
  this.description = description;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getId()
  */
 @Override
 public String getId()
 {
  return id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setModificationTime(long)
  */
 @Override
 public void setModificationTime(long time)
 {
  mtime = time;
 }
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getModificationTime()
  */
 @Override
 public long getModificationTime()
 {
  return mtime;
 }
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getSubmitter()
  */
 @Override
 public String getSubmitter()
 {
  return submitter;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setSubmitter(java.lang.String)
  */
 @Override
 public void setSubmitter(String submitter)
 {
  this.submitter = submitter;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getModifier()
  */
 @Override
 public String getModifier()
 {
  return modifier;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setModifier(java.lang.String)
  */
 @Override
 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getText()
  */
 @Override
 public String getText()
 {
  return text;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setText(java.lang.String)
  */
 @Override
 public void setText(String text)
 {
  this.text = text;
 }


 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setAux(java.lang.Object)
  */
 @Override
 public void setAux(Object aux)
 {
  this.aux = aux;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getAux()
  */
 @Override
 public Object getAux()
 {
  return aux;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setSubmissionTime(long)
  */
 @Override
 public void setSubmissionTime(long time)
 {
  ctime = time;
 }
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getSubmissionTime()
  */
 @Override
 public long getSubmissionTime()
 {
  return ctime;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#getDocVersion()
  */
 @Override
 public long getDocVersion()
 {
  return docVersion;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleMeta#setDocVersion(long)
  */
 @Override
 public void setDocVersion(long docVersion)
 {
  this.docVersion = docVersion;
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
