package uk.ac.ebi.age.storage;

public class RelationResolveException extends Exception
{
 private int lineNumber;
 private int columnNumber;
 
 public RelationResolveException( int ln, int cl, String msg )
 {
  super( msg );
  lineNumber=ln;
  columnNumber=cl;
 }
 
 public int getLineNumber()
 {
  return lineNumber;
 }

 public int getColumnNumber()
 {
  return columnNumber;
 }

 public void setColumn(int i)
 {
  columnNumber=i;
 }
}
