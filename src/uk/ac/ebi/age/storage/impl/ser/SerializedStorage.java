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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.log.impl.BufferLogger;
import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.mng.SubmissionManager;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Attributed;
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
import uk.ac.ebi.age.storage.IndexFactory;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.storage.TextIndex;
import uk.ac.ebi.age.storage.exeption.AttachmentIOException;
import uk.ac.ebi.age.storage.exeption.ModelStoreException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;
import uk.ac.ebi.age.storage.impl.AgeStorageIndex;
import uk.ac.ebi.age.storage.impl.SerializedDataModuleReaderWriter;
import uk.ac.ebi.age.storage.index.AgeIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.mg.filedepot.FileDepot;

public class SerializedStorage implements AgeStorageAdm
{
 private Log log = LogFactory.getLog(this.getClass());
 
 private static final String modelPath = "model";
 private static final String dmStoragePath = "data";
 private static final String fileStoragePath = "files";
 private static final String modelFileName = "model.ser";
 
 private File modelFile;
 private File dataDir;
 private File filesDir;
 
 private Map<String, AgeObjectWritable> mainIndexMap = new HashMap<String, AgeObjectWritable>();
 private Map<String, DataModuleWritable> moduleMap = new TreeMap<String, DataModuleWritable>();

 private Map<AgeIndex,AgeStorageIndex> indexMap = new HashMap<AgeIndex,AgeStorageIndex>();

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
 public DataModuleWritable getDataModule(String name)
 {
  return moduleMap.get(name);
 }
 
