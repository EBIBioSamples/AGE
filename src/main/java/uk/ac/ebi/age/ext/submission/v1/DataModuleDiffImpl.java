package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;

import uk.ac.ebi.age.ext.submission.DataModuleDiff;
import uk.ac.ebi.age.ext.submission.Status;

public class DataModuleDiffImpl implements Serializable, DataModuleDiff
{

 private static final long serialVersionUID = 1L;

 private String id;
 private Status status;
 
 private boolean metaChanged;
 private boolean dataChanged;
 
 private long oldDocumentVersion;
 private long newDocumentVersion;
 
 private String description;
 private long creationTime;
 private long modificationTime;
 private String creator;
 private String modifier;
 
 DataModuleDiffImpl()
 {}
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#getId()
  */
 @Override
 public String getId()
 {
  return id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setId(java.lang.String)
  */
 @Override
 public void setId(String id)
 {
  this.id = id;
 }
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#getStatus()
  */
 @Override
 public Status getStatus()
 {
  return status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setStatus(uk.ac.ebi.age.ext.submission.Status)
  */
 @Override
 public void setStatus(Status status)
 {
  this.status = status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#isMetaChanged()
  */
 @Override
 public boolean isMetaChanged()
 {
  return metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setMetaChanged(boolean)
  */
 @Override
 public void setMetaChanged(boolean metaChanged)
 {
  this.metaChanged = metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#isDataChanged()
  */
 @Override
 public boolean isDataChanged()
 {
  return dataChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setDataChanged(boolean)
  */
 @Override
 public void setDataChanged(boolean dataChanged)
 {
  this.dataChanged = dataChanged;
 }

 @Override
 public void setOldDocumentVersion(long oldDocumentVersion)
 {
  this.oldDocumentVersion = oldDocumentVersion;
 }

 @Override
 public long getOldDocumentVersion()
 {
  return oldDocumentVersion;
 }

 @Override
 public long getNewDocumentVersion()
 {
  return newDocumentVersion;
 }

 @Override
 public void setNewDocumentVersion(long newDocumentVersion)
 {
  this.newDocumentVersion = newDocumentVersion;
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
}
