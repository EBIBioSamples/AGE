package uk.ac.ebi.age.ext.submission;

public interface DataModuleMeta
{

 void setId(String stringId);

 String getDescription();

 void setDescription(String description);

 String getId();

 void setModificationTime(long time);

 long getModificationTime();

 String getSubmitter();

 void setSubmitter(String submitter);

 String getModifier();

 void setModifier(String modifier);

 String getText();

 void setText(String text);

 void setAux(Object aux);

 Object getAux();

 void setSubmissionTime(long time);

 long getSubmissionTime();

 long getDocVersion();

 void setDocVersion(long docVersion);

}