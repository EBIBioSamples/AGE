package uk.ac.ebi.age.parser;

public class AgeTabValue extends AgeTabElement
{
 private String value;
 private ClassReference colHeader;

 public AgeTabValue(int row, int col, String value, ClassReference prop)
 {
  super(row, col);
  this.value=value;
  colHeader=prop;
 }

 public String getValue()
 {
  return value;
 }

 public ClassReference getColumnHeader()
 {
  return colHeader;
 }
}
