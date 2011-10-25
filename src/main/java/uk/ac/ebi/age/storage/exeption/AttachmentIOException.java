package uk.ac.ebi.age.storage.exeption;

import java.io.IOException;

public class AttachmentIOException extends IOException
{

 private static final long serialVersionUID = 1L;

 public AttachmentIOException( String msg )
 {
  super(msg);
 }
 
 public AttachmentIOException( String msg, Throwable t )
 {
  super(msg, t);
 }
}
