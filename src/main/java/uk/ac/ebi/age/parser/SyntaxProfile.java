package uk.ac.ebi.age.parser;

import java.util.HashMap;
import java.util.Map;


public class SyntaxProfile
{
 private SyntaxProfileDefinition commonSyntaxProfileDefinition = AgeDefaultSyntaxProfileDefinition.getInstance();
 private Map<String,SyntaxProfileDefinition> classDefMap = new HashMap<String, SyntaxProfileDefinition>();
 
 public SyntaxProfileDefinition getCommonSyntaxProfile()
 {
  return commonSyntaxProfileDefinition;
 }
 
 public void setCommonSyntaxProfile( SyntaxProfileDefinition def )
 {
  commonSyntaxProfileDefinition = def;
 }

 public SyntaxProfileDefinition getClassSpecificSyntaxProfile( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef != null )
   return clDef;
  
  return commonSyntaxProfileDefinition;
 }
 
 public void addClassSpecificSyntaxProfile( String className, SyntaxProfileDefinition profile )
 {
  classDefMap.put(className, profile);
 }
}
