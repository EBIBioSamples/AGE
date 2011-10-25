package uk.ac.ebi.age.authz.writable;

import java.util.Collection;

import uk.ac.ebi.age.authz.UserGroup;

public interface UserGroupWritable extends UserGroup
{
 Collection<? extends UserWritable> getUsers();
 Collection< ? extends UserGroupWritable> getGroups();

 void setId(String id);

 void setDescription(String description);

 void addUser(UserWritable u);

 void addGroup(UserGroupWritable g);

 void removeUser(UserWritable ub);

 void removeGroup(UserGroup gp);

}