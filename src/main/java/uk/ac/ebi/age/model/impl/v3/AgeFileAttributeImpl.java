package uk.ac.ebi.age.model.impl.v3;

import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class AgeFileAttributeImpl extends AgeStringAttributeImpl implements AgeFileAttributeWritable
{
 private static final long serialVersionUID = 3L;

 private transient String fileSysRef;

 public AgeFileAttributeImpl(AttributeClassRef attrClass, AttributedWritable host)
 {
  super(attrClass, host);
 }

 @Override
 public String getFileId()
 {
  return (String)super.getValue();
 }

 @Override
 public String getFileSysRef()
 {
  return fileSysRef;
 }

 @Override
 public void setFileId(String fRef)
 {
  super.setValue(fRef);
 }

 @Override
 public void setFileSysRef(String fId)
 {
  fileSysRef = fId;
 }

}
