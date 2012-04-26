package uk.ac.ebi.age.storage.impl.serswap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import uk.ac.ebi.age.conf.Constants;
import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.LogNode.Level;
import uk.ac.ebi.age.ext.log.SimpleLogNode;
import uk.ac.ebi.age.log.BufferLogger;
import uk.ac.ebi.age.log.TooManyErrorsException;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.ModuleKey;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.impl.ModelFactoryImpl;
import uk.ac.ebi.age.model.impl.v1.SemanticModelImpl;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.storage.ConnectionInfo;
import uk.ac.ebi.age.storage.DataChangeListener;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;
import uk.ac.ebi.age.storage.MaintenanceModeListener;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.storage.exeption.AttachmentIOException;
import uk.ac.ebi.age.storage.exeption.IndexIOException;
import uk.ac.ebi.age.storage.exeption.ModelStoreException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;
import uk.ac.ebi.age.storage.impl.SerializedDataModuleReaderWriter;
import uk.ac.ebi.age.storage.impl.ser.InMemoryQueryProcessor;
import uk.ac.ebi.age.storage.impl.ser.SerializedStorage;
import uk.ac.ebi.age.storage.impl.ser.SerializedStorageConfiguration;
import uk.ac.ebi.age.storage.impl.ser.Stats;
import uk.ac.ebi.age.storage.impl.serswap.v3.AgeObjectMergedLinkProxy;
import uk.ac.ebi.age.storage.impl.serswap.v3.AgeObjectProxy;
import uk.ac.ebi.age.storage.impl.serswap.v3.SwapDataModuleImpl;
import uk.ac.ebi.age.storage.impl.serswap.v3.SwapModelFactory;
import uk.ac.ebi.age.storage.impl.serswap.v3.SwapModelFactoryImpl;
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
import uk.ac.ebi.mg.assertlog.Log;
import uk.ac.ebi.mg.assertlog.LogFactory;
import uk.ac.ebi.mg.filedepot.FileDepot;
import uk.ac.ebi.mg.time.UniqTime;

import com.pri.util.Extractor;
import com.pri.util.M2codec;
import com.pri.util.Pair;
import com.pri.util.StringUtils;
import com.pri.util.collection.CollectionsUnion;
import com.pri.util.collection.ExtractorCollection;

public class SerializedSwapStorage implements AgeStorageAdm
{

  private static Extractor<DataModuleWritable, Collection<AgeObjectWritable>> objExtractor = new Extractor<DataModuleWritable, Collection<AgeObjectWritable>>()
  {
   @Override
   public Collection<AgeObjectWritable> extract(DataModuleWritable dm)
   {
    return dm.getObjects();
   }
  };
  
  private static Log log = LogFactory.getLog(SerializedStorage.class);
  
  private static final String modelPath = "model";
  private static final String indexPath = "index";
  private static final String dmStoragePath = "data";
  private static final String fileStoragePath = "files";
  private static final String modelFileName = "model.ser";
  private static final String indexCacheFileName = "indexCache.ser";
  
  final private File modelFile;
  final private File indexCacheFile;
  final private File dataDir;
  final private File filesDir;
  final private File indexDir;
  
  
  private Map<String, AgeObjectProxy> globalIndexMap = new HashMap<String, AgeObjectProxy>();
  private Map<String, Map<String,AgeObjectProxy>> clusterIndexMap = new HashMap<String, Map<String,AgeObjectProxy>>();
  
  private static volatile boolean iterateModulesDirect=true;  
  private TreeMap<ModuleKey, ModuleRef > moduleMap = new TreeMap<ModuleKey, ModuleRef >();

  private Map<String,AgeIndexWritable> indexMap = new HashMap<String, AgeIndexWritable>();

  private SemanticModel model;
  
  private ReentrantReadWriteLock dbLock = new ReentrantReadWriteLock();
  
  private DataModuleReaderWriter submRW = new SerializedDataModuleReaderWriter();

  private Collection<DataChangeListener> chgListeners = new ArrayList<DataChangeListener>(3);
  private Collection<MaintenanceModeListener> mmodListeners = new ArrayList<MaintenanceModeListener>(3);
  
  private FileDepot dataDepot; 
  private FileDepot fileDepot; 
  
  private boolean master = false;
  
  private boolean maintenanceMode = false;
  
  private long mModeTimeout;

  private long lastUpdate;
  
  private boolean dataDirty = false;
  
