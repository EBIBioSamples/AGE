package uk.ac.ebi.age.authz;

public enum BuiltInGroups
{
 EVERYONE("$everyone", "Built-in everyone group"),
 AUTHENTICATED("$authenticated", "Built-in authenticated users group");
 
 private String name;
 private String description;
 
 BuiltInGroups( String name, String desc )
 {
  this.name=name;
  description=desc;
 }

 public String getName()
 {
  return name;
 }

 public String getDescription()
 {
  return description;
 }

}
