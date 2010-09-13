package at.tugraz.ist.s2a.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.util.Log;
import at.tugraz.ist.s2a.ConstructionSiteActivity;

public class Utils {
	
	public static boolean copyFile(String from, String to){
		File fileFrom = new File(from);
		File fileTo = new File(to);
		
		if(fileTo.exists())
			deleteFile(fileTo.getAbsolutePath());
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
	
	public static boolean deleteFolder(String path){
		File fileFrom = new File(path);
		  if (fileFrom.isDirectory()) {
		    for (File c : fileFrom.listFiles()){
		    	if(c.isDirectory())
		    		deleteFolder(c.getAbsolutePath());
		    	else
		    		c.delete();
		    }     
		  }else
			  fileFrom.delete();
		  
		  return true;
		}
	
	public static boolean deleteFolder(String path, String ignoreFile){
		File fileFrom = new File(path);
		  if (fileFrom.isDirectory()) {
		    for (File c : fileFrom.listFiles()){
		    	if(c.isDirectory())
		    		deleteFolder(c.getAbsolutePath(), ignoreFile);
		    	else{
					  if(!c.getName().equals(ignoreFile))
						  c.delete();
		    	}
		    		
		    }     
		  }else{
			  if(!fileFrom.getName().equals(ignoreFile))
				  fileFrom.delete();
		  }
			  
		  
		  return true;
		}
	
	public static String concatPaths(String first, String second){
		if(first == null && second == null)
			return null;
		if(first == null)
			return second;
		if(second == null)
			return first;
		if(first.endsWith("/"))
			if(second.startsWith("/"))
				return first+second.replaceFirst("/", "");
			else
				return first+second;
		else
			if(second.startsWith("/"))
				return first+second;
			else
				return first+"/"+second;
	}
	
	public static String addDefaultFileEnding(String filename){
		if(!filename.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
			return filename + ConstructionSiteActivity.DEFAULT_FILE_ENDING;
		return filename;
	}
	
	public static void saveBitmapOnSDCardAsPNG(String full_path, Bitmap bitmap){
		 File file = new File(full_path);
		 try {
			FileOutputStream os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.close();
		 } catch (FileNotFoundException e) {
			 Log.e("UTILS", e.getMessage());
			e.printStackTrace();
		   } catch (IOException e) {
			   Log.e("UTILS", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String changeFileEndingToPng(String filename)
	{
		String newFileName;
		
		int beginOfFileEnding = filename.lastIndexOf(".");
		newFileName = filename.replace(filename.substring(beginOfFileEnding), "");
		
	    newFileName = newFileName + ".png";
		return newFileName;
	}

}

