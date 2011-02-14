package uk.ac.ebi.age.storage.impl.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.log.impl.BufferLogger;
import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.mng.SubmissionManager;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.service.IdGenerator;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.storage.DataChangeListener;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;
import uk.ac.ebi.age.storage.IndexFactory;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.storage.TextIndex;
import uk.ac.ebi.age.storage.exeption.ModelStoreException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;
import uk.ac.ebi.age.storage.impl.AgeStorageIndex;
import uk.ac.ebi.age.storage.impl.SerializedDataModuleReaderWriter;
import uk.ac.ebi.age.storage.index.AgeIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.validator.AgeSemanticValidator;

public class SerializedStorage implements AgeStorageAdm
{
 private Log log = LogFactory.getLog(this.getClass());
 
 private static final String modelPath = "model";
 private static final String dmStiragePath = "data";
 private static final String modelFileName = "model.ser";
 
 private File modelFile;
 private File dataDir;
 
 private Map<String, AgeObjectWritable> mainIndexMap = new HashMap<String, AgeObjectWritable>();
 private Map<String, DataModuleWritable> moduleMap = new TreeMap<String, DataModuleWritable>();

 private Map<AgeIndex,AgeStorageIndex> indexMap = new HashMap<AgeIndex,AgeStorageIndex>();

 private SemanticModel model;
 
 private ReadWriteLock dbLock = new ReentrantReadWriteLock();
 
 private DataModuleReaderWriter submRW = new SerializedDataModuleReaderWriter();

 private Collection<DataChangeListener> chgListeners = new ArrayList<DataChangeListener>(3);
 
 private boolean master = false;
 
 public SerializedStorage()
 {
 }
 
 public SemanticModel getSemanticModel()
 {
  return model;
 }
 
 public void setMaster( boolean m )
 {
  master=m;
 }
// public AgeIndex createTextIndex(AgeQuery qury, TextValueExtractor cb)
// {
//  AgeIndex idx = new AgeIndex();
//
//  TextIndex ti = IndexFactory.getInstance().createFullTextIndex();
//
//  try
//  {
//   dbLock.readLock().lock();
//   
//   ti.index(executeQuery(qury), cb);
//
//   indexMap.put(idx, ti);
//
//   return idx;
//
//  }
//  finally
//  {
//   dbLock.readLock().unlock();
//  }
// }

 @Override
 public DataModuleWritable getDataModule(String name)
 {
  return moduleMap.get(name);
 }
 
