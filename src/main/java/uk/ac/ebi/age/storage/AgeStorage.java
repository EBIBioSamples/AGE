package uk.ac.ebi.age.storage;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.ModuleKey;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.exeption.IndexIOException;
import uk.ac.ebi.age.storage.index.KeyExtractor;
import uk.ac.ebi.age.storage.index.SortedTextIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextIndex;

public interface AgeStorage
{
 void lockRead();
 void unlockRead();
 
 Collection<AgeObject> executeQuery( AgeQuery qury );
 

 SemanticModel getSemanticModel();
 
 void shutdown();

 public Collection<? extends AgeObject> getAllObjects();
 public AgeObject getGlobalObject(String objID);
 public AgeObject getClusterObject(String clustId, String objID);

// boolean hasObject(String id);
 boolean hasDataModule(String clstId, String id);
 boolean hasDataModule(ModuleKey mk);

 void addDataChangeListener(DataChangeListener dataChangeListener);
 void addMaintenanceModeListener(MaintenanceModeListener mmListener);
 

 DataModule getDataModule(String clstId, String name);

 Collection<? extends DataModule> getDataModules();


 TextIndex createTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> cb ) throws IndexIOException;
 public <KeyT> SortedTextIndex<KeyT> createSortedTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> exts,
   KeyExtractor<KeyT> keyExtractor, Comparator<KeyT> comparator) throws IndexIOException;

 File getAttachment(String id);
 File getAttachment(String id, String clustId);

}
