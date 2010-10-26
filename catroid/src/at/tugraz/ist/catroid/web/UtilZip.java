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
	private static final int BUFFER = 2048;
	
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
		      File f = new File(file_pathes[i]);
		      if(f.isDirectory())
		    	  writeDirToZip(f, zout, f.getName()+"/");
		      else
		    	  writeFileToZip(f, zout, "");
		      
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
	
	private static void writeDirToZip(File zipDir, ZipOutputStream zout, String zipEntryPath) throws IOException {
		String[] dirList = zipDir.list(); 
        
        for(int i=0; i<dirList.length; i++) { 
            File f = new File(zipDir, dirList[i]); 
	        if(f.isDirectory()) { 
	            writeDirToZip(f, zout, zipEntryPath+f.getName()+"/"); 
	            continue; 
	        }
	        writeFileToZip(f, zout, zipEntryPath);
    
        } 
	}
	
	private static void writeFileToZip(File file, ZipOutputStream zout, String zipEntryPath) throws IOException {
		byte[] readBuffer = new byte[BUFFER]; 
        int bytesIn = 0; 
        
		FileInputStream fis = new FileInputStream(file); 
		ZipEntry anEntry = new ZipEntry(zipEntryPath+file.getName()); 
		zout.putNextEntry(anEntry); 

        while((bytesIn = fis.read(readBuffer)) != -1) 
        { 
            zout.write(readBuffer, 0, bytesIn); 
        } 
        zout.closeEntry();
        fis.close(); 
	}
	
	public static boolean unZipFile(String zipfile, String outdir) {
		try {
			FileInputStream fin = new FileInputStream(zipfile);
		    ZipInputStream zin = new ZipInputStream(fin);
		    ZipEntry ze = null;
		    
		    BufferedOutputStream dest = null;
		    byte data[] = new byte[BUFFER];
		    while ((ze = zin.getNextEntry()) != null) {
		      //System.out.println("unzip: "+ze.getName());
		      if(ze.isDirectory()) {
		    	  File f = new File(outdir+ze.getName());
		    	  f.mkdir();
		    	  zin.closeEntry();
		    	  continue;
		      }
		      File f = new File(outdir+ze.getName());
		      f.getParentFile().mkdirs();
		      FileOutputStream fout = new FileOutputStream(f);
		      
		      int count;
	          dest = new BufferedOutputStream(fout, BUFFER);
	          while ((count = zin.read(data, 0, BUFFER)) 
	            != -1) {
	             dest.write(data, 0, count);
	          }
	          dest.flush();
	          dest.close();     
	            
		    }
		    zin.close();
			    
			return true;
			} catch(FileNotFoundException e) {
				System.out.println("readzip exception");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
	}
	
}