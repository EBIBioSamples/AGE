package uk.ac.ebi.age.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.PermissionManager;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.log.BufferLogger;
import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SyntaxProfile;
import uk.ac.ebi.age.parser.impl.AgeTab2AgeConverterImpl;
import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.impl.ser.SerializedStorage;

import com.pri.util.stream.StreamPump;

public class Test
{
 static final String ontologyFile = "file:///d:/workspaceGL/eclipse/ESD/semantic/test.owl";
// static final String ontologyFile = "file:///d:/workspaceGL/eclipse/ESD/semantic/ESD_model_hp_SMALL2.owl";
// static final String ontologyFile = "file:///d:/workspaceGL/eclipse/ESD/semantic/school.owl";

 /** /ESD/semantic/ESD_model_hp_SMALL2.owl
  * @param args
  */
 public static void main(String[] args)
 {
  try
  {
   SemanticManager smngr = SemanticManager.getInstance();
   
//   smngr.initModel(ontologyFile);
   
   ByteArrayOutputStream bais = new ByteArrayOutputStream();
   FileInputStream fis = new FileInputStream( new File("/d:/workspaceGL/eclipse/ESD/semantic/test.csv") );
   
   StreamPump.doPump(fis, bais);
   
   String text = new String(bais.toByteArray());
 
   SyntaxProfile synProf = new SyntaxProfile();
   
   AgeTabModule sbm =  new AgeTabSyntaxParserImpl( synProf ).parse(text);
   
   BufferLogger logBuf = new BufferLogger( 30 );
  
   DataModuleWritable dblock = new AgeTab2AgeConverterImpl( new DefPM() ).convert(sbm, smngr.getContextModel(), synProf, logBuf.getRootNode() );
   
   if( dblock == null )
   {
    System.out.println("Convertion failed");
    return;
   }
   
//   AgeStorageGrafImpl str = new AgeStorageGrafImpl();
   SerializedStorage str = new SerializedStorage();
   
   str.storeDataModule(dblock);
   
   System.out.println("Done");
   
   str.shutdown();
  }
//  catch(ModelException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
  catch(FileNotFoundException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch bloc
   e.printStackTrace();
  }
  catch(RelationResolveException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(ParserException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(ModuleStoreException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
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

