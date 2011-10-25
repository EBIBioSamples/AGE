package uk.ac.ebi.age.parser.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.PermissionManager;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.AgeTabValue;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ClassReference;
import uk.ac.ebi.age.parser.ConvertionException;
import uk.ac.ebi.age.parser.SyntaxProfile;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;

public class AgeTab2AgeConverterImpl implements AgeTab2AgeConverter
{
// private AttrAttchRel attributeAttachmentClass;
 
// private static final String prototypeId = "*";
 private PermissionManager permissionManager;
 
 public AgeTab2AgeConverterImpl( PermissionManager pMngr )
 {
  permissionManager = pMngr;
 }
 
 @Override
 public DataModuleWritable convert(AgeTabModule data, ContextSemanticModel sm, SyntaxProfile syntaxProfile, LogNode log )// throws SemanticException, ConvertionException
 {
  boolean result = true;
  
  DataModuleWritable res = sm.createDataModule();
  
  Map<AgeClass, Map<String,AgeObjectWritable>> classMap = new HashMap<AgeClass, Map<String,AgeObjectWritable>>();
  Map<AgeClass, AgeObjectWritable> prototypeMap = new HashMap<AgeClass, AgeObjectWritable>();
  
  Map<String,AgeObjectWritable> objectMap = null;
  
  Map< BlockHeader, AgeClass > blk2classMap = new LinkedHashMap<BlockHeader, AgeClass>();
  
  for( BlockHeader hdr : data.getBlocks() )
  {
   ClassReference colHdr = hdr.getClassColumnHeader();

   SyntaxProfileDefinition profileDef = colHdr.isCustom()?
     syntaxProfile.getCommonSyntaxProfile():syntaxProfile.getClassSpecificSyntaxProfile(colHdr.getName());

   LogNode blkLog = log.branch("Processing block for class "+colHdr.getName()+" at line: "+colHdr.getRow());
   
   
   if( colHdr.getQualifiers() != null )
   {
    blkLog.log(Level.WARN, "Class reference must not be qualified. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());
//    throw new SemanticException(colHdr.getRow(),colHdr.getCol(),"Class reference must not be qualified");
   }
   
   AgeClass cls = getClassForBlock(colHdr, sm, blkLog);

   if( cls == null )
   {
    result = false;
    continue;
   }
  
   ClassRef clsRef = sm.getModelFactory().createClassRef(sm.getAgeClassPlug(cls), colHdr.getRow(), colHdr.getOriginalReference(), hdr.isHorizontal(), sm);
   
   blk2classMap.put(hdr, cls);
   
   objectMap = classMap.get(cls);
   
   if(objectMap == null)
    classMap.put( cls, objectMap=new HashMap<String, AgeObjectWritable>() );
   
   
   for( AgeTabObject atObj : data.getObjects(hdr) ) // Creating (if necessary) objects for every new ID
   {
    
    if( atObj.isPrototype() && ! profileDef.isResetPrototype() )
    {
     AgeObjectWritable obj = prototypeMap.get(cls);
     
     if( obj == null )
     {
      obj = sm.createAgeObject(clsRef, profileDef.getPrototypeObjectId());
      obj.setOrder( atObj.getRow() );
      
      prototypeMap.put(cls, obj);
     }
     
     continue;
    }
    
    AgeObjectWritable obj = objectMap.get(atObj.getId());
    
    if( obj == null ) // if obj != null it means that it has been defined earlier in the other or the same block 
    {
     String id = atObj.getId();

     if( ! atObj.isIdDefined() )
     {
      id=null; //We will generate ID later. The commented code below is obsolete

//      id = cls.getIdPrefix()+IdGenerator.getInstance().getStringId()+"-"+atObj.getId();
//      
//      if( atObj.isIdDefined() )
//       id+="-"+atObj.getId();
     }
     
     obj = sm.createAgeObject(clsRef, id);
     obj.setOrder( atObj.getRow() );
     obj.setId(id);
     obj.setIdScope(atObj.getIdScope());
     
     objectMap.put(atObj.getId(), obj);
    }
   }
  }
  
  List<ValueConverter> convs = new ArrayList<ValueConverter>(20);
  
  for( Map.Entry<BlockHeader, AgeClass> me : blk2classMap.entrySet() )
  {
   LogNode subLog = log.branch("Creating value converters for class '"+me.getValue()+"'. Block at: "+me.getKey().getClassColumnHeader().getRow());
   
   if( ! createConvertors( me.getKey(), me.getValue(), convs, sm, classMap, subLog ) )
   {
    subLog.log(Level.ERROR,"Convertors creation failed");
    result = false; //We don't stop here, erroneous columns will be ignored
   }
 
   SyntaxProfileDefinition profileDef = me.getKey().getClassColumnHeader().isCustom()?
     syntaxProfile.getCommonSyntaxProfile():syntaxProfile.getClassSpecificSyntaxProfile(me.getKey().getClassColumnHeader().getName());

   AgeObjectWritable prototype = prototypeMap.get(me.getValue());
   
   objectMap = classMap.get( me.getValue() );
  
   subLog = log.branch("Converting values for class '"+me.getValue().getName()+"'. Block at :"+me.getKey().getClassColumnHeader().getRow());
 
   for( AgeTabObject atObj : data.getObjects(me.getKey()) )
   {
    LogNode objLog = subLog.branch("Processing object: "+atObj.getId());

    AgeObjectWritable obj = null;
    
    
    if( atObj.isPrototype()  )
    {
     if( profileDef.isResetPrototype() )
     {
      ClassReference colHdr = me.getKey().getClassColumnHeader();
      ClassRef clsRef = sm.getModelFactory().createClassRef(sm.getAgeClassPlug(me.getValue()), colHdr.getRow(), colHdr.getOriginalReference(), me.getKey().isHorizontal(), sm);

      obj = sm.createAgeObject(clsRef, profileDef.getPrototypeObjectId());
      obj.setOrder( atObj.getRow() );
      
      prototypeMap.put(me.getValue(), obj);
     }
     else
      obj = prototypeMap.get(me.getValue());
    }
    else
     obj = objectMap.get(atObj.getId());

    for( ValueConverter cnv : convs )
     cnv.reset(obj);
    
    if( prototype != null && ! atObj.isPrototype() )
     applyPrototype(obj, prototype);
    
    boolean hasValue=true;
    int ln=0;
    while( hasValue )
    {
     hasValue=false;
     
     for( ValueConverter cnv : convs )
     {
      
      List<AgeTabValue> vals = atObj.getValues(cnv.getClassReference());
      

       if(vals == null || vals.size() <= ln)
       {
        try
        {
         cnv.convert(null);
        }
        catch (ConvertionException e)
        {
         objLog.log(Level.ERROR, "Empty value processing error: "+e.getMessage()+". Row: "+e.getRow()+" Col: "+e.getColumn());
         result = false;
        }
       }
       else
       {
        hasValue = true;

        AgeTabValue val = vals.get(ln);

//        LogNode colLog = objLog.branch("Processing column: " + cnv.getClassReference().getCol() + ". Value: '" + val.getValue() + "'");


        try
        {
         cnv.convert(val);
//         colLog.log(Level.INFO, "Ok");
        }
        catch (ConvertionException e) 
        {
         objLog.log(Level.ERROR, "Conversion error: "+e.getMessage()+". Row: "+e.getRow()+" Col: "+e.getColumn());
         result = false;
        }

       }
      
     }
     
     ln++;
    }
    
    
    if( ! atObj.isPrototype() )
    {
     res.addObject(obj);
     obj.setDataModule(res);
    }
   }
  }
  
  if( ! result )
  {
//   log.log(Level.ERROR, "Conversion failed");
   return null;
  }
  

//  attributeAttachmentClass = new AttrAttchRel(sm.getAttributeAttachmentClass());
  
  finalizeValues( res.getObjects() );
  
//  for( Collection<AgeObjectWritable> pObjs : prototypeMap.values() )
//   finalizeValues( pObjs );
  

//  AgeClass lastClass=null;
//  Collection<AgeObjectWritable> protos=null;
//  for( AgeObjectWritable obj : res.getObjects() )
//  {
//   if( lastClass != obj.getAgeElClass() )
//   {
//    lastClass=obj.getAgeElClass();
//    protos = prototypeMap.get(lastClass);
//   }
//   
//   if( protos != null )
//   {
//    for( AgeObjectWritable po : protos )
//    {
//     if( po.getAttributes() != null)
//     {
//      for( AgeAttributeWritable prat : po.getAttributes() )
//       obj.addAttribute(prat.createClone());
//     }
//     
//     if( po.getRelations() != null)
//     {
//      for( AgeRelationWritable prel : po.getRelations() )
//       obj.addRelation(prel.createClone());
//     }
//    }
//   }
//  }
  
//  validateData(res);
  
  imputeInverseRelations( res );
  
  return res;
 }
 
 
 private void applyPrototype(AgeObjectWritable obj, AgeObjectWritable prototype)
 {
  if( prototype.getAttributes() != null )
  {
   Collection<? extends AgeAttributeClass> protoAtCls = prototype.getAttributeClasses();
   
   clsloop : for( AgeAttributeClass pAtCla : protoAtCls)
   {
    if( obj.getAttributes() != null )
    {
     for( AgeAttributeWritable oa : obj.getAttributes() )
      if( oa.getAgeElClass() == pAtCla )
       continue clsloop;
    } 
    
    for( AgeAttributeWritable ptAttr : prototype.getAttributesByClass(pAtCla, false) )
     obj.addAttribute( ptAttr.createClone(obj) );
   }
  }
  
  if( prototype.getRelations() != null )
  {
   Collection<? extends AgeRelationClass> protoRlCls = prototype.getRelationClasses();
   
   clsloop : for( AgeRelationClass pRlCla : protoRlCls)
   {
    if( obj.getRelationClasses() != null && obj.getRelationClasses().size() > 0 )
    {
     for( AgeRelationWritable or : obj.getRelations() )
      if( or.getAgeElClass() == pRlCla )
       continue clsloop;
    } 
    
    for( AgeRelationWritable ptRel : prototype.getRelationsByClass(pRlCla, false) )
     obj.addRelation( ptRel.createClone(obj) );
   }
  }
 }
 
 private AgeClass getClassForBlock(ClassReference colHdr, ContextSemanticModel sm, LogNode blkLog)
 {
  if( colHdr.isCustom() )
  {
   if( permissionManager.checkSystemPermission(SystemAction.CUSTCLASSDEF) == Permit.ALLOW )
   {
    AgeClass parent = null;
    
    if( colHdr.getParentClass() != null )
    {
     parent = sm.getDefinedAgeClass(colHdr.getParentClass());
     
     if( parent == null )
     {
      blkLog.log(Level.ERROR, "Defined class '"+colHdr.getParentClass()+"' (used as superclass) is not found. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol() );

      return null;
     }
    }
     
    return sm.getOrCreateCustomAgeClass(colHdr.getName(), null, parent);
   }
   else
   {
    blkLog.log(Level.ERROR, "Custom classes are not allowed within this context. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());

    return null;
   }
  }
  else
  {
   AgeClass cls = sm.getDefinedAgeClass( colHdr.getName() );
   
   if( cls == null )
   {
    blkLog.log(Level.ERROR, "Defined class '"+colHdr.getName()+"' not found. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());

    return null;
   }
   
   if( cls.isAbstract() )
   {
    blkLog.log(Level.ERROR, "Abstract class instantiation '"+colHdr.getName()+"'. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());

    return null;
   }

   
   return cls;
  }
 }
 
// private AgeClass getCustomAgeClass(ClassReference colHdr, ContextSemanticModel sm)
// {
//  AgeClass parent = null;
//  
//  if( colHdr.getParentClass() != null )
//  {
//   parent = sm.getDefinedAgeClass(colHdr.getParentClass());
//   
//   if( parent == null )
//    return null;
//  }
//   
//  return  sm.getOrCreateCustomAgeClass(colHdr.getName(),null,parent);
// }
 
 private void finalizeValues( Collection<? extends AttributedWritable> data ) //TODO
 {
  class AttrInfo
  {
   AgeAttributeWritable attr;
   AttributedWritable obj;
   
//   boolean isBool=false;
//   boolean isInt=false;
//   boolean isReal=false;
   
   int     intValue;
   boolean boolValue;
   double  realValue;
  }
  
  class AttrClassInfo
  {
   AgeAttributeClassWritable atClass;

   boolean isBool=true;
   boolean isInt=true;
   boolean isReal=true;
   
   List<AttrInfo> attributes = new ArrayList<AttrInfo>();
  }

  Map<AttributedClass, Map<AgeAttributeClass,AttrClassInfo > > wMap = new HashMap<AttributedClass, Map<AgeAttributeClass,AttrClassInfo >>();
  
  Map<AgeAttributeClass, AttrClassInfo > cClassMap = null;
  AttributedClass cClass = null;
  
  for( AttributedWritable obj : data )
  {
   obj.sortAttributes();
   
   if( obj instanceof AgeObjectWritable )
   {
    AgeObjectWritable aow = (AgeObjectWritable)obj;
    
    if( aow.getRelations() != null )
     finalizeValues(aow.getRelations());
   }
   
   if( obj.getAttributes() != null )
    finalizeValues(obj.getAttributes());
   
   if(obj.getAttributedClass() != cClass)
   {
    cClass = obj.getAttributedClass();

    cClassMap = wMap.get(cClass);

    if(cClassMap == null)
    {
     cClassMap = new HashMap<AgeAttributeClass, AttrClassInfo>();
     wMap.put(cClass, cClassMap);
    }
   }

   if(obj.getAttributes() != null)
   {
    for(AgeAttributeWritable attr : obj.getAttributes())
    {
     attr.finalizeValue();

     if(attr.getAgeElClass().getDataType() == DataType.GUESS)
     {
      AttrClassInfo atcInfo = cClassMap.get(attr.getAgeElClass());

      if(atcInfo == null)
      {
       atcInfo = new AttrClassInfo();
       atcInfo.atClass = (AgeAttributeClassWritable) attr.getAgeElClass();

       cClassMap.put(atcInfo.atClass, atcInfo);
      }

      AttrInfo aInf = new AttrInfo();
      aInf.attr = attr;
      aInf.obj = obj;

      atcInfo.attributes.add(aInf);

      String value = attr.getValue().toString().trim();

      if(atcInfo.isBool)
      {
       if(value.equalsIgnoreCase("true"))
       {
        // aInf.isBool = true;
        aInf.boolValue = true;
       }
       else if(value.equalsIgnoreCase("false"))
       {
        // aInf.isBool = true;
        aInf.boolValue = false;
       }
       else
        atcInfo.isBool = false;
      }

      if(!atcInfo.isBool)
      {
       if(atcInfo.isInt)
       {
        try
        {
         aInf.intValue = Integer.parseInt(value);
         // aInf.isInt=true;
        }
        catch(Exception e)
        {
         atcInfo.isInt = false;
        }
       }

       if(atcInfo.isReal)
       {
        try
        {
         aInf.realValue = Double.parseDouble(value);
         // aInf.isReal=true;
        }
        catch(Exception e)
        {
         atcInfo.isReal = false;
        }
       }
      }
     }
    }
   }
  }
  
  for( Map<AgeAttributeClass,AttrClassInfo > actMap : wMap.values() )
  {
   for( AttrClassInfo acInfo : actMap.values() )
   {
    DataType typ;
    
    if( acInfo.isBool )
     acInfo.atClass.setDataType( typ = DataType.BOOLEAN );
    else if( acInfo.isInt )
     acInfo.atClass.setDataType( typ = DataType.INTEGER );
    else if( acInfo.isReal )
     acInfo.atClass.setDataType( typ = DataType.REAL );
    else
     acInfo.atClass.setDataType( typ = DataType.STRING );
    
    if( typ != DataType.STRING)
    {
     for( AttrInfo ai : acInfo.attributes )
     {
      ai.obj.removeAttribute(ai.attr);
      AgeAttributeWritable nAttr = ai.obj.createAgeAttribute(ai.attr.getClassRef());
      
      if( typ == DataType.BOOLEAN )
       nAttr.setBooleanValue(ai.boolValue);
      else if( typ == DataType.INTEGER )
       nAttr.setIntValue(ai.intValue);
      else if( typ == DataType.REAL )
       nAttr.setDoubleValue(ai.realValue);
     }
    }
   }
  }
  
 }
 
 

 private void imputeInverseRelations( DataModuleWritable data )
 {
  Map<AgeRelationClass, RelationClassRef> relRefMap = new HashMap<AgeRelationClass, RelationClassRef>();
  
  for( AgeObjectWritable obj : data.getObjects() )
  {
   
   if( obj.getRelations() == null )
    continue;
   
   for( AgeRelationWritable rl : obj.getRelations() )
   {
    if( rl instanceof AgeExternalRelation )
     continue;
    
    if( rl.getInverseRelation() != null )
     continue;
    
    AgeRelationClass invClass = rl.getAgeElClass().getInverseRelationClass();
    
    if( invClass == null )
     continue;
    
    boolean found=false;
    
    if( rl.getTargetObject().getRelations() != null )
    {
     for(AgeRelationWritable irl : rl.getTargetObject().getRelations())
     {
      if( irl.getTargetObject() == obj && irl.getAgeElClass().equals(invClass) )
      {
       if( irl.getInverseRelation() == null )
        irl.setInverseRelation(rl);
       
       rl.setInverseRelation(irl);
       
       found = true;
       break;
      }
     }
    }
    
    if( ! found )
    {
     RelationClassRef invCRef = relRefMap.get(invClass);
     
     if( invCRef == null )
     {
      invCRef =data.getContextSemanticModel().getModelFactory().createRelationClassRef(
        data.getContextSemanticModel().getAgeRelationClassPlug(invClass), 0, invClass.getId());
      
      relRefMap.put(invClass, invCRef);
     }
     
     
     AgeRelationWritable invRel = rl.getTargetObject().createRelation(invCRef, obj );

     invRel.setInverseRelation(rl);
     invRel.setInferred(true);
     rl.setInverseRelation(invRel);
    }
   }
  }
 }



 
 private AgeAttributeClass getCustomAttributeClass( ClassReference cr , AgeClass aCls, ContextSemanticModel sm, LogNode log)
 {
   if(permissionManager.checkSystemPermission(SystemAction.CUSTATTRCLASSDEF) != Permit.ALLOW)
   {
    log.log(Level.ERROR, "Custom attribure class (" + cr.getName() + ") is not allowed within this context. Row: "+cr.getRow()+" Col: "+cr.getCol() );
    return null;
   }
   
   AgeAttributeClass attrClass = sm.getCustomAgeAttributeClass(cr.getName(), aCls);

   String typeName = cr.getFlagValue(AgeTabSyntaxParser.typeFlag);
   
   ClassReference targCR = cr.getTargetClassRef();
   
   DataType type = DataType.GUESS;

   if(typeName != null)
   {
    try
    {
     type = DataType.valueOf(typeName);
    }
    catch(Exception e)
    {
     log.log(Level.ERROR, "Invalid type name: " + typeName+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null;
//     throw new SemanticException(cr.getRow(), cr.getCol(), "Invalid type name: " + typeName);
    }
   }
   else
   {
    if(attrClass != null)
     type = attrClass.getDataType();
   }

   if( attrClass != null )
   {
    if(attrClass.getDataType() != type)
    {
     log.log(Level.ERROR, "Data type ('" + type + "') mismatches with the previous definition: " + attrClass.getDataType()+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null; 
//     throw new SemanticException(cr.getRow(), cr.getCol(), "Data type ('" + type + "') mismatches with the previous definition: " + attrClass.getDataType());
    }

    if( type == DataType.OBJECT )
    {
     if( attrClass.getTargetClass() == null )
     {
      log.log(Level.ERROR, "Reference to OBJECT attribute class with no target class. Row: "+cr.getRow()+" Col: "+cr.getCol() );
      return null; 
     }
     
     if( targCR != null && ! ( targCR.getName().equals(attrClass.getTargetClass().getName()) && targCR.isCustom() == attrClass.isCustom()) )
     {
      String prevTarg = attrClass.getTargetClass().getName();
      
     
      log.log(Level.ERROR, "Target class '"+targCR.getName()+"' "+
        (targCR.isCustom()?"(custom) ":"")+"mismatches with previous definition: '"+prevTarg+"'"+
        (attrClass.getTargetClass().isCustom()?" (custom)":"")+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
      return null; 
     }
    }
   }
   
   AgeAttributeClassWritable parent = null;

   if( cr.getParentClass() != null )
   {
    parent = (AgeAttributeClassWritable)sm.getDefinedAgeAttributeClass(cr.getParentClass());
    
    if( parent == null )
    {
     log.log(Level.ERROR, "Defined attribute class '"+cr.getParentClass()+"' (used as superclass) is not found. Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null; 
    }
   }
   
   AgeClass targetClass=null;
   
   if( targCR != null )
   {
    if( type == DataType.GUESS )
     type = DataType.OBJECT;
    
    if( targCR.isCustom() )
     targetClass = sm.getCustomAgeClass(targCR.getName());
    else
     targetClass = sm.getDefinedAgeClass(targCR.getName());
    
    if( targetClass == null )
    {
     log.log(Level.ERROR, "Target class '"+targCR.getName()+"' "+
       (targCR.isCustom()?"(custom ) ":"")+"not found. Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null; 
    }
   }
   
   if( type == DataType.OBJECT && targetClass == null)
   {
    log.log(Level.ERROR, "Target class must be defined for object attrubute class: '"+cr.getName()+"'"+
      (cr.isCustom()?" (custom )":"")+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
    return null; 
   }
    
   
   AgeAttributeClassWritable attrClassW = sm.getOrCreateCustomAgeAttributeClass(cr.getName(), type, aCls, parent);

   if( targetClass != null )
    attrClassW.setTargetClass(targetClass);
   
   
   return attrClassW;
 }
 
 private int addConverter( List<ValueConverter> convs, ValueConverter cnv )
 {
  int i=0;
  
  if( cnv.getClassReference() != null )
  {
   for(ValueConverter exstC : convs)
   {
    if(exstC.getClassReference() == null)
     continue;

    if(exstC.getClassReference().equals(cnv.getClassReference()))
    {
     convs.add(new InvalidColumnConvertor(cnv.getClassReference()));
     return i;
    }

    i++;
    
    // if( exstC.getProperty() == cnv.getProperty() &&
    // exstC.getQualifiedProperty() == cnv.getQualifiedProperty() &&
    // cnv.getProperty() != null )
    // throw new SemanticException(cnv.getClassReference().getRow(),
    // cnv.getClassReference().getCol(),
    // "Column header duplicates header at column "+exstC.getClassReference().getCol());
   }
  }
  convs.add(cnv);
  return -1;
 }
 
 private boolean createConvertors( BlockHeader blck, AgeClass blkCls, List<ValueConverter> convs, ContextSemanticModel sm,
   Map<AgeClass, Map<String,AgeObjectWritable>> classMap, LogNode log )// throws SemanticException
 {
  boolean result = true;
  
  convs.clear();
  
  for( ClassReference attHd : blck.getColumnHeaders() )
  {
   if( attHd == null )
   {
    addConverter(convs, new EmptyColumnConvertor(attHd) );
    continue;
   }
   
   List<ClassReference> qList = attHd.getQualifiers();
   if( qList != null && qList.size() > 0 )
   {
    ClassReference qualif = qList.get(qList.size()-1);
    
    if( qualif.getQualifiers() != null )
    {
     log.log(Level.ERROR, "A qualifier reference must not be qualified ifself. Use syntax attr[qual1][qual2]. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
     //throw new SemanticException(attHd.getRow(), attHd.getCol(), "A qualifier reference must not be qualified ifself. Use syntax attr[qual1][qual2]");
    }
    
    ValueConverter hostConverter = null;
    for( int i=convs.size()-1; i >= 0; i-- )
    {
     ValueConverter vc = convs.get(i);
     
     ClassReference cr = vc.getClassReference();
     
     if( cr == null || ! attHd.isQualifierFor(cr) )
      continue;
     else
     {
      hostConverter=vc;
      break;
     }
    }
    
    if( hostConverter == null )
    {
     log.log(Level.ERROR, "A qualifier must follow to a qualified property. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "A qualifier must follow to a qualified property.");
    }

    
    if( qualif.isCustom() && permissionManager.checkSystemPermission(SystemAction.CUSTQUALCLASSDEF) != Permit.ALLOW )
    {
     log.log(Level.ERROR, "Custom qualifier ("+qualif.getName()+") is not allowed within this context. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Custom qualifier ("+qualif.getName()+") is not allowed within this context.");
    }
    
    AgeAttributeClass qClass = null;
    
    if( qualif.isCustom() )
    {
     qClass = getCustomAttributeClass(qualif, blkCls, sm, log);
    
     if( qClass == null )
     {
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
     }
    }
    else
    {
     qClass=sm.getDefinedAgeAttributeClass(qualif.getName());
     
     if( qClass == null )
     {
      log.log(Level.ERROR, "Unknown attribute class (qualifier): '"+qualif.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
//      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Unknown attribute class (qualifier): '"+qualif.getName()+"'");
     }
     
     if( qClass.isAbstract() )
     {
      log.log(Level.ERROR, "Abstract class instantiation (qualifier): '"+qualif.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
     }

    }
    
    int dupCol = -1;
    
    if( qClass.getDataType() == DataType.OBJECT )
    {
     if( qClass.getTargetClass() == null )
     {
      log.log(Level.ERROR, "No target class defined for OBJECT attribute class '"+qClass.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      result=false;
      addConverter(convs, new InvalidColumnConvertor(attHd) );
     }
     else
     {
      dupCol = addConverter(convs, new ObjectQualifierConvertor(attHd, qClass, hostConverter, classMap.get(qClass.getTargetClass()), sm ) );
     }
    }
    else
     dupCol = addConverter(convs, new ScalarQualifierConvertor( attHd, qClass, hostConverter, sm ) );
    
//    if( dupCol != -1 )
//    {
//     log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
//       +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
//     result = false;
//    }
    
    continue;
   }
   
   if( attHd.isCustom() )
   {
//    String rangeClassName = attHd.getFlagValue(rangeFlag);
    
    ClassReference rgHdr=attHd.getRangeClassRef();
    
    if( rgHdr != null )
    {
     if( permissionManager.checkSystemPermission(SystemAction.CUSTRELCLASSDEF) != Permit.ALLOW )
     {
      log.log(Level.ERROR, "Custom relation class ("+attHd.getName()+") is not allowed within this context. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
//      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Custom relation class ("+attHd.getName()+") is not allowed within this context.");
     }
      
     AgeClass rangeClass=null;
     
   
     if( rgHdr.isCustom() )
      rangeClass = sm.getCustomAgeClass(rgHdr.getName());
     else
      rangeClass = sm.getDefinedAgeClass(rgHdr.getName());

     if( rangeClass == null )
     {
      log.log(Level.ERROR, "Invalid range class: '"+rgHdr.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
//      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Invalid range class: '"+rangeClassName+"'");
     }

     AgeRelationClass parent = null;
     
     if( attHd.getParentClass() != null )
     {
      parent = sm.getDefinedAgeRelationClass(attHd.getParentClass());
      
      if( parent == null )
      {
       log.log(Level.ERROR, "Defined relation class '"+attHd.getParentClass()+"' (used as superclass) is not found. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
       addConverter(convs, new InvalidColumnConvertor(attHd) );
       result = false;
       continue;
      }
     }
      
     AgeRelationClass relCls = sm.getOrCreateCustomAgeRelationClass(attHd.getName(), rangeClass, blkCls, parent);
     
//     AgeRelationClass relCls = sm.getCustomAgeRelationClass(attHd.getName());
//     
//     if( relCls == null )
//      relCls = sm.createCustomAgeRelationClass(attHd.getName(), rangeClass, blkCls);
     
     int dupCol = addConverter(convs, new CustomRelationConvertor(attHd,relCls,sm,classMap.get(rangeClass)) );
     
//     if( dupCol != -1 )
//     {
//      log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
//        +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
//      result = false;
//     }

    }
    else
    {
     AgeAttributeClass attrClass = getCustomAttributeClass(attHd, blkCls, sm, log);
     
     if( attrClass == null )
     {
      result=false;
      addConverter(convs, new InvalidColumnConvertor(attHd) );
     }
     else
     {
      int dupCol = -1;

      if( attrClass.getDataType() == DataType.OBJECT )
      {
       if( attrClass.getTargetClass() == null )
       {
        log.log(Level.ERROR, "No target class defined for OBJECT attribute class '"+attrClass.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
        result=false;
        addConverter(convs, new InvalidColumnConvertor(attHd) );
       }
       else
       {
        dupCol = addConverter(convs, new ObjectAttributeConvertor(attHd,attrClass, classMap.get(attrClass.getTargetClass()), sm ) );
       }
        
      }
      else
       dupCol = addConverter(convs, new AttributeConvertor(attHd, attrClass, sm));

//      if(dupCol != -1)
//      {
//       log.log(Level.ERROR, "Column header duplicates header at column " + convs.get(dupCol).getClassReference().getCol() + ". Row: " + attHd.getRow()
//         + " Col: " + attHd.getCol());
//       result = false;
//      }
     }
    }
   }
   else
   {
    AgeClassProperty prop = sm.getDefinedAgeClassProperty(attHd.getName());
    
    if( prop == null )
    {
     log.log(Level.ERROR, "Defined property '"+attHd.getName()+"' not found. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Unknown object property: '"+attHd.getName()+"'");
    }
    
    
//    if( ! sm.isValidProperty( prop, blck.ageClass ) )
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Defined property '"+attHd.getName()+"' is not valid for class '"+blck.ageClass.getName()+"'");

    if( prop instanceof AgeAttributeClass )
    {
     AgeAttributeClass attClass = (AgeAttributeClass)prop;

     if( attClass.isAbstract() )
     {
      log.log(Level.ERROR, "Abstract class instantiation '"+attHd.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
     }

     
     int dupCol = -1;
     
     if( attClass.getDataType() == DataType.OBJECT )
     {
      if( attClass.getTargetClass() == null )
      {
       log.log(Level.ERROR, "No target class defined for OBJECT attribute class '"+attClass.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
       result=false;
       addConverter(convs, new InvalidColumnConvertor(attHd) );
      }
      else
      {
       dupCol = addConverter(convs, new ObjectAttributeConvertor(attHd,attClass, classMap.get(attClass.getTargetClass()), sm ) );
      }
       
     }
     else
     {
      dupCol = addConverter(convs, new AttributeConvertor(attHd,attClass, sm) );
     }
     
     
//     if( dupCol != -1 )
//     {
//      log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
//        +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
//      result = false;
//     }
    }
    else
    {
     AgeRelationClass rCls = (AgeRelationClass)prop;
     
     if( rCls.isAbstract() )
     {
      log.log(Level.ERROR, "Abstract class instantiation '"+attHd.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
     }

     
     if( rCls.getDomain() != null && rCls.getDomain().size() > 0 )
     {
      boolean found=false;
      
      for( AgeClass dmcls : rCls.getDomain() )
      {
       if( blkCls.isClassOrSubclass(dmcls) )
       {
        found=true;
        break;
       }
      }
      
      if( !found )
      {
       log.log(Level.ERROR, "Class '"+blkCls+"' is not in the domain of relation class '"+rCls+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
       addConverter(convs, new InvalidColumnConvertor(attHd) );
       result = false;
       continue;
//       throw new SemanticException(attHd.getRow(),attHd.getCol(),"Class '"+blkCls.getName()+"' is not in the domain of relation class '"+rCls.getName()+"'");
      }
     }
     
     int dupCol = addConverter(convs, new DefinedRelationConvertor( attHd, rCls, sm, classMap) );
     
//     if( dupCol != -1 )
//     {
//      log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
//        +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
//      result = false;
//     }

    }
   }
  }
  
  return result;
 }
 
 
 private abstract class ValueConverter
 {
  protected ClassReference colHdr;
  protected AttributedWritable lastProp;
  
  protected ValueConverter( ClassReference hd )
  {
   colHdr=hd;
  }

  abstract public AgeClassProperty getProperty();
//  abstract public AgeClassProperty getQualifiedProperty();

  public abstract void convert(AgeTabValue vls) throws ConvertionException;

  public abstract void reset( AgeObjectWritable obj );
  
  public ClassReference getClassReference()
  {
   return colHdr;
  }
  
  protected void setLastConvertedValue( AttributedWritable p )
  {
   lastProp=p;
  }
  
  protected AttributedWritable getLastConvertedProperty()
  {
   return lastProp;
  }
 }
 
 private class DefinedRelationConvertor extends ValueConverter
 {
  private Collection<Map<String, AgeObjectWritable>> rangeObjects;
  private AgeRelationClass relClass;
  private AgeObjectWritable hostObject;
  private RelationClassRef rClsRef;
  
  public DefinedRelationConvertor(ClassReference hd, AgeRelationClass rlClass, ContextSemanticModel sm, Map<AgeClass, Map<String, AgeObjectWritable>> classMap)
  {
   super(hd);
   
   relClass=rlClass;
   
   Collection<AgeClass> rngSet = rlClass.getRange();
   
   if( rngSet == null || rngSet.size() == 0 )
    rangeObjects=classMap.values();
   else
   {
    rangeObjects = new LinkedList<Map<String,AgeObjectWritable>>();
    
    for( Map.Entry<AgeClass, Map<String, AgeObjectWritable>> me : classMap.entrySet() )
    {
     for(AgeClass rgClass : rngSet )
     {
      if( me.getKey().isClassOrSubclass(rgClass))
      {
       rangeObjects.add(me.getValue());
       break;
      }
     }
    }
   }
   
   rClsRef = sm.getModelFactory().createRelationClassRef(sm.getAgeRelationClassPlug(rlClass), hd.isHorizontal()?hd.getCol():hd.getRow(), hd.getOriginalReference());
  }
  
  public void reset( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getRelations() != null )
   {
    for( AgeRelationWritable r : obj.getRelations() )
     if( r.getAgeElClass() == relClass )
      obj.removeRelation(r);
   }
   
   setLastConvertedValue(null);

  }


  
  public AgeClassProperty getProperty()
  {
   return relClass;
  }
 
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

  
  @Override
  public void convert( AgeTabValue atVal ) throws ConvertionException
  {
//   setLastConvertedProperty(null);

   if(atVal == null)
    return;

   int found = 0;

   String val = atVal.getValue().trim();

   if(val.length() == 0)
    return;

   AgeObjectWritable targetObj = null;

   for(Map<String, AgeObjectWritable> omap : rangeObjects)
   {
    AgeObjectWritable candObj = omap.get(val);

    if(candObj != null)
    {
     targetObj = candObj;
     found++;
    }
   }

   if(found > 1)
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "Ambiguous reference");

   AgeRelationWritable rel = null;
   if(targetObj == null)
    rel = hostObject.createExternalRelation( rClsRef, val );
   else
    rel = hostObject.createRelation( rClsRef, targetObj);

   setLastConvertedValue(rel);

  }
 }

 
 
 private class ObjectAttributeConvertor extends ValueConverter
 {
  private Map<String, AgeObjectWritable> rangeObjects;
//  private AgeAttributeClass       attrClass;
  private AgeObjectWritable hostObject;
  private AttributeClassRef classRef;
  
  public ObjectAttributeConvertor(ClassReference hd, AgeAttributeClass aCls, Map<String, AgeObjectWritable> map, ContextSemanticModel sm)
  {
   super(hd);
   
   AgeAttributeClassPlug cPlug = sm.getAgeAttributeClassPlug(aCls);
   classRef = sm.getModelFactory().createAttributeClassRef(cPlug, hd.getCol(), hd.getOriginalReference());
   
   rangeObjects = map;
  }

  public void reset( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getAttributes() != null )
   {
    for( AgeAttributeWritable a : obj.getAttributes() )
     if( a.getAgeElClass() == classRef.getAttributeClass() )
      obj.removeAttribute(a);
   }

  }
  
  @Override
  public void convert(AgeTabValue atVal)
  {
//   setLastConvertedProperty(null);

   if(atVal == null )
    return;
   
   String val = atVal.getValue().trim();

   if(val.length() == 0)
    return;

   AgeObjectWritable targetObj = null;

   if(rangeObjects != null)
    targetObj = rangeObjects.get(val);

   AgeAttributeWritable obAttr = null;
   if(targetObj == null)
   {
    obAttr = hostObject.createExternalObjectAttribute( classRef, val );
   }
   else
   {
    obAttr = hostObject.createAgeAttribute(classRef);
    obAttr.setValue(targetObj);
   }
   
   setLastConvertedValue(obAttr);

  }
  
  @Override
  public AgeClassProperty getProperty()
  {
   return classRef.getAttributeClass();
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 
 private class CustomRelationConvertor extends ValueConverter
 {
  private Map<String, AgeObjectWritable> rangeObjects;
  private AgeRelationClass       relClass;
  private AgeObjectWritable hostObject;
  private RelationClassRef rClsRef;

  public CustomRelationConvertor(ClassReference hd, AgeRelationClass relCls, ContextSemanticModel sm, Map<String, AgeObjectWritable> map)
  {
   super(hd);
   rangeObjects = map;
   relClass = relCls;

   rClsRef = sm.getModelFactory().createRelationClassRef(sm.getAgeRelationClassPlug(relCls), hd.isHorizontal()?hd.getCol():hd.getRow(), hd.getOriginalReference());
  }

  public void reset( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getRelations() != null )
   {
    for( AgeRelationWritable r : obj.getRelations() )
     if( r.getAgeElClass() == relClass )
      obj.removeRelation(r);
   }

  }
  
  
  @Override
  public void convert(AgeTabValue atVal)
  {
//   setLastConvertedProperty(null);

   if(atVal == null )
    return;
   
   String val = atVal.getValue().trim();

   if(val.length() == 0)
    return;

   AgeObjectWritable targetObj = null;

   if(rangeObjects != null)
    targetObj = rangeObjects.get(val);

   AgeRelationWritable rel = null;
   if(targetObj == null)
    rel = hostObject.createExternalRelation(rClsRef, val);
   else
    rel = hostObject.createRelation(rClsRef, targetObj);

   setLastConvertedValue(rel);
  }
  
  @Override
  public AgeClassProperty getProperty()
  {
   return relClass;
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 
 private class EmptyColumnConvertor  extends ValueConverter
 {

  protected EmptyColumnConvertor( ClassReference cr )
  {
   super(cr);
  }

  public void reset( AgeObjectWritable ob )
  {}
  
  @Override
  public void convert( AgeTabValue vls) throws ConvertionException
  {
   if(vls == null)
    return;

   if(vls.getValue().length() > 0)
    throw new ConvertionException(vls.getRow(), vls.getCol(), "Cells in the column with no header must be empty");
  }

  @Override
  public AgeClassProperty getProperty()
  {
   return null;
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 
 private class InvalidColumnConvertor extends ValueConverter
 {

  public InvalidColumnConvertor( ClassReference hd )
  {
   super(hd);
  }

  
  @Override
  public AgeClassProperty getProperty()
  {
   return null;
  }

  @Override
  public void convert(AgeTabValue vls) throws ConvertionException
  {
  }


  @Override
  public void reset(AgeObjectWritable obj)
  {
  }
 }
 
 private class AttributeConvertor extends ValueConverter
 {
  private AttributeClassRef classRef;
  private AgeObjectWritable hostObject;

  public AttributeConvertor(ClassReference hd, AgeAttributeClass attCls, ContextSemanticModel sm) // throws SemanticException
  {
   super( hd );
   
   AgeAttributeClassPlug cPlug = sm.getAgeAttributeClassPlug(attCls);
   classRef = sm.getModelFactory().createAttributeClassRef(cPlug, hd.getCol(), hd.getOriginalReference());
  }

  public void reset( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getAttributes() != null )
   {
    for( AgeAttributeWritable a : obj.getAttributes() )
     if( a.getAgeElClass() == classRef.getAttributeClass() )
      obj.removeAttribute(a);
   }
   
   setLastConvertedValue(null);
  }
  
  @Override
  public void convert(AgeTabValue vl) throws ConvertionException
  {
//   setLastConvertedProperty(null);
   
   if( vl == null || vl.getValue().length() == 0 )
   {
    if( classRef.getAttributeClass().getDataType().isMultiline() )
    {
     AgeAttributeWritable attr = (AgeAttributeWritable)getLastConvertedProperty();
     
     if( attr != null )
     {
      try
      {
       attr.updateValue("");
      }
      catch(FormatException e)
      {}
     }
    }
    
    return;
   }
   
   AgeAttributeWritable attr = null;
   boolean exstAttr=false;
   
   if( classRef.getAttributeClass().getDataType().isMultiline() )
   {
    Collection<? extends AgeAttributeWritable> atcoll = hostObject.getAttributesByClass(classRef.getAttributeClass(), false);
    
    if( atcoll == null || atcoll.size() == 0 )
     attr = hostObject.createAgeAttribute(classRef);
    else
    {
     attr = atcoll.iterator().next();
     exstAttr=true;
    }
   }
   else
    attr = hostObject.createAgeAttribute(classRef);
   
 
//   AgeAttribute attr = obj.getAttribute(attrClass);
//   AgeAttributeWritable attrAlt = obj.createAgeAttribute(attrClass);
//   
//   if( attr == null )
//    attrAlt = obj.createAgeAttribute(attrClass);
//   else if( attr instanceof AgeAttributeWritable )
//    attrAlt= (AgeAttributeWritable) attr;
//   else
//    throw new ConvertionException(getColumnHeader().getRow(), getColumnHeader().getRow(), "Attribute '"+attrClass+"' already exists in the object '"+obj.getId()+"' and isn't alterable" );
   
    try
    {
     attr.updateValue(vl.getValue());
    }
    catch(FormatException e)
    {
     throw new ConvertionException(vl.getRow(), vl.getCol(), "Invalid value ("+vl.getValue()+") for attribute: "+classRef.getAttributeClass().getName() );
    }
   
   
    setLastConvertedValue(attr);
  }
  
  public AgeClassProperty getProperty()
  {
   return classRef.getAttributeClass();
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 private abstract class QualifierConvertor extends ValueConverter
 {
  protected AttributeClassRef classRef;
  private ValueConverter hostConverter;
  protected AttributedWritable contextProperty;

  public QualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc, ContextSemanticModel sm)// throws SemanticException
  {
   super(attHd);
   
   AgeAttributeClassPlug cPlug = sm.getAgeAttributeClassPlug(qClass);
   classRef = sm.getModelFactory().createAttributeClassRef(cPlug, attHd.getCol(), attHd.getOriginalReference());
   hostConverter = hc;
  }

  
  public ValueConverter getHostConvertor()
  {
   return hostConverter;
  }
  
  public void reset( AgeObjectWritable obj )
  {
   List<AgeAttributeClass> chain = new ArrayList<AgeAttributeClass>(5);
   
   chain.add(classRef.getAttributeClass());
   
   ValueConverter cHost = getHostConvertor();
   
   while( cHost instanceof QualifierConvertor )
   {
    chain.add( (AgeAttributeClass)cHost.getProperty() );
    cHost = ((QualifierConvertor)cHost).getHostConvertor();
   }

   AgeClassProperty topProp = cHost.getProperty();
   
   if( topProp instanceof AgeRelationClass && obj.getRelations() != null )
   {
    for( AgeRelationWritable r : obj.getRelations() )
    {
     if( r.getAgeElClass() == topProp )
      removeQualifiers( r, chain, chain.size()-1 );
    }
   }
   
  }
  
  @Override
  public AgeClassProperty getProperty()
  {
   return classRef.getAttributeClass();
  }

  protected AttributedWritable getLastConvertedProperty()
  {
   if( contextProperty == hostConverter.getLastConvertedProperty() )
    return super.getLastConvertedProperty();
   
   return null;
  }

  
  private void removeQualifiers( AttributedWritable host, List<AgeAttributeClass> chain, int lvl )
  {
   AgeAttributeClass lvlClass = chain.get(lvl);
   
   for( AgeAttributeWritable a : host.getAttributes() )
   {
    if( a.getAttributedClass() == lvlClass )
    {
     if( lvl == 0 )
      host.removeAttribute(a);
     else
      removeQualifiers(a, chain, lvl-1);
    }
   }
  }

 }
 
 private class ScalarQualifierConvertor extends QualifierConvertor
 {

  public ScalarQualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc, ContextSemanticModel sm)// throws SemanticException
  {
   super(attHd, qClass, hc, sm);
  }

  @Override
  public void convert(AgeTabValue val) throws ConvertionException
  {

   if(val == null || val.getValue().length() == 0)
   {
    if( classRef.getAttributeClass().getDataType().isMultiline() && getLastConvertedProperty() != null )
    {
     try
     {
      ((AgeAttributeWritable)getLastConvertedProperty()).updateValue("");
     }
     catch(FormatException e)
     {}
    }
    
    return;
   }
   
   AttributedWritable prop = getHostConvertor().getLastConvertedProperty();

   // if there is no host value checking whether the qualifier is multiline
   if( prop == null )
    throw new ConvertionException(val.getRow(), val.getCol(), "There is no main value for qualification");

   AgeAttributeWritable attrAlt = null;

   if(classRef.getAttributeClass().getDataType().isMultiline())
    attrAlt = (AgeAttributeWritable)getLastConvertedProperty();
   
   if( attrAlt == null )
    attrAlt = prop.createAgeAttribute(classRef);

   try
   {
    attrAlt.updateValue(val.getValue());
   }
   catch(FormatException e)
   {
    throw new ConvertionException(val.getRow(), val.getCol(), "Invalid value (" + val.getValue() + ") for attribute: "
      + classRef.getAttributeClass().getName());
   }

   
   contextProperty=prop;
   setLastConvertedValue(attrAlt);
  }

 }
 
 private class ObjectQualifierConvertor extends QualifierConvertor
 {
  private Map<String, AgeObjectWritable> rangeObjects;

  public ObjectQualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc, Map<String, AgeObjectWritable> map, ContextSemanticModel sm)
  {
   super(attHd, qClass, hc, sm);

   rangeObjects=map;
  }
  
  
  @Override
  public void convert(AgeTabValue atVal) throws ConvertionException
  {
   if(atVal == null )
    return;
   
   String val = atVal.getValue().trim();

   if(val.length() == 0)
    return;


   AttributedWritable prop = getHostConvertor().getLastConvertedProperty();


   if(prop == null )
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "There is no main value for qualification");

   
   AgeObjectWritable targetObj = null;

   if(rangeObjects != null)
    targetObj = rangeObjects.get(val);

   AgeAttributeWritable obAttr = null;
   if(targetObj == null)
    obAttr = prop.createExternalObjectAttribute(classRef, val);
   else
   {
    obAttr = prop.createAgeAttribute(classRef);
    obAttr.setValue(targetObj);
   }
   
   contextProperty = prop;
   setLastConvertedValue(obAttr);
  }
 }

}
