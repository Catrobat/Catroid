package com.tugraz.android.app.filesystem;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

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
	public final static String PICTURE_THUMB = "pic_thumb";
	public final static String PICTURE_ID = "pic_id";
	
	public MediaFileLoader(Context ctx){
		mCtx = ctx;
	}
	/**
	 * load pictures from sd card
	 */
	public void loadPictureContent(){
		mPictureContent = new ArrayList<HashMap<String,String>>();
		//get thumbnail data
		
		String[] projection = {
				MediaStore.Images.Thumbnails.DATA,
				MediaStore.Images.Thumbnails.IMAGE_ID};


		Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnails(mCtx.getContentResolver(),
				MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
				MediaStore.Images.Thumbnails.MINI_KIND,
				projection);   


		int column_thumb_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
		
		cursor.moveToFirst();

		HashMap<String,String> map;
		do{
			map = new HashMap<String, String>();
			//TODO insert id and check if same like picture
			map.put(PICTURE_THUMB, cursor.getString(column_thumb_index));
			mPictureContent.add(map);
		}while(cursor.moveToNext());
		
		
		cursor.close();
		
		//get picuter data
		String[] projectionOnOrig = {
					MediaStore.Images.Media.DATA,				
					MediaStore.Images.ImageColumns.TITLE,
					MediaStore.Images.Media._ID};

		
		cursor = MediaStore.Images.Media.query(mCtx.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionOnOrig, null, null,MediaStore.Images.Media._ID);   
		
		
		int column_data_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		int column_title_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE);
		
		cursor.moveToFirst();
		int count = 0;
		if(mPictureContent.size() != cursor.getCount())
			Log.e("MediaFileLoader", "Number of thumbnails not equal to number of pictures");
		else
		do{
			mPictureContent.get(count).put(PICTURE_NAME, cursor.getString(column_title_index));
			mPictureContent.get(count).put(PICTURE_PATH, cursor.getString(column_data_index));
			count++;
		}while(cursor.moveToNext());

		cursor.close();
		
		
	}
	
	
	public ArrayList<HashMap<String, String>> getPictureContent(){
		return mPictureContent;
	}
}
