package uk.ac.ebi.age.storage.impl.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ebi.age.conf.Constants;
import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.LogNode.Level;
import uk.ac.ebi.age.ext.log.SimpleLogNode;
import uk.ac.ebi.age.log.BufferLogger;
import uk.ac.ebi.age.log.TooManyErrorsException;
import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.storage.DataChangeListener;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;
import uk.ac.ebi.age.storage.ModuleKey;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.storage.exeption.AttachmentIOException;
import uk.ac.ebi.age.storage.exeption.IndexIOException;
import uk.ac.ebi.age.storage.exeption.ModelStoreException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;
import uk.ac.ebi.age.storage.impl.SerializedDataModuleReaderWriter;
import uk.ac.ebi.age.storage.index.AgeIndexWritable;
import uk.ac.ebi.age.storage.index.IndexFactory;
import uk.ac.ebi.age.storage.index.KeyExtractor;
import uk.ac.ebi.age.storage.index.SortedTextIndex;
import uk.ac.ebi.age.storage.index.SortedTextIndexWritable;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextIndex;
import uk.ac.ebi.age.storage.index.TextIndexWritable;
import uk.ac.ebi.age.util.FileUtil;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.age.validator.impl.AgeSemanticValidatorImpl;
import uk.ac.ebi.mg.filedepot.FileDepot;
import uk.ac.ebi.mg.time.UniqTime;

import com.pri.util.Extractor;
import com.pri.util.M2codec;
import com.pri.util.collection.CollectionsUnion;
import com.pri.util.collection.ExtractorCollection;

public class SerializedStorage implements AgeStorageAdm
{
 private static Extractor<DataModuleWritable, Collection<AgeObjectWritable>> objExtractor = new Extractor<DataModuleWritable, Collection<AgeObjectWritable>>()
 {
  @Override
  public Collection<AgeObjectWritable> extract(DataModuleWritable dm)
  {
   return dm.getObjects();
  }
 };
 
 private Log log = LogFactory.getLog(this.getClass());
 
 private static final String modelPath = "model";
 private static final String indexPath = "index";
 private static final String dmStoragePath = "data";
 private static final String fileStoragePath = "files";
 private static final String modelFileName = "model.ser";
 
 private File modelFile;
 private File dataDir;
 private File filesDir;
 private File indexDir;
 
 private Map<String, AgeObjectWritable> globalIndexMap = new HashMap<String, AgeObjectWritable>();
 private Map<String, Map<String,AgeObjectWritable>> clusterIndexMap = new HashMap<String, Map<String,AgeObjectWritable>>();
 
 private Map<ModuleKey, DataModuleWritable> moduleMap = new HashMap<ModuleKey, DataModuleWritable>();

 private Map<String,AgeIndexWritable> indexMap = new HashMap<String, AgeIndexWritable>();

 private SemanticModel model;
 
 private ReadWriteLock dbLock = new ReentrantReadWriteLock();
 
 private DataModuleReaderWriter submRW = new SerializedDataModuleReaderWriter();

 private Collection<DataChangeListener> chgListeners = new ArrayList<DataChangeListener>(3);
 
 private FileDepot dataDepot; 
 private FileDepot fileDepot; 
 
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
 public DataModuleWritable getDataModule( String clustName, String modname )
 {
  return moduleMap.get( new ModuleKey(clustName, modname) );
 }
 
