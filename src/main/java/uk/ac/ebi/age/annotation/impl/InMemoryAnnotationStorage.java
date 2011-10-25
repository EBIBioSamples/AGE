package uk.ac.ebi.age.annotation.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;

import uk.ac.ebi.age.annotation.AnnotationDBInitException;
import uk.ac.ebi.age.annotation.Topic;
import uk.ac.ebi.age.ext.annotation.AnnotationDBException;
import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.transaction.InconsistentStateException;
import uk.ac.ebi.age.transaction.InvalidStateException;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;
import uk.ac.ebi.mg.rwarbiter.InvalidTokenException;
import uk.ac.ebi.mg.rwarbiter.RWArbiter;
import uk.ac.ebi.mg.rwarbiter.TokenFactory;
import uk.ac.ebi.mg.rwarbiter.TokenW;

import com.pri.util.collection.Collections;

public class InMemoryAnnotationStorage extends AbstractAnnotationStorage
{
 private static final String serialFileName = "annotdb.ser";

 private Map< Topic, SortedMap<String,Serializable> > annotMap;

 private static class TrnImp implements Transaction, TokenW
 {
  boolean active = true;

  public boolean isActive()
  {
   return active;
  }

  public void setActive(boolean active)
  {
   this.active = active;
  }
 }
 
 private RWArbiter<TrnImp> arbiter = new RWArbiter<TrnImp>( new TokenFactory<TrnImp>()
 {
  @Override
  public TrnImp createToken()
  {
   return new TrnImp();
  }
 });
 
 private FileResourceManager txManager;
 private String serialFileRelPath;
 private File serialFile;
 
 private boolean dirty = false;
 
 public InMemoryAnnotationStorage(FileResourceManager frm, String annRelPath) throws AnnotationDBInitException
 {
  txManager=frm;
 
  serialFileRelPath = annRelPath+"/"+serialFileName;
  
  serialFile = new File(frm.getStoreDir(),serialFileRelPath);
  
  if( serialFile.exists() )
  {
   try
   {
    readData();
   }
   catch(IOException e)
   {
    throw new AnnotationDBInitException(e);
   }
  }
  else
  {
   annotMap = new HashMap<Topic, SortedMap<String,Serializable>>();

   String txId;

   try
   {
    txId = txManager.generatedUniqueTxId();
    txManager.startTransaction(txId);
    OutputStream outputStream = txManager.writeResource(txId, serialFileRelPath);
    
    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
    
    oos.writeObject(annotMap);
    
    oos.close();
    
    txManager.commitTransaction(txId);
   }
   catch(Exception e)
   {
    throw new AnnotationDBInitException(e);
   }

  }
 }
 
 @SuppressWarnings("unchecked")
 private void readData() throws IOException
 {
  FileInputStream fis = new FileInputStream(serialFile);
  ObjectInputStream ois = new ObjectInputStream( fis );
  
  try
  {
   annotMap = (Map<Topic, SortedMap<String,Serializable>>)ois.readObject();
  }
  catch(ClassNotFoundException e)
  {
   e.printStackTrace();
  }
  
  fis.close();
  
 }

 @Override
 public boolean addAnnotation(Topic tpc, Entity objId, Serializable value)
 {
  
  Transaction t = startTransaction();
  
  addAnnotation(t, tpc, objId, value);
  
  try
  {
   prepareTransaction(t);
   commitTransaction(t);
  }
  catch(TransactionException e2)
  {
   return false;
  }
  
  return true;
 }


