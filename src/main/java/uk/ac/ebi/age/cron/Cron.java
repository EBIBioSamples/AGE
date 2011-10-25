package uk.ac.ebi.age.cron;


public interface Cron
{
 void addTicListener( TickListener tl, int secInterval, boolean repeting );
 
 void removeTicListener( TickListener tl );
}
