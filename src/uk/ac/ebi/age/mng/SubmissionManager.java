package uk.ac.ebi.age.mng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.DataModuleMeta;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.impl.AgeTab2AgeConverterImpl;
import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.age.validator.impl.AgeSemanticValidatorImpl;

public class SubmissionManager
{
 private static SubmissionManager instance = new SubmissionManager();
 
 private static class ModMeta
 {
  String text;
  String id;
  AgeTabModule atMod;
  DataModuleWritable origModule;
  DataModuleWritable module;
 }
 
 public static SubmissionManager getInstance()
 {
  return instance;
 }
 
 private AgeTabSyntaxParser ageTabParser = new AgeTabSyntaxParserImpl();
 private AgeTab2AgeConverter converter = new AgeTab2AgeConverterImpl();
 private AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
 
 public DataModuleWritable prepareSubmission( List<DataModuleMeta> mods, boolean update,  SubmissionContext context, AgeStorageAdm stor, LogNode logRoot )
 {
//  AgeTabModule atSbm=null;
  
//  DataModuleWritable origSbm = null;
  
//  if( update && name != null )
//  {
//   origSbm = stor.getDataModule(name);
//   
//   if( origSbm == null )
//   {
//    logRoot.log(Level.ERROR, "The storage doesn't contain data module with ID='"+name+"'");
//    return null;
//   }
//  }
  
  List<ModMeta> modules = new ArrayList<ModMeta>( mods.size() );
  
  for( DataModuleMeta dm : mods )
  {
   ModMeta mm = new ModMeta();
   mm.text = dm.getText();
   mm.id = dm.getId();
  }
  
  
  if( update )
  {
   for( ModMeta mm : modules )
   {
    if( mm.id != null )
    {
     mm.origModule = stor.getDataModule(mm.id);

     if(mm.origModule == null)
     {
      logRoot.log(Level.ERROR, "The storage doesn't contain data module with ID='" + mm.id + "'");
      return null;
     }
    }
   }
  }

  boolean parseRes = true;
  
  for( int n=0; n < modules.size(); n++)
  {
   ModMeta mm = modules.get(n);
   
   LogNode modNode = logRoot.branch("Processing module: " + (n+1) );
   
   LogNode atLog = modNode.branch("Parsing AgeTab");
   try
   {
    mm.atMod = ageTabParser.parse(mm.text);
    atLog.log(Level.INFO, "Success");
   }
   catch(ParserException e)
   {
    atLog.log(Level.ERROR, "Parsing failed: " + e.getMessage() + ". Row: " + e.getLineNumber() + ". Col: " + e.getColumnNumber());
    parseRes = false;
    continue;
   }
   
   LogNode convLog = modNode.branch("Converting AgeTab to Age data module");
   mm.module = converter.convert(mm.atMod, SemanticManager.getInstance().getContextModel(context), convLog );
   
   if( mm.module != null )
    convLog.log(Level.INFO, "Success");
   else
   {
    convLog.log(Level.ERROR, "Conversion failed");
    parseRes = false;
   }
   
   boolean uniqRes1 = true;
   
   LogNode uniqGLog = modNode.branch("Checking global identifiers uniqueness");

   LogNode uniqLog = uniqGLog.branch("Checking main graph");

   for( AgeObjectWritable obj : mm.module.getObjects())
   {
    if( obj.getId() != null )
    {
     AgeObject origObj = stor.getObjectById( obj.getId() );
     
     if( origObj != null )
     {
      uniqLog.log(Level.ERROR, "Object id '"+obj.getId()+"' has been taken by the object from data module: "+origObj.getDataModule().getId());
      uniqRes1 = false;
     }
    }
   }
   
   if( uniqRes1 )
    uniqLog.log(Level.INFO, "Success");
   else
    uniqLog.log(Level.ERROR, "Failed");
  
   boolean uniqRes2 = true;

   if( modules.size() > 1 )
   {
    uniqLog = uniqGLog.branch("Checking other modules");
    
    for( int k=n+1; k < modules.size(); k++ )
    {
     DataModuleWritable om = modules.get(k).module;
     
     for( AgeObjectWritable obj : mm.module.getObjects())
     {
      if( obj.getId() != null )
      {
       for( AgeObject othObj : om.getObjects() )
       {
        if( othObj.getId() != null && othObj.getId().equals(obj.getId()) )
        {
         uniqLog.log(Level.ERROR, "Object id '"+obj.getId()+"' has been taken by the object from sibling data module: "+(k+1));
         uniqRes2 = false;
        }
       }
      }
     }
    }
   }
   
   if( uniqRes2 )
    uniqLog.log(Level.INFO, "Success");
   else
    uniqLog.log(Level.ERROR, "Failed");

   if( uniqRes1 && uniqRes2 )
    uniqGLog.log(Level.INFO, "Success");
   else
    uniqGLog.log(Level.ERROR, "Failed");

   
   parseRes = parseRes && uniqRes1 && uniqRes2;
  }
  
  if( ! parseRes )
   return null;

  
  
  try
  {
   LogNode connLog = logRoot.branch("Connecting data module to the main graph");
   stor.lockWrite();

   Map<AgeObject,Set<AgeRelationWritable>> invRelMap = new HashMap<AgeObject, Set<AgeRelationWritable>>();
   
   if( connectDataModule( modules, stor, invRelMap, connLog) )
    connLog.log(Level.INFO, "Success");
   else
   {
    connLog.log(Level.ERROR, "Connection failed");
    return null;
   }
   
   
   LogNode semLog = logRoot.branch("Validating semantic");

   if(validator.validate(ageSbm, semLog))
    semLog.log(Level.INFO, "Success");
   else
   {
    semLog.log(Level.ERROR, "Validation failed");
    return null;
   }

   Map<AgeObject,Set<AgeRelationWritable>> detachedRelMap = new HashMap<AgeObject, Set<AgeRelationWritable>>();

   if( origSbm != null )
   {
    Collection<AgeExternalRelationWritable> origExtRels = origSbm.getExternalRelations();
    
    if( origExtRels != null )
    {
     for(AgeExternalRelationWritable extRel : origExtRels )
     {
      AgeObject target = extRel.getTargetObject();
      
      Set<AgeRelationWritable> objectsRels = detachedRelMap.get(target); 
      
      if( objectsRels == null )
       detachedRelMap.put(target, objectsRels = new HashSet<AgeRelationWritable>() );
      
      objectsRels.add(extRel.getInverseRelation());
     }
    }
   }
   
   
   Set<AgeObject> affObjSet = new HashSet<AgeObject>();
   
   affObjSet.addAll( invRelMap.keySet() );
   affObjSet.addAll( detachedRelMap.keySet() );
   
   if( affObjSet.size() > 0 )
   {
    LogNode invRelLog = connLog.branch("Validating externaly related object semantic");
    
    boolean res = true;
    for( AgeObject obj :  affObjSet )
    {
     LogNode objLogNode = invRelLog.branch("Validating object Id: "+obj.getId()+" Class: "+obj.getAgeElClass());
     
     if( validator.validateRelations(obj, invRelMap.get(obj), detachedRelMap.get(obj), objLogNode) )
      objLogNode.log(Level.INFO, "Success");
     else
      res = false;
    }
    
    if(res)
     invRelLog.log(Level.INFO, "Success");
    else
    {
     invRelLog.log(Level.ERROR, "Validation failed");
     return null;
    }

    LogNode storLog = connLog.branch("Storing data module");
    try
    {
     stor.storeDataModule(ageSbm);
     storLog.log(Level.INFO, "Success");
    }
    catch(Exception e)
    {
     storLog.log(Level.ERROR, "Data module storing failed: "+e.getMessage());
     return null;
    }
    
    for( Map.Entry<AgeObject, Set<AgeRelationWritable>> me :  invRelMap.entrySet() )
     stor.addRelations(me.getKey().getId(),me.getValue());

   }

  }
  finally
  {
   stor.unlockWrite();
  }

  //Impute reverse relation and revalidate.

  return ageSbm;
 }

