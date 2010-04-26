package uk.ac.ebi.age.storage;

import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.storage.exeption.ModelStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;

public interface AgeStorageAdm extends AgeStorage
{
 String storeSubmission(SubmissionWritable sbm) throws StoreException;
 
 void updateSemanticModel( SemanticModel sm ) throws ModelStoreException;

 void init( String initStr) throws StorageInstantiationException;
 void shutdown();

}
