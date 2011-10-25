package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.ext.entity.EntityDomain;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

class DataModuleImpl  implements DataModuleWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
// private AgeClass submissionClass = getModelFactoy.createAgeClass("$submission", "SBM", null);
// private AgeRelationClass submissionRelationClass ;
// private AgeRelationClass submissionInvRelationClass;
  
// private Collection<AgeClass> classes = new ArrayList<AgeClass>(10);
 private Collection<AgeObjectWritable> objects = new ArrayList<AgeObjectWritable>(50);
 private ContextSemanticModel model;
 private Collection<AgeExternalRelationWritable> extRels ;
// private Collection<AgeExternalObjectAttributeWritable> extAttrs ;

// private Map<AgeRelationClass, Collection<AgeRelationWritable>> rels ;
// private Collection<AgeRelationWritable> relLst;
 
// private Collection<SubmissionBlock> blocks = new ArrayList<SubmissionBlock>(10);
// private Collection<SubmissionBlock> blocks = new ArrayList<SubmissionBlock>(10);

 private String id;
 private String descr;

 public DataModuleImpl(ContextSemanticModel sm)
 {
  model = sm;
//  submissionClass = sm.createAgeClass("$submission", "SBM", sm);
//  
//  submissionRelationClass = ModelFactoryImpl.getInstance().createAgeRelationClass("$insubmission", sm);
//  submissionInvRelationClass = ModelFactoryImpl.getInstance().createAgeRelationClass("!$insubmission", sm);
//  
//  submissionRelationClass.setInverseClass(submissionInvRelationClass);
//  submissionInvRelationClass.setInverseClass(submissionRelationClass);
//  
//  relLst = new ArrayList<AgeRelationAlt>(100);
//  
//  rels = Collections.singletonMap(submissionRelationClass, relLst);
  
 }


 public String getDescription()
 {
  return descr;
 }

 public void setDescription( String dsc )
 {
  descr=dsc;
 }

// public void addClass(AgeClass cls)
// {
//  classes.add(cls);
// }

 public void addObject(AgeObjectWritable obj)
 {
//  obj.createRelation(this, submissionInvRelationClass);
//  relLst.add(getSemanticModel().createAgeRelation(obj, submissionRelationClass));

  objects.add(obj);

  if( obj.getRelations() != null )
  {
   for(AgeRelation rel : obj.getRelations())
   {
    if(rel instanceof AgeExternalRelationWritable)
    {
     if(extRels == null)
      extRels = new ArrayList<AgeExternalRelationWritable>(10);

     extRels.add((AgeExternalRelationWritable) rel);
    }
   }
  }
  
//  collectExtAttrs( obj );
 }

// private void collectExtAttrs( Attributed atb )
// {
//  if( atb.getAttributes() == null )
//   return;
//  
//  for( Attributed at : atb.getAttributes() )
//  {
//   if( at instanceof AgeExternalObjectAttributeWritable )
//   {
//    if( extAttrs == null )
//     extAttrs = new ArrayList<AgeExternalObjectAttributeWritable>(10);
//
//    extAttrs.add((AgeExternalObjectAttributeWritable)at);
//   }
//   
//   collectExtAttrs(at);
//  }
// }
 
 public void setId(String id)
 {
  this.id=id;
 }

 public String getId()
 {
  return id;
 }

 public Collection<AgeObjectWritable> getObjects()
 {
  return objects;
 }


 @Override
 public ContextSemanticModel getContextSemanticModel()
 {
  return model;
 }


 @Override
 public Collection<AgeExternalRelationWritable> getExternalRelations()
 {
  return extRels;
 }


// @Override
// public Collection<AgeExternalObjectAttributeWritable> getExternalObjectAttributes()
// {
//  return extAttrs;
// }

 
 @Override
 public void setMasterModel( SemanticModel newModel )
 {
  for( AgeObjectWritable obj : objects )
  {
   resetAttributedObject(obj);
   
   if( obj.getRelations() != null )
   {
    for( AgeRelationWritable rel : obj.getRelations() )
     resetAttributedObject(rel);
   }
   
  }
  
  model.setMasterModel( newModel );
 }
 
 private void resetAttributedObject( AttributedWritable atbObj )
 {
  atbObj.reset();
  

  for(AttributedWritable atw : atbObj.getAttributes() )
   resetAttributedObject(atw);
 }

 public String getClusterId()
 {
  throw new UnsupportedOperationException();
 }


 public void setClusterId(String clusterId)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public Collection<AgeExternalObjectAttributeWritable> getExternalObjectAttributes()
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public Collection< ? extends AttributedWritable> getAttributed(AttributedSelector sel)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public Collection<AgeFileAttributeWritable> getFileAttributes()
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public void registerExternalRelation(AgeExternalRelationWritable rel)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public String getEntityID()
 {
  return getId();
 }


 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.AGEOBJECT;
 }


 @Override
 public Entity getParentEntity()
 {
  throw new UnsupportedOperationException();
 }
}