 public SerializedSwapStorage(SerializedStorageConfiguration conf) throws StorageInstantiationException
 {
  master = conf.isMaster();

  mModeTimeout = conf.getMaintenanceModeTimeout();

  File baseDir = conf.getStorageBaseDir();

  File modelDir = new File(baseDir, modelPath);

  modelFile = new File(modelDir, modelFileName);
  dataDir = new File(baseDir, dmStoragePath);
  filesDir = new File(baseDir, fileStoragePath);
  indexDir = new File(baseDir, indexPath);
  indexCacheFile = new File(filesDir,indexCacheFileName);
  
  if(baseDir.isFile())
   throw new StorageInstantiationException("The initial path must be directory: " + baseDir.getAbsolutePath());

  if(!baseDir.exists())
   baseDir.mkdirs();

  if(!modelDir.exists())
   modelDir.mkdirs();

  try
  {
   dataDepot = new FileDepot(dataDir, true);
  }
  catch(IOException e)
  {
   throw new StorageInstantiationException("Data depot init error: " + e.getMessage(), e);
  }

  try
  {
   fileDepot = new FileDepot(filesDir, true);
  }
  catch(IOException e)
  {
   throw new StorageInstantiationException("File depot init error: " + e.getMessage(), e);
  }

  // unitedIndex.setStoragePlug( new StoragePlug( this ) );

  if(modelFile.canRead())
   loadModel();
  else
   model = new SemanticModelImpl(new SwapModelFactoryImpl(ModelFactoryImpl.getInstance()));

  if( ! loadIndexCache ())
   loadData();

 }

 private boolean loadIndexCache()
 {
  if(!indexCacheFile.exists())
   return false;

  ObjectInputStream ois = null;

  try
  {
   ois = new ObjectInputStream(new FileInputStream(indexCacheFile));

   StorageIndex index = (StorageIndex) ois.readObject();
   
   globalIndexMap = index.getGlobalIndexMap();
   clusterIndexMap = index.getClusterIndexMap();
   moduleMap = index.getModuleMap();

   ois.close();
  }
  catch(Exception e)
  {
   indexCacheFile.delete();

   e.printStackTrace();

   return false;
  }
  finally
  {
   if(ois != null)
   {
    try
    {
     ois.close();
    }
    catch(IOException e1)
    {
     e1.printStackTrace();
    }
   }
  }

  return true;
 }

