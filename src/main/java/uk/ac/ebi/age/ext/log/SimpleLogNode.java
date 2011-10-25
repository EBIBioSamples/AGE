package uk.ac.ebi.age.ext.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SimpleLogNode implements LogNode,Serializable
{
 private static final long serialVersionUID = 1L;

 private String            nodeMessage;
 private Level             level;

 private List<LogNode>     subNodes;
 
 private transient ErrorCounter errCnt;

 SimpleLogNode()
 {}
 
 public SimpleLogNode(Level l, String msg, ErrorCounter rn)
 {
  nodeMessage = msg;
  level = l;
  errCnt = rn;
 }

 @Override
 public void log(Level lvl, String msg)
 {
  if(subNodes == null)
   subNodes = new ArrayList<LogNode>(10);

  subNodes.add(new SimpleLogNode(lvl, msg, errCnt));
  
  if( lvl.getPriority() >= Level.ERROR.getPriority() )
   errCnt.incErrorCounter();
 }

 @Override
 public LogNode branch(String msg)
 {
  if(subNodes == null)
   subNodes = new ArrayList<LogNode>(10);

  LogNode nnd = new SimpleLogNode(null, msg, errCnt);

  subNodes.add(nnd);

  return nnd;
 }

 @Override
 public void append(LogNode node)
 {
  if(subNodes == null)
   subNodes = new ArrayList<LogNode>(10);

  subNodes.add(node);
  
  errCnt.addErrorCounter( countErrors(node) );
 }

 private int countErrors( LogNode node )
 {
  
  if( node.getSubNodes() == null )
  {
   if( node.getLevel().getPriority() >= Level.ERROR.getPriority() )
    return 1;
   else
    return 0;
  }
  else
  {
   int res = 0;

   for( LogNode sn : node.getSubNodes() )
    res += countErrors(sn);

   return res;
  }
 }
 
 @Override
 public String getMessage()
 {
  return nodeMessage;
 }

 @Override
 public Level getLevel()
 {
  return level;
 }

 @Override
 public void setLevel(Level l)
 {
  level = l;
 }

 @Override
 public List<LogNode> getSubNodes()
 {
  return subNodes;
 }

 
 @Override
 public void success()
 {
  level = Level.SUCCESS;
 }

 public static void setLevels( LogNode ln )
 {
  setLevels(ln, Level.getMinLevel());
 }

 public static void setLevels(LogNode ln, Level reqLevel)
 {
  if( ln.getSubNodes() == null )
  {
   if( ln.getLevel() == null )
    ln.setLevel(Level.INFO);
   
   return;
  }
  
  LogNode.Level maxLevel = ln.getLevel()!=null?ln.getLevel():Level.getMinLevel();
  
  for( LogNode snd : ln.getSubNodes() )
  {
   setLevels(snd,reqLevel);
   
   if( snd.getLevel().getPriority() > maxLevel.getPriority() )
    maxLevel = snd.getLevel();
  }
  
  ln.setLevel(maxLevel);
  
  if( maxLevel.getPriority() >= reqLevel.getPriority()  )
  {
   Iterator< ? extends LogNode> lnIter = ln.getSubNodes().iterator();

   while(lnIter.hasNext())
   {
    LogNode iln = lnIter.next();

    if(iln.getLevel().getPriority() < reqLevel.getPriority())
     lnIter.remove();
   }
  }
 }
}
