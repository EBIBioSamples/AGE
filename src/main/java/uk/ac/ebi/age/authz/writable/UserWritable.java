package uk.ac.ebi.age.authz.writable;

import java.util.Collection;

import uk.ac.ebi.age.authz.User;

public interface UserWritable extends User
{
 Collection< ? extends UserGroupWritable> getGroups();

 void setId(String id);

 void setName(String name);

 void setEmail(String name);

 void setPass(String pass);

 void addGroup(UserGroupWritable grp);

 void removeGroup(UserGroupWritable gb);

}