package uk.ac.ebi.age.storage.impl.ser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextIndexWritable;

import com.pri.util.collection.Collections;

public class LuceneFullTextIndex implements TextIndexWritable
{
// private static final String AGEOBJECTFIELD="AgeObject";
 private String defaultFieldName;
 
 private Directory index;
 private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
 private QueryParser queryParser;
 private IndexSearcher searcher;
 
 private List<AgeObject> objectList = Collections.emptyList();
 
 private AgeQuery query;
 private Collection<TextFieldExtractor> extractors;
 
 private boolean dirty=false;

 public LuceneFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts) throws IOException
 {
  this(qury,exts,null);
 }

 public LuceneFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts, File path) throws IOException
 {
  query=qury;
  extractors=exts;
  
  defaultFieldName = extractors.iterator().next().getName();
  
  queryParser = new QueryParser( Version.LUCENE_30, defaultFieldName, analyzer);

  
  if( path == null )
   index = new RAMDirectory();
  else
  {
   index = new NIOFSDirectory( path );

   
   if( index.listAll().length != 0  )
    searcher = new IndexSearcher(IndexReader.open(index));
  }
  

 }

 public void close()
 {
  try
  {
   if( searcher != null )
    searcher.close();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
 
// public void index(List<AgeObject> aol, Collection<TextFieldExtractor> extf)
// {
//  try
//  {
//   IndexWriter iWriter = new IndexWriter(index, analyzer, false,
//     IndexWriter.MaxFieldLength.UNLIMITED);
//
//   if( objectList == null )
//    objectList=aol;
//   else
//    objectList.addAll(aol);
//   
//   for(AgeObject ao : objectList )
//   {
//    Document doc = new Document();
//    
//    for(TextFieldExtractor tfe : extf )
//     doc.add(new Field(tfe.getName(), tfe.getExtractor().getValue(ao), Field.Store.NO, Field.Index.ANALYZED));
//    
//    iWriter.addDocument(doc);
//   }
//
//   iWriter.close();
//   
//   defaultFieldName = extf.iterator().next().getName();
//  }
//  catch(CorruptIndexException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
//  catch(IOException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
// }
 
 
 public int count(String query)
 {
  if( searcher == null )
   return 0;
  
  Query q;
  try
  {
   q = queryParser.parse(query);

   
   CountCollector cc = new CountCollector();
   searcher.search(q,cc);
   
   return cc.getCount();
  }
  catch(ParseException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

  
  //ScoreDoc[] hits = collector.topDocs().scoreDocs;
  
  return -1;
 }

 @Override
 public List<AgeObject> select(String query)
 {
  return select(query,0,-1);
 }

 @Override
 public List<AgeObject> select(String query, final int offs, final int limit)
 {
  if( searcher == null )
   return Collections.emptyList();
  
  final List<AgeObject> res = new ArrayList<AgeObject>();
  
  Query q;
  
  try
  {
   q = queryParser.parse(query);
   
   Collector coll = new Collector()
   {
    int base;
    int count=-1;
    
    @Override
    public void setScorer(Scorer arg0) throws IOException
    {
    }
    
    @Override
    public void setNextReader(IndexReader arg0, int arg1) throws IOException
    {
//     System.out.println("Next Reader: "+arg1);
     base=arg1;
    }
    
    @Override
    public void collect(int docId) throws IOException
    {
     count++;
//     System.out.println("Found doc: "+ind+". Object: "+objectList.get(ind).getId()+". Class: "+objectList.get(ind).getAgeElClass().getName() );
     if( count >= offs && (limit <= 0 || count < (offs+limit) ) )
      res.add( objectList.get(docId+base) );
    }
    
    @Override
    public boolean acceptsDocsOutOfOrder()
    {
     return false;
    }
   };

   searcher.search(q,coll);

  }
  catch(ParseException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
  //ScoreDoc[] hits = collector.topDocs().scoreDocs;
  
  return res;
 }

 @Override
 public AgeQuery getQuery()
 {
  return query;
 }

 @Override
 public void index(List<AgeObject> aol, boolean append)
 {
  List<AgeObject> naol = null;
  
  if( append )
  {
   naol = new ArrayList<AgeObject>( aol.size() + objectList.size() );
   
   naol.addAll(objectList);
   naol.addAll(aol);
  }
  else
  {
   naol = new ArrayList<AgeObject>( aol.size() );
   
   naol.addAll(aol);
  }
  
  objectList = naol;
  
  indexList( aol, append );
 }
 
 protected void indexList(List<AgeObject> aol, boolean append )
 {
  try
  {
   if( searcher != null )
   {
    searcher.getIndexReader().close();
    searcher.close();
   }
   
   IndexWriterConfig idxCfg = new IndexWriterConfig(Version.LUCENE_36, analyzer);
   
   idxCfg.setRAMBufferSizeMB(50);
   idxCfg.setOpenMode(append?OpenMode.APPEND:OpenMode.CREATE);
   
   IndexWriter iWriter = new IndexWriter(index, idxCfg );

   for(AgeObject ao : aol )
   {
    Document doc = new Document();
    
//    System.out.println("---Index rec--");
    for(TextFieldExtractor tfe : extractors )
    {
     String name = tfe.getName();
     String val = tfe.getExtractor().getValue(ao);
     
     doc.add(new Field(name, val, Field.Store.NO, Field.Index.ANALYZED));
//     System.out.println(name+" = "+val);
    }
    
    iWriter.addDocument(doc);
   }

   iWriter.close();
   
   
   searcher = new IndexSearcher( IndexReader.open(index) );
  }
  catch(CorruptIndexException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
 }


// @Override
// public void reset()
// {
//  try
//  {
//   IndexWriter iWriter = new IndexWriter(index, analyzer, true,
//     IndexWriter.MaxFieldLength.UNLIMITED);
//   iWriter.close();
//
//   objectList=null;
//  }
//  catch(CorruptIndexException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
//  catch(LockObtainFailedException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
//  catch(IOException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
// }

 private static class CountCollector extends Collector
 {
  int count = 0;

  @Override
  public void setScorer(Scorer arg0) throws IOException
  {
  }

  @Override
  public void setNextReader(IndexReader arg0, int arg1) throws IOException
  {
  }

  @Override
  public void collect(int docId) throws IOException
  {
   count++;
  }

  @Override
  public boolean acceptsDocsOutOfOrder()
  {
   return true;
  }
  
  int getCount()
  {
   return count;
  }
 }

 @Override
 public List<AgeObject> getObjectList()
 {
  return objectList;
 }

 protected void setObjectList( List<AgeObject> lst )
 {
  objectList = lst;
  
  indexList(objectList, false);
 }

 @Override
 public boolean isDirty()
 {
  return dirty;
 }

 @Override
 public void setDirty(boolean dirty)
 {
  this.dirty=dirty;
 }
}