 @Override
 public Collection<? extends DataModuleWritable> getDataModules()
 {
  return moduleMap.values();
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

 private void updateIndices( Collection<DataModuleWritable> mods, boolean fullreset )
 {
  ArrayList<AgeObject> res = new ArrayList<AgeObject>();

  for( Map.Entry<AgeIndex, AgeStorageIndex> me : indexMap.entrySet() )
  {
   AgeStorageIndex idx = me.getValue();
   
   if( ! fullreset )
   {
    for( DataModuleWritable s : mods )
     if( idx.getQuery().getExpression().isTestingRelations() && s.getExternalRelations() != null && s.getExternalRelations().size() > 0 )
     {
      fullreset=true;
      break;
     }
   }
   
   if( fullreset )
   {
    idx.reset();
    mods = moduleMap.values();
   }
   
   
   Iterable<AgeObject> trv = traverse(idx.getQuery(), mods);

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

 @Override
 public void update( Collection<DataModuleWritable> mods2Ins, Collection<String> mods2Del ) throws RelationResolveException, ModuleStoreException
 {
  if( ! master )
   throw new ModuleStoreException("Only the master instance can store data");
  

  try
  {
   dbLock.writeLock().lock();

   boolean changed = false;

   if( mods2Del != null )
   {
    for(String dmId : mods2Del)
     changed = changed || removeDataModule(dmId);
   }
   
   if( mods2Ins != null )
   {
    for(DataModuleWritable dm : mods2Ins)
    {
     changed = changed || removeDataModule(dm.getId());
     
     saveDataModule(dm);
     
     moduleMap.put(dm.getId(), dm);
     
     for(AgeObjectWritable obj : dm.getObjects())
      mainIndexMap.put(obj.getId(), obj);
    }
    
   }
   
   updateIndices(mods2Ins, changed);

   for(DataChangeListener chls : chgListeners)
    chls.dataChanged();

  }
  finally
  {
   dbLock.writeLock().unlock();
  }

 }
 
 public void storeDataModule(DataModuleWritable sbm) throws RelationResolveException, ModuleStoreException
 {
  if( ! master )
   throw new ModuleStoreException("Only the master instance can store data");
  
  if( sbm.getId() == null )
   throw new ModuleStoreException("Module ID is null");
  
  try
  {
   dbLock.writeLock().lock();

   boolean changed = removeDataModule( sbm.getId() );
 
   saveDataModule(sbm);
   
   moduleMap.put(sbm.getId(), sbm);

   
   for( AgeObjectWritable obj : sbm.getObjects() )
    mainIndexMap.put(obj.getId(), obj);
   
   updateIndices( Collections.singletonList(sbm), changed );
   
   for(DataChangeListener chls : chgListeners )
    chls.dataChanged();
   
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
  dataDir = new File( baseDir, dmStoragePath ); 
  filesDir = new File( baseDir, fileStoragePath ); 
  

  if( baseDir.isFile() )
   throw new StorageInstantiationException("The initial path must be directory: "+initStr);
  
  if( ! baseDir.exists() )
   baseDir.mkdirs();

  if( ! modelDir.exists() )
   modelDir.mkdirs();
  
  try
  {
   dataDepot = new FileDepot(dataDir);
  }
  catch(IOException e)
  {
   throw new StorageInstantiationException( "Data depot init error: "+e.getMessage(),e);
  }
 
  try
  {
   fileDepot = new FileDepot(filesDir);
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

 
 private void loadData() throws StorageInstantiationException
 {
  try
  {
   dbLock.writeLock().lock();
   
   for( File f : dataDepot.listFiles() )
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
       
       AgeExternalRelationWritable invRel = tgObj.getAgeElClass().getSemanticModel().createExternalRelation(tgObj, exr.getSourceObject().getId(), invRCls);
       invRel.setTargetObject(exr.getSourceObject());

       invRel.setInferred(true);
       
       tgObj.addRelation(invRel);
      }
      
     }
    }
   
    for( AgeFileAttributeWritable fattr : smb.getFileAttributes() )
    {
     String fid = makeLocalFileID(fattr.getFileReference(), smb.getClusterId());
     
     if( fileDepot.getFilePath(fid).exists() )
      fattr.setFileId(fid);
     else
     {
      fid = makeGlobalFileID(fattr.getFileReference());

      if( fileDepot.getFilePath(fid).exists() )
       fattr.setFileId(fid);
      else
       log.error("Can't resolve file attribute: '"+fattr.getFileReference()+"'. Cluster: "+smb.getClusterId()
         +" Module: "+smb.getId());
     }
    }
   
   }
   
   for( AgeObjectWritable obj : mainIndexMap.values() )
   {
    if( obj.getRelations() != null )
    {
     for( AgeRelationWritable rel : obj.getRelations() )
      connectObjectAttributes(rel);
    }
    
    connectObjectAttributes( obj );
   }
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
  File modFile = dataDepot.getFilePath(sm.getId(), sm.getVersion() );
  
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
 
 private boolean removeDataModule(String modId) throws ModuleStoreException
 {
  DataModuleWritable dm = moduleMap.get(modId);
  
  if( dm == null )
   return false;
  
  File modFile = dataDepot.getFilePath(dm.getId(), dm.getVersion() );
  
  if( ! modFile.delete() )
   throw new ModuleStoreException("Can't delete module file: "+modFile.getAbsolutePath());
  
  if( dm.getExternalRelations() != null )
  {
   for( AgeExternalRelationWritable rel : dm.getExternalRelations() )
    rel.getTargetObject().removeRelation(rel.getInverseRelation());
  }
  
  for( AgeObjectWritable obj : dm.getObjects() )
   mainIndexMap.remove(obj.getId());
   
  moduleMap.remove(modId);
 
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
 public boolean hasDataModule(String dmID)
 {
  return moduleMap.containsKey( dmID );
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
 
 @Override
 public void removeRelations(String key, Collection<AgeRelationWritable> rels)
 {
  AgeObjectWritable obj = mainIndexMap.get(key);
  
  for( AgeRelationWritable r : rels )
   obj.removeRelation(r);
 }

 @Override
 public File getAttachment(String id)
 {
  File f = fileDepot.getFilePath(id);
  
  if( ! f.exists() )
   return null;
  
  return f;
 }

 @Override
 public String makeGlobalFileID(String id)
 {
  return "G"+id;
 }

 @Override
 public String makeLocalFileID(String id, String clustID)
 {
  return "L"+id+"-"+clustID;
 }

 @Override
 public boolean isFileIdGlobal(String fileID)
 {
  return fileID.charAt(0) == 'G';
 }


 @Override
 public boolean deleteAttachment(String id)
 {
  File f = fileDepot.getFilePath(id);

  return f.delete();
 }

 @Override
 public void storeAttachment(String id, File aux) throws AttachmentIOException
 {
  File fDest = fileDepot.getFilePath(id);
  fDest.delete();
  
  if( ! aux.renameTo(fDest) )
  {
   try
   {
    FileUtils.copyFile(aux, fDest);
   }
   catch(IOException e)
   {
    throw new AttachmentIOException("Store attachment error: "+e.getMessage(), e);
   }
  }
 }


 @Override
 public void renameAttachment(String src, String dst) throws AttachmentIOException
 {
  File fSrc = fileDepot.getFilePath(src);
  File fDest = fileDepot.getFilePath(dst);
  
  if( ! fSrc.renameTo(fDest) )
   throw new AttachmentIOException("Can't rename file '"+fSrc.getAbsolutePath()+"' to '"+fDest.getAbsolutePath()+"'");
 }
}
