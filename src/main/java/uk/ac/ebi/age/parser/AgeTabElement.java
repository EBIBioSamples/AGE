package uk.ac.ebi.age.parser;

public abstract class AgeTabElement
{
 protected int row;
 protected int col;

 protected AgeTabElement(int row, int col)
 {
  super();
  this.row = row;
  this.col = col;
 }
 
 public int getRow()
 {
  return row;
 }
 
 public void setRow(int row)
 {
  this.row = row;
 }
 
 public int getCol()
 {
  return col;
 }
 
 public void setCol(int col)
 {
  this.col = col;
 }
 
 
}