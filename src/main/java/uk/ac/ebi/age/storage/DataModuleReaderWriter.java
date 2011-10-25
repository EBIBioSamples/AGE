package uk.ac.ebi.age.storage;

import java.io.File;
import java.io.IOException;

import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public interface DataModuleReaderWriter
{
 DataModuleWritable read( File f ) throws IOException, ClassNotFoundException;
 void write( DataModule s, File f ) throws IOException;
 String getExtension();
}
