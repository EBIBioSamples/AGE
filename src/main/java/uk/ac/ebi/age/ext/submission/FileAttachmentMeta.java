package uk.ac.ebi.age.ext.submission;

import java.util.List;

import uk.ac.ebi.age.ext.authz.TagRef;

public interface FileAttachmentMeta
{

 String getId();

 void setId(String id);

 String getDescription();

 void setDescription(String description);

 void setGlobal(boolean b);

 boolean isGlobal();

 Object getAux();

 void setAux(Object aux);

 long getSubmissionTime();

 void setSubmissionTime(long submissionTime);

 long getModificationTime();

 void setModificationTime(long modificationTime);

 String getSubmitter();

 void setSubmitter(String submitter);

 String getModifier();

 void setModifier(String modifier);

// void setSystemId(String gid);
//
// String getSystemId();

 long getFileVersion();

 void setFileVersion(long fileVersion);

 List<TagRef> getTags( );
 void setTags(List<TagRef> annotation);

}