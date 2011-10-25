package uk.ac.ebi.age.util;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractClass;

public abstract class Collector<T>
{
 public abstract T get( AgeAbstractClass cls );
 
 public static <T> void collectFromHierarchy( AgeAbstractClass cls, Collection<T> allRest, Collector<T> src )
 {
  T clct = src.get(cls);
  
  if( clct != null )
   allRest.add( clct );
  
  for( AgeAbstractClass supcls : cls.getSuperClasses() )
  {
   collectFromHierarchy(supcls,allRest,src);
  }
 }
}