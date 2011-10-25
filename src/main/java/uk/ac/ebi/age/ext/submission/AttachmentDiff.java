package uk.ac.ebi.age.ext.submission;


public interface AttachmentDiff
{

 String getId();

 void setId(String id);

 Status getStatus();

 void setStatus(Status status);

 boolean isMetaChanged();

 void setMetaChanged(boolean metaChanged);

 boolean isDataChanged();

 void setDataChanged(boolean dataChanged);

 boolean isVisibilityChanged();

 void setVisibilityChanged(boolean visibilityChanged);

 long getOldFileVersion();

 void setOldFileVersion(long oldFileVersion);

 long getNewFileVersion();

 void setNewFileVersion(long newFileVersion);

 String getDescription();
 void setDescription(String s);

 String getCreator();
 void setCreator(String s);

 String getModifier();
 void setModifier(String s);

 long getCreationTime();
 void setCreationTime(long t);

 long getModificationTime();
 void setModificationTime(long t);
 
 boolean isGlobal();
 void setGlobal( boolean glb);
}