package uk.ac.ebi.age.authz;

import java.util.Collection;

import uk.ac.ebi.age.authz.writable.UserWritable;
import uk.ac.ebi.mg.collection.Named;

public interface UserGroup extends Subject, Named<String>
{
 String getId();
 String getDescription();

 Collection<? extends User> getUsers();
 Collection< ? extends UserGroup> getGroups();
 
 boolean isPartOf(UserGroup pb);

 UserWritable getUser(String userId);
 UserGroup getGroup(String partId);

}
