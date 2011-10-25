package uk.ac.ebi.age.storage;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.exeption.IndexIOException;
import uk.ac.ebi.age.storage.index.KeyExtractor;
import uk.ac.ebi.age.storage.index.SortedTextIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextIndex;

public interface AgeStorage
{
 Collection<AgeObject> executeQuery( AgeQuery qury );
 
// List<AgeObject> queryTextIndex(IndexID idx, String query);
// int queryTextIndexCount(IndexID idx, String query);
 
 SemanticModel getSemanticModel();
 
 void shutdown();

 public Collection<? extends AgeObject> getAllObjects();
 public AgeObject getGlobalObject(String objID);
 public AgeObject getClusterObject(String clustId, String objID);

// AgeObject getObjectById(String grpID);

// boolean hasObject(String id);
 boolean hasDataModule(String clstId, String id);
 boolean hasDataModule(ModuleKey mk);

 void addDataChangeListener(DataChangeListener dataChangeListener);
 
 File getAttachment(String id, String clustId, boolean global);
 File getAttachmentBySysRef(String sysid);
 
 DataModule getDataModule(String clstId, String name);

 Collection<? extends DataModule> getDataModules();

 boolean isFileIdGlobal(String fileID);

 TextIndex createTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> cb ) throws IndexIOException;
 public <KeyT> SortedTextIndex<KeyT> createSortedTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> exts,
   KeyExtractor<KeyT> keyExtractor, Comparator<KeyT> comparator) throws IndexIOException;

}
