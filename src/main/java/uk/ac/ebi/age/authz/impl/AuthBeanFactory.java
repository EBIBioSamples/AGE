package uk.ac.ebi.age.authz.impl;

import uk.ac.ebi.age.authz.writable.ClassifierWritable;
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
 private static AuthBeanFactory instance = new AuthBeanFactory();
 
 public static AuthBeanFactory getInstance()
 {
  return instance;
 }
 
 private uk.ac.ebi.age.authz.impl.v1.AuthBeanFactory factoryV1 = new uk.ac.ebi.age.authz.impl.v1.AuthBeanFactory();
 
 public UserGroupWritable createAuthenticatedGroupBean()
 {
  return factoryV1.createAuthenticatedGroupBean();
 }
 
 public ClassifierWritable createClassifierBean()
 {
  return factoryV1.createClassifierBean();
 }

 public TagWritable createTagBean()
 {
  return factoryV1.createTagBean();
 }

 public UserWritable createUserBean()
 {
  return factoryV1.createUserBean();
 }

 public UserGroupWritable createEveryoneGroupBean()
 {
  return factoryV1.createEveryoneGroupBean();
 }

 public UserGroupWritable createGroupBean()
 {
  return factoryV1.createGroupBean();
 }

 public PermissionProfileWritable createProfileBean()
 {
  return factoryV1.createProfileBean();
 }

 public PermissionWritable createPermissionBean()
 {
  return factoryV1.createPermissionBean();
 }

 public ProfileForGroupACRWritable createProfileForGroupACRBean()
 {
  return factoryV1.createProfileForGroupACRBean();
 }

 public ProfileForUserACRWritable createProfileForUserACRBean()
 {
  return factoryV1.createProfileForUserACRBean();
 }

 public PermissionForUserACRWritable createPermissionForUserACRBean()
 {
  return factoryV1.createPermissionForUserACRBean();
 }

 public PermissionForGroupACRWritable createPermissionForGroupACRBean()
 {
  return factoryV1.createPermissionForGroupACRBean();
 }
}
