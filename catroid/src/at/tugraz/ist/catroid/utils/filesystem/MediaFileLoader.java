package at.tugraz.ist.catroid.utils.filesystem;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ConstructionSiteActivity;

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
	
	public void openPictureGallery(int elementPosition, ImageView pictureView){
		
		((ConstructionSiteActivity) mCtx).rememberLastSelectedElementAndView(elementPosition, pictureView);
		
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
			((Activity) mCtx).startActivityForResult(intent, GALLERY_INTENT_CODE);
		} catch ( ActivityNotFoundException e) {
			Log.e("MediaFileLoader", "No Gallery found");
		}
	}
	
	
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
		
		
		
		
		if(cursor.moveToFirst()){
			int column_data_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			int column_title_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
			int column_id_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
			
			HashMap<String,String> map;
			
			do{
				map = new HashMap<String, String>();
				map.put(SOUND_ID, cursor.getString(column_id_index));
				map.put(SOUND_NAME, cursor.getString(column_title_index));
				map.put(SOUND_PATH, cursor.getString(column_data_index));
				mSoundContent.add(map);
			}while(cursor.moveToNext());
		}
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
