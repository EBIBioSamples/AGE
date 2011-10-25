package uk.ac.ebi.age.ext.authz;

public enum SystemAction
{
 READ("Read object",ActionGroup.OBJECT_ACCESS),
 CHANGE("Amend object",ActionGroup.OBJECT_ACCESS),
 DELETE("Delete object",ActionGroup.OBJECT_ACCESS),
 CREATE("Instantiate object",ActionGroup.INSTANTATION),
 
 CUSTCLASSDEF("Define custom class", ActionGroup.CUSTCLASSDEF),
 CUSTATTRCLASSDEF("Define custom attribute class", ActionGroup.CUSTCLASSDEF),
 CUSTQUALCLASSDEF("Define custom qualifier class", ActionGroup.CUSTCLASSDEF),
 CUSTRELCLASSDEF("Define custom relation class", ActionGroup.CUSTCLASSDEF),
 
 CREATESUBM("Create submission", ActionGroup.SUBMISSION );

 
 public static enum ActionGroup
 {
  OBJECT_ACCESS("Common object access"),
  INSTANTATION("Object instantation"),
  CUSTCLASSDEF("Custom class definition"),
  SUBMISSION("Submissions");

  String description;
  
  ActionGroup( String d )
  {
   description = d;
  }

  public String getDescription()
  {
   return description;
  }

 };
 
 ActionGroup group;
 String description;
 
 SystemAction(String desc, ActionGroup grp)
 {
  group=grp;
  description=desc;
 }

 public ActionGroup getGroup()
 {
  return group;
 }

 public String getDescription()
 {
  return description;
 }
}
