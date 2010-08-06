package com.tugraz.android.app.filesystem;

import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.MainActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

/**
 * this class provides reading media files like pictures from sd card
 * @author niko
 *
 */
public class MediaFileLoader {

	//private ArrayList<HashMap<String, String>> mPictureContent;
	private ArrayList<HashMap<String, String>> mSoundContent;
	private Context mCtx;
	
	public final static int GALLERY_INTENT_CODE = 1111;
	
	public final static String PICTURE_NAME = "pic_name";
	public final static String PICTURE_PATH = "pic_path";
	public final static String PICTURE_THUMB = "pic_thumb";
	public final static String PICTURE_ID = "pic_id";
	
	public final static String SOUND_NAME = "sound_name";
	public final static String SOUND_PATH = "sound_path";
	public final static String SOUND_ID = "sound_id";
	
	public final static String NO_DATA_FOUND = "noDataFound";
	
	public MediaFileLoader(Context ctx){
		mCtx = ctx;
	}
	
	public void openPictureGallery(int elementPosition, View pictureView){
		
		((MainActivity) mCtx).rememberLastSelectedElementAndView(elementPosition, pictureView);
		
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
			((Activity) mCtx).startActivityForResult(intent, GALLERY_INTENT_CODE);
		} catch ( ActivityNotFoundException e) {
			Log.e("MediaFileLoader", "No Gallery found");
		}
	}
	
	
	/**
	 * load pictures from sd card
	 */
	/*public void loadPictureContent(){
		mPictureContent = new ArrayList<HashMap<String,String>>();

		
		//get picuter data
		String[] projectionOnOrig = {
					MediaStore.Images.Media.DATA,				
					MediaStore.Images.ImageColumns.TITLE,
					MediaStore.Images.Media._ID};

		Cursor cursor = MediaStore.Images.Media.query(mCtx.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionOnOrig, null, null,MediaStore.Images.Media._ID);   
		
		
		int column_data_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		int column_title_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE);
		int column_id_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
		
		HashMap<String,String> map;
		
		if(cursor.moveToFirst()){
			do{
				map = new HashMap<String, String>();
				map.put(PICTURE_ID, cursor.getString(column_id_index));
				map.put(PICTURE_NAME, cursor.getString(column_title_index));
				map.put(PICTURE_PATH, cursor.getString(column_data_index));
				mPictureContent.add(map);
			}while(cursor.moveToNext());
		}

		//Log.d("TEST", mPictureContent.toString());
		cursor.close();

		//get thumbnail data
		
		String[] projection = {
				MediaStore.Images.Thumbnails.DATA};


		//probably ineffective on a high number of pictures
		for(int i = 0; i < mPictureContent.size(); i++){
			
			cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
					mCtx.getContentResolver(), 
					Integer.parseInt(mPictureContent.get(i).get(PICTURE_ID)), 
					MediaStore.Images.Thumbnails.MINI_KIND, 
					projection);
			
			if(cursor.moveToFirst())
				mPictureContent.get(i).put(PICTURE_THUMB, cursor.getString(0));
			
			cursor.close();
		}
		
	}*/
	
	
	/**
	 * scan the sd card for audio files and store the names
	 */
	public void loadSoundContent(){
		mSoundContent = new ArrayList<HashMap<String,String>>();

		
		//get picuter data
		String[] projectionOnOrig = {
					MediaStore.Audio.Media.DATA,				
					MediaStore.Audio.AudioColumns.TITLE,
					MediaStore.Audio.Media._ID};

		Cursor cursor = mCtx.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionOnOrig, null, null,MediaStore.Audio.Media._ID);   
		
		
		int column_data_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		int column_title_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
		int column_id_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
		
		HashMap<String,String> map;
		
		if(cursor.moveToFirst()){
			do{
				map = new HashMap<String, String>();
				map.put(SOUND_ID, cursor.getString(column_id_index));
				map.put(SOUND_NAME, cursor.getString(column_title_index));
				map.put(SOUND_PATH, cursor.getString(column_data_index));
				mSoundContent.add(map);
			}while(cursor.moveToNext());
		}

		//Log.d("TEST", mPictureContent.toString());
		cursor.close();
		
	}
	
	
	/**
	 * 
	 * @return an array list containing a HashMap with information about sounds
	 */
	public ArrayList<HashMap<String,String>> getSoundContent(){
		return mSoundContent;
	}
}
