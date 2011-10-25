package uk.ac.ebi.age.annotation;

import java.io.Serializable;

import uk.ac.ebi.age.ext.annotation.AnnotationDBException;
import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionalDB;

public interface AnnotationManager extends TransactionalDB
{
 Object getAnnotation(Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException;

 Object getAnnotation(ReadLock lock, Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException;

 boolean addAnnotation(Topic tpc, Entity objId, Serializable value) throws AnnotationDBException;

 boolean addAnnotation(Transaction trn, Topic tpc, Entity objId, Serializable value) throws AnnotationDBException;

 boolean removeAnnotation(Transaction trn, Topic tpc, Entity objId, boolean rec) throws AnnotationDBException;
}
