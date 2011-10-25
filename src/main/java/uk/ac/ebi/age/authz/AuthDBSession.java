package uk.ac.ebi.age.authz;

import java.util.List;




public interface AuthDBSession
{
 User createUser( String userId );
 User getUser( String userId );
 List<User> getUsers( int offset, int limit);

 UserGroup createUserGroup( String userId );
 UserGroup getUserGroup( String userId );
 List<UserGroup> getGroups( int offset, int limit);
 
 PermissionProfile createProfile( String Id );
 PermissionProfile getProfile( String Id );
 List<PermissionProfile> getPermissionProfiles( int offset, int limit);
 
 AccessTag createAccessTag( String Id );
 AccessTag getAccessTag( String Id );
 List<AccessTag> getAccessTags( int offset, int limit);

 
 void commit();
 void release();
}
