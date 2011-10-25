package uk.ac.ebi.age.model;

import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public interface ContextSemanticModel extends SemanticModel
{
// AgeClass getDefinedAgeClass(String name);
 AgeClass getCustomAgeClass(String name);

 AgeRelationClass getCustomAgeRelationClass(String name);
 AgeAttributeClass getCustomAgeAttributeClass(String name, AgeClass blkCls);

 AgeClassWritable getOrCreateCustomAgeClass(String name, String pfx, AgeClass parent);
 AgeAttributeClassWritable getOrCreateCustomAgeAttributeClass(String name, DataType type, AgeClass owner, AgeAttributeClassWritable superClass);
 AgeRelationClassWritable getOrCreateCustomAgeRelationClass(String name, AgeClass range, AgeClass owner, AgeRelationClass superClass);

 AgeClassProperty getDefinedAgeClassProperty( String name );

 DataModuleWritable createDataModule();

 SemanticModel getMasterModel();
 void setMasterModel(SemanticModel newModel);

 AgeAttributeClassPlug getAgeAttributeClassPlug(AgeAttributeClass attrClass);
 
 AgeClassPlug getAgeClassPlug(AgeClass cls);
 
 AgeRelationClassPlug getAgeRelationClassPlug(AgeRelationClass relClass);

 
 AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass, AttributedWritable host);
 AgeObjectWritable createAgeObject( ClassRef clsR, String id);
 
 AgeExternalRelationWritable createExternalRelation(RelationClassRef clsRef, AgeObjectWritable sourceObj, String val);
 AgeAttributeWritable createExternalObjectAttribute( AttributeClassRef atCls, AttributedWritable host, String val );
 AgeRelationWritable createAgeRelation(RelationClassRef clsRef, AgeObjectWritable sourceObj, AgeObjectWritable targetObj);

}
