package uk.ac.ebi.age.parser;

import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public interface AgeTab2AgeConverter
{
 
 public DataModuleWritable convert( AgeTabModule data, ContextSemanticModel sm, SyntaxProfile syntaxProfile, LogNode logNode );
}
