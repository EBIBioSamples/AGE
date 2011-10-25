package uk.ac.ebi.age.storage;

public class ModuleKey implements Comparable<ModuleKey>
{
 private String clusterId;
 private String moduleId;
 
 public ModuleKey()
 {}
 
 public ModuleKey(String clusterId, String moduleId)
 {
  this.clusterId = clusterId;
  this.moduleId = moduleId;
 }

 public boolean equals( Object mk2 )
 {
  return ((ModuleKey)mk2).clusterId.equals(clusterId) && ((ModuleKey)mk2).moduleId.equals(moduleId);
 }
 
 public int hashCode()
 {
  return clusterId.hashCode()+moduleId.hashCode();
 }

 public String getClusterId()
 {
  return clusterId;
 }

 public void setClusterId(String clusterId)
 {
  this.clusterId = clusterId;
 }

 public String getModuleId()
 {
  return moduleId;
 }

 public void setModuleId(String moduleId)
 {
  this.moduleId = moduleId;
 }

 @Override
 public int compareTo(ModuleKey o)
 {
  int dif = clusterId.compareTo(o.clusterId);
  
  if( dif != 0 )
   return dif;
  
  return moduleId.compareTo(o.moduleId);
 }
}