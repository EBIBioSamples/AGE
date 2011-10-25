package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeContextSemanticElement;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public abstract class AttributedObject implements AttributedWritable, AgeContextSemanticElement, Serializable
{

 private static final long serialVersionUID = 3L;

 private List<AgeAttributeWritable> attributes = com.pri.util.collection.Collections.emptyList();
 
// private transient Map<AgeAttributeClass,List<AgeAttributeWritable>> attribMap; // = new HashMap<String,List<AgeAttributeWritable>>();

 
// private transient List<AgeAttributeClass> atClasses = null;

 
// public AttributedObject( ContextSemanticModel m )
// {
//  super(m);
// }

 
// @Override
// public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass)
// {
//  AgeAttributeWritable attr = getSemanticModel().createAgeAttribute(attrClass);
//  
//  addAttribute(attr);
//  
//  return attr;
// }
 
 
 @Override
 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass)
 {
  AgeAttributeWritable attr = getSemanticModel().createAgeAttribute(attrClass, this);
  
  addAttribute(attr);
  
  return attr;
 }

 
 @Override
 public AgeAttributeWritable createExternalObjectAttribute( AttributeClassRef attrClass, String val )
 {
  AgeAttributeWritable attr = getSemanticModel().createExternalObjectAttribute(  attrClass, this, val );

  addAttribute(attr);

  return attr;
 }
 
// @Override
// public AgeAttributeWritable createExternalObjectAttribute(String val, AgeAttributeClass attrClass)
// {
//  AgeAttributeWritable attr = getSemanticModel().createExternalObjectAttribute(attrClass, val);
//  
//  addAttribute(attr);
//  
//  return attr;
// }

 
 @Override
 public Collection<AgeAttributeWritable> getAttributes()
 {
  return attributes;
 }
 
 @Override
 public synchronized void addAttribute(AgeAttributeWritable attr)
 {
  if( attributes.isEmpty() )
   attributes = new ArrayList<AgeAttributeWritable>(15);
  
  attributes.add(attr);
  
//  if( attribMap != null )
//   addAttribToMap(attr);
 }

 @Override
 public synchronized void removeAttribute(AgeAttributeWritable attr)
 {
  if( ! attributes.remove(attr) )
   return;
  
  if( attributes.isEmpty() )
   attributes = com.pri.util.collection.Collections.emptyList();

  
//  if( attribMap == null )
//   return;
//  
//  Collection<AgeAttributeWritable> coll = attribMap.get(attr.getAgeElClass());
//  
//  if( coll == null )
//   return;
//  
//  coll.remove(attr);
//  
//  if( coll.size() == 0 )
//   attribMap.remove(attr.getAgeElClass());
 }

// @Override
// public synchronized Collection< ? extends AgeAttributeWritable> getAttributes(AgeAttributeClass cls)
// {
//  return getAttribMap().get(cls);
// }

 @Override
 public synchronized AgeAttribute getAttribute(AgeAttributeClass cls)
 {
  for( AgeAttributeWritable  at : attributes )
  {
   if( at.getAgeElClass().equals(cls) )
    return at;
  }
  
  return null;
 }


 
// private Map<AgeAttributeClass,List<AgeAttributeWritable>> getAttribMap()
// {
//  if( attribMap != null )
//   return attribMap;
//  
//  attribMap = new HashMap<AgeAttributeClass, List<AgeAttributeWritable>>();
//  
//  if( attributes != null )
//  {
//   for( AgeAttributeWritable attr : attributes )
//    addAttribToMap(attr);
//  }
//  
//  return attribMap;
// }
//
// private void addAttribToMap( AgeAttributeWritable attr )
// {
//  List<AgeAttributeWritable> coll = attribMap.get(attr.getAgeElClass());
//  
//  if( coll == null )
//   attribMap.put(attr.getAgeElClass(),Collections.singletonList(attr));
//  else if( coll instanceof ArrayList<?> )
//   coll.add(attr);
//  else
//  {
//   ArrayList<AgeAttributeWritable> nc = new ArrayList<AgeAttributeWritable>(3);
//   nc.addAll(coll);
//   nc.add(attr);
//   
//   attribMap.put(attr.getAgeElClass(),nc);
//  }
// }

// @Override
// public Collection<String> getAttributeClassesIds()
// {
//  return attributes.keySet();
// }

// @Override
// public Collection<? extends AgeAttributeWritable> getAttributesByClassId(String cid, boolean wSubCls)
// {
//  if( ! wSubCls )
//   return getAttribMap().get(cid);
// }

 @Override
 public synchronized Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
 {
//  Map<AgeAttributeClass,List<AgeAttributeWritable>> map = getAttribMap();
//  
//  if( ! wSubCls )
//   return map.get(cls);
  
  List< AgeAttributeWritable > lst = new ArrayList<AgeAttributeWritable>();
  
  for( AgeAttributeWritable  at : attributes )
  {
   if( at.getAgeElClass().isClassOrSubclass(cls) )
    lst.add(at);
  }
  
  return lst;
 }

 @Override
 public synchronized Collection<? extends AgeAttributeClass> getAttributeClasses()
 {
  Set<AgeAttributeClass> res = new HashSet<AgeAttributeClass>();
  
  for( AgeAttributeWritable  at : attributes )
   res.add(at.getAgeElClass());

  
  return res;
 }

 @Override
 public synchronized void reset()
 {
//  attribMap=null;
 }
 
 protected synchronized void cloneAttributes( AttributedObject objClone )
 {
//  attribMap=null;
  
  if( attributes != null )
  {
   for( AgeAttributeWritable attr : attributes )
    objClone.addAttribute(attr.createClone(objClone));
  }
 }
 
 public synchronized void sortAttributes()
 {
  if( attributes == null )
   return;
  
  reset();
  
  Collections.sort(attributes, new Comparator<AgeAttributeWritable>()
  {

   @Override
   public int compare(AgeAttributeWritable o1, AgeAttributeWritable o2)
   {
    return o1.getOrder()-o2.getOrder();
   }
  });
 }
}
