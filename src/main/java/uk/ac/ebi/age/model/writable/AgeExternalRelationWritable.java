package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeExternalRelation;

public interface AgeExternalRelationWritable extends AgeExternalRelation, AgeRelationWritable
{
 void setSourceObject(AgeObjectWritable ageObject);
 void setTargetObject( AgeObjectWritable obj );
 
 
 AgeExternalRelationWritable getInverseRelation();
 
 void setInverseRelation( AgeExternalRelationWritable inrv );

}
