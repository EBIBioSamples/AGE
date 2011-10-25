package uk.ac.ebi.age.authz;

import java.io.File;

public class Session
{
 private String sessionKey;
 private String user;
 private long lastAccessTime;
 
 private File sessionDir;
 
 private int tmpFileCounter = 0;

 public Session( File sessDir )
 {
  sessionDir = sessDir;
 }
 
 public String getUser()
 {
  return user;
 }

 public void setUser(String user)
 {
  this.user = user;
 }

 public long getLastAccessTime()
 {
  return lastAccessTime;
 }

 public void setLastAccessTime(long lastAccessTime)
 {
  this.lastAccessTime = lastAccessTime;
 }

 public String getSessionKey()
 {
  return sessionKey;
 }

 public void setSessionKey(String sessionKey)
 {
  this.sessionKey = sessionKey;
 }

 public File makeTempFile()
 {
  return new File( sessionDir, String.valueOf(++tmpFileCounter));
 }



 public void destroy()
 {
  if( sessionDir != null )
  {
   for( File f : sessionDir.listFiles() )
    f.delete();
   
   sessionDir.delete();
  }
 }
}
