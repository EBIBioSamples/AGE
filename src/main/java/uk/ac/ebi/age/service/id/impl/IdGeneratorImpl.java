package uk.ac.ebi.age.service.id.impl;

import uk.ac.ebi.age.service.id.IdGenerator;

public class IdGeneratorImpl extends IdGenerator
{
 private long init=(System.currentTimeMillis() - 1270000000L*1000L)*100;

 @Override
 public String getStringId()
 {
  return String.valueOf(init++);
 }

 @Override
 public String getStringId( String theme )
 {
  return getStringId();
 }

 @Override
 public void shutdown()
 {
 }

}
