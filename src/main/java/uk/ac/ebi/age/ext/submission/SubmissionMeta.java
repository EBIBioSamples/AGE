package uk.ac.ebi.age.ext.submission;

import java.util.List;

import uk.ac.ebi.age.ext.authz.TagRef;


public interface SubmissionMeta
{

 String getSubmitter();

 void setSubmitter(String submitter);

 String getModifier();

 void setModifier(String modifier);

 long getSubmissionTime();

 void setSubmissionTime(long submissionTime);

 long getModificationTime();

 void setModificationTime(long modificationTime);

 String getDescription();

 void setDescription(String string);

 String getId();

 void setId(String id);

 void addDataModule(DataModuleMeta dmMeta);

 List<DataModuleMeta> getDataModules();

 void addAttachment(FileAttachmentMeta fAtMeta);

 List<FileAttachmentMeta> getAttachments();

 void setStatus(Status st);

 Status getStatus();

 void setRemoved(boolean rm);
 boolean isRemoved();

 void setTags( List<TagRef> tgs );
 List<TagRef> getTags();
}