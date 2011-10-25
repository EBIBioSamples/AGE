package uk.ac.ebi.age.parser.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.SyntaxProfile;

public class AgeTabModuleImpl implements AgeTabModule
{
 private SyntaxProfile profile;
 private Map<BlockHeader, Map<String,AgeTabObject>> blockObjectMap = new LinkedHashMap<BlockHeader, Map<String,AgeTabObject>>();
 
 
 public AgeTabModuleImpl( SyntaxProfile p )
 {
  profile = p;
 }
 
 @Override
 public void addBlock( BlockHeader blkHdr )
 {
  blockObjectMap.put(blkHdr,null);
 }

 
 @Override
 public AgeTabObject getObject(String part, BlockHeader classColumnHeader)
 {
  Map<String,AgeTabObject> objMap = blockObjectMap.get(classColumnHeader);
  
  if( objMap == null )
   return null;
  
  return objMap.get(part);
 }


 @Override
 public AgeTabObject getOrCreateObject(String objId, BlockHeader hdr, int ln)
 {
  Map<String,AgeTabObject> objMap = blockObjectMap.get(hdr);
  
  AgeTabObject obj = null;
  
  if( objMap == null )
  {
   objMap = new LinkedHashMap<String,AgeTabObject>();
   blockObjectMap.put(hdr, objMap);
  }
  else
   obj = objMap.get(objId);
  
  if( obj == null )
  {
   obj = new AgeTabObject(ln);
   obj.setId(objId);
   
   objMap.put(objId, obj);
  }
  
  return obj;
 }


 @Override
 public Collection<BlockHeader> getBlocks()
 {
  return blockObjectMap.keySet();
 }


 @Override
 public Collection<AgeTabObject> getObjects(BlockHeader hdr)
 {
  Map<String, AgeTabObject> mp = blockObjectMap.get(hdr);
  
  if( mp == null )
   return null;
  
  return mp.values();
 }


 @Override
 public AgeTabObject createObject(String objId, BlockHeader hdr, int ln)
 {
  Map<String,AgeTabObject> objMap = blockObjectMap.get(hdr);
  
  AgeTabObject obj = null;
  
  if( objMap == null )
  {
   objMap = new LinkedHashMap<String,AgeTabObject>();
   blockObjectMap.put(hdr, objMap);
  }

  obj = new AgeTabObject( ln );
  obj.setId(objId);
  
  objMap.put(objId, obj);
  
  return obj;
 }

 @Override
 public SyntaxProfile getSyntaxProfile()
 {
  return profile;
 }

}
