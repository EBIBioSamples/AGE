package uk.ac.ebi.age.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil
{
 public static void splitExcelString(String line, String sep, List<String> accum)
 {
  int spos;
  StringBuilder sb=null;
  
  while( line.length() > 0 )
  {
   
   if( line.charAt(0) != '"' )
   {
    spos = line.indexOf(sep);
    
    if( spos < 0 )
    {
     accum.add(line);
     return;
    }
    
    accum.add(line.substring(0,spos));
    line = line.substring(spos+sep.length());
   }
   else
   {
    int qpos;
    int beg = 1;
    
    while( true ) //looking for closing "
    {
     qpos = line.indexOf('"',beg);
     
     if( qpos == -1 ) // actually this is the erroneous situation - quoted part is not finished by the quotation symbol. 
     {
      if( sb != null && sb.length() > 0 )
      {
       sb.append(line.substring(beg));
       accum.add(sb.toString());
      }
      else
       accum.add(line.substring(beg));
      
      return;
     }
     else if( qpos == line.length()-1 )
     {
      if( sb != null && sb.length() > 0 )
      {
       sb.append(line.substring(beg,line.length()-1));
       accum.add(sb.toString());
      }
      else
       accum.add(line.substring(beg,line.length()-1));

      return;
     }
     
     if( line.charAt(qpos+1) == '"' ) //this is a double quote
     {
      if( sb == null )
       sb = new StringBuilder(200);
      
      sb.append(line.substring(beg, qpos+1));
      beg = qpos+2;
     }
     else
     {
      if( line.startsWith(sep, qpos+1) ) // Ok, we've found the end of the token
      {
       if( sb != null && sb.length() > 0 )
       {
        sb.append(line.substring(beg, qpos) );
        accum.add(sb.toString());
        sb.setLength(0);
       }
       else
        accum.add(line.substring(beg, qpos));

       line=line.substring(qpos+sep.length()+1);
       break;
      }
      else // actually this is the erroneous situation - quotation symbol have to be followed by separator or to be doubled . 
      {
       if( sb == null )
        sb = new StringBuilder(200);
       
       sb.append(line.substring(beg, qpos+1));
       beg = qpos+1;
      }
     }
     
    }
   }
  }
 }

 
 public static List<String> splitExcelString(String line, String sep)
 {
  List<String> res = new ArrayList<String>(50);
  
  splitExcelString(line, sep, res);
  
  return res;
 }
 
 public static List<String> splitString(String line, String sep)
 {
  List<String> res = new ArrayList<String>(10);
  
  int cpos = 0;
  int len = line.length();
  int seplen = sep.length();
  
  while( cpos < len )
  {
   int pos = line.indexOf(sep);
   
   if( pos == -1 )
   {
    res.add(line.substring(cpos));
    break;
   }
   
   res.add(line.substring(cpos,pos));
   cpos = pos + seplen;
  }
  
  return res;
 }
}