 private boolean connectDataModule(List<ModMeta> mods, AgeStorageAdm stor, Map<AgeObject,Set<AgeRelationWritable>> invRelMap, LogNode connLog)
 {
  boolean res = true;
  
  LogNode extAttrLog = connLog.branch("Connecting external object attributes");
  boolean extAttrRes = true;

//  LogNode uniqLog = connLog.branch("Verifing object Id uniqueness");
//  boolean uniqRes = true;
  
  Stack<Attributed> attrStk = new Stack<Attributed>();
  
  int n=0;
  for( ModMeta mm : mods )
  {
   n++;
   
   LogNode extAttrModLog = extAttrLog.branch("Processing module: "+n);
   
   for( AgeObjectWritable obj : mm.module.getObjects() )
   {
    attrStk.clear();
    attrStk.push(obj);
    
    boolean mdres = connectExternalAttrs( attrStk, stor, mods, extAttrModLog  );
    
    if( mdres )
     extAttrModLog.log(Level.INFO, "Success");
    else
     extAttrModLog.log(Level.ERROR, "Failed");

    extAttrRes = extAttrRes && mdres;
   }
   
  }
  
  n=0;
  for( ModMeta mm : mods )
  {
   n++;
   
   if( mm.module.getExternalObjectAttributes() == null )
    continue;
   
   LogNode extAttrModLog = extAttrLog.branch("Processing module: "+n);
   
   for( AgeExternalObjectAttributeWritable extAttr : mm.module.getExternalObjectAttributes() )
   {
    boolean exAtModRes = true;
    
    String ref = extAttr.getTargetObjectId();
    
    AgeObject tgObj = stor.getObjectById( ref );
    
    
    if( tgObj == null )
    {
     modloop :for( ModMeta refmm : mods )
     {
      if( mm == refmm )
       continue;
      
      for( AgeObjectWritable candObj : mm.module.getObjects() )
      {
       if( candObj.getId() != null && candObj.getId().equals(ref) )
       {
        tgObj = candObj;
        break modloop;
       }
      }
     }
    }

    
    if( tgObj == null )
    {
     AgeObject obj  = (AgeObject)atStk.get(0);
     
     String attrName = attr.getAgeElClass().getName();
     
     if( atStk.size() > 1 )
     {
      StringBuilder sb = new StringBuilder(200);
      
      sb.append(atStk.get(1).getAttributedClass().getName());
      
      for( int i = 2; i < atStk.size(); i++ )
       sb.append("[").append(atStk.get(i).getAttributedClass().getName()).append("]");
      
      sb.append("[").append(attr.getAgeElClass().getName()).append("]");
      
      attrName = sb.toString();
     }
     
     extAttrModLog.log(Level.ERROR,"Invalid external reference: '"+ref+"'. Target object not found. Source object: '"+obj.getId()+"' (Class: "+obj.getAgeElClass()
       +", Order: "+obj.getOrder()+"). Attribute: "+attrName+" Order: "+extAttr.getOrder());
     
     exAtModRes = false;
    }
    else
    {
     if( ! tgObj.getAgeElClass().isClassOrSubclass(extAttr.getAgeElClass().getTargetClass()) )
     {
      AgeObject obj  = (AgeObject)atStk.get(0);

      String attrName = attr.getAgeElClass().getName();
      if( atStk.size() > 1 )
      {
       attrName = atStk.get(1).getAttributedClass().getName();
       for( int i = 2; i < atStk.size(); i++ )
        attrName += "["+atStk.get(i).getAttributedClass().getName()+"]";
       
       attrName+= "["+attr.getAgeElClass().getName()+"]";
      }

      extAttrModLog.log(Level.ERROR,"Inappropriate target object's (Id: '"+ref+"') class: "+tgObj.getAgeElClass()
        +". Expected class: "+extAttr.getAgeElClass().getTargetClass()+" Source object: '"+obj.getId()+"' (Class: "+obj.getAgeElClass()
        +", Order: "+obj.getOrder()+"). Attribute: "+attrName+" Order: "+attr.getOrder());
      
      exAtModRes = false;
     }
     else
      extAttr.setTargetObject(tgObj);
    }


    
    if( exAtModRes )
     extAttrModLog.log(Level.INFO, "Success");
    else
     extAttrModLog.log(Level.ERROR, "Failed");

    extAttrRes = extAttrRes && exAtModRes;
   }
   
  }

  
  
  if( extAttrRes )
   extAttrLog.log(Level.INFO, "Success");
  else
   extAttrLog.log(Level.ERROR, "Failed");

  
  LogNode extRelLog = connLog.branch("Connecting external object relations");
  boolean extRelRes = true;
  
  n=0;
  for( ModMeta mm : mods )
  {
   n++;
   
   LogNode extRelModLog = extRelLog.branch("Processing module: "+n);
   
   boolean extModRelRes = true;
   
   for( AgeExternalRelationWritable exr : mm.module.getExternalRelations() )
   {
    String ref = exr.getTargetObjectId();
    
    AgeObjectWritable tgObj = (AgeObjectWritable)stor.getObjectById(ref);
    
    if( tgObj == null )
    {
     modloop : for( ModMeta refmm : mods )
     {
      if( refmm == mm )
       continue;
      
      for( AgeObjectWritable candObj : mm.module.getObjects() )
      {
       if( candObj.getId() != null && candObj.getId().equals(ref) )
       {
        tgObj = candObj;
        break modloop;
       }
      }
     }
    }
    
    if( tgObj == null )
    {
     extModRelRes = false;
     extRelModLog.log(Level.ERROR,"Invalid external relation: '"+ref+"'. Target object not found."
       +" Module: "+n
       +" Source object: '"+exr.getSourceObject().getId()
       +"' (Class: "+exr.getSourceObject().getAgeElClass()
       +", Order: "+exr.getSourceObject().getOrder()+"). Relation: "+exr.getAgeElClass()+" Order: "+exr.getOrder());
    }
    else
    {
      if( ! exr.getAgeElClass().isWithinRange(tgObj.getAgeElClass()) )
      {
       extModRelRes = false;
       extRelModLog.log(Level.ERROR,"External relation target object's class is not within range. Target object: '"+ref
         +"' (Class: "+tgObj.getAgeElClass()
         +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
         +"' (Class: "+exr.getSourceObject().getAgeElClass()
         +", Order: "+exr.getSourceObject().getOrder()+"). Relation: "+exr.getAgeElClass()+" Order: "+exr.getOrder());
      }
      else
      {
       AgeRelationClass invRCls = exr.getAgeElClass().getInverseRelationClass();
       
       boolean invClassOk=false;
       if( invRCls != null )
       {
        if(invRCls.isCustom())
        {
         extModRelRes = false;
         extRelModLog.log(Level.ERROR,"Class of external inverse relation can't be custom. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else if( ! invRCls.isWithinDomain(tgObj.getAgeElClass()) )
        {
         extModRelRes = false;
         extRelModLog.log(Level.ERROR,"Target object's class is not within domain of inverse relation. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else if( ! invRCls.isWithinRange(exr.getSourceObject().getAgeElClass()) )
        {
         extModRelRes = false;
         extRelModLog.log(Level.ERROR,"Source object's class is not within range of inverse relation. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else
         invClassOk=true;
       }
       
       if( invClassOk )
       {
        AgeExternalRelationWritable invRel = tgObj.getAgeElClass().getSemanticModel().createExternalRelation(tgObj, exr.getSourceObject().getId(), invRCls);
        invRel.setTargetObject(exr.getSourceObject());
        invRel.setInferred(true);
        
        Set<AgeRelationWritable> rels = invRelMap.get(tgObj);
        
        if( rels == null )
        {
         rels = new HashSet<AgeRelationWritable>();
         invRelMap.put(tgObj, rels);
        }
        
        rels.add(invRel);
       }
       
       
       exr.setTargetObject(tgObj);
      }
    }
    
   }
   
   extRelRes = extRelRes && extModRelRes;
   
  }

  res = res && extRelRes;

  
  return res;
 }

 
 private boolean connectExternalAttrs( Stack<Attributed> atStk, AgeStorageAdm stor, List<ModMeta> mods, LogNode log )
 {
  boolean res = true;
  
  Attributed atInst = atStk.peek();
  
  if( atInst.getAttributes() == null )
   return true;
  
  for( AgeAttribute attr : atInst.getAttributes() )
  {
   if( attr instanceof AgeExternalObjectAttributeWritable )
   {
    AgeExternalObjectAttributeWritable extAttr = (AgeExternalObjectAttributeWritable)attr;
    
    String ref = extAttr.getTargetObjectId();
    
    AgeObject tgObj = stor.getObjectById( ref );
    
    boolean found = false;
    
    if( tgObj == null )
    {
     AgeObject obj  = (AgeObject)atStk.get(0);

     for( ModMeta mm : mods )
     {
      if( mm.module == obj.getDataModule() )
       continue;
      
      for( AgeObjectWritable candObj : mm.module.getObjects() )
      {
       if( candObj.getId() != null && candObj.getId().equals(ref) )
       {
        tgObj = candObj;
        found = true;
        break;
       }
      }
     }
    }
    else
    {
     found = true;

    }
    
    if( ! found )
    {
     AgeObject obj  = (AgeObject)atStk.get(0);
     
     String attrName = attr.getAgeElClass().getName();
     
     if( atStk.size() > 1 )
     {
      StringBuilder sb = new StringBuilder(200);
      
      sb.append(atStk.get(1).getAttributedClass().getName());
      
      for( int i = 2; i < atStk.size(); i++ )
       sb.append("[").append(atStk.get(i).getAttributedClass().getName()).append("]");
      
      sb.append("[").append(attr.getAgeElClass().getName()).append("]");
      
      attrName = sb.toString();
     }
     
     log.log(Level.ERROR,"Invalid external reference: '"+ref+"'. Target object not found. Source object: '"+obj.getId()+"' (Class: "+obj.getAgeElClass()
       +", Order: "+obj.getOrder()+"). Attribute: "+attrName+" Order: "+attr.getOrder());
     res = false;
    }
    else
    {
     if( ! tgObj.getAgeElClass().isClassOrSubclass(extAttr.getAgeElClass().getTargetClass()) )
     {
      AgeObject obj  = (AgeObject)atStk.get(0);

      String attrName = attr.getAgeElClass().getName();
      if( atStk.size() > 1 )
      {
       attrName = atStk.get(1).getAttributedClass().getName();
       for( int i = 2; i < atStk.size(); i++ )
        attrName += "["+atStk.get(i).getAttributedClass().getName()+"]";
       
       attrName+= "["+attr.getAgeElClass().getName()+"]";
      }

      log.log(Level.ERROR,"Inappropriate target object's (Id: '"+ref+"') class: "+tgObj.getAgeElClass()
        +". Expected class: "+extAttr.getAgeElClass().getTargetClass()+" Source object: '"+obj.getId()+"' (Class: "+obj.getAgeElClass()
        +", Order: "+obj.getOrder()+"). Attribute: "+attrName+" Order: "+attr.getOrder());
      res = false;
     }
     else
      extAttr.setTargetObject(tgObj);
    }

   }
   
   atStk.push(attr);
   res = res && connectExternalAttrs(atStk,stor, mods, log);
   atStk.pop();
  }
 
  return res;
 }
 
 
 public AgeTabSyntaxParser getAgeTabParser()
 {
  return ageTabParser;
 }

 public AgeTab2AgeConverter getAgeTab2AgeConverter()
 {
  return converter;
 }

 public AgeSemanticValidator getAgeSemanticValidator()
 {
  return validator;
 }
}
