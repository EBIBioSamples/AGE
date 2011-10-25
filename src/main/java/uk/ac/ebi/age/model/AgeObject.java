package uk.ac.ebi.age.model;

import java.util.Collection;

import uk.ac.ebi.age.ext.entity.Entity;

/**
 @model
*/

public interface AgeObject extends AgeAbstractObject, Entity
{

 /** @model */
 String getId();
 IdScope getIdScope();
// String getOriginalId();

 AgeClass getAgeElClass();
 
 Collection<? extends AgeRelation> getRelations();

// Collection<String> getRelationClassesIds();
 Collection< ? extends AgeRelationClass> getRelationClasses();

// Collection< ? extends AgeRelation> getRelationsByClassId(String cid);
 Collection< ? extends AgeRelation> getRelationsByClass(AgeRelationClass cls, boolean wSubCls);

 
 
 Object getAttributeValue( AgeAttributeClass cls );
 
 int getOrder();

 DataModule getDataModule();

}
