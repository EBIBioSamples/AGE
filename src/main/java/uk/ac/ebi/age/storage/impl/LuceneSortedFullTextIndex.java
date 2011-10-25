package uk.ac.ebi.age.storage.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.index.KeyExtractor;
import uk.ac.ebi.age.storage.index.SortedTextIndexWritable;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;

public class LuceneSortedFullTextIndex<KeyT> extends LuceneFullTextIndex implements SortedTextIndexWritable<KeyT>
{
 private class ObjComparator implements Comparator<AgeObject>
 {
  @Override
  public int compare(AgeObject o1, AgeObject o2)
  {
   KeyT objk1 = keyExtractor.extractKey(o1);
   KeyT objk2 = keyExtractor.extractKey(o2);

   int res  = keyComparator.compare(objk1, objk2 );
   
   keyExtractor.recycleKey(objk1);
   keyExtractor.recycleKey(objk2);
   
   return res;
  }
 }
 
 public ObjComparator objectComparator = new ObjComparator();
 
 public KeyExtractor<KeyT> keyExtractor;
 public Comparator<KeyT> keyComparator;

 public LuceneSortedFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts, KeyExtractor<KeyT> kext, Comparator<KeyT> keyComp, File path) throws IOException
 {
  super(qury,exts, path);
  
  keyExtractor = kext;
  keyComparator = keyComp;
 }

 
 public int indexOfKey( KeyT key )
 {
  List<AgeObject> list = getObjectList();
  
  int low = 0;
  int high = list.size() - 1;

  while(low <= high)
  {
   int mid = (low + high) >>> 1;
   AgeObject midVal = list.get(mid);
   
   KeyT objk = keyExtractor.extractKey(midVal);
   
   int cmp = keyComparator.compare( objk, key);

   keyExtractor.recycleKey(objk);
   
   if(cmp < 0)
    low = mid + 1;
   else if(cmp > 0)
    high = mid - 1;
   else
    return mid; // key found
  }
  return -(low + 1); // key not found
 }

 
 @Override
 public AgeObject getAgeObject(KeyT key)
 {
  int ind = indexOfKey(key);
  
  if( ind >= 0 )
   return getObjectList().get(ind);
  
  return null; // key not found
 }

 @Override
 public void index(List<AgeObject> aol, boolean append)
 {
  List<AgeObject> naol = null;
  
  if( append )
  {
   naol = new ArrayList<AgeObject>( aol.size() + getObjectList().size() );
   
   naol.addAll( getObjectList() );
   naol.addAll(aol);
  }
  else
  {
   naol = new ArrayList<AgeObject>( aol.size() );
   
   naol.addAll(aol);
  }

  Collections.sort(naol, objectComparator );

  setObjectList( naol );
 }
}
