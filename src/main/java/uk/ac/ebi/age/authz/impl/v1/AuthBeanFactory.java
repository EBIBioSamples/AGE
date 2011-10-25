package uk.ac.ebi.age.authz.impl.v1;

import uk.ac.ebi.age.authz.writable.PermissionForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.PermissionForUserACRWritable;
import uk.ac.ebi.age.authz.writable.PermissionProfileWritable;
import uk.ac.ebi.age.authz.writable.PermissionWritable;
import uk.ac.ebi.age.authz.writable.ProfileForGroupACRWritable;
import uk.ac.ebi.age.authz.writable.ProfileForUserACRWritable;
import uk.ac.ebi.age.authz.writable.TagWritable;
import uk.ac.ebi.age.authz.writable.UserGroupWritable;
import uk.ac.ebi.age.authz.writable.UserWritable;

public class AuthBeanFactory
{
 public AuthenticatedGroupBean createAuthenticatedGroupBean()
 {
  return new AuthenticatedGroupBean();
 }
 
 public ClassifierBean createClassifierBean()
 {
  return new ClassifierBean();
 }
 
 public EveryoneGroupBean createEveryoneGroupBean()
 {
  return new EveryoneGroupBean();
 }

 public UserGroupWritable createGroupBean()
 {
  return new GroupBean();
 }

 public PermissionWritable createPermissionBean()
 {
  return new PermissionBean();
 }

 public PermissionForGroupACRWritable createPermissionForGroupACRBean()
 {
  return new PermissionForGroupACRBean();
 }

 public PermissionForUserACRWritable createPermissionForUserACRBean()
 {
  return new PermissionForUserACRBean();
 }

 public PermissionProfileWritable createProfileBean()
 {
  return new ProfileBean();
 }

 public ProfileForGroupACRWritable createProfileForGroupACRBean()
 {
  return new ProfileForGroupACRBean();
 }

 public ProfileForUserACRWritable createProfileForUserACRBean()
 {
  return new ProfileForUserACRBean();
 }

 public TagWritable createTagBean()
 {
  return new TagBean();
 }

 public UserWritable createUserBean()
 {
  return new UserBean();
 }

}
