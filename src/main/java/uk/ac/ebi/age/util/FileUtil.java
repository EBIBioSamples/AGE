package uk.ac.ebi.age.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.win32.StdCallLibrary;

public class FileUtil
{
 private interface Win32API extends StdCallLibrary
 {
  abstract boolean CreateHardLinkA(String fromFile, String toFile, String rsrv );
 }
 
 private static class Win32
 {
  private static Win32API api;
  
  public static Win32API getAPI()
  {
   if( api == null )
    api = (Win32API) Native.loadLibrary("kernel32", Win32API.class);
   
   return api;
  }
 }
 
 public interface PosixAPI extends Library
 {
  int link(String fromFile, String toFile);
 }

 private static class Posix
 {
  private static PosixAPI api;
  
  public static PosixAPI getAPI()
  {
   if( api == null )
    api = (PosixAPI) Native.loadLibrary( "c", PosixAPI.class);
   
   return api;
  }
 }

 
 public static boolean linkOrCopyFile( File exstFile, File newFile ) throws IOException
 {
  boolean res = false;

  try
  {
   if(Platform.isWindows())
   {
    res = Win32.getAPI().CreateHardLinkA(exstFile.getAbsolutePath(), newFile.getAbsolutePath(), null);
   }
   else
   {
    res = ( Posix.getAPI().link(newFile.getAbsolutePath(), exstFile.getAbsolutePath())  == 0 );
   }
  }
  catch(Exception e)
  {
  }

  if( ! res )
   FileUtils.copyFile(exstFile, newFile);
  
  return res;
 }
}
