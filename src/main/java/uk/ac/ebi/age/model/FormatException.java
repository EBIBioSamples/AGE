package uk.ac.ebi.age.model;

public class FormatException extends Exception
{
 int row, col;

 public FormatException(String msg, Exception e)
 {
  super(msg,e);
 }

 public int getRow()
 {
  return row;
 }
 
 public int getCol()
 {
  return row;
 }

}
