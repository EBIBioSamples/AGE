package uk.ac.ebi.age.authz.impl;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import uk.ac.ebi.age.authz.BuiltInUsers;
import uk.ac.ebi.age.authz.Session;
import uk.ac.ebi.age.authz.SessionManager;

public class SessionManagerImpl implements SessionManager, Runnable
{
 private static final int CHECK_INTERVAL = 30000;
 private static final int MAX_SESSION_IDLE_TIME = 3000000;
 
 private Thread controlThread = new Thread( this );
 private boolean shutdown = false;

 private Map<String, Session> sessionMap = new TreeMap<String, Session>();
 private Map<Thread, Session> threadMap = new HashMap<Thread, Session>();
 private Lock lock = new ReentrantLock(); 
 
 private File sessDirRoot;
 
 public SessionManagerImpl( File sdr )
 {
  sessDirRoot=sdr;
  
  controlThread.setName("Session GC");
  controlThread.setDaemon(true);
  
  controlThread.start();
 }
 
 @Override
 public Session createSession(String userName)
 {
  String key = generateSessionKey(userName);

  File sessDir = new File(sessDirRoot,key);
  sessDir.mkdirs();
  
  Session sess = new Session( sessDir );
    
  sess.setSessionKey(key);
  sess.setUser(userName);
  sess.setLastAccessTime( System.currentTimeMillis() );

  try
  {
   lock.lock();

   sessionMap.put(key,sess);
  }
  finally
  {
   lock.unlock();
  }
  
  return sess;
 }

 @Override
 public Session getSession(String sessID)
 {
  try
  {
   lock.lock();

   Session sess = sessionMap.get(sessID);
   
   if( sess == null )
    return null;
   
   sess.setLastAccessTime( System.currentTimeMillis() );
   
   return sessionMap.get(sessID);
  }
  finally
  {
   lock.unlock();
  }
 }

 @Override
 public void shutdown()
 {
  shutdown = true;
  controlThread.interrupt();
 }

 @Override
 public void run()
 {
  while( ! shutdown )
  {
   try
   {
    Thread.sleep(CHECK_INTERVAL);
   }
   catch(InterruptedException e)
   {
   }

   
   try
   {
    lock.lock();
    
    long time = System.currentTimeMillis();

    Iterator<Session> sitr = sessionMap.values().iterator();
    
    while( sitr.hasNext() )
    {
     Session sess = sitr.next();
     
     if( ( time - sess.getLastAccessTime() ) > MAX_SESSION_IDLE_TIME || shutdown )
     {
      sitr.remove();
      sess.destroy();
      
      Iterator<Map.Entry<Thread, Session>> thIter = threadMap.entrySet().iterator();
      
      while( thIter.hasNext() )
      {
       Map.Entry<Thread, Session> me = thIter.next();
       
       if( me.getValue() == sess )
        thIter.remove();
      }
       
     }
      
    }
   }
   finally
   {
    lock.unlock();
   }
  }
 }

 final static String algorithm="MD5";
 
 private String generateSessionKey( String strs )
 {

  StringBuffer message = new StringBuffer(100);

  message.append( Math.random() );
  
  message.append(strs);

  message.append(System.currentTimeMillis());

  try
  {
   MessageDigest md5d = MessageDigest.getInstance(algorithm);

   byte[] digest = md5d.digest(message.toString().getBytes());

   message.setLength(0);
   message.append("K");

   for(int i = 0; i < digest.length; i++)
   {
    String byteHex = Integer.toHexString(digest[i]);
    
    if( byteHex .length() < 2 )
     message.append('0').append(byteHex.charAt(0));
    else
     message.append(byteHex.substring(byteHex.length()-2));
   }
   
   return message.toString();
  }
  catch(NoSuchAlgorithmException ex)
  {
   ex.printStackTrace();
   return String.valueOf(System.currentTimeMillis());
  }

 }


 @Override
 public Session getSession()
 {
  try
  {
   lock.lock();
   
   return threadMap.get(Thread.currentThread());
  }
  finally
  {
   lock.unlock();
  }
 }

 @Override
 public Session checkin(String sessId)
 {
  try
  {
   lock.lock();
   
   Session sess = sessionMap.get( sessId );
   
   if( sess != null )
   {
    sess.setLastAccessTime( System.currentTimeMillis() );
    
    threadMap.put(Thread.currentThread(), sess);
   }
   return sess;
  }
  finally
  {
   lock.unlock();
  }
 }

 @Override
 public Session checkout()
 {
  try
  {
   lock.lock();
   
   return threadMap.remove(Thread.currentThread());
  }
  finally
  {
   lock.unlock();
  }
 }

 @Override
 public String getEffectiveUser()
 {
  Session sess = getSession();
  
  if( sess == null )
   return BuiltInUsers.ANONYMOUS.getName();
  
  return sess.getUser();
 }

 @Override
 public Session getSessionByUser(String uid)
 {
  try
  {
   lock.lock();
   
   Iterator<Session> sessItr = sessionMap.values().iterator();
   
   while( sessItr.hasNext() )
   {
    Session s = sessItr.next();
    if(s.getUser().equals(uid) )
     return s;
   } 
   
   return null;
  }
  finally
  {
   lock.unlock();
  }
 }

}
