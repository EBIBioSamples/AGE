package uk.ac.ebi.age.parser;

import uk.ac.ebi.age.model.IdScope;

public class AgeDefaultSyntaxProfileDefinition implements SyntaxProfileDefinition
{
 public static final String customTokenBrackets="{}";
 public static final String flagsTokenBrackets="<>";
 public static final String qualifierTokenBrackets="[]";
 public static final String flagsSeparatorSign=";";
 public static final String flagsEqualSign="=";
 public static final String prototypeObjectId="*";
 public static final String anonymousObjectId="?";
 
 public static final String globalIdPrefix="^";
 public static final String clusterIdPrefix="&";
 public static final String moduleIdPrefix="$";

 public static final String horizontalBlockPrefix="-";
 public static final String verticalBlockPrefix="|";
 
 public static final IdScope defaultIdScope=IdScope.CLUSTER;
 public static final boolean horizontalBlockDefault = true;
 public static final boolean resetPrototype = true;
 
 private static AgeDefaultSyntaxProfileDefinition instance  = new AgeDefaultSyntaxProfileDefinition();
 
 public static AgeDefaultSyntaxProfileDefinition getInstance()
 {
  return instance;
 }
 
 private AgeDefaultSyntaxProfileDefinition()
 {}
 
 @Override
 public String getCustomTokenBrackets()
 {
  return customTokenBrackets;
 }
 
 @Override
 public String getFlagsTokenBrackets()
 {
  return flagsTokenBrackets;
 }

 @Override
 public String getQualifierTokenBrackets()
 {
  return qualifierTokenBrackets;
 }

 @Override
 public String getFlagsSeparatorSign()
 {
  return flagsSeparatorSign;
 }
 
 @Override
 public String getFlagsEqualSign()
 {
  return flagsEqualSign;
 }
 
 @Override
 public String getPrototypeObjectId()
 {
  return prototypeObjectId;
 }
 @Override
 public String getAnonymousObjectId()
 {
  return anonymousObjectId;
 }
 @Override
 public String getGlobalIdPrefix()
 {
  return globalIdPrefix;
 }
 @Override
 public String getClusterIdPrefix()
 {
  return clusterIdPrefix;
 }
 @Override
 public String getModuleIdPrefix()
 {
  return moduleIdPrefix;
 }
 @Override
 public IdScope getDefaultIdScope()
 {
  return defaultIdScope;
 }

 @Override
 public String getHorizontalBlockPrefix()
 {
  return horizontalBlockPrefix;
 }

 @Override
 public String getVerticalBlockPrefix()
 {
  return verticalBlockPrefix;
 }

 @Override
 public boolean isHorizontalBlockDefault()
 {
  return horizontalBlockDefault;
 }

 @Override
 public boolean isResetPrototype()
 {
  return resetPrototype;
 }

}
