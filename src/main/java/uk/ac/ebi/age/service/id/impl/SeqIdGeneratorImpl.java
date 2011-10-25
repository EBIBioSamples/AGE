package uk.ac.ebi.age.service.id.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.age.service.id.IdGenException;
import uk.ac.ebi.age.service.id.IdGenerator;

public class SeqIdGeneratorImpl extends IdGenerator
{
 private static final String zeros = "000000000";
 
 private final static int MIN_ID_DIGITS=4; 
 private final static int ID_BLOCK_LEN=10; 

 
 
 private static class Counter
 { 
  int nextId=1;
  int maxId=1;
 }
 
 private Counter defaultCounter = new Counter();
 
 private Map<String,Counter> themeCounters = new HashMap<String,Counter>();
 
 private File idFile;
 private File tmpFile;
 private File backupFile;
 
 public SeqIdGeneratorImpl( String path )
 {
  idFile = new File(path);
  idFile.getParentFile().mkdirs( );
  
  tmpFile = new File(path+".tmp");
  backupFile = new File(path+".bak");
  
  init();
 }
 
 private void init()
 {
  RandomAccessFile file = null;
  
  if( ! idFile.exists() )
   return;
  
  try
  {
   file = new RandomAccessFile(idFile, "r");
   String line = null;
   
   boolean first = true;
   while( (line = file.readLine()) != null )
   {
    line = line.trim();
    
    String key=null;
    String value = null;
    
    if( first )
    {
     first = false;
     value=line;
    }
    else
    {
     int pos = line.indexOf(':');
     
     if( pos != -1 && pos != 0 && pos != line.length()-1 )
     {
      key = line.substring(0,pos).trim();
      value = line.substring(pos+1).trim();
     }
    }
    
    if( value == null )
     continue;
    
    int id;
    
    try
    {
     id = Integer.parseInt(value);
    }
    catch(Exception e)
    {
     continue;
    }
    
    if( key == null )
     defaultCounter.maxId = defaultCounter.nextId = id;
    else
    {
     Counter c = new Counter();
     c.maxId = c.nextId = id;
     
     themeCounters.put(key, c);
    }

   }
  }
  catch(IOException e)
  {
   throw new IdGenException("Can't read/write ID store file. ", e);
  }
  finally
  {
   if(file != null)
   {
    try
    {
     file.close();
    }
    catch(Exception e2)
    {
     throw new IdGenException("Can't close ID store file. ", e2);
    }
   }

  }

 }
 
 
 private void update( int blockSize )
 {
  FileWriter file = null;
  try
  {
   file = new FileWriter(tmpFile);

   file.write(String.valueOf(defaultCounter.nextId+blockSize)+"\n");
   
   for( Map.Entry<String, Counter> me : themeCounters.entrySet() )
    file.write(me.getKey()+':'+String.valueOf(me.getValue().nextId+blockSize)+"\n");
   
   file.close();
   file = null;
   
   backupFile.delete();
   idFile.renameTo(backupFile);
   tmpFile.renameTo(idFile);
   
   defaultCounter.maxId=defaultCounter.nextId+blockSize;
   
   for(Counter cnt : themeCounters.values() )
    cnt.maxId=cnt.nextId+blockSize;
  }
  catch(IOException e)
  {
   throw new IdGenException("Can't read/write ID store file. ", e);
  }
  finally
  {
   if(file != null)
   {
    try
    {
     file.close();
    }
    catch(Exception e2)
    {
     throw new IdGenException("Can't close ID store file. ", e2);
    }
   }

  }

 }
 
 private String makeID( String id )
 {
  int len = id.length();
  
  if( len >= MIN_ID_DIGITS )
   return id;
  
  return zeros.substring(0,MIN_ID_DIGITS-len)+id;
 }
 
 @Override
 public synchronized String getStringId()
 {
  if( defaultCounter.nextId == defaultCounter.maxId )
   update(ID_BLOCK_LEN);
  
  return makeID(String.valueOf(defaultCounter.nextId++));
 }
 
 @Override
 public synchronized String getStringId( String theme )
 {
  Counter cnt = themeCounters.get(theme);
  
  if( cnt == null )
  {
   cnt = new Counter();
   themeCounters.put(theme, cnt);
  }
  
  if( cnt.nextId == cnt.maxId )
   update(ID_BLOCK_LEN);
  
  return makeID(String.valueOf(cnt.nextId++));
 }


 @Override
 public synchronized void shutdown()
 {
  update(0);
 }

}
