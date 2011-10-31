package uk.ac.ebi.age.test;

import java.util.Collection;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.PermissionManager;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public class ExtObjAttrTest
{
 public static void main( String[] argv )
 {
  SemanticModel mod = SemanticManager.createModelInstance();
  
  SemanticManager.getInstance().setMasterModel(mod);
  
  ContextSemanticModel cMod = SemanticManager.getInstance().getContextModel();
  
  DataModuleWritable dm = mod.getModelFactory().createDataModule(cMod);
  
  AgeClassWritable cls = cMod.createAgeClass("MyClass", "clsId", "MC", mod.getRootAgeClass());
 
  AgeAttributeClassWritable atClsS = cMod.createAgeAttributeClass("MyText","MyText",DataType.STRING, mod.getRootAgeAttributeClass());
  
  AgeAttributeClassPlug cPlug = cMod.getAgeAttributeClassPlug(atClsS);
  AttributeClassRef classRefS = cMod.getModelFactory().createAttributeClassRef(cPlug, 0,"MyText");

  
  AgeAttributeClassWritable atClsO = cMod.createAgeAttributeClass("MyObj","MyObj",DataType.OBJECT, mod.getRootAgeAttributeClass());
  
  cPlug = cMod.getAgeAttributeClassPlug(atClsO);
  AttributeClassRef classRefO = cMod.getModelFactory().createAttributeClassRef(cPlug, 0, "MyObj");

  ClassRef clsRef = cMod.getModelFactory().createClassRef(cMod.getAgeClassPlug(cls), 0, cls.getId(), true, cMod);
  
  for( int i=0; i<5; i++ )
  {
   AgeObjectWritable obj = cMod.createAgeObject(clsRef, "obj"+i);
   
   obj.createAgeAttribute(classRefS).setValue("Val");
   
   if( i == 1 )
   {
    obj.createExternalObjectAttribute( classRefO, "ObjRef"+i );
   }
   else if( i == 2 )
   {
    obj.createExternalObjectAttribute(classRefO, "ObjRef"+i+"+1L1");
    obj.createAgeAttribute(classRefS).setValue("Val2");
    AttributedWritable attr = obj.createExternalObjectAttribute(classRefO ,"ObjRef"+i+"+2L1");
    obj.createAgeAttribute(classRefS).setValue("Val3");
    obj.createExternalObjectAttribute(classRefO , "ObjRef"+i+"+3L1");

    attr.createAgeAttribute(classRefS).setValue("Val2+");
    attr.createExternalObjectAttribute(classRefO, "ObjRef"+i+"+1L2");
    attr.createExternalObjectAttribute(classRefO, "ObjRef"+i+"+2L2");
    
    AgeAttributeWritable sat = attr.createAgeAttribute(classRefS);
    sat.setValue("Val3+");
    attr=sat;
    
    attr.createAgeAttribute(classRefS).setValue("Val2++");
    attr.createExternalObjectAttribute(classRefO, "ObjRef"+i+"+1L3");
    attr.createAgeAttribute(classRefS).setValue("Val3++");
    attr.createExternalObjectAttribute(classRefO, "ObjRef"+i+"+2L3");
   }
   
   dm.addObject(obj);
  }
  
  for( AgeExternalObjectAttributeWritable extO : dm.getExternalObjectAttributes() )
  {
   System.out.println( extO.getTargetObjectId() );
  }
  
 }

 static class DefPM implements PermissionManager
 {

  @Override
  public Permit checkSystemPermission(SystemAction act)
  {
   return Permit.ALLOW;
  }

  @Override
  public Permit checkPermission(SystemAction act, Entity objId)
  {
   return Permit.ALLOW;
  }

  @Override
  public Permit checkPermission(SystemAction act, String objOwner, Entity objId)
  {
   return Permit.ALLOW;
  }

  @Override
  public Permit checkSystemPermission(SystemAction act, String user)
  {
   return Permit.ALLOW;
  }

  @Override
  public Collection<TagRef> getEffectiveTags(Entity objId)
  {
   // TODO Auto-generated method stub
   return null;
  }

  @Override
  public Collection<TagRef> getAllowTags(SystemAction act, String user)
  {
   // TODO Auto-generated method stub
   return null;
  }

  @Override
  public Collection<TagRef> getDenyTags(SystemAction act, String user)
  {
   // TODO Auto-generated method stub
   return null;
  }
  
 }
}

