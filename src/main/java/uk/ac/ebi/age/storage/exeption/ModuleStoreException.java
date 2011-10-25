package uk.ac.ebi.age.storage.exeption;


public class ModuleStoreException extends Exception
{

 private static final long serialVersionUID = 1L;

 public ModuleStoreException(String string)
 {
  super(string);
 }

 
 public ModuleStoreException(String string, Exception e)
 {
  super(string,e);
 }

}
