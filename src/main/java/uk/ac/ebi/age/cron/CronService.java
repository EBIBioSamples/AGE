/*
 * Created on 14.04.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package uk.ac.ebi.age.cron;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import com.pri.util.ObjectRecycler;
import com.pri.util.Random;


public class CronService implements Runnable,Cron
{
 private long timeQuant;
 
 private Thread thr;
 long lastEventMin;
 long lastEventHour;
 long lastEventWeek;
 long lastEventMon;
 volatile int runningTasks=0;
 volatile boolean hasDelayed=false;

 private ExecutorService exeSrv;
 
 private List<TickListenerInfo> tickListeners=new LinkedList<TickListenerInfo>();
 
 private ObjectRecycler<Task> taskCache=new ObjectRecycler<Task>(4);
 
 private int avrgLoad=0;
 private int runCount=0;
 private int sumRunningTasks=0;

 private long nearestEvent;

 public CronService( )
 {
  this( null );
 }

 public CronService( ExecutorService es )
 {
  lastEventMin=lastEventHour=lastEventWeek=lastEventMon=System.currentTimeMillis();
  
  timeQuant = 1000;
  
  exeSrv=es;
 }

 public void destroy()
 {
  if( thr != null )
  {
   Thread t = thr;
   thr=null;
   t.interrupt();
  }
  
 }

 public void run()
 {
  while( thr != null )
  {
   runTasks();
   
   try
   {
    long toSleep = nearestEvent - System.currentTimeMillis();
    
    if( toSleep > 0 )
     Thread.sleep( toSleep );
   }
   catch(InterruptedException e)
   {}
  }
 }
 
 /*
 public void run()
 {
  Message minm = new Message( minuteListenerAddress );
  Message hourm = new Message( hourListenerAddress );
  Message weekm = new Message( weekListenerAddress );
  Message monm = new Message( monthListenerAddress );

  do
  {
   try
   {
    Thread.sleep( 60 * 1000 );
   }
   catch (InterruptedException e)
   {
    System.out.println("Cron thread ended");
    if( msgr == null )
     break;
   }

   long cTime = System.currentTimeMillis();
   
   try
   {
    if( cTime >= (lastEventMin + 60*1000) )
    {
     msgr.syncSend(minm);
     lastEventMin=cTime;
    }
   }
   catch (RecipientNotFoundException e)
   {}
   catch (NetworkException e)
   {}

   try
   {
    if( cTime >= (lastEventHour + 3600*1000) )
    {
     msgr.syncSend(hourm);
     lastEventHour=cTime;
    }
   }
   catch (RecipientNotFoundException e)
   {}
   catch (NetworkException e)
   {}

   try
   {
    if (cTime >= (lastEventWeek + 3600L * 24L * 7L * 1000L))
    {
     msgr.syncSend(weekm);
     lastEventWeek=cTime;
    }
   }
   catch (RecipientNotFoundException e)
   {
   }
   catch (NetworkException e)
   {
   }

   try
   {
    if (cTime >= (lastEventMon + 3600L * 24L * 30L * 1000L))
    {
     msgr.syncSend(monm);
     lastEventMon=cTime;
    }
   }
   catch (RecipientNotFoundException e)
   {
   }
   catch (NetworkException e)
   {
   }
  

  }
  while( thr != null );
 }
*/
 
 public synchronized void addTicListener(TickListener tl, int secInterval, boolean repeating)
 {
  tickListeners.add( new TickListenerInfo(tl,secInterval,repeating) );
  
  if( thr == null )
  {
   thr=new Thread(this, "CronService");
   thr.start();
  }
  else
   thr.interrupt();
 }
 
 public synchronized void removeTicListener(TickListener tl)
 {
  for(Iterator<TickListenerInfo> tlii = tickListeners.iterator(); tlii.hasNext();)
  {
   if( tlii.next().getListener() == tl )
   {
    tlii.remove();
    break;
   }
  }
  
  if( tickListeners.size() == 0 )
  {
   Thread t = thr;
   thr=null;
   t.interrupt();
  }
 }
 
 private synchronized void runTasks()
 {
  nearestEvent = 300 * 1000+System.currentTimeMillis();
  hasDelayed = false;
  for(TickListenerInfo tli : tickListeners)
  {
   if(tli.isReady())
   {
    if(!tli.isDelayTolerant())
     runTask(tli);
    else
    {
     if(runningTasks <= avrgLoad)
      runTask(tli);
     else
     {
      hasDelayed = true;
      long dly = System.currentTimeMillis()+timeQuant;
      
      if(nearestEvent > dly)
       nearestEvent = dly;
     }
    }
   }
   else if(nearestEvent > tli.getScheduledTime())
    nearestEvent = tli.getScheduledTime();
  }
 }
 
 private void runTask( TickListenerInfo tli )
 {
  Task task = newTask();
  task.setListener(tli);
  tli.scheduleNext();

  try
  {
   exeSrv.execute(task);
  }
  catch( RejectedExecutionException e )
  {
   e.printStackTrace();
   
   return;
  }

  sumRunningTasks+=runningTasks;
  runCount++;
  avrgLoad=(sumRunningTasks+runCount/2)/runCount;
 }
 
 private Task newTask()
 {
  Task t = taskCache.getObject();
  
  if( t == null )
   return new Task();
  
  return t;
 }
 
 private void recycleTask( Task t )
 {
  taskCache.recycleObject(t);
 }
 
 private class Task implements Runnable
 {
  private TickListenerInfo tli;
  
  void setListener( TickListenerInfo l )
  {
   tli=l;
  }

  public void run()
  {
   Thread.currentThread().setName("Cron task");
   
   runningTasks++;
   tli.getListener().clockTick();
   runningTasks--;

   if( !tli.isRepeating() )
    removeTicListener(tli.getListener());
   
   while(hasDelayed && thr != null)
   {
    tli = null;
    synchronized( CronService.this )
    {
     for(TickListenerInfo t : tickListeners)
     {
      if(t.isReady())
      {
       tli = t;
       tli.scheduleNext();
       break;
      }
     }
     
     if( tli == null )
      hasDelayed=false;
    }

    if(tli != null)
     tli.getListener().clockTick();
   }
   
   tli=null;
   recycleTask( this );
   
   Thread.currentThread().setName("Idle thread");
  }
 }
 
 
 private class TickListenerInfo
 {
  final static int TLR_DELAY_PERCENT=10;
  
  private TickListener listener;
  private int interval;
  private long schedTime;
  private long tlrDelay;
  private boolean repeating;
  
  public TickListenerInfo(TickListener tl, int ival, boolean rept )
  {
   listener=tl;
   interval=ival;
   
   schedTime=System.currentTimeMillis()+Random.randInt(0,interval)*1000;
   tlrDelay=interval*10*TLR_DELAY_PERCENT;
   
   repeating = rept;
  }

  public int getInterval()
  {
   return interval;
  }

  public TickListener getListener()
  {
   return listener;
  }

  public long getScheduledTime()
  {
   return schedTime;
  }
  
  public void scheduleNext()
  {
   schedTime+=interval*1000;
  }

  public boolean isReady()
  {
   return schedTime-tlrDelay < System.currentTimeMillis();
  }
  
  public boolean isDelayTolerant()
  {
   return schedTime+tlrDelay > System.currentTimeMillis();
  }

  public boolean isRepeating()
  {
   return repeating;
  }
 }
}
