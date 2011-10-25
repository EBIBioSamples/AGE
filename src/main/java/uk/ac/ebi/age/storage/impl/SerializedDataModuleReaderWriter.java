package uk.ac.ebi.age.storage.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;

public class SerializedDataModuleReaderWriter implements DataModuleReaderWriter
{

 @Override
 public DataModuleWritable read(File f) throws IOException, ClassNotFoundException
 {
  ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) );
  
  DataModuleWritable module = (DataModuleWritable)ois.readObject();
  
  ois.close();
  
  return module;
 }

 @Override
 public void write(DataModule s, File f) throws IOException
 {
  FileOutputStream fileOut = new FileOutputStream(f);
  
  ObjectOutputStream oos = new ObjectOutputStream( fileOut );
  
  oos.writeObject(s);
  
  oos.close();
 }

 @Override
 public String getExtension()
 {
  return ".ser";
 }

}
