/**
 * 
 */
package uk.ac.ebi.age.storage.index;


public class TextFieldExtractor
{
 private String name;
 private TextValueExtractor extractor;
 
 public TextFieldExtractor(String fName, TextValueExtractor extr)
 {
  name=fName;
  extractor = extr;
 }

 public String getName()
 {
  return name;
 }
 
 public void setName(String name)
 {
  this.name = name;
 }
 
 public TextValueExtractor getExtractor()
 {
  return extractor;
 }
 
 public void setExtractor(TextValueExtractor extractor)
 {
  this.extractor = extractor;
 }
 

}