package uk.ac.ebi.age.storage.impl.ser;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.query.AgeQuery;

import com.pri.util.collection.EmptyIterator;

public class InMemoryQueryProcessor implements Iterable<AgeObject>, Iterator<AgeObject>
{
 private AgeQuery query;
 private Iterator<? extends DataModule> submsItr ;
 
 private CondIterator objItr = new CondIterator();
 
 public InMemoryQueryProcessor(AgeQuery qury, Collection<? extends DataModule> roots)
 {
  query = qury;
  
  if( roots.size() == 0 )
  {
   submsItr = EmptyIterator.getInstance();
   objItr.setObjIterator(EmptyIterator.<AgeObject>getInstance());
  }
  else
  {
   submsItr = roots.iterator();
   objItr.setObjIterator(submsItr.next().getObjects().iterator());
  }
 }

 public Iterator<AgeObject> iterator()
 {
  return this;
 }

 public boolean hasNext()
 {
  if( objItr.hasNext() )
   return true;
  
  while( submsItr.hasNext() )
  {
   objItr.setObjIterator(submsItr.next().getObjects().iterator());

   if( objItr.hasNext() )
    return true;
  }
  
  return false;
 }

 public AgeObject next()
 {
  if( !hasNext() )
   throw new NoSuchElementException();

  return objItr.next();
 }

 public void remove()
 {
 }

 private boolean testQueryConditions(AgeObject obj)
 {
  return query.getExpression().test(obj);
 }
 
 private class CondIterator implements Iterator<AgeObject>
 {
  private Iterator<? extends AgeObject> objItr;
  private AgeObject prepObj;
 
  CondIterator()
  {}
  
  void setObjIterator( Iterator<? extends AgeObject> oi )
  {
   objItr = oi;
  }

  public boolean hasNext()
  {
   if( prepObj != null )
    return true;
   
   if( ! objItr.hasNext() )
    return false;

   while( objItr.hasNext() )
   {
    prepObj = objItr.next();
    
    if( testQueryConditions( prepObj ) )
     return true; 
   }
   
   prepObj=null;
   return false;
  }



  public AgeObject next()
  {
   if(prepObj == null && !hasNext() )
    throw new NoSuchElementException();

   AgeObject toReturn = prepObj;
   prepObj = null;
   return toReturn;
  }

  public void remove()
  {
  }
  
 }
 
 
}
