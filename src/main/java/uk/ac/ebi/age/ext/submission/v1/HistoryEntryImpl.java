package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;

import uk.ac.ebi.age.ext.submission.HistoryEntry;
import uk.ac.ebi.age.ext.submission.SubmissionDiff;

public class HistoryEntryImpl implements Serializable, HistoryEntry
{

 private static final long serialVersionUID = 1L;

 private long modificationTime;
 private String modifier;
 private String description;
 
 private SubmissionDiff diff;

 
 HistoryEntryImpl()
 {}
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.HistoryEntry#getModificationTime()
  */
 @Override
 public long getModificationTime()
 {
  return modificationTime;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.HistoryEntry#setModificationTime(long)
  */
 @Override
 public void setModificationTime(long modificationTime)
 {
  this.modificationTime = modificationTime;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.HistoryEntry#getModifier()
  */
 @Override
 public String getModifier()
 {
  return modifier;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.HistoryEntry#setModifier(java.lang.String)
  */
 @Override
 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.HistoryEntry#getDescription()
  */
 @Override
 public String getDescription()
 {
  return description;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.HistoryEntry#setDescription(java.lang.String)
  */
 @Override
 public void setDescription(String description)
 {
  this.description = description;
 }

 @Override
 public SubmissionDiff getDiff()
 {
  return diff;
 }

 @Override
 public void setDiff(SubmissionDiff diff)
 {
  this.diff = diff;
 }


}
