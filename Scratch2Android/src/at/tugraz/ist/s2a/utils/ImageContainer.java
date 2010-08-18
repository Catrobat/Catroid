package at.tugraz.ist.s2a.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.tugraz.ist.s2a.utils.filesystem.FileSystem;
/**
 * 
 * @author AlexanderKalchauer
 *	This class contains Images in different sizes for the Images in the root ordner to use in scratch
 */


public class ImageContainer {

	private static final int MAX_WIDTH = (460*3);
	private static final int MAX_HEIGHT = (800*3);
	static String ROOTPATH;
	private HashMap<String, Bitmap> mImageMap;
	private ImageEditing mEditor;
	private FileSystem mFilesystem;
	
	public ImageContainer(String rootpath) {
		mImageMap = new HashMap<String, Bitmap>();
		mEditor = new ImageEditing();
		mFilesystem = new FileSystem();
		ROOTPATH = rootpath;
		init();
	}
	
	public void init(){
		File rootFile = new File(ROOTPATH);
		String[] rootFileList = rootFile.list();
		for(int i=0; i<rootFileList.length; i++)
    	{
    		if(rootFileList[i].contains(".png")){
    			
    			mImageMap.put(rootFileList[i], BitmapFactory.decodeFile(getFullImagePath(rootFileList[i])));
    		}
    			
    	}
	}
	
	public void saveImage(String path){
		if(mImageMap.containsKey(path)){
			//do nothing image already exists
		}
		else{
			Bitmap bm = null;		
		    bm = BitmapFactory.decodeFile(getFullImagePath(path));
		    Bitmap newbm = mEditor.scaleBitmap(bm, MAX_HEIGHT, MAX_WIDTH);
			mImageMap.put(path, newbm);
			saveBitmapOnSDCardAsPNG(path, newbm);
		}	
	}
	
	public String saveBitmap(String path, Bitmap bitmap){
			saveBitmapOnSDCardAsPNG(path, bitmap);
			mImageMap.put(path, bitmap);
			return path;
	}

	public Bitmap getImage(String name){
		if(!mImageMap.containsKey(name))
			return null;	
		return mImageMap.get(name);
	}
	
	private String getFullImagePath(String path){
		return (ROOTPATH+path);
	}

	public void deleteImage(String name){
		mFilesystem.deleteFile(getFullImagePath(name), null);
		mImageMap.remove(name);
	}
	public void deleteAll(){
		mImageMap.clear();
	}
	public void saveBitmapOnSDCardAsPNG(String path, Bitmap bitmap){
		 File file = new File(getFullImagePath(path));
		 try {
			FileOutputStream os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
