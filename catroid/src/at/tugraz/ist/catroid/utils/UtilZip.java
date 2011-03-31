/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.utils;

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
	private static final int QUICKEST_COMPRESSION = 0;
	
	private static ZipOutputStream mZipOutputStream;
	
	public static boolean writeToZipFile(String[] file_pathes, String zipfile) {
	    	    
	    try {
		    FileOutputStream fileOutputStream = new FileOutputStream(zipfile);
		    mZipOutputStream = new ZipOutputStream(fileOutputStream);
		    mZipOutputStream.setLevel(QUICKEST_COMPRESSION);
		    for (int i = 0; i < file_pathes.length; i++) {
		      File file = new File(file_pathes[i]);
		      if(file.isDirectory())
		    	  writeDirToZip(file, file.getName()+"/");
		      else
		    	  writeFileToZip(file, "");
		      
		    }
		    mZipOutputStream.close();
		    return true;
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	    
	  return false;
	}
	
	private static void writeDirToZip(File dir, String zipEntryPath) 
			throws IOException {
		String[] dirList = dir.list(); 
        
        for(int i=0; i<dirList.length; i++) { 
            File f = new File(dir, dirList[i]); 
	        if(f.isDirectory()) { 
	            writeDirToZip(f, zipEntryPath+f.getName()+"/"); 
	            continue; 
	        }
	        writeFileToZip(f, zipEntryPath);
    
        } 
	}
	
	private static void writeFileToZip(File file, String zipEntryPath) throws IOException {
		byte[] readBuffer = new byte[BUFFER]; 
        int bytesIn = 0; 
        
		FileInputStream fis = new FileInputStream(file); 
		ZipEntry anEntry = new ZipEntry(zipEntryPath+file.getName()); 
		mZipOutputStream.putNextEntry(anEntry); 

        while((bytesIn = fis.read(readBuffer)) != -1) 
        { 
        	mZipOutputStream.write(readBuffer, 0, bytesIn); 
        } 
        mZipOutputStream.closeEntry();
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
	}
	
}