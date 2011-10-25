package uk.ac.ebi.age.transaction;

public interface TransactionalDB
{
 ReadLock getReadLock();
 void releaseLock( ReadLock l );
 
 Transaction startTransaction();
 void commitTransaction( Transaction t ) throws TransactionException;
 void prepareTransaction( Transaction t ) throws TransactionException;
 void rollbackTransaction( Transaction t ) throws TransactionException;
}
