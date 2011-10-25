package uk.ac.ebi.age.ext.submission;

import java.util.Collection;

public interface SubmissionDiff
{

 void setId(String id2);

 String getId();

 long getModificationTime();

 void setModificationTime(long modificationTime);

 String getCreator();
 void setCreator(String crtr);
 
 String getModifier();
 void setModifier(String modifier);

 boolean isDescriptionChanged();

 void setDescriptionChanged(boolean descriptionChanged);

 void addDataModuleDiff(DataModuleDiff mdif);
 Collection<DataModuleDiff> getDataModuleDiffs();

 void addAttachmentDiff(AttachmentDiff adif);
 Collection<AttachmentDiff> getAttachmentDiffs();

 String getDescription();
 void setDescription( String d);

 long getCreationTime();
 void setCreationTime( long t );

}