 @Override
 public Object getAnnotation(Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException
 {
  ReadLock lck = getReadLock();
  
  try
  {
   return getAnnotation(lck, tpc, objId, recurs);
  }
  finally
  {
   releaseLock(lck);
  }
 }

 private void sync() throws ResourceManagerException, IOException
 {
  String txId;

  txId = txManager.generatedUniqueTxId();

  txManager.startTransaction(txId);

  txManager.moveResource(txId, serialFileRelPath, serialFileRelPath + ".bak", true);

  OutputStream outputStream = txManager.writeResource(txId, serialFileRelPath);

  ObjectOutputStream oos = new ObjectOutputStream(outputStream);

  oos.writeObject(annotMap);
  
  oos.close();

  txManager.commitTransaction(txId);
 }

 @Override
 public ReadLock getReadLock()
 {
  return arbiter.getReadLock();
 }

 @Override
 public void releaseLock(ReadLock l)
 {
  try
  {
   arbiter.releaseLock((TrnImp) l);
  }
  catch(InvalidTokenException e)
  {
   e.printStackTrace();
  }
 }

 @Override
 public Transaction startTransaction()
 {
  return arbiter.getWriteLock();
 }

 @Override
 public void commitTransaction(Transaction t) throws TransactionException
 {
  if(!((TrnImp) t).isActive())
   throw new InvalidStateException();
  
  
  try
  {
   if( ! dirty )
    return;

   sync();

   dirty = false;
  }
  catch(Exception e)
  {
   try
   {
    readData();
    dirty = false;
   }
   catch(IOException e1)
   {
    e1.printStackTrace();
   }
  }
  finally
  {
   try
   {
    arbiter.releaseLock((TrnImp) t);
   }
   catch(InvalidTokenException e)
   {
    throw new InvalidStateException("Invalid transaction token");
   }
  }
 }

 @Override
 public void rollbackTransaction(Transaction t) throws TransactionException
 {
  if(!((TrnImp) t).isActive())
   throw new InvalidStateException();

  try
  {
   if( ! dirty )
    return;

   readData();
   dirty = false;
  }
  catch(IOException e1)
  {
   e1.printStackTrace();
   
   throw new InconsistentStateException("Transaction rollback failed", e1);
  }
  finally
  {
   try
   {
    arbiter.releaseLock((TrnImp) t);
   }
   catch(InvalidTokenException e)
   {
    throw new InvalidStateException("Invalid transaction token");
   }
  }
  
 
 }

 @Override
 public boolean addAnnotation(Transaction trn, Topic tpc, Entity objId, Serializable value)
 {
  if(!((TrnImp) trn).isActive())
   throw new InvalidStateException();

  dirty = true;
  
  SortedMap<String, Serializable> tMap = annotMap.get(tpc);

  if(tMap == null)
  {
   tMap = new TreeMap<String, Serializable>();

   annotMap.put(tpc, tMap);
  }


  tMap.put(createEntityId(objId), value);

  return true;
 }

 @Override
 public boolean removeAnnotation(Transaction trn, Topic tpc, Entity objId, boolean rec)
 {
  if(!((TrnImp) trn).isActive())
   throw new InvalidStateException();

  dirty = true;

  Collection<SortedMap<String, Serializable>> maps;

  if(tpc == null)
   maps = annotMap.values();
  else
  {
   SortedMap<String, Serializable> tMap = annotMap.get(tpc);

   if(tMap != null)
    maps = java.util.Collections.singleton(tMap);
   else
    maps = Collections.emptyList();
  }

  boolean removed = false;

  String id = createEntityId(objId);

  for(SortedMap<String, Serializable> tMap : maps)
  {

   if(!rec)
    return tMap.remove(id) != null;
   else
   {
    Map<String, Serializable> smp = tMap.tailMap(id);

    Iterator<String> keys = smp.keySet().iterator();

    while(keys.hasNext())
    {
     String key = keys.next();

     if(key.startsWith(id))
     {
      keys.remove();

      removed = true;
     }
     else
      break;
    }

   }


  }

  return removed;
 }

 @Override
 public Object getAnnotation(ReadLock lock, Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException
 {
  if(!((TrnImp) lock).isActive())
   throw new InvalidStateException();

  SortedMap<String, Serializable> tMap = annotMap.get(tpc);

  if(tMap == null)
   return null;

  Entity cEnt = objId;

  do
  {
   String id = createEntityId(cEnt);

   Object annt = tMap.get(id);

   if(annt != null || !recurs)
    return annt;

   cEnt = cEnt.getParentEntity();
  } while(cEnt != null);

  return null;
 }

 @Override
 public void prepareTransaction(Transaction t) throws TransactionException
 {
  if(!((TrnImp) t).isActive())
   throw new InvalidStateException();
 }
}
