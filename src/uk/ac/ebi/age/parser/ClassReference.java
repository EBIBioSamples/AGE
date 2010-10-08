package uk.ac.ebi.age.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClassReference extends AgeTabElement
{

 private boolean custom;
 private String name;
 private Map<String,String> flags;
 private LinkedList<ClassReference> qualifiers;
 private String parentClass;

 public ClassReference()
 {
  super(0,0);
 }
 
 public ClassReference(int row, int col)
 {
  super(row, col);
 }
 
 
 public void setCustom(boolean t)
 {
  custom=t;
 }

 public boolean isCustom()
 {
  return custom;
 }

 public void setName(String nm)
 {
  name=nm;
 }

 public String getName()
 {
  return name;
 }

 public List<ClassReference> getQualifiers()
 {
  return qualifiers;
 }
 
 public void addQualifier(ClassReference s)
 {
  if( qualifiers == null )
   qualifiers = new LinkedList<ClassReference>();
  
  qualifiers.add(s);
 }

 public void insertQualifier(ClassReference s)
 {
  if( qualifiers == null )
   qualifiers = new LinkedList<ClassReference>();
  
  qualifiers.addFirst(s);
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

 public String getParentClass()
 {
  return parentClass;
 }

 public void setParentClass(String parentClass)
 {
  this.parentClass = parentClass;
 }

 public boolean isQualifierFor(ClassReference cr)
 {
  if( ! ( getName().equals( cr.getName()) && isCustom() == isCustom() ) )
   return false;

  if( getQualifiers() == null || getQualifiers().size() == 0 )
    return false;
  
  if( cr.getQualifiers() == null && getQualifiers().size() != 1 )
   return false;

  if( getQualifiers().size() != ( cr.getQualifiers().size()+1 ) )
   return false;
  
  Iterator<ClassReference> iter1 = getQualifiers().iterator();
  Iterator<ClassReference> iter2 = cr.getQualifiers().iterator();
 
  while( iter2.hasNext() )
   if( ! iter1.next().equals(iter2.next()) )
    return false;
  
  return true;
 }

 public boolean equals( ClassReference cr )
 {
  if( ! ( getName().equals( cr.getName()) && isCustom() == isCustom() ) )
   return false;

  if( getQualifiers() == null || getQualifiers().size() == 0 )
  {
   if( cr.getQualifiers() == null || cr.getQualifiers().size() == 0 )
    return true;
   else
    return false;
  }
  
  if( cr.getQualifiers() == null || cr.getQualifiers().size() == 0 )
   return false;

  if( getQualifiers().size() != cr.getQualifiers().size() )
   return false;
  
  Iterator<ClassReference> iter1 = getQualifiers().iterator();
  Iterator<ClassReference> iter2 = cr.getQualifiers().iterator();
 
  while( iter1.hasNext() )
   if( ! iter1.next().equals(iter2.next()) )
    return false;
  
  return true;
 }
 
}
