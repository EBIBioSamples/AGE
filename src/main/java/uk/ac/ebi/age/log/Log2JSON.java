package uk.ac.ebi.age.log;

import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.SimpleLogNode;

import com.pri.util.StringUtils;

public class Log2JSON
{
 
 public static String convert( LogNode nd )
 {
  StringBuilder sb = new StringBuilder();
  
  SimpleLogNode.setLevels( nd );
  
  convertNode(nd, sb, 0);
  
  return sb.toString();
 }
 
 private static void convertNode( LogNode ln, StringBuilder sb, int lyr )
 {
  sb.append("{\n");
  sb.append(" level: \"").append(ln.getLevel().name()).append("\",\n");
  
  boolean needComma=false;
  
  if(  ln.getMessage() != null )
  {
   sb.append(" message: \"");
   StringUtils.appendBackslashed(sb, ln.getMessage(), '"');
   sb.append('"');

   needComma = true;
  }
  
  if( ln.getSubNodes() != null )
  {
   if( needComma )
    sb.append(",\n");

   sb.append(" subnodes: [");
   
   needComma=false;
   
   for( LogNode snd : ln.getSubNodes() )
   {
    if( needComma )
     sb.append(",\n");
    else
     needComma = true;
    
    convertNode(snd,sb,lyr+1);
   }
   
   sb.append("]\n");
  }

  sb.append("}");
 }
 

}
