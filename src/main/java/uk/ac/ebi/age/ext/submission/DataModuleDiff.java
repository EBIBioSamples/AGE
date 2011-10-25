package uk.ac.ebi.age.ext.submission;


public interface DataModuleDiff
{

 String getId();

 void setId(String id);

 Status getStatus();

 void setStatus(Status status);

 boolean isMetaChanged();

 void setMetaChanged(boolean metaChanged);

 boolean isDataChanged();

 void setDataChanged(boolean dataChanged);

 void setOldDocumentVersion(long oldDocumentVersion);

 long getOldDocumentVersion();

 long getNewDocumentVersion();

 void setNewDocumentVersion(long newDocumentVersion);

 String getDescription();
 void setDescription(String s);

 long getCreationTime();
 void setCreationTime(long t);

 long getModificationTime();
 void setModificationTime(long t);

 String getCreator();
 void setCreator(String s);

 String getModifier();
 void setModifier(String s);

}