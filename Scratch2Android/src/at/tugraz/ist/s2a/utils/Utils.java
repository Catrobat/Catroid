package at.tugraz.ist.s2a.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
	
	public static boolean copyFile(String from, String to){
		File fileFrom = new File(from);
		File fileTo = new File(to);
		
		try {
			fileTo.createNewFile();
		} catch (IOException e1) {
			return false;
		}
		
		if(!fileFrom.exists() || !fileTo.exists())
			return false;
		
		FileInputStream fis;
		FileOutputStream fos;
		try {
			fis  = new FileInputStream(fileFrom);
			fos = new FileOutputStream(fileTo);
	    
	        byte[] buf = new byte[1024];
	        int i = 0;
	        while ((i = fis.read(buf)) != -1) {
	            fos.write(buf, 0, i);
	        }
	        
	        if (fis != null) fis.close();
	        if (fos != null) fos.close();
	    } 
	    catch (Exception e) {
	    	return false;
	    }
	    
	    return true;
	}
	
	public static boolean deleteFile(String path){
		File fileFrom = new File(path);
		return fileFrom.delete();
	}

}
