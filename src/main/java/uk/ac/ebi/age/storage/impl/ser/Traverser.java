package uk.ac.ebi.age.storage.impl.ser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;

public class Traverser implements Iterable<AgeObject>, Iterator<AgeObject>
{
 enum Strategy
 {
  DEPTH_FIRST,
  WIDTH_FIRST
 }
 
 private static final Strategy strategy = Strategy.DEPTH_FIRST;
 
 private LinkedList<AgeObject> listToVisit = new LinkedList<AgeObject>();
 
 private AgeObject preparedObject;
 private Set<AgeObject> visited = new HashSet<AgeObject>();

 public Traverser( AgeObject stNd )
 {
  preparedObject = stNd;
  addNodesToVisit(stNd);
 }

 
 public Iterator<AgeObject> iterator()
 {
  return this;
 }

 public boolean hasNext()
 {
  if( preparedObject != null )
   return true;
  
  if( listToVisit.size() == 0 )
   return false;
  
  if( strategy == Strategy.DEPTH_FIRST )
  {
   preparedObject = listToVisit.getLast();
   listToVisit.removeLast();
  }
  else
  {
   preparedObject = listToVisit.getFirst();
   listToVisit.removeFirst();
  }
 
  addNodesToVisit(preparedObject);
  
  return true;
 }

 private void addNodesToVisit(AgeObject obj)
 {
  for( AgeRelation rel : obj.getRelations() )
  {
   if( visited.add(rel.getTargetObject()) )
   {
    listToVisit.add(rel.getTargetObject());
   }
  }
 }


 public AgeObject next()
 {
  if( preparedObject == null && !  hasNext() )
   throw new NoSuchElementException();
  
  
  if( preparedObject != null )
  {
   AgeObject toReturn =preparedObject;
   preparedObject = null;
   return toReturn;
  }
  
  if( ! hasNext() )
   throw new NoSuchElementException();
  
  return preparedObject;
 }

 public void remove()
 {
 }

}
