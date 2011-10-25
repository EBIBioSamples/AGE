package uk.ac.ebi.age.annotation.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import uk.ac.ebi.age.annotation.AnnotationManager;
import uk.ac.ebi.age.ext.entity.Entity;

import com.pri.util.ObjectRecycler;
import com.pri.util.StringUtils;

public abstract class AbstractAnnotationStorage implements AnnotationManager
{
 private ObjectRecycler< Deque<Entity> > dqRecycler = new ObjectRecycler<Deque<Entity>>(3);
 private ObjectRecycler< StringBuilder > sbRecycler = new ObjectRecycler<StringBuilder>(3);

 protected String createEntityId( Entity ett )
 {
  StringBuilder sb = sbRecycler.getObject();

  if( sb == null )
   sb = new StringBuilder(300);
  
  appendEntityId(ett, sb, false, '0', '0');
  
  String id = sb.toString();
  
  sb.setLength(0);
  sbRecycler.recycleObject(sb);
  
  return id;
 }

 protected void appendEntityId( Entity ett, StringBuilder sb, boolean esc, char shch, char escch )
 {
  Deque<Entity> dq = dqRecycler.getObject();
  
  if( dq == null )
   dq = new ArrayDeque<Entity>(10);
  
  Entity cEnt = ett;
  
  do
  {
   dq.add(cEnt);
   
   cEnt=cEnt.getParentEntity();
  }
  while( cEnt != null );
  
  Iterator<Entity> etItr = dq.descendingIterator();
  
  while( etItr.hasNext() )
  {
   Entity e = etItr.next();
   
   sb.append('/')
     .append(e.getEntityDomain().name()).append(':')
     .append(e.getEntityID().length()).append(':');
   
     if( esc )
      StringUtils.appendEscaped(sb, e.getEntityID(), shch, escch);
     else
      sb.append(e.getEntityID());
  }
  
 
  dq.clear();
  dqRecycler.recycleObject(dq);
  
 }
 
}