 public AgeIndex createTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts)
 {
  AgeIndex idx = new AgeIndex();

  TextIndex ti = IndexFactory.getInstance().createFullTextIndex(qury,exts);

  try
  {
   dbLock.readLock().lock();

   ti.index(executeQuery(qury) );

   indexMap.put(idx, ti);

   return idx;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }

 private void updateIndices( DataModuleWritable s )
 {
  ArrayList<AgeObject> res = new ArrayList<AgeObject>();

  for( Map.Entry<AgeIndex, AgeStorageIndex> me : indexMap.entrySet() )
  {
   AgeStorageIndex idx = me.getValue();
   
   Collection<DataModuleWritable> objects = null;
   
   if( idx.getQuery().getExpression().isTestingRelations() && s.getExternalRelations() != null && s.getExternalRelations().size() > 0 )
   {
    idx.reset();
    objects = moduleMap.values();
   }
   else
    objects = Collections.singleton(s);
   
   
   Iterable<AgeObject> trv = traverse(idx.getQuery(), objects);

   res.clear();

   for(AgeObject nd : trv)
    res.add(nd);

   if(res.size() > 0)
    idx.index(res);
  }
 }
 
 
 public List<AgeObject> executeQuery(AgeQuery qury)
 {
  try
  {
   dbLock.readLock().lock();

   Iterable<AgeObject> trv = traverse(qury, moduleMap.values());

   ArrayList<AgeObject> res = new ArrayList<AgeObject>();

   for(AgeObject nd : trv)
    res.add(nd);

   return res;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }

 private Iterable<AgeObject>  traverse(AgeQuery query, Collection<DataModuleWritable> sbms)
 {
  return new InMemoryQueryProcessor(query,sbms);
 }

 public List<AgeObject> queryTextIndex(AgeIndex idx, String query)
 {
  TextIndex ti = (TextIndex)indexMap.get(idx);
  
  return ti.select(query);
 }
 
 public int queryTextIndexCount(AgeIndex idx, String query)
 {
  TextIndex ti = (TextIndex)indexMap.get(idx);
  
  return ti.count(query);
 }


 public String storeDataModule(DataModuleWritable sbm) throws RelationResolveException, ModuleStoreException
 {
  if( ! master )
   throw new ModuleStoreException("Only the master instance can store data");
  
  try
  {
   dbLock.writeLock().lock();


   String newDMId = "DM" + IdGenerator.getInstance().getStringId();

   sbm.setId(newDMId);
 
   saveDataModule(sbm);
   
   moduleMap.put(newDMId, sbm);

   
   for( AgeObjectWritable obj : sbm.getObjects() )
    mainIndexMap.put(obj.getId(), obj);
   
   updateIndices( sbm );
   
   for(DataChangeListener chls : chgListeners )
    chls.dataChanged();
   
   return newDMId;
  }
  finally
  {
   dbLock.writeLock().unlock();
  }

 }


 public void init(String initStr) throws StorageInstantiationException
 {
  File baseDir = new File( initStr );

  File modelDir = new File( baseDir, modelPath );
  
  modelFile = new File(modelDir, modelFileName );
  dataDir = new File( baseDir, dmStiragePath ); 
  
  if( baseDir.isFile() )
   throw new StorageInstantiationException("The initial path must be directory: "+initStr);
  
  if( ! baseDir.exists() )
   baseDir.mkdirs();

  if( ! modelDir.exists() )
   modelDir.mkdirs();

  if( ! dataDir.exists() )
   dataDir.mkdirs();
 
  if( modelFile.canRead() )
   loadModel();
  else
   model = SemanticManager.getInstance().createMasterModel();
  
  loadData();
 }

 
 private void loadData() throws StorageInstantiationException
 {
  try
  {
   dbLock.writeLock().lock();
   
   for( File f : dataDir.listFiles() )
   {
    DataModuleWritable dm = submRW.read(f);
    
    moduleMap.put(dm.getId(), dm);
    
    for( AgeObjectWritable obj : dm.getObjects() )
     mainIndexMap.put(obj.getId(), obj);
    
    dm.setMasterModel(model);
   }
   
   for( DataModuleWritable smb : moduleMap.values() )
   {
    if( smb.getExternalRelations() != null )
    {
     for( AgeExternalRelationWritable exr : smb.getExternalRelations() )
     {
      if( exr.getTargetObject() != null )
       continue;
      
      AgeObjectWritable tgObj = mainIndexMap.get(exr.getTargetObjectId());
      
      if( tgObj == null )
       log.warn("Can't resolve external relation. "+exr.getTargetObjectId());
      
      exr.setTargetObject(tgObj);
      
      AgeRelationClass invRCls = exr.getAgeElClass().getInverseRelationClass();
      
      if( invRCls == null )
       continue;
      
      boolean hasInv = false;
      
      for( AgeRelation rl : tgObj.getRelations() )
      {
       if( ! rl.getAgeElClass().isClassOrSubclass(invRCls) )
        continue;
       
       if( rl.getTargetObject() == exr.getSourceObject() )
       {
        hasInv=true;
        break;
       }
       else if( rl instanceof AgeExternalRelationWritable)
       {
        AgeExternalRelationWritable invExR = (AgeExternalRelationWritable) rl;
        
        if( invExR.getTargetObjectId().equals(exr.getSourceObject().getId()) )
        {
         invExR.setTargetObject(exr.getSourceObject());
         hasInv=true;
         break;
        }
       }
      }
      
      if( ! hasInv )
      {
       AgeExternalRelationWritable invRel = tgObj.getAgeElClass().getSemanticModel().createExternalRelation(tgObj, exr.getSourceObject().getId(), invRCls);
       invRel.setTargetObject(exr.getSourceObject());
       invRel.setInferred(true);
       tgObj.addRelation(invRel);
      }
      
     }
    }
   }
   
   for( AgeObjectWritable obj : mainIndexMap.values() )
    connectObjectAttributes( obj );
  }
  catch(Exception e)
  {
   throw new StorageInstantiationException("Can't read data modules. System error", e);
  }
  finally
  {
   dbLock.writeLock().unlock();
  }
 }
 
 private void connectObjectAttributes( Attributed host )
 {
  if( host.getAttributes() == null )
   return;
  
  for( AgeAttribute attr : host.getAttributes() )
  {
   if( attr instanceof AgeExternalObjectAttributeWritable )
   {
    AgeExternalObjectAttributeWritable obAttr = (AgeExternalObjectAttributeWritable)attr;
    
    AgeObject targObj = mainIndexMap.get(obAttr.getTargetObjectId());
    
    if( targObj == null )
     log.warn("Can't resolve object attribute: "+obAttr.getTargetObjectId());
    else
     obAttr.setTargetObject(targObj);
   }
   
   connectObjectAttributes(attr);
  }
 }
 
 
 private void loadModel() throws StorageInstantiationException
 {
  try
  {
   ObjectInputStream ois = new ObjectInputStream( new FileInputStream(modelFile) );
   
   model = (SemanticModel)ois.readObject();
   
   ois.close();
   
   SemanticManager.getInstance().setMasterModel( model );
  }
  catch(Exception e)
  {
   throw new StorageInstantiationException("Can't read model. System error", e);
  }
 }
 
 private void saveModel(SemanticModel sm) throws ModelStoreException
 {
  File modelFile2 = new File( modelFile.getAbsolutePath() );
  File tmpModelFile = new File(modelFile.getAbsolutePath()+".tmp");
  File oldModelFile = new File( modelFile.getAbsolutePath()+"."+System.currentTimeMillis() );
  
  try
  {
   FileOutputStream fileOut = new FileOutputStream(tmpModelFile);
   
   ObjectOutputStream oos = new ObjectOutputStream( fileOut );
   
   oos.writeObject(sm);
   
   oos.close();
   
   if( modelFile2.exists() )
    modelFile2.renameTo(oldModelFile);
   
   tmpModelFile.renameTo( modelFile );
   
  }
  catch(Exception e)
  {
   throw new ModelStoreException("Can't store model: "+e.getMessage(), e);
  }
 }

 private void saveDataModule(DataModuleWritable sm) throws ModuleStoreException
 {
  File sbmFile = new File( dataDir, sm.getId()+submRW.getExtension() );
  
  try
  {
   submRW.write(sm, sbmFile);
  }
  catch(Exception e)
  {
   sbmFile.delete();
   
   throw new ModuleStoreException("Can't store model: "+e.getMessage(), e);
  }
 }
 
 public void shutdown()
 {

 }

 @Override
 public boolean updateSemanticModel(SemanticModel sm, LogNode bfLog ) //throws ModelStoreException
 {
  if( ! master )
  {
   bfLog.log(Level.ERROR, "Only the master instance can store data");
   return false;
  }
  
  try
  {
   dbLock.writeLock().lock();
 
   AgeSemanticValidator validator = SubmissionManager.getInstance().getAgeSemanticValidator();
   
   boolean res = true;
   
   LogNode vldBranch = bfLog.branch("Validating model"); 
   
   for(DataModuleWritable sbm : moduleMap.values())
   {
    BufferLogger submLog=new BufferLogger();
    
    LogNode ln = submLog.getRootNode().branch("Validating data module: "+sbm.getId());
    
    if( ! validator.validate(sbm, sm, ln) )
    {
     ln.log(Level.ERROR,"Validation failed");
     res = false;
     vldBranch.append( submLog.getRootNode() );
    }
   }
   
   if( !res )
   {
    BufferLogger.printBranch(vldBranch);
    
    vldBranch.log(Level.ERROR,"Validation failed");    
    return false;
   }
   else
    vldBranch.log(Level.INFO,"Success");    

   
   LogNode saveBranch = bfLog.branch("Saving model"); 

   try
   {
    saveModel(sm);
   }
   catch(ModelStoreException e)
   {
    saveBranch.log(Level.ERROR, "Model saving failed: "+e.getMessage());
    return false;
   }

   saveBranch.log(Level.INFO, "Success");

   LogNode setupBranch = bfLog.branch("Installing model"); 

   
   for(DataModuleWritable sbm : moduleMap.values())
    sbm.setMasterModel(sm);

   model = sm;

   SemanticManager.getInstance().setMasterModel(model);
   
   setupBranch.log(Level.INFO, "Success");
  }
  finally
  {
   dbLock.writeLock().unlock();
  }

  return true;
 }


 @Override
 public AgeObject getObjectById(String objID)
 {
  return mainIndexMap.get( objID );
 }
 
 @Override
 public boolean hasObject(String objID)
 {
  return mainIndexMap.containsKey( objID );
 }


 @Override
 public void addDataChangeListener(DataChangeListener dataChangeListener)
 {
  chgListeners.add(dataChangeListener);
 }

 @Override
 public void lockWrite()
 {
  dbLock.writeLock().lock();
 }

 @Override
 public void unlockWrite()
 {
  dbLock.writeLock().unlock();
 }

 @Override
 public void addRelations(String key, Collection<AgeRelationWritable> rels)
 {
  AgeObjectWritable obj = mainIndexMap.get(key);
  
  for( AgeRelationWritable r : rels )
   obj.addRelation(r);
 }
 
}
