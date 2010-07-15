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
	private Context mCtx;
	
	
	public final static String PICTURE_NAME = "pic_name";
	public final static String PICTURE_PATH = "pic_path";
	
	public MediaFileLoader(Context ctx){
		mCtx = ctx;
	}
	/**
	 * load pictures from sd card
	 */
	public void loadPictureContent(){
		mPictureContent = new ArrayList<HashMap<String,String>>();
		
		String[] projection = {
				MediaStore.Images.Media.DATA,				
				MediaStore.Images.ImageColumns.TITLE};
		
		
		Cursor cursor = mCtx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,null);   
		
		
		int column_data_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		int column_title_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE);
		cursor.moveToFirst();
		
		HashMap<String,String> map;
		for(int i = 0; i < cursor.getCount(); i++){
			map = new HashMap<String, String>();
			map.put(PICTURE_NAME, cursor.getString(column_title_index));
			map.put(PICTURE_PATH, cursor.getString(column_data_index));
			
			mPictureContent.add(map);
		}
		
		cursor.close();
	}
	
	
	public ArrayList<HashMap<String, String>> getPictureContent(){
		return mPictureContent;
	}
}
