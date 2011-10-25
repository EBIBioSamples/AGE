package uk.ac.ebi.age.parser;

import java.util.Map;
import java.util.TreeMap;

public class Name
{
 private Type type;
 private String name;
 private Map<String,String> flags;
 
 public enum Type
 {
  CUSTOM,
  VALIDATED,
  RELATION
 }

 public void setType(Type t)
 {
  type=t;
  
 }

 public Type getType()
 {
  return type;
 }

 public void setName(String nm)
 {
  name=nm;
 }

 public String getName()
 {
  return name;
 }

 public void addFlag(String nm, String vl)
 {
  if( flags == null )
   flags = new TreeMap<String, String>();
  
  flags.put(nm, vl);
 }

 public boolean isFlagSet( String fl )
 {
  if( flags == null )
  return false;
  
  return flags.containsKey(fl);
 }
 
 public String getFlagValue( String fl )
 {
  if( flags == null )
  return null;
  
  return flags.get(fl);
 }
 
 public Map<String,String> getFlags()
 {
  return flags;
 }
}
