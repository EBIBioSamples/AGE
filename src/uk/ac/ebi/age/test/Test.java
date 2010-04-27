package uk.ac.ebi.age.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.ModelException;
import uk.ac.ebi.age.model.RestrictionException;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.AgeTabSemanticValidator;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ConvertionException;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SemanticException;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.storage.exeption.SubmissionStoreException;
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
   
   smngr.initModel(ontologyFile);
   
   ByteArrayOutputStream bais = new ByteArrayOutputStream();
   FileInputStream fis = new FileInputStream( new File("/d:/workspaceGL/eclipse/ESD/semantic/test.csv") );
   
   StreamPump.doPump(fis, bais);
   
   String text = new String(bais.toByteArray());
 
   AgeTabSubmission sbm =  AgeTabSyntaxParser.getInstance().parse(text);
   
   SubmissionWritable dblock = AgeTabSemanticValidator.getInstance().parse(sbm, smngr.getContextModel(new DefContext()));
   
//   AgeStorageGrafImpl str = new AgeStorageGrafImpl();
   SerializedStorage str = new SerializedStorage();
   
   str.storeSubmission(dblock);
   
   System.out.println("Done");
   
   str.shutdown();
  }
  catch(ModelException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
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

  catch(SemanticException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(ConvertionException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(RestrictionException e)
  {
   // TODO Auto-generated catch block
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
  catch(SubmissionStoreException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }

 static class DefContext implements SubmissionContext
 {

  public boolean isCustomAttributeClassAllowed()
  {
   return true;
  }

  public boolean isCustomClassAllowed()
  {
   return true;
  }

  public boolean isCustomRelationClassAllowed()
  {
   return true;
  }
  
 }
 
}

