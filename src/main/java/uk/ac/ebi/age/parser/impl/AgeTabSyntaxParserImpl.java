package uk.ac.ebi.age.parser.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ClassReference;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SyntaxProfile;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;
import uk.ac.ebi.age.service.id.IdGenerator;

import com.pri.util.SpreadsheetReader;

public class AgeTabSyntaxParserImpl extends AgeTabSyntaxParser
{
 public AgeTabSyntaxParserImpl(SyntaxProfile sp)
 {
  super(sp);
 }


 interface BlockSupplier
 {
//  List<String> getHeaderLine();

  int getLineNum();

  List<String> getLine(List<String> parts);
 }
 
 static class HorizontalBlockSupplier implements BlockSupplier
 {
  private List<String> firstLine;
  private SpreadsheetReader reader;
  
  HorizontalBlockSupplier(SpreadsheetReader r, List<String> fstLine)
  {
   reader = r;
   firstLine = new ArrayList<String>( fstLine.size() );
   
   for( String s : fstLine )
    firstLine.add(s);
  }
  

//  @Override
//  public List<String> getHeaderLine()
//  {
//   return firstLine;
//  }


  @Override
  public int getLineNum()
  {
   return reader.getLineNumber();
  }


  @Override
  public List<String> getLine(List<String> parts)
  {
   if( firstLine != null )
   {
    
    if( parts != null )
    {
     parts.clear();
     
     for( String s : firstLine )
      parts.add(s);
     
     firstLine=null;
     
     return parts;
    }
    
    List<String> fl = firstLine;
    firstLine = null;
    return fl;
   }
   
   List<String> line = reader.readRow(parts);
   
   if( line == null )
    return null;
   
   if( isEmptyLine(line) )
    return null;
   
   return line;
  }
  
 }
 
 static class VerticalBlockSupplier implements BlockSupplier
 {
//  private List<List<String>> matrix = new ArrayList<List<String>>( 100 );
  
  private int ptr = 0;
  private List<List<String>> lines = new ArrayList<List<String>>( 50 );
  private int maxDim = 0;
 
  VerticalBlockSupplier(SpreadsheetReader reader, List<String> fstLine)
  {
   List<String> line = new ArrayList<String>( fstLine.size() );
   
   for( String s : fstLine )
    line.add(s);

   lines.add(line);
   
   while( ( line = reader.readRow(null) ) != null && ! isEmptyLine(line) )
   {
    lines.add(line);
   
    if( maxDim < line.size() )
     maxDim = line.size();
   }
   
//   for( int i=0; i < maxDim; i++ )
//   {
//    line = new ArrayList<String>( lines.size() );
//    matrix.add(line);
//    
//    for( List<String> l : lines )
//     line.add( i >= l.size()?"":l.get(i));
//   }
   
  }
  

  @Override
  public int getLineNum()
  {
   return ptr;
  }


  @Override
  public List<String> getLine(List<String> line)
  {
   if( ptr >= maxDim )
    return null;
   
   if( line == null )
    line = new ArrayList<String>( lines.size() );
   else
    line.clear();
   
   for( List<String> l : lines )
    line.add( ptr >= l.size()?"":l.get(ptr));
   
   ptr++;
   
   return line;
  }
  
 }

 

