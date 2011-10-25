package uk.ac.ebi.age.parser.impl;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;

public class ClassSpecificSyntaxProfileDefinitionImpl implements SyntaxProfileDefinition
{
 private SyntaxProfileDefinition commonProfileDefinition;
 
 private String  customTokenBrackets;
 private String  flagsTokenBrackets;
 private String  qualifierTokenBrackets;
 private String  flagsSeparatorSign;
 private String  flagsEqualSign;
 private String  prototypeObjectId;
 private String  anonymousObjectId;

 private String  globalIdPrefix;
 private String  clusterIdPrefix;
 private String  moduleIdPrefix;

 private String  horizontalBlockPrefix;
 private String  verticalBlockPrefix;

 private IdScope defaultIdScope;
 private Boolean horizontalBlockDefault;
 private Boolean resetPrototype;

 public ClassSpecificSyntaxProfileDefinitionImpl( SyntaxProfileDefinition comm )
 {
  commonProfileDefinition = comm;
 }
 
 public String getCustomTokenBrackets()
 {
  if( customTokenBrackets != null )
   return customTokenBrackets;
  
  return commonProfileDefinition.getCustomTokenBrackets();
 }

 public void setCustomTokenBrackets(String customTokenBrackets)
 {
  this.customTokenBrackets = customTokenBrackets;
 }

 public String getFlagsTokenBrackets()
 {
  if( flagsTokenBrackets != null )
  return flagsTokenBrackets;
  
  return commonProfileDefinition.getFlagsTokenBrackets();
 }

 public void setFlagsTokenBrackets(String flagsTokenBrackets)
 {
  this.flagsTokenBrackets = flagsTokenBrackets;
 }

 public String getQualifierTokenBrackets()
 {
  if( qualifierTokenBrackets != null )
  return qualifierTokenBrackets;
  
  return commonProfileDefinition.getQualifierTokenBrackets();
 }

 public void setQualifierTokenBrackets(String qualifierTokenBrackets)
 {
  this.qualifierTokenBrackets = qualifierTokenBrackets;
 }

 public String getFlagsSeparatorSign()
 {
  if( flagsSeparatorSign != null )
  return flagsSeparatorSign;
  
  return commonProfileDefinition.getFlagsSeparatorSign();
 }

 public void setFlagsSeparatorSign(String flagsSeparatorSign)
 {
  this.flagsSeparatorSign = flagsSeparatorSign;
 }

 public String getFlagsEqualSign()
 {
  if( flagsEqualSign != null )
  return flagsEqualSign;
  
  return commonProfileDefinition.getFlagsEqualSign();
 }

 public void setFlagsEqualSign(String flagsEqualSign)
 {
  this.flagsEqualSign = flagsEqualSign;
 }

 public String getPrototypeObjectId()
 {
  if( prototypeObjectId != null )
  return prototypeObjectId;
  
  return commonProfileDefinition.getPrototypeObjectId();
 }

 public void setPrototypeObjectId(String prototypeObjectId)
 {
  this.prototypeObjectId = prototypeObjectId;
 }

 public String getAnonymousObjectId()
 {
  if( anonymousObjectId != null )
  return anonymousObjectId;
  
  return commonProfileDefinition.getAnonymousObjectId();
 }

 public void setAnonymousObjectId(String anonymousObjectId)
 {
  this.anonymousObjectId = anonymousObjectId;
 }

 public String getGlobalIdPrefix()
 {
  if( globalIdPrefix != null )
  return globalIdPrefix;
  
  return commonProfileDefinition.getGlobalIdPrefix();
 }

 public void setGlobalIdPrefix(String globalIdPrefix)
 {
  this.globalIdPrefix = globalIdPrefix;
 }

 public String getClusterIdPrefix()
 {
  if( clusterIdPrefix != null )
  return clusterIdPrefix;
  
  return commonProfileDefinition.getClusterIdPrefix();
 }

 public void setClusterIdPrefix(String clusterIdPrefix)
 {
  this.clusterIdPrefix = clusterIdPrefix;
 }

 public String getModuleIdPrefix()
 {
  if( moduleIdPrefix != null )
  return moduleIdPrefix;
  
  return commonProfileDefinition.getModuleIdPrefix();
 }

 public void setModuleIdPrefix(String moduleIdPrefix)
 {
  this.moduleIdPrefix = moduleIdPrefix;
 }

 public String getHorizontalBlockPrefix()
 {
  if( horizontalBlockPrefix != null )
  return horizontalBlockPrefix;
  
  return commonProfileDefinition.getHorizontalBlockPrefix();
 }

 public void setHorizontalBlockPrefix(String horizontalBlockPrefix)
 {
  this.horizontalBlockPrefix = horizontalBlockPrefix;
 }

 public String getVerticalBlockPrefix()
 {
  if( verticalBlockPrefix != null )
  return verticalBlockPrefix;
  
  return commonProfileDefinition.getVerticalBlockPrefix();
 }

 public void setVerticalBlockPrefix(String verticalBlockPrefix)
 {
  this.verticalBlockPrefix = verticalBlockPrefix;
 }

 public IdScope getDefaultIdScope()
 {
  if( defaultIdScope != null )
  return defaultIdScope;
  
  return commonProfileDefinition.getDefaultIdScope();
 }

 public void setDefaultIdScope(IdScope defaultScope)
 {
  this.defaultIdScope = defaultScope;
 }

 public boolean isHorizontalBlockDefault()
 {
  if( horizontalBlockDefault != null )
  return horizontalBlockDefault;
  
  return commonProfileDefinition.isHorizontalBlockDefault();
 }

 public void setHorizontalBlockDefault(boolean defaultHorizontal)
 {
  this.horizontalBlockDefault = defaultHorizontal;
 }

 public boolean isResetPrototype()
 {
  if( resetPrototype != null )
  return resetPrototype;
  
  return commonProfileDefinition.isResetPrototype();
 }

 public void setResetPrototype(boolean resetPrototype)
 {
  this.resetPrototype = resetPrototype;
 }

}