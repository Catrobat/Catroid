package at.tugraz.ist.s2a.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import at.tugraz.ist.s2a.ConstructionSiteActivity;
import at.tugraz.ist.s2a.utils.filesystem.FileSystem;
/**
 * 
 * @author AlexanderKalchauer
 *	This class contains Images in different sizes for the Images in the root ordner to use in scratch
 */

public class ImageContainer {

	private static final int MAX_WIDTH = (460*3);
	private static final int MAX_HEIGHT = (800*3);
	private static final int THUMBNAIL_WIDTH = 60;
	private static final int THUMBNAIL_HEIGHT = 60;
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
		if(!(rootFileList==null)){
			for(int i=0; i<rootFileList.length; i++)
			{
				Bitmap bm = BitmapFactory.decodeFile(getFullImagePath(rootFileList[i]));
				if(bm != null)
					mImageMap.put(rootFileList[i], bm);
				
			}
		}
	}
	
	public String saveImage(String path){
		File imagePath = new File(path);
		String folderPath = imagePath.getParent();
		String image = Calendar.getInstance().getTimeInMillis() + imagePath.getAbsolutePath().replace(folderPath, "").replace("/", "");
	
		Bitmap bm = null;		
		bm = BitmapFactory.decodeFile((path));
		Bitmap newbm = null;
		if(MAX_HEIGHT < bm.getHeight() && MAX_WIDTH < bm.getWidth())
		   newbm = mEditor.scaleBitmap(bm, MAX_HEIGHT, MAX_WIDTH);
		if(MAX_HEIGHT >= bm.getHeight() && MAX_WIDTH < bm.getWidth())
		   newbm = mEditor.scaleBitmap(bm, bm.getHeight(), MAX_WIDTH);
		if(MAX_HEIGHT < bm.getHeight() && MAX_WIDTH >= bm.getWidth())
		   newbm = mEditor.scaleBitmap(bm, MAX_HEIGHT, bm.getWidth());
		if(MAX_HEIGHT >= bm.getHeight() && MAX_WIDTH >= bm.getWidth())
		   newbm = bm;
		
		Utils.saveBitmapOnSDCardAsPNG(Utils.concatPaths(ConstructionSiteActivity.ROOT_IMAGES, image), newbm);
		
		if(bm != null)
			bm.recycle();
		if(newbm != null)
			newbm.recycle();
		return image;
	}
	
	public String saveThumbnail(String path){
		File imagePath = new File(path);
		String folderPath = imagePath.getParent();
		String image = Calendar.getInstance().getTimeInMillis() + imagePath.getAbsolutePath().replace(folderPath, "").replace("/", "thumb");
		Bitmap bm = null;
		bm = BitmapFactory.decodeFile((path));
		Bitmap newbm = null;
		newbm = mEditor.scaleBitmap(bm, THUMBNAIL_HEIGHT, THUMBNAIL_WIDTH);
		mImageMap.put(image, newbm);
		Utils.saveBitmapOnSDCardAsPNG(Utils.concatPaths(ConstructionSiteActivity.ROOT_IMAGES, image), newbm);
		
		if(bm != null)
			bm.recycle();
		
		return image;
	}
	

	public Bitmap getImage(String name){
		Log.d("TEST", name + " " +mImageMap.containsKey(name));
		if(!mImageMap.containsKey(name))
			return null;	
		
		return mImageMap.get(name);
	}
	
	public Bitmap getThumbnail(String name){
		return getImage(name);
	}
	
	private String getFullImagePath(String path){
		return (Utils.concatPaths(ROOTPATH, path));
	}

	public void deleteImage(String name){
		mFilesystem.deleteFile(getFullImagePath(name), null);
		mImageMap.remove(name);
	}
	
	public void deleteAll(){
		mImageMap.clear();
	}
	


}
