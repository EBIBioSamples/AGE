package uk.ac.ebi.age.parser;

import uk.ac.ebi.age.model.IdScope;

public interface SyntaxProfileDefinition
{
 public String getCustomTokenBrackets();
 
 public String getFlagsTokenBrackets();
 
 public String getQualifierTokenBrackets();
 
 public String getFlagsSeparatorSign();
 
 public String getFlagsEqualSign();
 
 public String getPrototypeObjectId();
 
 public String getAnonymousObjectId();
 
 public String getGlobalIdPrefix();

 public String getClusterIdPrefix();

 public String getModuleIdPrefix();

 public IdScope getDefaultIdScope();

 public String getHorizontalBlockPrefix();
 
 public String getVerticalBlockPrefix();
 
 public boolean isHorizontalBlockDefault();
 
 public boolean isResetPrototype();
}
