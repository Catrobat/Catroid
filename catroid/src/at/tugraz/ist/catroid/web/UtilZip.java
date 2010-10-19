package at.tugraz.ist.catroid.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class UtilZip {

	public static boolean writeToZipFile(String[] file_pathes, String zipfile) {
	    // Default to maximum compression
	    int level = 9;
	    int start = 0;
	    System.out.println("length: "+ file_pathes.length);
	    try {
	    	
		    FileOutputStream fout = new FileOutputStream(zipfile);
		    ZipOutputStream zout = new ZipOutputStream(fout);
		    zout.setLevel(level);
		    for (int i = start; i < file_pathes.length; i++) {
		      String filename = file_pathes[i].substring( file_pathes[i].lastIndexOf("/")+1);
		     
		      ZipEntry ze = new ZipEntry(filename);
		      FileInputStream fin = new FileInputStream(file_pathes[i]);
		      try {
		        //System.out.println("Compressing " + file_pathes[i]);
		        zout.putNextEntry(ze);
		        for (int c = fin.read(); c != -1; c = fin.read()) {
		          zout.write(c);
		        }
		      } finally {
		        fin.close();
		      }
		    }
		    zout.close();
		    return true;
	    } catch(IOException e) {
	    	e.printStackTrace();
	    } catch(NumberFormatException e) {
  		  e.printStackTrace();
	  }
	    return false;
	}
	
	public static boolean unZipFile(String zipfile, String outdir) {
		final int BUFFER = 2048;

		try {
			FileInputStream fin = new FileInputStream(zipfile);
		    ZipInputStream zin = new ZipInputStream(fin);
		    ZipEntry ze = null;
		    
		    //System.out.println("loooos: "+zin.available());
		    
		    BufferedOutputStream dest = null;
		    byte data[] = new byte[BUFFER];
		    while ((ze = zin.getNextEntry()) != null) {
		      //System.out.println("Unzipping " + ze.getName());
		      if(ze.isDirectory()) {
		    	  File f = new File(outdir+ze.getName());
		    	  f.mkdir();
		    	  zin.closeEntry();
		    	  continue;
		      }
		      FileOutputStream fout = new FileOutputStream(outdir+ze.getName());
		      int count;

          dest = new 
          BufferedOutputStream(fout, BUFFER);
          while ((count = zin.read(data, 0, BUFFER)) 
            != -1) {
             dest.write(data, 0, count);
          }
          dest.flush();
          dest.close();     
            
		    }
		    //System.out.println("fertig");
		    zin.close();
		    
		    return true;
		} catch(FileNotFoundException e) {
			System.out.println("readzip exception");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}