 @Override
 public AgeTabModule parse( String txt ) throws ParserException
 {
  SyntaxProfile profile = getSyntaxProfile();
  
  AgeTabModule data = new AgeTabModuleImpl( getSyntaxProfile() );
  
  List<String> parts = new ArrayList<String>(100);

  SpreadsheetReader reader = new SpreadsheetReader(txt);
 
  BlockSupplier block;
  
  while( reader.readRow(parts) != null )
  {
   if( isEmptyLine(parts) )
    continue;

   String classRef = parts.get(0);
   
   BlockHeader header = new BlockHeaderImpl(data);

   if( classRef.startsWith(profile.getCommonSyntaxProfile().getHorizontalBlockPrefix()) )
   {
    parts.set(0, classRef.substring(profile.getCommonSyntaxProfile().getHorizontalBlockPrefix().length()));
    block = new HorizontalBlockSupplier( reader, parts );
    
    header.setHorizontal(true);
   }
   else if( classRef.startsWith(profile.getCommonSyntaxProfile().getVerticalBlockPrefix()) )
   {
    parts.set(0, classRef.substring(profile.getCommonSyntaxProfile().getVerticalBlockPrefix().length()));
    block = new VerticalBlockSupplier( reader, parts );
    header.setHorizontal(false);
   }
   else if( profile.getClassSpecificSyntaxProfile(classRef).isHorizontalBlockDefault() )
   {
    block = new HorizontalBlockSupplier( reader, parts );
    header.setHorizontal(true);
   }
   else
   {
    block = new VerticalBlockSupplier( reader, parts );

    header.setHorizontal(false);
   }
   
   analyzeHeader(header, block.getLine(parts), block.getLineNum() );
   data.addBlock(header);
   
   AgeTabObject cObj = null;
   
   SyntaxProfileDefinition profileDef = header.getClassColumnHeader().isCustom()?
     profile.getCommonSyntaxProfile():profile.getClassSpecificSyntaxProfile(header.getClassColumnHeader().getName());
   
   parts.clear();
   while( block.getLine(parts) != null )
   {
    Iterator<String> partIter = parts.iterator();
    
    String part = partIter.next();
    
    if( part.length() != 0 )
    {
     if( part.equals( profileDef.getAnonymousObjectId() ) )
     { 
      String id = "??"+IdGenerator.getInstance().getStringId("tempObjectId");
      cObj = data.createObject(id,header,block.getLineNum());
      cObj.setIdDefined(false);
      cObj.setIdScope(IdScope.MODULE);
     }
     else
     {
      String id = part;
      boolean defined = ! part.startsWith( profileDef.getAnonymousObjectId());
      
      IdScope scope = defined? profileDef.getDefaultIdScope() : IdScope.MODULE;

      String pfx = profileDef.getGlobalIdPrefix();
      
      if( part.startsWith(pfx) )
      {
       id = part.substring(pfx.length());
       scope = IdScope.GLOBAL;
      }
      else
      {
       pfx = profileDef.getClusterIdPrefix();
       
       if( part.startsWith(pfx) )
       {
        id = part.substring(pfx.length());
        scope = IdScope.CLUSTER;
       }
       else
       {
        pfx = profileDef.getModuleIdPrefix();
        
        if( part.startsWith(pfx) )
        {
         id = part.substring(pfx.length());
         scope = IdScope.MODULE;
        }
       }
      }
      
   

      cObj = data.getOrCreateObject(id,header, block.getLineNum() );
      
      cObj.setIdScope(scope);
      cObj.setIdDefined( defined );
      cObj.setPrototype( part.equals( profileDef.getPrototypeObjectId() ) );
     }
    }
    else if( cObj == null )
     throw new ParserException(block.getLineNum(), 1, "Object identifier is expected"); // TODO provide correct coords here
   
    int col=1; 
    for( ClassReference prop : header.getColumnHeaders() )
    {
     col++;
     
     if(!partIter.hasNext())
      break;
     
     String val = partIter.next();

     if( prop != null )
     {
      if( val.length() > 0 )
       cObj.addValue(block.getLineNum(),col,val,prop);
     }
     else if( val.length() > 0 )
     {
      throw new ParserException(block.getLineNum(),col,"Not empty value in the empty-headed column");
     }
     
    }
   }
   
  }

  return data;
 }

 private void analyzeHeader(BlockHeader hdr, List<String> parts, int row) throws ParserException
 {
//  BlockHeader hdr = new BlockHeaderImpl( this );
  
  Iterator<String> itr = parts.iterator();
  
  ClassReference partName;
  try
  {
   partName = string2ClassReference(itr.next());
   partName.setHorizontal(hdr.isHorizontal());
   partName.setRow(row);
   partName.setCol(1);
  }
  catch(ParserException e)
  {
   e.setLineNumber(row);
   e.setColumn(1);
   throw e;
  }
  
  
  hdr.setClassColumnHeader(partName);
  
  int ord=1;
  while( itr.hasNext() )
  {
   ord++;
   
   String hdrStr = itr.next();

   
   
   if( hdrStr.trim().length() == 0 )
   {
    hdr.addColumnHeader(null);
    continue;
   }
   
   try
   {
    partName = string2ClassReference(hdrStr);
    
    if( hdr.isHorizontal() )
    {
     partName.setRow(row);
     partName.setCol(ord);
    }
    else
    {
     partName.setRow(row+ord);
     partName.setCol(1);
    }
    
    if( partName.getQualifiers() != null )
    {
     for( ClassReference qref : partName.getQualifiers() )
     {
      if( hdr.isHorizontal() )
      {
       qref.setRow(row);
       qref.setCol(ord);
      }
      else
      {
       qref.setRow(row+ord);
       qref.setCol(1);
      }
     }
    }
   }
   catch(ParserException e)
   {
    if( hdr.isHorizontal() )
    {
     e.setLineNumber(row);
     e.setColumn(ord);
    }
    else
    {
     e.setLineNumber(row+ord);
     e.setColumn(1);
    }

    throw e;
   }
   
   
   hdr.addColumnHeader(partName);
  }
  
  
//  return hdr;
 }

 
 private static boolean isEmptyLine( List<String> parts )
 {
  for(String pt : parts )
   if( pt.length() != 0 )
    return false;
  
  return true;
 }
 
}

