package uk.ac.ebi.age.parser;

public class ParserException extends Exception
{
 private int lineNumber;
 private int columnNumber;
 
 public ParserException( int ln, int cl, String msg )
 {
  super( msg );
  lineNumber=ln;
  columnNumber=cl;
 }
 
 public int getLineNumber()
 {
  return lineNumber;
 }

 public void setLineNumber( int ln )
 {
  lineNumber = ln;
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
