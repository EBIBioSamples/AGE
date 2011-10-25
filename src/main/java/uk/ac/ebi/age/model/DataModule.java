package uk.ac.ebi.age.model;

import java.util.Collection;

import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;


public interface DataModule extends Entity
{
 public static interface AttributedSelector
 {
  boolean select( Attributed at );
 }
 
 String getId();
// long getVersion();
 String getClusterId();

 Collection<? extends AgeObject> getObjects();
 
// String getDescription();
 
 ContextSemanticModel getContextSemanticModel();
 
 Collection<? extends AgeExternalRelation> getExternalRelations();
 Collection<? extends AgeExternalObjectAttribute> getExternalObjectAttributes();
 Collection<AgeFileAttributeWritable> getFileAttributes();

 Collection<? extends Attributed> getAttributed( AttributedSelector sel );
}