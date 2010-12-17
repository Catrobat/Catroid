package at.tugraz.ist.catroid.utils;

import java.io.File;

public class UtilFile {
	
	static public boolean clearDirectory(File path) {
    if( path.exists() ) {
      File[] filesInDirectory = path.listFiles();
      for(int i=0; i<filesInDirectory.length; i++) {
         if(filesInDirectory[i].isDirectory()) {
           deleteDirectory(filesInDirectory[i]);
         }
         else {
           filesInDirectory[i].delete();
         }
      }
    }
    return true;
  }
	
  static public boolean deleteDirectory(File path) {
    clearDirectory(path);
    return( path.delete() );
  }
}
