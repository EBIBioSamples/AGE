package uk.ac.ebi.age.log;

import java.io.PrintWriter;

import uk.ac.ebi.age.ext.log.ErrorCounter;
import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.SimpleLogNode;

public class BufferLogger implements ErrorCounter
{
 private SimpleLogNode rootNode;
 
 private static final long serialVersionUID = 1L;

 private int maxErr;
 private int errCnt = 0;
 
 public BufferLogger( int maxErr )
 {
  rootNode = new SimpleLogNode( null, "", this );
  this.maxErr = maxErr;
 }
 
 @Override
 public int getErrorCounter()
 {
  return errCnt;
 }
 
 @Override
 public void resetErrorCounter()
 {
  errCnt = 0;
 }


 @Override
 public void incErrorCounter()
 {
  errCnt++;
  
  if( maxErr > 0 && errCnt > maxErr )
   throw new TooManyErrorsException( errCnt );
 }


 @Override
 public void addErrorCounter(int countErrors)
 {
  errCnt+=countErrors;
 }
 
 public SimpleLogNode getRootNode()
 {
  return rootNode;
 }
 
 public static void printBranch( LogNode node )
 {
  printBranch(node, new PrintWriter( System.out ), 0);
 }
 
 public static void printBranch( LogNode node, PrintWriter out  )
 {
  printBranch(node, out, 0);
 }

 
 private static void printBranch( LogNode node, PrintWriter out, int lvl )
 {
  for( int i=0; i < lvl; i++ )
   out.print("  ");
  
  if( node.getLevel() != null )
  {
   switch( node.getLevel() )
   {
    case DEBUG:
     out.print("DEBG: ");
     break;
    case INFO:
     out.print("INFO: ");
     break;
    case WARN:
     out.print("WARN: ");
     break;
    case ERROR:
     out.print("ERRR: ");
     break;
    case SUCCESS:
     out.print("SUCS: ");
     break;
   }
   
   out.println(node.getMessage());
  }
  else
  {
   out.println(node.getMessage());
   
   if( node.getSubNodes() != null )
   {
    for(LogNode sbNode : node.getSubNodes() )
     printBranch(sbNode,out, lvl+1);
   }
  }
   
 }

}
