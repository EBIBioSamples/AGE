package uk.ac.ebi.age.mng;

import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.impl.v1.ContextSemanticModelImpl;
import uk.ac.ebi.age.model.impl.v1.ModelFactoryImpl;
import uk.ac.ebi.age.model.impl.v1.SemanticModelImpl;

public class SemanticManager
{
 
 private static SemanticManager instance = new SemanticManager();
 
 private SemanticModel model;
 
 private SemanticManager()
 {}
 
 public static SemanticManager getInstance()
 {
  return instance;
 }
 
// public void initModel(String uri) throws ModelException
// {
//  model = new SemanticModelImpl(uri, ModelFactoryImpl.getInstance());
// }
 
 public SemanticModel getMasterModel()
 {
  return model;
 }
 
 public ContextSemanticModel getContextModel( SubmissionContext ctxt )
 {
  return new ContextSemanticModelImpl(model,ctxt);
 }

 public SemanticModel createMasterModel()
 {
  return model = createModelInstance();
 }

 public void setMasterModel(SemanticModel mod)
 {
  model = mod;
 }

 public static SemanticModel createModelInstance()
 {
  return new SemanticModelImpl(ModelFactoryImpl.getInstance());
 }

}
