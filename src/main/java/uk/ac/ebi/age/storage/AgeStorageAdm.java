package uk.ac.ebi.age.storage;

import java.io.File;
import java.util.Collection;

import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.exeption.AttachmentIOException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;

public interface AgeStorageAdm extends AgeStorage
{
 public AgeObjectWritable getGlobalObject(String objID);
 public AgeObjectWritable getClusterObject(String clustId, String objID);
 public Collection<? extends AgeObjectWritable> getAllObjects();

 
// void storeDataModule(DataModuleWritable sbm) throws RelationResolveException, ModuleStoreException;
 void update(Collection<DataModuleWritable> modListToIns, Collection<ModuleKey> modListToDel) throws RelationResolveException, ModuleStoreException;
// void removeDataModule(Collection<String> ids);

 boolean updateSemanticModel( SemanticModel sm, LogNode log ); // throws ModelStoreException;

 void init( String initStr) throws StorageInstantiationException;
 void shutdown();

 void lockWrite();
 void unlockWrite();

// void addRelations(String key, Collection<AgeRelationWritable> value);
// void removeRelations(String id, Collection<AgeRelationWritable> value);

 DataModuleWritable getDataModule(String clstId, String name);
 Collection<? extends DataModuleWritable> getDataModules();

 void setMaster(boolean master);

// String makeGlobalFileID(String id);
// String makeLocalFileID(String id, String clustID);
 
 boolean deleteAttachment(String id, String clusterId, boolean global);
 File storeAttachment(String id, String clusterId, boolean global, File aux) throws AttachmentIOException;
 void changeAttachmentScope(String id, String clusterId, boolean global) throws AttachmentIOException;

 String makeFileSysRef(String id);
 String makeFileSysRef(String id, String clustID);

 public void rebuildIndices();

 
 //void renameAttachment(String id, String id2) throws AttachmentIOException;

}
