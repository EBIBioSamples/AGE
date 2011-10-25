package uk.ac.ebi.age.storage.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;

import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.impl.LuceneFullTextIndex;
import uk.ac.ebi.age.storage.impl.LuceneSortedFullTextIndex;

public class IndexFactory
{
 private static IndexFactory instance = new IndexFactory() ;
 
 public static IndexFactory getInstance()
 {
  return instance;
 }

 public TextIndexWritable createFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts, File path) throws IOException
 {
  return new LuceneFullTextIndex( qury, exts, path );
 }

 public <KeyT> SortedTextIndexWritable<KeyT> createSortedFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts,
     KeyExtractor<KeyT> kext, Comparator<KeyT> keyComp, File path) throws IOException
 {
  return new LuceneSortedFullTextIndex<KeyT>( qury, exts, kext, keyComp, path );
 }

}
