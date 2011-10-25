package uk.ac.ebi.age.storage.exeption;

import java.util.Collection;

public class NotUniqueIdException extends ModuleStoreException
{
 private static final long serialVersionUID = 1L;

 private Collection<String> ids;
 
 public NotUniqueIdException( Collection<String> ids )
 {
  super("Identifiers are already used");
  
  this.ids = ids;
 }
 
 public Collection<String> getIdentifiers()
 {
  return ids;
 }
}
