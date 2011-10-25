package uk.ac.ebi.age.ext.submission;

public interface HistoryEntry
{

 long getModificationTime();

 void setModificationTime(long modificationTime);

 String getModifier();

 void setModifier(String modifier);

 String getDescription();

 void setDescription(String description);

 SubmissionDiff getDiff();

 void setDiff(SubmissionDiff diff);

}