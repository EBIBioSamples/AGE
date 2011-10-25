package uk.ac.ebi.age.authz;

public enum BuiltInUsers
{
 ANONYMOUS("$anonymous", "Built-in anonymous user"),
 SUPERVISOR("$supervisor", "Built-in supervisor user");
 
 private String name;
 private String description;
 
 BuiltInUsers( String name, String desc )
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