 @Override
 public Collection<? extends DataModuleWritable> getDataModules()
 {
  return moduleMap.values();
 }

 
 public TextIndex createTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> exts) throws IndexIOException
 {
  File dir = new File( indexDir, M2codec.encode(name) );
  
  if( ! dir.exists() )
   dir.mkdirs();
  
  TextIndexWritable ti = null ;
  try
  {
   ti = IndexFactory.getInstance().createFullTextIndex( qury, exts, dir );
  }
  catch(IOException e)
  {
   throw new IndexIOException(e.getMessage(),e);
  }

  try
  {
   dbLock.readLock().lock();

   ti.index( executeQuery(qury), false );

   indexMap.put(name, ti);

   return ti;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }
 
 public <KeyT> SortedTextIndex<KeyT> createSortedTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> exts, KeyExtractor<KeyT> keyExtractor, Comparator<KeyT> comparator) throws IndexIOException
 {
  File dir = new File( indexDir, M2codec.encode(name) );
  
  if( ! dir.exists() )
   dir.mkdirs();

  SortedTextIndexWritable<KeyT> ti;
  try
  {
   ti = IndexFactory.getInstance().createSortedFullTextIndex(qury,exts,keyExtractor, comparator, dir);
  }
  catch(IOException e)
  {
   throw new IndexIOException(e.getMessage(),e);
  }

  try
  {
   dbLock.readLock().lock();

   ti.index( executeQuery(qury), false );

   indexMap.put(name,ti);

   return ti;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }

 private void updateIndices( Collection<DataModuleWritable> mods, boolean fullreset )
 {
  ArrayList<AgeObject> res = new ArrayList<AgeObject>();

  for( AgeIndexWritable idx : indexMap.values() )
  {
   
   if( ! fullreset )
   {
    for( DataModuleWritable s : mods )
    {
     if( idx.getQuery().getExpression().isTestingRelations() && s.getExternalRelations() != null && s.getExternalRelations().size() > 0 )
     {
      fullreset=true;
      break;
     }
    }
   }
   
   if( fullreset )
    mods = moduleMap.values();
   
   
   Iterable<AgeObject> trv = traverse(idx.getQuery(), mods);

   res.clear();

   for(AgeObject nd : trv)
    res.add(nd);

   
   if(res.size() > 0)
    idx.index( res, ! fullreset );
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

// public List<AgeObject> queryTextIndex(IndexID idx, String query)
// {
//  TextIndex ti = (TextIndex)indexList.get(idx);
//  
//  return ti.select(query);
// }
// 
// public int queryTextIndexCount(IndexID idx, String query)
// {
//  TextIndex ti = (TextIndex)indexList.get(idx);
//  
//  return ti.count(query);
// }

 @Override
 public void update( Collection<DataModuleWritable> mods2Ins, Collection<ModuleKey> mods2Del ) throws RelationResolveException, ModuleStoreException
 {
  if( ! master )
   throw new ModuleStoreException("Only the master instance can store data");
  

  try
  {
   dbLock.writeLock().lock();

   boolean changed = false;

   if( mods2Del != null )
   {
    for(ModuleKey dmId : mods2Del)
     changed = changed || removeDataModule(dmId);
   }
   
   if( mods2Ins != null )
   {
    for(DataModuleWritable dm : mods2Ins)
    {
     changed = changed || removeDataModule(dm.getClusterId(), dm.getId());
     
     saveDataModule(dm);
     
     moduleMap.put(new ModuleKey(dm.getClusterId(), dm.getId()), dm);
     
     Map<String, AgeObjectWritable> clustMap = clusterIndexMap.get(dm.getClusterId());
     
     for(AgeObjectWritable obj : dm.getObjects())
     {
      if( obj.getIdScope() == IdScope.MODULE )
       continue;
      
      if( clustMap == null )
       clusterIndexMap.put(dm.getClusterId(),clustMap = new HashMap<String, AgeObjectWritable>());
      
      clustMap.put(obj.getId(), obj);

      if( obj.getIdScope() == IdScope.GLOBAL )
       globalIndexMap.put(obj.getId(), obj);
     }
    }
    
   }
   
   updateIndices(mods2Ins, changed);

  }
  finally
  {
   dbLock.writeLock().unlock();
  }

  for(DataChangeListener chls : chgListeners)
   chls.dataChanged();
 }
 
 public void storeDataModule(DataModuleWritable dm) throws RelationResolveException, ModuleStoreException
 {
  if( ! master )
   throw new ModuleStoreException("Only the master instance can store data");
  
  if( dm.getId() == null )
   throw new ModuleStoreException("Module ID is null");
  
  try
  {
   dbLock.writeLock().lock();

   boolean changed = removeDataModule( dm.getClusterId(), dm.getId() );
 
   saveDataModule(dm);
   
   moduleMap.put(new ModuleKey(dm.getClusterId(), dm.getId()), dm);

   Map<String, AgeObjectWritable> clustMap = clusterIndexMap.get(dm.getClusterId());
   
   for(AgeObjectWritable obj : dm.getObjects())
   {
    if( obj.getIdScope() == IdScope.MODULE )
     continue;
    
    if( clustMap == null )
     clusterIndexMap.put(dm.getClusterId(),clustMap = new HashMap<String, AgeObjectWritable>());
    
    clustMap.put(obj.getId(), obj);

    if( obj.getIdScope() == IdScope.GLOBAL )
     globalIndexMap.put(obj.getId(), obj);
   }
   
 
   updateIndices( Collections.singletonList(dm), changed );
   
   for(DataChangeListener chls : chgListeners )
    chls.dataChanged();
   
  }
  finally
  {
   dbLock.writeLock().unlock();
  }

 }

 @Override
 public Collection<? extends AgeObjectWritable> getAllObjects()
 {
  return new CollectionsUnion<AgeObjectWritable>( new ExtractorCollection<DataModuleWritable, Collection<AgeObjectWritable>>( moduleMap.values(), objExtractor) );
 }

 public void init(String initStr) throws StorageInstantiationException
 {
  File baseDir = new File( initStr );

  File modelDir = new File( baseDir, modelPath );
  
  modelFile = new File(modelDir, modelFileName );
  dataDir = new File( baseDir, dmStoragePath ); 
  filesDir = new File( baseDir, fileStoragePath ); 
  indexDir = new File( baseDir, indexPath ); 
  

  if( baseDir.isFile() )
   throw new StorageInstantiationException("The initial path must be directory: "+initStr);
  
  if( ! baseDir.exists() )
   baseDir.mkdirs();

  if( ! modelDir.exists() )
   modelDir.mkdirs();
  
  try
  {
   dataDepot = new FileDepot(dataDir, true);
  }
  catch(IOException e)
  {
   throw new StorageInstantiationException( "Data depot init error: "+e.getMessage(),e);
  }
 
  try
  {
   fileDepot = new FileDepot(filesDir, true);
  }
  catch(IOException e)
  {
   throw new StorageInstantiationException( "File depot init error: "+e.getMessage(),e);
  }


  if( modelFile.canRead() )
   loadModel();
  else
   model = SemanticManager.getInstance().createMasterModel();
  
  loadData();
 }

 
 private class ModuleLoader implements Runnable
 {
  private BlockingQueue<File> queue;
  private Stats               totals;
  
  private CountDownLatch latch;
  
  ModuleLoader( BlockingQueue<File> qu, Stats st, CountDownLatch ltch )
  {
   queue=qu;
   totals=st;
   latch=ltch;
  }
  
  @Override
  public void run()
  {
   while(true)
   {
    File modFile = null;

    try
    {
     modFile = queue.take();
    }
    catch(InterruptedException e1)
    {
     continue;
    }

    if(modFile.getName().length() == 0)
    {
     while(true)
     {
      try
      {
       queue.put(modFile);
       
       latch.countDown();
       
       return;
      }
      catch(InterruptedException e)
      {
      }
     }

    }

    long fLen = modFile.length();
    totals.incFileCount(1);
    totals.incModules(1);
    totals.incFileSize(fLen);

    DataModuleWritable dm = null;

    try
    {
     dm = submRW.read(modFile);
    }
    catch(Exception e)
    {
     totals.incFailedModules(1);
     System.out.println("Can't load data module from file: " + modFile.getAbsolutePath() + " Error: " + e.getMessage());
     continue;
    }

    synchronized(moduleMap)
    {
     moduleMap.put(new ModuleKey(dm.getClusterId(), dm.getId()), dm);
    }
    
    Map<String, AgeObjectWritable> clustMap = null;

    synchronized(clusterIndexMap)
    {
     clustMap = clusterIndexMap.get(dm.getClusterId());
    }

    
    dm.setMasterModel(model);

    for(AgeObjectWritable obj : dm.getObjects())
    {
     totals.incObjects(1);

     totals.collectObjectStats(obj);

     if(obj.getIdScope() == IdScope.MODULE)
      continue;

     if(clustMap == null)
     {
      synchronized(clusterIndexMap)
      {
       clustMap = clusterIndexMap.get(dm.getClusterId());
       
       if( clustMap == null )
        clusterIndexMap.put(dm.getClusterId(), clustMap = new HashMap<String, AgeObjectWritable>());
      }
     }
      
     synchronized(clustMap)
     {
      clustMap.put(obj.getId(), obj);
     }
     

     if(obj.getIdScope() == IdScope.GLOBAL)
     {
      synchronized(globalIndexMap)
      {
       globalIndexMap.put(obj.getId(), obj);
      }
     }

    }
   }
  }

 }
 
 private void loadData() throws StorageInstantiationException
 {
  Map<AgeRelationClass, RelationClassRef> relRefMap = new HashMap<AgeRelationClass, RelationClassRef>();

  Stats totals = new Stats();
 
  System.out.printf("Free mem: %,d Max mem: %,d Total mem: %,d\n",Runtime.getRuntime().freeMemory(),Runtime.getRuntime().maxMemory(),Runtime.getRuntime().totalMemory());
 
  long stTime = System.currentTimeMillis();
  long stMem = Runtime.getRuntime().totalMemory();
  
  try
  {
   dbLock.writeLock().lock();

   int nCores = Runtime.getRuntime().availableProcessors();
   
   CountDownLatch latch = new CountDownLatch(nCores);
   BlockingQueue<File> queue = new LinkedBlockingDeque<File>(100);
   
   System.out.println("Starting "+nCores+" parallel loaders");
   
   for( int i=1; i <= nCores; i++ )
    new Thread( new ModuleLoader(queue,totals,latch), "Data Loader "+i).start();
   
   for( File modFile : dataDepot )
   {
    while(true)
    {
     try
     {
      queue.put(modFile);
      break;
     }
     catch(InterruptedException e)
     {
     }
    }
   }
   
   while(true)
   {
    try
    {
     queue.put(new File(""));
     break;
    }
    catch(InterruptedException e)
    {
    }
   }
   
   latch.await();

   
   for( DataModuleWritable mod : moduleMap.values() )
   {
    Map<String, AgeObjectWritable> clustMap = clusterIndexMap.get(mod.getClusterId());

    
    if( mod.getExternalRelations() != null )
    {
     for( AgeExternalRelationWritable exr : mod.getExternalRelations() )
     {
      if( exr.getTargetObject() != null )
       continue;
      
      AgeObjectWritable tgObj = null;
      
      if( clustMap != null )
       tgObj = clustMap.get(exr.getTargetObjectId());
      
      if( tgObj == null )
       tgObj = globalIndexMap.get(exr.getTargetObjectId());
      
      if( tgObj == null )
       log.warn("Can't resolve external relation. "+exr.getTargetObjectId());
      
      exr.setTargetObject(tgObj);
      
      AgeRelationClass invRCls = exr.getAgeElClass().getInverseRelationClass();
      
      if( invRCls == null ) //it should not be as implicit class must be generated if there is no explicit one
       continue;
      
      boolean hasInv = false;
      
      for( AgeRelationWritable rl : tgObj.getRelations() )
      {
       if( ! rl.getAgeElClass().equals(invRCls) )
        continue;
       
       if( rl.getTargetObject() == exr.getSourceObject() )
       {
        exr.setInverseRelation(rl);
        rl.setInverseRelation(exr);
        
        hasInv=true;
        break;
       }
       else if( rl instanceof AgeExternalRelationWritable)
       {
        AgeExternalRelationWritable invExR = (AgeExternalRelationWritable) rl;
        
        if( invExR.getTargetObjectId().equals(exr.getSourceObject().getId()) )
        {
         exr.setInverseRelation(rl);
         rl.setInverseRelation(exr);

         invExR.setTargetObject(exr.getSourceObject());
         hasInv=true;
         break;
        }
       }
      }
      
      if( ! hasInv )
      {
//       AgeRelationWritable iRel = tgObj.getAgeElClass().getSemanticModel().createAgeRelation(tgObj, invRCls);
       RelationClassRef invCRef = relRefMap.get(invRCls);
       
       if( invCRef == null )
       {
        invCRef =tgObj.getDataModule().getContextSemanticModel().getModelFactory().createRelationClassRef(
          tgObj.getDataModule().getContextSemanticModel().getAgeRelationClassPlug(invRCls), 0, invRCls.getId());
        
        relRefMap.put(invRCls, invCRef);
       }
       
       
       AgeExternalRelationWritable invRel = tgObj.getDataModule().getContextSemanticModel().createExternalRelation(invCRef, tgObj, exr.getSourceObject().getId() );

       invRel.setTargetObject(exr.getSourceObject());
       invRel.setInverseRelation(exr);
       invRel.setInferred(true);
       exr.setInverseRelation(invRel);
       
       tgObj.addRelation(invRel);
      }
      
     }
    }
   
    if( mod.getFileAttributes() != null )
    {
     for(AgeFileAttributeWritable fattr : mod.getFileAttributes())
     {
      String fid = makeFileSysRef(fattr.getFileId(), mod.getClusterId());

      if(fileDepot.getFilePath(fid).exists())
       fattr.setFileSysRef(fid);
      else
      {
       fid = makeFileSysRef(fattr.getFileId());

       if(fileDepot.getFilePath(fid).exists())
        fattr.setFileSysRef(fid);
       else
        log.error("Can't resolve file attribute: '" + fattr.getFileId() + "'. Cluster: " + mod.getClusterId()
          + " Module: " + mod.getId());
      }
     }
    }
   }
   
   String cClustID = null;
   Map<String, AgeObjectWritable> clustMap = null;
   for( AgeObjectWritable obj : getAllObjects() )
   {
    String clstId = obj.getDataModule().getClusterId();
    
    if( !clstId.equals(cClustID) )
    {
     clustMap = clusterIndexMap.get(obj.getDataModule().getClusterId());
     cClustID=clstId;
    }
    
    
    if( obj.getRelations() != null )
    {
     for( AgeRelationWritable rel : obj.getRelations() )
      connectObjectAttributes(rel, clustMap);
    }
    
    connectObjectAttributes( obj, clustMap );
   }
   
   long totTime = System.currentTimeMillis()-stTime;
   
   System.out.println("Loaded"
     +"\nmodules: "+totals.getModulesCount()
     +"\nfailed modules: "+totals.getFailedModulesCount()
     +"\nobjects: "+totals.getObjectCount()
     +"\nattributes: "+totals.getAttributesCount()
     +"\nrelations: "+totals.getRelationsCount()
     +"\nstrings: "+totals.getStringsCount()
     +"\nlong strings (>100): "+totals.getLongStringsCount()
     +"\nstrings objects: "+totals.getStringObjects()
     +"\nstrings unique (only for J7): "+totals.getStringsUnique()
     +"\nstrings total length: "+totals.getStringsSize()
     +"\nstrings average length: "+(totals.getStringsCount()==0?0:totals.getStringsSize()/totals.getStringsCount())
     +"\ntotal time: "+totTime+"ms"
     +"\ntime per module: "+(totals.getModulesCount()==0?0:totTime/totals.getModulesCount())+"ms"
     +"\nmemory delta: "+(Runtime.getRuntime().totalMemory()-stMem)+"bytes"
     );
   
   totals = null;
   
   System.out.printf("Free mem: %,d Max mem: %,d Total mem: %,d Time: %dms\n",
     Runtime.getRuntime().freeMemory(),Runtime.getRuntime().maxMemory(),Runtime.getRuntime().totalMemory(),System.currentTimeMillis()-stTime);

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
 
 private void connectObjectAttributes( Attributed host, Map<String, AgeObjectWritable> clustMap )
 {
  if( host.getAttributes() == null )
   return;
  
  for( AgeAttribute attr : host.getAttributes() )
  {
   if( attr instanceof AgeExternalObjectAttributeWritable )
   {
    AgeExternalObjectAttributeWritable obAttr = (AgeExternalObjectAttributeWritable)attr;
    
    AgeObjectWritable targObj = clustMap.get(obAttr.getTargetObjectId());
    
    if( targObj == null )
     targObj = globalIndexMap.get( obAttr.getTargetObjectId() );
    
    if( targObj == null )
     log.warn("Can't resolve object attribute: "+obAttr.getTargetObjectId());
    else
     obAttr.setTargetObject(targObj);
   }
   
   connectObjectAttributes(attr, clustMap);
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
   model.setModelFactory(SemanticManager.getModelFactory());
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
  File oldModelFile = new File( modelFile.getAbsolutePath()+"."+UniqTime.getTime() );
  
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
  File modFile = dataDepot.getFilePath( M2codec.encode( sm.getId() ) );
  
  try
  {
   submRW.write(sm, modFile);
  }
  catch(Exception e)
  {
   modFile.delete();
   
   throw new ModuleStoreException("Can't store data module: "+e.getMessage(), e);
  }
 }
 
 private boolean removeDataModule(String clstId, String modId) throws ModuleStoreException
 {
  return removeDataModule(new ModuleKey(clstId,modId));
 }

 private boolean removeDataModule(ModuleKey mk) throws ModuleStoreException
 {
  DataModuleWritable dm = moduleMap.get(mk);
  
  if( dm == null )
   return false;
  
  File modFile = dataDepot.getFilePath( dm.getId() );
  
  if( ! modFile.delete() )
   throw new ModuleStoreException("Can't delete module file: "+modFile.getAbsolutePath());
  
  if( dm.getExternalRelations() != null )
  {
   for( AgeExternalRelationWritable rel : dm.getExternalRelations() )
    rel.getTargetObject().removeRelation(rel.getInverseRelation());
  }
  
  Map<String, AgeObjectWritable> clustMap = clusterIndexMap.get(dm.getClusterId());
  
  for( AgeObjectWritable obj : dm.getObjects() )
  {
   if( obj.getIdScope() == IdScope.MODULE )
    continue;
   
   clustMap.remove(obj.getId());
   
   if( obj.getIdScope() == IdScope.GLOBAL )
    globalIndexMap.remove(obj.getId());
  }
  
  moduleMap.remove(mk);
 
  return true;
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
 
   AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
   
   boolean res = true;
   
   LogNode vldBranch = bfLog.branch("Validating model"); 
   
   for(DataModuleWritable sbm : moduleMap.values())
   {
    BufferLogger submLog=new BufferLogger( Constants.MAX_ERRORS );
    
    LogNode ln = submLog.getRootNode().branch("Validating data module: "+sbm.getId());
    
    boolean vldRes = false;
    
    try
    {
     long tm = System.currentTimeMillis();
     
     vldRes = validator.validate(sbm, sm, ln);
     
     res = res && vldRes;
     
     if( ! vldRes )
      ln.log(Level.ERROR,"Validation failed");
    }
    catch(TooManyErrorsException e)
    {
     res = false;
     ln.log(Level.ERROR,"Too many errors: "+e.getErrorCount());
    }

    if( ! vldRes )
    {
     SimpleLogNode.setLevels( ln, Level.WARN );
     vldBranch.append( ln );
    }
    
   }
   
   if( res )
    vldBranch.success();
   else
    return false;

   
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

   saveBranch.success();

   LogNode setupBranch = bfLog.branch("Installing model"); 

   
   for(DataModuleWritable sbm : moduleMap.values())
    sbm.setMasterModel(sm);

   model = sm;

   SemanticManager.getInstance().setMasterModel(model);
   
   setupBranch.success();
  }
  finally
  {
   dbLock.writeLock().unlock();
  }

  return true;
 }


 @Override
 public AgeObjectWritable getGlobalObject(String objID)
 {
  return globalIndexMap.get( objID );
 }
 
 @Override
 public AgeObjectWritable getClusterObject(String clustId, String objID)
 {
  Map<String,AgeObjectWritable> clstMap = clusterIndexMap.get(clustId);
  
  if( clstMap == null )
   return null;
  
  return clstMap.get( objID );
 }

 
// @Override
// public boolean hasObject(String objID)
// {
//  return mainIndexMap.containsKey( objID );
// }

 @Override
 public boolean hasDataModule(String clustId, String dmID)
 {
  return hasDataModule( new ModuleKey(clustId,dmID) );
 }

 @Override
 public boolean hasDataModule(ModuleKey mk )
 {
  return moduleMap.containsKey( mk );
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

// @Override
// public void addRelations(String key, Collection<AgeRelationWritable> rels)
// {
//  AgeObjectWritable obj = mainIndexMap.get(key);
//  
//  for( AgeRelationWritable r : rels )
//   obj.addRelation(r);
// }
// 
// @Override
// public void removeRelations(String key, Collection<AgeRelationWritable> rels)
// {
//  AgeObjectWritable obj = mainIndexMap.get(key);
//  
//  for( AgeRelationWritable r : rels )
//   obj.removeRelation(r);
// }

 @Override
 public File getAttachment(String id, String clusterId, boolean global)
 {
  return getAttachmentBySysRef(global?makeFileSysRef(id):makeFileSysRef(id, clusterId));
 }
 
 @Override
 public File getAttachmentBySysRef(String ref)
 {
  File f = fileDepot.getFilePath(ref);
  
  if( ! f.exists() )
   return null;
  
  return f;
 }



 @Override
 public String makeFileSysRef(String id)
 {
  return "G"+M2codec.encode(id);
 }

 @Override
 public String makeFileSysRef(String id, String clustID)
 {
  return String.valueOf(id.length())+'_'+M2codec.encode(id+clustID);
 }

 @Override
 public boolean isFileIdGlobal(String fileID)
 {
  return fileID.charAt(0) == 'G';
 }


 @Override
 public boolean deleteAttachment(String id, String clusterId, boolean global)
 {
  File f = fileDepot.getFilePath(global?makeFileSysRef(id):makeFileSysRef(id, clusterId));

  return f.delete();
 }

 @Override
 public File storeAttachment(String id, String clusterId, boolean global, File aux) throws AttachmentIOException
 {
  File fDest = fileDepot.getFilePath(global?makeFileSysRef(id):makeFileSysRef(id, clusterId));
  fDest.delete();
  
  try
  {
   FileUtil.linkOrCopyFile(aux, fDest);
  }
  catch(IOException e)
  {
   throw new AttachmentIOException("Store attachment error: "+e.getMessage(), e);
  }
  
  return fDest;
 }


 public void changeAttachmentScope( String id, String clusterId, boolean global ) throws AttachmentIOException
 {
  File fSrc = fileDepot.getFilePath(global?makeFileSysRef(id, clusterId):makeFileSysRef(id));
  File fDest = fileDepot.getFilePath(global?makeFileSysRef(id):makeFileSysRef(id, clusterId));
  fDest.delete();
  
  if( ! fSrc.renameTo(fDest) )
   throw new AttachmentIOException("Can't rename file '"+fSrc.getAbsolutePath()+"' to '"+fDest.getAbsolutePath()+"'");
 }

 @Override
 public void rebuildIndices()
 {
  updateIndices(null, true);
 }
 
}
