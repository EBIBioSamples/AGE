package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.lang.ref.SoftReference;

import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.impl.v3.AgeExternalRelationImpl;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.storage.impl.serswap.SerializedSwapStorage;

public class SwapExternalRelation extends AgeExternalRelationImpl
{
 private static final long serialVersionUID = 3L;

 private SoftReference<AgeExternalRelationWritable> softInvRel;
 
 protected SwapExternalRelation(RelationClassRef cRef, AgeObjectWritable srcOb, String id)
 {
  super(cRef, srcOb, id);
 }

 public AgeObjectProxy getSourceObject()
 {
  AgeObjectWritable src = super.getSourceObject();
  
  if( src.getClass() == AgeObjectProxy.class )
   return (AgeObjectProxy)super.getSourceObject();
  
  AgeObjectProxy sObjPx = ((SwapDataModule)src.getDataModule()).getModuleRef().getObjectProxy(src.getId());
  
  super.setSourceObject(sObjPx);
  
  return sObjPx;
 }

 
 public AgeObjectWritable getTargetObject()
 {
  if( super.getTargetObject() != null )
   return super.getTargetObject();
  
  SerializedSwapStorage stor = getSourceObject().getStorage();
  
  AgeObjectWritable tgt = null;
  
  if( isTargetGlobal() )
   tgt = stor.getGlobalObject( getTargetObjectId() );
  else
   tgt = stor.getClusterObject(getSourceObject().getDataModule().getClusterId(), getTargetObjectId());
  
  setTargetObject(tgt);

  return tgt;
 }
 
 public AgeExternalRelationWritable getInverseRelation()
 {
  if( super.getInverseRelation() != null )
   return super.getInverseRelation();
  
  if( softInvRel != null && softInvRel.get() != null )
   return softInvRel.get();
  
  for( AgeRelationWritable rel : getTargetObject().getRelations() )
  {
   if( rel instanceof AgeExternalRelationWritable && rel.getTargetObject() == getSourceObject() && rel.getAgeElClass().equals( getAgeElClass().getInverseRelationClass() ) )
   {
    if( rel instanceof SwapImplicitInvExtRelation )
     super.setInverseRelation( rel );
    else
     softInvRel = new SoftReference<AgeExternalRelationWritable>( (AgeExternalRelationWritable)rel );
    
    return (AgeExternalRelationWritable)rel;
   }
  }
  
  return null;
 }
 
}
