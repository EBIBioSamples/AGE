package uk.ac.ebi.age.storage.impl;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;

public class XMLDataModuleReaderWriter implements DataModuleReaderWriter
{

 @Override
 public DataModuleWritable read(File f) throws IOException, ClassNotFoundException
 {
  FileInputStream os = new FileInputStream(f);
  XMLDecoder encoder = new XMLDecoder(os);
  DataModuleWritable sw = (DataModuleWritable) encoder.readObject();
  encoder.close();
 
  return sw;
 }

 @Override
 public void write(DataModule s, File f) throws IOException
 {
  FileOutputStream os = new FileOutputStream(f);
  XMLEncoder encoder = new XMLEncoder(os);
  encoder.writeObject(s);
  encoder.close();
 }

 @Override
 public String getExtension()
 {
  return ".xml";
 }

}
