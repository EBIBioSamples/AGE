package uk.ac.ebi.age.authz.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.authz.AuthDB;
import uk.ac.ebi.age.authz.AuthDBSession;
import uk.ac.ebi.age.authz.User;

import com.pri.util.collection.ListFragment;

public class H2AuthDBImpl implements AuthDB
{
 private List<User> userList;
 
 public H2AuthDBImpl()
 {
  userList = new ArrayList<User>(200);
  
  for( int i=1; i < 150; i++ )
  {
   UserBean u = new UserBean();
   
   u.setId("User"+i);
   u.setName("Test User №"+i);
   
   userList.add(u);
  }
  
 }

 @Override
 public AuthDBSession createSession()
 {
  // TODO Auto-generated method stub
  throw new dev.NotImplementedYetException();
  //return null;
 }

 @Override
 public List<User> getUsers(int begin, int end)
 {
  int to = end != -1?end:userList.size();
  
  return userList.subList(begin, to);
 }

 @Override
 public ListFragment<User> getUsers(String idPat, String namePat, int begin, int end)
 {
  int pos=0;
  
  int to = end != -1?end:userList.size();

  
  ListFragment<User> res = new ListFragment<User>();
  
  List<User> sel = new ArrayList<User>();
  
  res.setList(sel);
  
  for( User u : userList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getName().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos <= to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public int getUsersTotal()
 {
  return userList.size();
 }

}
