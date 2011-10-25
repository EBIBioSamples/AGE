package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.SemanticModel;

public interface DataModuleWritable extends DataModule
{
 void setId( String id );
// void setVersion( long ts );
// void setDescription( String des );
 void setClusterId( String clstId );
 
// void addClass(AgeClass cls);
 void addObject( AgeObjectWritable obj );

 Collection<AgeObjectWritable> getObjects();
 
 void setMasterModel(SemanticModel newModel);
 
 Collection<? extends AgeExternalRelationWritable> getExternalRelations();
 Collection<? extends AgeExternalObjectAttributeWritable> getExternalObjectAttributes();

 Collection<? extends AttributedWritable> getAttributed( AttributedSelector sel );

 
 void registerExternalRelation( AgeExternalRelationWritable rel );
}
