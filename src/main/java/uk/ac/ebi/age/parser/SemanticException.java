package uk.ac.ebi.age.parser;

public class SemanticException extends Exception
{
 private int column;
 private int row;
 
 public SemanticException(int row, int col, String string)
 {
  super(string);
  
  column=col;
  this.row=row;
 }

 public SemanticException(int row, int col, String string, Exception e)
 {
  super(string,e);
  
  column=col;
  this.row=row;
 }

 public int getColumn()
 {
  return column;
 }

 public int getRow()
 {
  return row;
 }

}