 private void saveIndexCache()
 {
  try
  {
   ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(indexCacheFile) );
   
   StorageIndex index = new StorageIndex();
   
   index.setGlobalIndexMap( globalIndexMap );
   index.setClusterIndexMap( clusterIndexMap );
   index.setModuleMap( moduleMap );
   
   oos.writeObject(index);
   
   oos.close();
   
  }
  catch(IOException e)
  {
   log.error("Can't write index cache", e);
   e.printStackTrace();
  }
 }
 
 public SemanticModel getSemanticModel()
 {
  return model;
 }

 @Override
 public DataModuleWritable getDataModule(String clustName, String modname)
 {
  ModuleKey modK = new ModuleKey(clustName, modname);

  ModuleRef ref = moduleMap.get(modK);

  if(ref == null)
   return null;

  DataModuleWritable mod = ref.getModule();

  if(mod == null)
   mod = loadModule(modK);

  return mod;
 }

 @Override
 public Collection< ? extends DataModuleWritable> getDataModules()
 {
  return new ModulesCollection();
 }

 public TextIndex createTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> exts) throws IndexIOException
 {
  File dir = new File(indexDir, M2codec.encode(name));

  if(!dir.exists())
   dir.mkdirs();

  TextIndexWritable ti = null;
  try
  {
   ti = IndexFactory.getInstance().createFullTextIndex(qury, exts, dir);
  }
  catch(IOException e)
  {
   throw new IndexIOException(e.getMessage(), e);
  }

  try
  {
   dbLock.readLock().lock();

   ti.index(executeQuery(qury), false);

   indexMap.put(name, ti);

   return ti;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }

 public <KeyT> SortedTextIndex<KeyT> createSortedTextIndex(String name, AgeQuery qury, Collection<TextFieldExtractor> exts,
   KeyExtractor<KeyT> keyExtractor, Comparator<KeyT> comparator) throws IndexIOException
 {
  File dir = new File(indexDir, M2codec.encode(name));

  if(!dir.exists())
   dir.mkdirs();

  SortedTextIndexWritable<KeyT> ti;
  try
  {
   ti = IndexFactory.getInstance().createSortedFullTextIndex(qury, exts, keyExtractor, comparator, dir);
  }
  catch(IOException e)
  {
   throw new IndexIOException(e.getMessage(), e);
  }

  try
  {
   dbLock.readLock().lock();

   ti.index(executeQuery(qury), false);

   indexMap.put(name, ti);

   return ti;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }

 private void updateIndices(Collection<DataModuleWritable> mods, boolean fullreset)
 {
  ArrayList<AgeObject> res = new ArrayList<AgeObject>();

  for(AgeIndexWritable idx : indexMap.values())
  {

   if(!fullreset)
   {
    for(DataModuleWritable s : mods)
    {
     if(idx.getQuery().getExpression().isTestingRelations() && s.getExternalRelations() != null && s.getExternalRelations().size() > 0)
     {
      fullreset = true;
      break;
     }
    }
   }

   if(fullreset)
    mods = new ModulesCollection();

   Iterable<AgeObject> trv = traverse(idx.getQuery(), mods);

   res.clear();

   for(AgeObject nd : trv)
    res.add(nd);

   if(res.size() > 0 || fullreset)
    idx.index(res, !fullreset);
  }
 }

 public List<AgeObject> executeQuery(AgeQuery qury)
 {
  try
  {
   dbLock.readLock().lock();

   Iterable<AgeObject> trv = traverse(qury, new ModulesCollection());

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

 private Iterable<AgeObject> traverse(AgeQuery query, Collection<DataModuleWritable> sbms)
 {
  return new InMemoryQueryProcessor(query, sbms);
 }

 @Override
 public void update(Collection<DataModuleWritable> mods2Ins, Collection<ModuleKey> mods2Del, ConnectionInfo connectionInfo) throws RelationResolveException, ModuleStoreException
 {
  if(!master)
   throw new ModuleStoreException("Only the master instance can store data");

  try
  {
   dbLock.writeLock().lock();

   if( ! dataDirty )
    indexCacheFile.delete();
   
   dataDirty = true;
   lastUpdate = System.currentTimeMillis();

   boolean changed = false;

   if(mods2Del != null)
   {
    for(ModuleKey dmId : mods2Del)
     changed = changed || removeDataModule(dmId);
   }

   if(mods2Ins != null)
   {
    for(DataModuleWritable dm : mods2Ins)
    {
     changed = changed || removeDataModule(dm.getClusterId(), dm.getId());

     saveDataModule(dm);

     ModuleRef modref = new ModuleRef();
     
     modref.setStorage( this );
     modref.setModuleKey(new ModuleKey(dm.getClusterId(), dm.getId()));
     modref.setModule((ProxyDataModule)dm);
     
     moduleMap.put(modref.getModuleKey(), modref);

     Map<String, AgeObjectProxy> clustMap = clusterIndexMap.get(dm.getClusterId());

     for(AgeObjectWritable obj : dm.getObjects())
     {
      AgeObjectProxy pxo = new AgeObjectProxy(obj, modref.getModuleKey(), modref);
      
      modref.addObject(pxo.getId(), pxo);
      
      if(obj.getIdScope() == IdScope.MODULE)
       continue;

      if(clustMap == null)
       clusterIndexMap.put(dm.getClusterId(), clustMap = new HashMap<String, AgeObjectProxy>());

      clustMap.put(obj.getId(), pxo);

      if(obj.getIdScope() == IdScope.GLOBAL)
       globalIndexMap.put(obj.getId(), pxo);
     }
    }

   }
   
   
   if( connectionInfo.getObjectAttributesReconnection() != null )
   {
    for(Pair<AgeExternalObjectAttributeWritable, AgeObject> cn : connectionInfo.getObjectAttributesReconnection())
    {
     AgeObject tgObj = cn.getSecond();
     AgeObjectProxy tgPx = null;
     
     if( tgObj instanceof AgeObjectProxy)
      tgPx =  (AgeObjectProxy)tgObj;
     else
      tgPx = ((SwapDataModuleImpl)tgObj.getDataModule()).getModuleRef().getObjectProxy( tgObj.getId() );
     
     cn.getFirst().setTargetObject(tgPx);
    }
   }
   
   if( connectionInfo.getRelationsReconnection() != null)
   {
    for(AgeExternalRelationWritable rel : connectionInfo.getRelationsReconnection())
    {
     AgeObject tgObj = rel.getSourceObject();
     AgeObjectProxy tgPx = null;
     
     if( tgObj instanceof AgeObjectProxy)
      tgPx =  (AgeObjectProxy)tgObj;
     else
      tgPx = ((SwapDataModuleImpl)tgObj.getDataModule()).getModuleRef().getObjectProxy( tgObj.getId() );

     
     rel.getInverseRelation().setTargetObject( tgPx );
     rel.getInverseRelation().setInverseRelation(rel);
    }
   }

   if( connectionInfo.getFileAttributesResolution() != null)
   {
    for(Pair<AgeFileAttributeWritable, Boolean> fc : connectionInfo.getFileAttributesResolution())
     fc.getFirst().setResolvedGlobal(fc.getSecond());
   }

   if( connectionInfo.getRelationsRemoval() != null )
   {
    for( AgeRelationWritable rel : connectionInfo.getRelationsRemoval() )
     rel.getSourceObject().removeRelation(rel);
   }
   
   if( connectionInfo.getRelationsAttachment() != null )
   {
    for( AgeRelationWritable rel : connectionInfo.getRelationsRemoval() )
     rel.getSourceObject().addRelation(rel);
   }

   if(!maintenanceMode)
   {
    updateIndices(mods2Ins, changed);
    saveIndexCache();
   }
   
  }
  finally
  {
   lastUpdate = System.currentTimeMillis();

   dbLock.writeLock().unlock();
  }

  if(!maintenanceMode)
  {
   synchronized(chgListeners)
   {
    for(DataChangeListener chls : chgListeners)
     chls.dataChanged();
   }
  }

 }

 /*
 private void storeDataModule(DataModuleWritable dm) throws RelationResolveException, ModuleStoreException
 {
  if(!master)
   throw new ModuleStoreException("Only the master instance can store data");

  if(dm.getId() == null)
   throw new ModuleStoreException("Module ID is null");

  try
  {
   dbLock.writeLock().lock();

   boolean changed = removeDataModule(dm.getClusterId(), dm.getId());

   saveDataModule(dm);

   moduleMap.put(new ModuleKey(dm.getClusterId(), dm.getId()), new SoftReference<DataModuleWritable>(dm));

   Map<String, AgeObjectWritable> clustMap = clusterIndexMap.get(dm.getClusterId());

   for(AgeObjectWritable obj : dm.getObjects())
   {
    if(obj.getIdScope() == IdScope.MODULE)
     continue;

    if(clustMap == null)
     clusterIndexMap.put(dm.getClusterId(), clustMap = new HashMap<String, AgeObjectWritable>());

    clustMap.put(obj.getId(), obj);

    if(obj.getIdScope() == IdScope.GLOBAL)
     globalIndexMap.put(obj.getId(), obj);
   }

   updateIndices(Collections.singletonList(dm), changed);

   synchronized(chgListeners)
   {
    for(DataChangeListener chls : chgListeners)
     chls.dataChanged();
   }

  }
  finally
  {
   dbLock.writeLock().unlock();
  }

 }

*/

 @Override
 public Collection< ? extends AgeObjectWritable> getAllObjects()
 {
  return new CollectionsUnion<AgeObjectWritable>(new ExtractorCollection<DataModuleWritable, Collection<AgeObjectWritable>>(new ModulesCollection(),
    objExtractor));
 }

 private class ModuleLoader implements Runnable
 {
  private BlockingQueue<File>           queue;
  private Stats                         totals;

  private CountDownLatch                latch;

  private Map<String, List<ExtRelInfo>> extRelMap;

  ModuleLoader(BlockingQueue<File> qu, Stats st, CountDownLatch ltch, Map<String, List<ExtRelInfo>> erm)
  {
   queue = qu;
   totals = st;
   latch = ltch;

   extRelMap = erm;
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

    ProxyDataModule dm = null;

    try
    {
     dm = (ProxyDataModule) submRW.read(modFile);
    }
    catch(Exception e)
    {
     totals.incFailedModules(1);
     log.error("Can't load data module from file: " + modFile.getAbsolutePath() + " Error: " + e.getMessage());
     continue;
    }

    ModuleKey modKey = new ModuleKey(dm.getClusterId(), dm.getId());

    ModuleRef modRef = new ModuleRef();

    modRef.setStorage(SerializedSwapStorage.this);

    modRef.setModuleKey(modKey);
    modRef.setModule(dm);

    synchronized(moduleMap)
    {
     moduleMap.put(modKey, modRef);
    }

    Map<String, AgeObjectProxy> clustMap = null;

    synchronized(clusterIndexMap)
    {
     clustMap = clusterIndexMap.get(dm.getClusterId());
    }

    dm.setMasterModel(model);

    for(AgeObjectWritable obj : dm.getObjects())
    {
     totals.incObjects(1);

     totals.collectObjectStats(obj);

     // if(obj.getIdScope() == IdScope.MODULE)
     // continue;

     List<ExtRelInfo> extList = null;

     AgeObjectProxy pxObj = new AgeObjectProxy(obj, modKey, modRef);

     for(AgeRelationWritable rel : obj.getRelations())
     {
      if(rel instanceof AgeExternalRelationWritable)
      {
       if(extList == null)
       {
        extList = new ArrayList<ExtRelInfo>();

        synchronized(extRelMap)
        {
         extRelMap.put(dm.getClusterId().length() + dm.getClusterId() + dm.getId().length() + dm.getId() + obj.getId(), extList);

         // if(obj.getIdScope() == IdScope.CLUSTER || obj.getIdScope() ==
         // IdScope.GLOBAL )
         // extRelMap.put("C"+dm.getClusterId().length()+dm.getClusterId()+obj.getId(),
         // extList);
         //
         // if(obj.getIdScope() == IdScope.GLOBAL)
         // extRelMap.put("G"+obj.getId(), extList);

        }
       }

       ExtRelInfo rlinf = new ExtRelInfo((AgeExternalRelationWritable) rel);

       extList.add(rlinf);
      }

     }


     if(clustMap == null)
     {
      synchronized(clusterIndexMap)
      {
       clustMap = clusterIndexMap.get(dm.getClusterId());

       if(clustMap == null)
        clusterIndexMap.put(dm.getClusterId(), clustMap = new HashMap<String, AgeObjectProxy>());
      }
     }

     if(obj.getIdScope() == IdScope.CLUSTER || obj.getIdScope() == IdScope.GLOBAL)
     {
      synchronized(clustMap)
      {
       clustMap.put(obj.getId(), pxObj);
      }
     }

     if(obj.getIdScope() == IdScope.GLOBAL)
     {
      synchronized(globalIndexMap)
      {
       globalIndexMap.put(obj.getId(), pxObj);
      }
     }

     modRef.addObject(obj.getId(), pxObj);
    }
   }
  }

 }

 private String getModuleFile(ModuleKey key)
 {
  return getModuleFile(key.getClusterId(), key.getModuleId());
 }

 private String getModuleFile(String clstId, String modId)
 {
  return M2codec.encode(clstId.length() + clstId + modId);
 }

 ProxyDataModule loadModule(ModuleKey key)
 {
  ProxyDataModule dm = null;

  File modFile = dataDepot.getFilePath(getModuleFile(key));

  try
  {
   dm = (ProxyDataModule) submRW.read(modFile);
  }
  catch(Exception e)
  {
   log.error("Can't load data module from file: " + modFile.getAbsolutePath() + " Error: " + e.getMessage());
   return null;
  }

  dm.setMasterModel(model);

  return dm;
 }

 DataModuleWritable ensureModuleLoaded(ModuleKey key)
 {
  ProxyDataModule dm = null;

  ModuleRef modref = moduleMap.get(key);

  if(modref == null)
  {
   log.warn("Wrong module requested: CID=" + key.getClusterId() + " MID=" + key.getModuleId());
   return null;
  }

  File modFile = dataDepot.getFilePath(getModuleFile(key));

  synchronized(modref)
  {
   dm = modref.getModule();

   if(dm != null)
    return dm;
   
   dm=loadModule(key);
   
   
   dm.setModuleRef(modref);
   modref.setModule(dm);
  }

  return dm;
 }

 private void loadData() throws StorageInstantiationException
 {
//  Map<AgeRelationClass, RelationClassRef> relRefMap = new HashMap<AgeRelationClass, RelationClassRef>();

  Map<String, List<ExtRelInfo>> extRelMap = new HashMap<String, List<ExtRelInfo>>();

  Stats totals = new Stats();

  assert log.info(String.format("Free mem: %,d Max mem: %,d Total mem: %,d", Runtime.getRuntime().freeMemory(), Runtime.getRuntime().maxMemory(), Runtime
    .getRuntime().totalMemory()));

  long stTime = System.currentTimeMillis();
  long stMem = Runtime.getRuntime().totalMemory();

  try
  {
   dbLock.writeLock().lock();

   int nCores = Runtime.getRuntime().availableProcessors() + 2;

   if(nCores > 4)
    nCores = 4;

   CountDownLatch latch = new CountDownLatch(nCores);
   BlockingQueue<File> queue = new LinkedBlockingDeque<File>(100);

   log.info("Starting " + nCores + " parallel loaders");

   long startTime = 0;

   assert (startTime = System.currentTimeMillis()) != 0;

   for(int i = 1; i <= nCores; i++)
    new Thread(new ModuleLoader(queue, totals, latch, extRelMap), "Data Loader " + i).start();

   for(File modFile : dataDepot)
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
     queue.put(new File("")); // To terminate the queue
     break;
    }
    catch(InterruptedException e)
    {
    }
   }

   latch.await();

   assert log.info("Module files load time: " + StringUtils.millisToString(System.currentTimeMillis() - startTime));

   assert (startTime = System.currentTimeMillis()) != 0;

   for(ModuleRef modref : moduleMap.values())
   {
    String cid = modref.getModuleKey().getClusterId();
    String mid = modref.getModuleKey().getModuleId();

    Map<String, AgeObjectProxy> clustMap = clusterIndexMap.get(modref.getModuleKey().getClusterId());

    for(AgeObjectProxy pxo : modref.getObjectProxies())
    {
     List<ExtRelInfo> extLinks = extRelMap.get(cid.length() + cid + mid.length() + mid + pxo.getId());

     if(extLinks != null)
     {
      for(ExtRelInfo relInfo : extLinks)
      {
       AgeObjectProxy tgObj = null;

       
       if( relInfo.getTargetResolveScope() == ResolveScope.GLOBAL  )
        tgObj = globalIndexMap.get(relInfo.getTargetId());
       else
       {
        if( clustMap != null )
         tgObj = clustMap.get(relInfo.getTargetId());
        
        if( tgObj == null && relInfo.getTargetResolveScope() == ResolveScope.CASCADE_CLUSTER )
         tgObj = globalIndexMap.get(relInfo.getTargetId());
         
       }


       if(tgObj == null)
       {
        log.warn("Can't resolve external relation. Source: " + modref.getModuleKey().getClusterId() + ":" + modref.getModuleKey().getModuleId() + ":"
          + pxo.getId() + " Target: " + relInfo.getTargetId() + " Scope: " + relInfo.getTargetResolveScope().name());

        continue;
       }

       AgeRelationWritable invRel = null;

       if(relInfo.getCustomClassName() != null || relInfo.getSourceObjectScope() == IdScope.MODULE )
        invRel = ((SwapModelFactory)model.getModelFactory()).createCustomInferredExternalInverseRelation(tgObj, pxo, relInfo.getCustomClassName());
       else
       {
        ModuleKey tgModKey = tgObj.getModuleKey();

        List<ExtRelInfo> tgtExtLinks = extRelMap.get(tgModKey.getClusterId().length() + tgModKey.getClusterId() + tgModKey.getModuleId().length()
          + tgModKey.getModuleId() + tgObj.getId());

        boolean found = false;
        if( tgtExtLinks != null )
        {
         for(ExtRelInfo tgRel : tgtExtLinks)
         {
          if(  tgRel.getRelationClass().equals(relInfo.getRelationClass().getInverseRelationClass() )
            && tgRel.getTargetId().equals(pxo.getId())
            && ( relInfo.getSourceObjectScope() == IdScope.GLOBAL || pxo.getModuleKey().getClusterId().equals(tgObj.getModuleKey().getClusterId()) )
            && ( tgRel.getTargetResolveScope() != ResolveScope.CLUSTER || pxo.getModuleKey().getClusterId().equals(tgObj.getModuleKey().getClusterId()) )
           )
          {
           found = true;
           break;
          }
         }
        }
        
 
        if(!found)
         invRel = ((SwapModelFactory)model.getModelFactory()).createDefinedInferredExternalInverseRelation(tgObj, pxo, relInfo.getRelationClass());
       }

       if(invRel != null)
       {
        if(!(tgObj instanceof AgeObjectMergedLinkProxy))
        {
         AgeObjectMergedLinkProxy lnkTgPx;
         AgeObjectWritable tgObjNat = tgObj.tryGetObject();

         if(tgObjNat != null)
          lnkTgPx = new AgeObjectMergedLinkProxy(tgObjNat, tgObj.getModuleKey(), modref);
         else
          lnkTgPx = new AgeObjectMergedLinkProxy(tgObj.getId(), tgObj.getModuleKey(), modref);

         if(globalIndexMap.get(tgObj.getId()) == tgObj)
          globalIndexMap.put(tgObj.getId(), lnkTgPx);

         Map<String, AgeObjectProxy> tgClstMap = clusterIndexMap.get(tgObj.getModuleKey().getClusterId());

         if(tgClstMap != null)
         {
          if(tgClstMap.get(tgObj.getId()) == tgObj)
           tgClstMap.put(tgObj.getId(), lnkTgPx);
         }

         moduleMap.get(tgObj.getModuleKey()).addObject(tgObj.getId(), lnkTgPx);

         tgObj = lnkTgPx;
        }

        ((AgeObjectMergedLinkProxy) tgObj).addRelation(invRel);
       }

      }

     }
    }

   }

   assert log.info("Module linking time: " + StringUtils.millisToString(System.currentTimeMillis() - startTime));

   long totTime = System.currentTimeMillis() - stTime;

   log.info("Loaded:" + "\nmodules: " + totals.getModulesCount() + "\nfailed modules: " + totals.getFailedModulesCount() + "\nobjects: "
     + totals.getObjectCount() + "\nattributes: " + totals.getAttributesCount() + "\nrelations: " + totals.getRelationsCount() + "\nstrings: "
     + totals.getStringsCount() + "\nlong strings (>100): " + totals.getLongStringsCount() + "\nstrings objects: " + totals.getStringObjects()
     + "\nstrings unique (only for J7): " + totals.getStringsUnique() + "\nstrings total length: " + totals.getStringsSize()
     + "\nstrings average length: " + (totals.getStringsCount() == 0 ? 0 : totals.getStringsSize() / totals.getStringsCount()) + "\ntotal time: "
     + totTime + "ms" + "\ntime per module: " + (totals.getModulesCount() == 0 ? 0 : totTime / totals.getModulesCount()) + "ms" + "\nmemory delta: "
     + (Runtime.getRuntime().totalMemory() - stMem) + "bytes");

   totals = null;

   assert log.info(String.format("Free mem: %,d Max mem: %,d Total mem: %,d Time: %dms", Runtime.getRuntime().freeMemory(), Runtime.getRuntime()
     .maxMemory(), Runtime.getRuntime().totalMemory(), System.currentTimeMillis() - stTime));

   
   saveIndexCache();
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

 private void loadModel() throws StorageInstantiationException
 {
  long startTime = 0;

  assert (startTime = System.currentTimeMillis()) != 0;

  try
  {
   ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile));

   model = (SemanticModel) ois.readObject();

   ois.close();

   // SemanticManager.getInstance().setMasterModel( model );
   // model.setModelFactory(SemanticManager.getModelFactory());
   model.setModelFactory(new SwapModelFactoryImpl(ModelFactoryImpl.getInstance()));
  }
  catch(Exception e)
  {
   throw new StorageInstantiationException("Can't read model. System error", e);
  }

  assert log.info("Model load time: " + (System.currentTimeMillis() - startTime) + "ms");
 }

 private void saveModel(SemanticModel sm) throws ModelStoreException
 {
  File modelFile2 = new File(modelFile.getAbsolutePath());
  File tmpModelFile = new File(modelFile.getAbsolutePath() + ".tmp");
  File oldModelFile = new File(modelFile.getAbsolutePath() + "." + UniqTime.getTime());

  try
  {
   FileOutputStream fileOut = new FileOutputStream(tmpModelFile);

   ObjectOutputStream oos = new ObjectOutputStream(fileOut);

   oos.writeObject(sm);

   oos.close();

   if(modelFile2.exists())
    modelFile2.renameTo(oldModelFile);

   tmpModelFile.renameTo(modelFile);

  }
  catch(Exception e)
  {
   throw new ModelStoreException("Can't store model: " + e.getMessage(), e);
  }
 }

 private void saveDataModule(DataModuleWritable sm) throws ModuleStoreException
 {
  File modFile = dataDepot.getFilePath(getModuleFile(sm.getClusterId(), sm.getId()));

  try
  {
   submRW.write(sm, modFile);
  }
  catch(Exception e)
  {
   modFile.delete();

   throw new ModuleStoreException("Can't store data module: " + e.getMessage(), e);
  }
 }

 private boolean removeDataModule(String clstId, String modId) throws ModuleStoreException
 {
  return removeDataModule(new ModuleKey(clstId, modId));
 }

 private boolean removeDataModule(ModuleKey mk) throws ModuleStoreException
 {
  ModuleRef dmr = moduleMap.get(mk);

  if(dmr == null)
   return false;

  ProxyDataModule mod = dmr.getModule();

  File modFile = dataDepot.getFilePath(getModuleFile(mk));

  if(!modFile.delete())
   throw new ModuleStoreException("Can't delete module file: " + modFile.getAbsolutePath());

  if(mod.getExternalRelations() != null)
  {
   for(AgeExternalRelationWritable rel : mod.getExternalRelations())
   {
    AgeExternalRelationWritable invRel = rel.getInverseRelation();
    
    if( invRel.isInferred() )
     rel.getTargetObject().removeRelation(invRel);
   }
  }

  Map<String, AgeObjectProxy> clustMap = clusterIndexMap.get(dmr.getModuleKey().getClusterId());

  for(AgeObject obj : mod.getObjects())
  {
   if(obj.getIdScope() == IdScope.MODULE)
    continue;

   if(clustMap != null)
    clustMap.remove(obj.getId());

   if(obj.getIdScope() == IdScope.GLOBAL)
    globalIndexMap.remove(obj.getId());
  }

  moduleMap.remove(mk);

//  updateIndices(null, true);

  return true;
 }

 public void shutdown()
 {
  for(AgeIndexWritable idx : indexMap.values())
   idx.close();
 }

 @Override
 public boolean updateSemanticModel(SemanticModel sm, LogNode bfLog) // throws
                                                                     // ModelStoreException
 {
  if(!master)
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

   for(ModuleRef modref : moduleMap.values())
   {
    BufferLogger submLog = new BufferLogger(Constants.MAX_ERRORS);

    LogNode ln = submLog.getRootNode().branch(
      "Validating data module: " + modref.getModuleKey().getClusterId() + ":" + modref.getModuleKey().getModuleId());

    boolean vldRes = false;

    try
    {
     vldRes = validator.validate(modref.getModule(), sm, ln);

     res = res && vldRes;

     if(!vldRes)
      ln.log(Level.ERROR, "Validation failed");
    }
    catch(TooManyErrorsException e)
    {
     res = false;
     ln.log(Level.ERROR, "Too many errors: " + e.getErrorCount());
    }

    if(!vldRes)
    {
     SimpleLogNode.setLevels(ln, Level.WARN);
     vldBranch.append(ln);
    }

   }

   if(res)
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
    saveBranch.log(Level.ERROR, "Model saving failed: " + e.getMessage());
    return false;
   }

   saveBranch.success();

   LogNode setupBranch = bfLog.branch("Installing model");

   for(ModuleRef modref : moduleMap.values())
   {
    DataModuleWritable mod = modref.getModuleNoLoad();
    
    if( mod != null )
     mod.setMasterModel(model);
   }

   model = sm;

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
  return globalIndexMap.get(objID);
 }

 @Override
 public AgeObjectWritable getClusterObject(String clustId, String objID)
 {
  Map<String, AgeObjectProxy> clstMap = clusterIndexMap.get(clustId);

  if(clstMap == null)
   return null;

  return clstMap.get(objID);
 }

 // @Override
 // public boolean hasObject(String objID)
 // {
 // return mainIndexMap.containsKey( objID );
 // }

 @Override
 public boolean hasDataModule(String clustId, String dmID)
 {
  return hasDataModule(new ModuleKey(clustId, dmID));
 }

 @Override
 public boolean hasDataModule(ModuleKey mk)
 {
  return moduleMap.containsKey(mk);
 }

 @Override
 public void addDataChangeListener(DataChangeListener dataChangeListener)
 {
  synchronized(chgListeners)
  {
   chgListeners.add(dataChangeListener);
  }
 }

 @Override
 public void addMaintenanceModeListener(MaintenanceModeListener mmListener)
 {
  synchronized(mmodListeners)
  {
   mmodListeners.add(mmListener);
  }
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
 public File getAttachment(String id)
 {
  return getAttachmentBySysRef(makeFileSysRef(id));
 }

 
 @Override
 public File getAttachment(String id, String clusterId)
 {
  return getAttachmentBySysRef(makeFileSysRef(id, clusterId));
 }
 
 private File getAttachmentBySysRef(String ref)
 {
  File f = fileDepot.getFilePath(ref);
  
  if( ! f.exists() )
   return null;
  
  return f;
 }



 private String makeFileSysRef(String id)
 {
  return "G"+M2codec.encode(id);
 }

 private String makeFileSysRef(String id, String clustID)
 {
  return String.valueOf(id.length())+'_'+M2codec.encode(id+clustID);
 }

 private boolean isFileSysRefGlobal(String fileID)
 {
  return fileID.charAt(0) == 'G';
 }


 @Override
 public boolean deleteAttachment(String id, String clusterId, boolean global)
 {
  if( global )
   fileDepot.getFilePath(makeFileSysRef(id)).delete();
  
  File f = fileDepot.getFilePath(makeFileSysRef(id, clusterId));

  return f.delete();
 }

 @Override
 public File storeAttachment(String id, String clusterId, boolean global, File aux) throws AttachmentIOException
 {
  File fDest = fileDepot.getFilePath(makeFileSysRef(id, clusterId));
  fDest.delete();
  
  try
  {
   FileUtil.linkOrCopyFile(aux, fDest);
   
   if( global )
   {
    File glbfDest = fileDepot.getFilePath(makeFileSysRef(id));
    glbfDest.delete();
    
    FileUtil.linkOrCopyFile(aux, glbfDest);

   }
  }
  catch(IOException e)
  {
   throw new AttachmentIOException("Store attachment error: "+e.getMessage(), e);
  }
  
  return fDest;
 }


 public void changeAttachmentScope( String id, String clusterId, boolean global ) throws AttachmentIOException
 {
  File globFile = fileDepot.getFilePath(makeFileSysRef(id));
  
  try
  {
   if(global)
   {
    File fSrc = fileDepot.getFilePath(makeFileSysRef(id, clusterId));
    FileUtil.linkOrCopyFile(fSrc, globFile);
   }
   else
    globFile.delete();
  }
  catch(IOException e)
  {
   throw new AttachmentIOException("Can't link file: "+e.getMessage(), e);
  }
 }
 
 @Override
 public void rebuildIndices()
 {
  updateIndices(null, true);
 }

 @Override
 public boolean setMaintenanceMode(boolean mmode)
 {
  lockWrite();

  boolean dataModified = false;

  try
  {
   if(maintenanceMode == mmode)
    return false;

   if(mmode == true)
   {
    Thread wdT = new Thread()
    {

     @Override
     public void run()
     {
      long wtCycle = mModeTimeout + 500;

      setName("MaintenanceMode watch dog timer");

      while(true)
      {
       try
       {
        sleep(wtCycle);
       }
       catch(InterruptedException e)
       {
       }

       if(!maintenanceMode)
        return;

       if((System.currentTimeMillis() - lastUpdate) > mModeTimeout && !dbLock.isWriteLocked())
       {
        setMaintenanceMode(false);
        return;
       }

      }
     }
    };

    wdT.start();
   }

   maintenanceMode = mmode;

   if(dataDirty)
   {
    updateIndices(null, true);
    saveIndexCache();
    
    dataModified = true;

    dataDirty = false;
   }

  }
  finally
  {
   unlockWrite();
  }

  if(dataModified)
  {
   synchronized(chgListeners)
   {
    for(DataChangeListener chls : chgListeners)
     chls.dataChanged();
   }
  }

  synchronized(mmodListeners)
  {
   for(MaintenanceModeListener mml : mmodListeners)
   {
    if(mmode)
     mml.enterMaintenanceMode();
    else
     mml.exitMaintenanceMode();
   }
  }

  return true;
 }

 public AgeObjectWritable getLowLevelObject(ModuleKey moduleId, String objectId)
 {
  ModuleRef mr = moduleMap.get(moduleId);

  if(mr == null)
   return null;

  return mr.getModule().getObject(objectId);
 }

 private class ModulesCollection implements Collection<DataModuleWritable>
 {
  @Override
  public int size()
  {
   return moduleMap.size();
  }

  @Override
  public boolean isEmpty()
  {
   return moduleMap.isEmpty();
  }

  @Override
  public boolean contains(Object o)
  {
   return moduleMap.containsValue(o);
  }

  @Override
  public Iterator<DataModuleWritable> iterator()
  {
   final boolean dir = (iterateModulesDirect = !iterateModulesDirect);

   return new Iterator<DataModuleWritable>()
   {
    private Iterator<ModuleRef> iterator = dir ? moduleMap.values().iterator() 
      : moduleMap.descendingMap().values().iterator();

    @Override
    public boolean hasNext()
    {
     return iterator.hasNext();
    }

    @Override
    public DataModuleWritable next()
    {
     ModuleRef ref = iterator.next();

     return ref.getModule();
    }

    @Override
    public void remove()
    {
     throw new UnsupportedOperationException();
    }
   };

  }

  @Override
  public Object[] toArray()
  {
   throw new UnsupportedOperationException();
   // return moduleMap.values().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
   throw new UnsupportedOperationException();
   // return moduleMap.values().toArray(a);
  }

  @Override
  public boolean add(DataModuleWritable e)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection< ? > c)
  {
   return moduleMap.values().containsAll(c);
  }

  @Override
  public boolean addAll(Collection< ? extends DataModuleWritable> c)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection< ? > c)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection< ? > c)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
   throw new UnsupportedOperationException();
  }

 }
}
