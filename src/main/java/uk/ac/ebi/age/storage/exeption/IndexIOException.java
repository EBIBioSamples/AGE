package uk.ac.ebi.age.storage.exeption;

import java.io.IOException;

public class IndexIOException extends IOException
{

 private static final long serialVersionUID = 1L;

 public IndexIOException( String msg )
 {
  super(msg);
 }
 
 public IndexIOException( String msg, Throwable t )
 {
  super(msg, t);
 }
}
