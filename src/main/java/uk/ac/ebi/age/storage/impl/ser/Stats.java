package uk.ac.ebi.age.storage.impl.ser;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;

public class Stats
{

 private Map<String,String> strMap = new HashMap<String,String>();
 
 private int modulesCount;
 private int fileCount;
 private long fileSize;
 private int objectsCount;
 private int attributesCount;
 private int stringsObjects;
 private int stringsUnique;
 private int stringsCount;
 private long stringsSize;
 private int relationsCount;
 private int longStrings;
 private int failedMoules;

 public void incFileCount(int i)
 {
  fileCount+=i;
 }

 public void incFileSize(long length)
 {
  fileSize+=length;
 }

 public void incObjects(int i)
 {
  objectsCount += i;
 }

 public void incModules(int i)
 {
  modulesCount += i;
 }

 public void incFailedModules(int i)
 {
  failedMoules+=i;
 }
 
 public void collectObjectStats(AgeObject obj)
 {
  collectAttributedStats( obj );
  
  if( obj.getRelations() != null )
  {
   relationsCount +=  obj.getRelations().size();

   for( AgeRelation rel : obj.getRelations() )
    collectAttributedStats(rel);
  }
  
  String intrnid =  obj.getId().intern();
     
  if( intrnid != obj.getId() )
   ((AgeObjectWritable)obj).setId(intrnid);
 }
 
 public void collectAttributedStats(Attributed obj)
 {
  if( obj.getAttributes() != null  )
  {
   attributesCount += obj.getAttributes().size();
 
   for( AgeAttribute attr : obj.getAttributes() )
   {
    DataType typ = attr.getAgeElClass().getDataType();
    
    if( typ == DataType.STRING || typ == DataType.TEXT || typ == DataType.URI )
    {
     stringsCount++;
     
     String val =  attr.getValue().toString();
     
     stringsSize += val.length();
     
     if( val.length() > 100 )
      longStrings++;
     
     String intrnval = val.intern();
     
//     String mapped = strMap.get(val);
//
//     if( mapped == null )
//     {
//      strMap.put(val, val);
//      stringsUnique++;
//      stringsObjects++;
//     }
//     else if( mapped != val )
//      stringsObjects++;
      
     if( intrnval != val )
      ((AgeAttributeWritable)attr).setValue(intrnval);
     else
      stringsUnique++;
     
    }
    
    
    collectAttributedStats(attr);
   }
  }

 }

 public long getFileSize()
 {
  return fileSize;
 }

 public int getFileCount()
 {
  return fileCount;
 }

 public int getObjectCount()
 {
  return objectsCount;
 }

 public int getAttributesCount()
 {
  return attributesCount;
 }

 public int getStringsCount()
 {
  return stringsCount;
 }

 public long getStringsSize()
 {
  return stringsSize;
 }

 public int getRelationsCount()
 {
  return relationsCount;
 }

 public long getStringObjects()
 {
  return stringsObjects;
 }

 public int getModulesCount()
 {
  return modulesCount;
 }

 public int getStringsUnique()
 {
  return stringsUnique;
 }

 public int getLongStringsCount()
 {
  return longStrings;
 }

 public int getFailedModulesCount()
 {
  return failedMoules;
 }



}
