package uk.ac.ebi.age.ext.log;

import java.util.List;

public interface LogNode
{
 static enum Level
 {
  ERROR   (5),
  WARN    (4),
  SUCCESS (3),
  INFO    (2),
  DEBUG   (1);
  
  private int level;
  Level( int l )
  {
   level=l;
  }
  
  public int getPriority()
  {
   return level;
  }
  
  public static Level getMinLevel()
  {
   return DEBUG;
  }
 }
 
 void success();
 void log(Level lvl, String msg);
 LogNode branch(String msg);
 void setLevel( Level lvl );
 
 String getMessage();
 Level getLevel();
 List<? extends LogNode> getSubNodes();
 void append(LogNode rootNode);
}
