package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;

import uk.ac.ebi.age.ext.submission.AttachmentDiff;
import uk.ac.ebi.age.ext.submission.Status;

public class AttachmentDiffImpl implements Serializable, AttachmentDiff
{

 private static final long serialVersionUID = 1L;

 private String  id;
 private Status  status;

 private boolean metaChanged;
 private boolean dataChanged;
 private boolean visibilityChanged;
 
 private long oldFileVersion;
 private long newFileVersion;
 
 private String description;
 private long creationTime;
 private long modificationTime;
 private String creator;
 private String modifier;
 private boolean global;
 
 AttachmentDiffImpl()
 {}
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#getId()
  */
 @Override
 public String getId()
 {
  return id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setId(java.lang.String)
  */
 @Override
 public void setId(String id)
 {
  this.id = id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#getStatus()
  */
 @Override
 public Status getStatus()
 {
  return status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setStatus(uk.ac.ebi.age.ext.submission.Status)
  */
 @Override
 public void setStatus(Status status)
 {
  this.status = status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#isMetaChanged()
  */
 @Override
 public boolean isMetaChanged()
 {
  return metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setMetaChanged(boolean)
  */
 @Override
 public void setMetaChanged(boolean metaChanged)
 {
  this.metaChanged = metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#isDataChanged()
  */
 @Override
 public boolean isDataChanged()
 {
  return dataChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setDataChanged(boolean)
  */
 @Override
 public void setDataChanged(boolean dataChanged)
 {
  this.dataChanged = dataChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#isVisibilityChanged()
  */
 @Override
 public boolean isVisibilityChanged()
 {
  return visibilityChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setVisibilityChanged(boolean)
  */
 @Override
 public void setVisibilityChanged(boolean visibilityChanged)
 {
  this.visibilityChanged = visibilityChanged;
 }

 @Override
 public long getOldFileVersion()
 {
  return oldFileVersion;
 }

 @Override
 public void setOldFileVersion(long oldFileVersion)
 {
  this.oldFileVersion = oldFileVersion;
 }

 @Override
 public long getNewFileVersion()
 {
  return newFileVersion;
 }

 @Override
 public void setNewFileVersion(long newFileVersion)
 {
  this.newFileVersion = newFileVersion;
 }

 public String getDescription()
 {
  return description;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 public long getCreationTime()
 {
  return creationTime;
 }

 public void setCreationTime(long creationTime)
 {
  this.creationTime = creationTime;
 }

 public long getModificationTime()
 {
  return modificationTime;
 }

 public void setModificationTime(long modificationTime)
 {
  this.modificationTime = modificationTime;
 }

 public String getCreator()
 {
  return creator;
 }

 public void setCreator(String creator)
 {
  this.creator = creator;
 }

 public String getModifier()
 {
  return modifier;
 }

 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }

 public boolean isGlobal()
 {
  return global;
 }

 public void setGlobal(boolean global)
 {
  this.global = global;
 }

}
