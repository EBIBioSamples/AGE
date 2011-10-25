package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import uk.ac.ebi.age.ext.entity.ClusterEntity;
import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.ext.entity.EntityDomain;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.Attributed;
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
 private static final long serialVersionUID = 3L;
 
// private long version;
 private Collection<AgeObjectWritable> objects = new ArrayList<AgeObjectWritable>(50);
 private ContextSemanticModel model;
 private Collection<AgeExternalRelationWritable> extRels ;
 private Collection<AgeExternalObjectAttributeWritable> extObjAttrs ;
 private Collection<AgeFileAttributeWritable> fileRefs ;

 private String id;
// private String descr;
 
 private String clusterId;

 public DataModuleImpl(ContextSemanticModel sm)
 {
  model = sm;
 }


// public String getDescription()
// {
//  return descr;
// }
//
// public void setDescription( String dsc )
// {
//  descr=dsc;
// }

 public void addObject(AgeObjectWritable obj)
 {

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
  
  obj.setDataModule( this );
 }

 
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

 @Override
 public Collection<? extends AttributedWritable> getAttributed( AttributedSelector sel )
 {
  return new SelectionCollection<AttributedWritable>( sel );
 }
 
 @Override
 public Collection<AgeExternalObjectAttributeWritable> getExternalObjectAttributes()
 {
  if( extObjAttrs != null )
   return extObjAttrs;
  
  return new SelectionCollection<AgeExternalObjectAttributeWritable>( new AttributedSelector()
  {
   @Override
   public boolean select(Attributed at)
   {
    return at instanceof AgeExternalObjectAttributeWritable;
   }
  });

 }

 @Override
 public Collection<AgeFileAttributeWritable> getFileAttributes()
 {
  if( fileRefs != null )
   return fileRefs;
  
  return new SelectionCollection<AgeFileAttributeWritable>( new AttributedSelector()
  {
   @Override
   public boolean select(Attributed at)
   {
    return at instanceof AgeFileAttributeWritable;
   }
  });

 }

 
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

// @Override
// public long getVersion()
// {
//  return version;
// }
//
// @Override
// public void setVersion(long version)
// {
//  this.version = version;
// }


 public String getClusterId()
 {
  return clusterId;
 }


 public void setClusterId(String clusterId)
 {
  this.clusterId = clusterId;
 }
 
 private class SelectionCollection<T extends AttributedWritable> extends AbstractCollection<T>
 {
  private AttributedSelector selector;
  
  SelectionCollection( AttributedSelector sel )
  {
   selector = sel;
  }
  
  class SelAttrIter implements Iterator<T>
  {
   private List<Iterator<? extends Attributed>> stk = new ArrayList<Iterator<? extends Attributed>>(5);
   
   {
    stk.add(objects.iterator());
   }

   private T nextEl;
   
   @SuppressWarnings("unchecked")
   @Override
   public boolean hasNext()
   {
    if( nextEl != null )
     return true;
    
    Attributed atbt = null;
    int last = stk.size()-1;
    Iterator<? extends Attributed> cIter = stk.get(last);
    
    do
    {

     while(!cIter.hasNext())
     {
      if(last == 0 )
       return false;
      
      stk.remove(last);
      cIter = stk.get(--last);
     }
    
     atbt = cIter.next();
    
     if( ! atbt.getAttributes().isEmpty() )
     {
      stk.add(cIter = atbt.getAttributes().iterator());
      last++;
     }
     
    } while(!selector.select(atbt));

    nextEl = (T)atbt;
    
    return true;
   }

   @Override
   public T next()
   {
    if( ! hasNext() )
     throw new NoSuchElementException();

    T nxt = nextEl;
    nextEl = null;
    
    return nxt;
   }

   @Override
   public void remove()
   {
    throw new UnsupportedOperationException();
   }
   
  }

  @Override
  public Iterator<T> iterator()
  {
   return new SelAttrIter();
  }

  @Override
  public int size()
  {
   throw new UnsupportedOperationException();
  }
 }

 @Override
 public void registerExternalRelation(AgeExternalRelationWritable rel)
 {
  if(extRels == null)
   extRels = new ArrayList<AgeExternalRelationWritable>(10);

  extRels.add((AgeExternalRelationWritable) rel);
 }


 @Override
 public String getEntityID()
 {
  return getId();
 }


 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.DATAMODULE;
 }


 @Override
 public Entity getParentEntity()
 {
  return new ClusterEntity(clusterId);
 }
}
