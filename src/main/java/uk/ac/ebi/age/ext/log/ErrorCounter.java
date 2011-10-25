package uk.ac.ebi.age.ext.log;

public interface ErrorCounter
{
 int getErrorCounter();
 void incErrorCounter();
 void addErrorCounter(int countErrors);
 void resetErrorCounter();
}
