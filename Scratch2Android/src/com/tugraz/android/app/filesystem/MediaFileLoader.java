package com.tugraz.android.app.filesystem;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * this class provides reading media files like pictures from sd card
 * @author niko
 *
 */
public class MediaFileLoader {

	private ArrayList<HashMap<String, String>> mPictureContent;
	private FileSystem mFileSystem;
	private Context mCtx;
	
	private void getPictureContent(String path){
		//THIS IS A HINT
		//Cursor cc = mCtx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,null);  
//		startManagingCursor(cc);  
//		
//		 ImageView im = new ImageView(mContext);   
//		 im.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, ""+id))
	}
	
	public final static String PICTURE_NAME = "pic_name";
	public final static String PICTURE_PATH = "pic_path";
	
	public MediaFileLoader(Context ctx){
		mCtx = ctx;
	}
	/**
	 * load pictures from sd card
	 */
	public void loadPictureContent(){
		
		getPictureContent("ADD PATH HERE");
	}
	
	/**
	 * load pictures from sd card from specific folder
	 */
	public void loadPictureContent(String folderPath){
		getPictureContent(folderPath);
	}
	
	public ArrayList<HashMap<String, String>> getPictureContent(){
		return mPictureContent;
	}
}
