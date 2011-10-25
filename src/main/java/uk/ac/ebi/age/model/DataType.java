package uk.ac.ebi.age.model;


public enum DataType
{
 INTEGER(false,true,false),
 REAL   (false,true,false),
 STRING (true,false,false),
 TEXT   (true,false,true),
 URI    (true,false,false),
 BOOLEAN(false,false,false),
 GUESS  (false,false,false), 
 OBJECT (false,false,false),
 FILE   (false,false,false);
 
 
 boolean isTextual;
 boolean isNumeric;
 boolean isMultiline;
 
 private DataType(boolean txt, boolean num, boolean ml)
 {
  isTextual = txt;
  isNumeric = num;
  isMultiline = ml;
 }
 
 public boolean isTextual()
 {
  return isTextual;
 }
 
 public boolean isNumeric()
 {
  return isNumeric;
 }

 public boolean isMultiline()
 {
  return isMultiline;
 }
}
