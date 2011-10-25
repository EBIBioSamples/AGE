package uk.ac.ebi.age.authz;

import java.util.Collection;

import uk.ac.ebi.mg.collection.Named;

public interface User extends Subject, Named<String>
{
 String getId();
 String getName();
 String getEmail();
 String getPass();

 Collection< ? extends UserGroup> getGroups();
}