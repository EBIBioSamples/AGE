package uk.ac.ebi.age.model;

public interface AgeFileAttribute extends AgeAttribute
{
 String getFileId();
 ResolveScope getTargetResolveScope();

 String getFileSysRef();
}
