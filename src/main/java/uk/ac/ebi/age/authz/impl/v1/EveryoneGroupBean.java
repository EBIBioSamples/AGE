package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.mg.collection.Named;

public class EveryoneGroupBean extends GroupBean  implements UserGroup, Named<String>, Serializable
{

 private static final long serialVersionUID = 1L;

 EveryoneGroupBean()
 {}

 public boolean isPartOf(UserGroup pb)
 {
  return false;
 }

 @Override
 public boolean isUserCompatible(User u)
 {
  return true;
 }

}
