package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.RelationClassRef;



/**
 @model
*/

public interface AgeObjectWritable extends AgeObject, AttributedWritable
{
 DataModuleWritable getDataModule();
 
 Collection<? extends AgeRelationWritable> getRelations();
 
// Collection< ? extends AgeRelationWritable> getRelationsByClassId(String cid);
 Collection< ? extends AgeRelationWritable> getRelationsByClass(AgeRelationClass cls, boolean wSbCl);
 
 
 void addAttribute(AgeAttributeWritable attr);
 void removeAttribute(AgeAttributeWritable attr);

 void addRelation(AgeRelationWritable r);
 void removeRelation(AgeRelationWritable rel);


 AgeExternalRelationWritable createExternalRelation( RelationClassRef ref, String val);

 AgeRelationWritable createRelation(RelationClassRef ref, AgeObjectWritable targetObj);

 void setOrder(int row);

 void setDataModule( DataModuleWritable s );
 
 void setId( String id );

 void setIdScope( IdScope scp );
// void setOriginalId(String nId);


 
// void resetModel();
}
