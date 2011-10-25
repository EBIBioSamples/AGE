package uk.ac.ebi.age.model;

public enum RestrictionType
{
 MAY ("MAY"),
 MUST ("MUST"),
 MUSTNOT ("MUST NOT");
 
 private String title;
 
 RestrictionType(String tl)
 {
  title=tl;
 }
 
 public String getTitle()
 {
  return title;
 }
}
