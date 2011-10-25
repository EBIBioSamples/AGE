package uk.ac.ebi.age.parser;

import java.util.List;

import uk.ac.ebi.age.util.StringUtil;

public abstract class AgeTabSyntaxParser
{
 public static final String rangeFlag="RANGE";
 public static final String typeFlag="TYPE";
 public static final String targetFlag="TARGET";

 private SyntaxProfile syntaxProfile;
 private SyntaxProfileDefinition syntaxProfileDef;

 protected AgeTabSyntaxParser(SyntaxProfile sp)
 {
  syntaxProfile = sp;
  syntaxProfileDef = sp.getCommonSyntaxProfile();
 }
 
 private static interface StrProc
 {
  String getBrackets();
  void process(ClassReference nm, String s) throws ParserException;
 }
 

 
 private StrProc[] prc = new StrProc[]{
   new StrProc()
   {
    public String getBrackets(){ return syntaxProfileDef.getFlagsTokenBrackets(); }
    public void process(ClassReference nm, String s) throws ParserException
    {
     List<String> flags = StringUtil.splitString(s, syntaxProfileDef.getFlagsSeparatorSign() );
     
     for( String flagstr : flags )
     {
      int eqpos = flagstr.indexOf(syntaxProfileDef.getFlagsEqualSign());
      
      if( eqpos == -1 )
       nm.addFlag(flagstr,null);
      else
      {
       String fname = flagstr.substring(0,eqpos);
       String fval =  flagstr.substring(eqpos+1);
       
       nm.addFlag(fname,fval);
       
       if( fname.equals( rangeFlag ) )
       {
        try
        {
         nm.setRangeClassRef( string2ClassReference(fval) );
        }
        catch (ParserException e)
        {
         throw new ParserException(0,0,"Invalid range class reference: "+e.getMessage());
        }
       }
       else if( fname.equals( targetFlag ) )
       {
        try
        {
         nm.setTargetClassRef( string2ClassReference(fval) );
        }
        catch (ParserException e)
        {
         throw new ParserException(0,0,"Invalid target class reference: "+e.getMessage());
        }
       }
      }
     }
    }
   },
   
   new StrProc()
   {
    public String getBrackets(){ return syntaxProfileDef.getQualifierTokenBrackets(); }
    public void process(ClassReference nm,String s) throws ParserException
    {
     ClassReference cr = string2ClassReference(s);
     
     nm.insertQualifier(cr);
    }
   }
   
 };
 
 
 public SyntaxProfile getSyntaxProfile()
 {
  return syntaxProfile;
 }
 
// public static AgeTabSyntaxParser getInstance()
// {
//  return new AgeTabSyntaxParserImpl(  );
// }
//
// public AgeTabModule parse( String txt ) throws ParserException
// {
//  return parse( txt, syntaxProfile );
// }
 
 public abstract AgeTabModule parse( String txt ) throws ParserException;

 
 public ClassReference string2ClassReference( String str ) throws ParserException
 {
  final ClassReference nm = new ClassReference();
  
  nm.setRawReference( str.trim() );
  
  while( str.length() > 0 )
  {
   String ps = null;
   
   for(int i=0; i < prc.length; i++ ) // Looking for expressions in braces and calling 'process' for such expressions
   {
    if( str.charAt(str.length()-1) == prc[i].getBrackets().charAt(1) )
    {
     int level = 0;
     
     int j;
     for( j= str.length()-2; j>=0; j--)
     {
      if( str.charAt(j) == prc[i].getBrackets().charAt(1) ) //We've found another closing brace so assuming a nested expression
       level++;
      else if( str.charAt(j) == prc[i].getBrackets().charAt(0) )
      {
       if( level > 0 )
        level--;
       else
       {
        ps = str.substring(j+1,str.length()-1);
        prc[i].process(nm, ps);

        str=str.substring(0,j);

        break;
       }
      }
     }
     
     if( j < 0 )
      throw new ParserException(0,0, "No opening bracket for section: '"+prc[i].getBrackets().charAt(0)+"'");
     
    }
     
   }
   
   if( ps == null )
    break;
  }
  
  
  String name = null;
  
  if( str.charAt(0) == syntaxProfileDef.getCustomTokenBrackets().charAt(0) )
  {
   int pos = str.indexOf(syntaxProfileDef.getCustomTokenBrackets().charAt(1));
   
   if( pos == -1 )
    throw new ParserException(0,0, "No closing bracket: '"+syntaxProfileDef.getCustomTokenBrackets().charAt(1)+"'");
   
   if( pos != (str.length()-1) )
    throw new ParserException(0,0, "Invalid character at: "+(pos+1)+". The closing bracket must be the last symbol of the token.");
   
   name = str.substring(1, pos);
   nm.setCustom(true);
  }
  else
  {
   if( str.charAt(str.length()-1) == syntaxProfileDef.getCustomTokenBrackets().charAt(1) )
   {
    int pos = str.indexOf(syntaxProfileDef.getCustomTokenBrackets().charAt(0));
    
    if( pos == -1 )
     throw new ParserException(0,0, "Invalid character at: "+(str.length())+". The closing bracket must correspond to opening one.");
    
    name = str.substring(pos+1,str.length()-1);
    nm.setParentClass( str.substring(0,pos) );
    nm.setCustom(true);
   }
   else
   {
    name = str;
    nm.setCustom(false);
   }
  }
  
  if( name.length() == 0 )
   throw new ParserException(0,0, "Name in the header column can't be empty");
  
  nm.setName(name);
  
  return nm;
 }
 

}

