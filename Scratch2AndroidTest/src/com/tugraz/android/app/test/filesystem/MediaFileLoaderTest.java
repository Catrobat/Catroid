package com.tugraz.android.app.test.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.filesystem.MediaFileLoader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.util.Log;

public class MediaFileLoaderTest extends AndroidTestCase{

	private MediaFileLoader mMediaFileLoader;
	private Context mCtx;
	
	protected void setUp() throws Exception {
		super.setUp();
		mCtx = getContext().createPackageContext("com.tugraz.android.app", Context.CONTEXT_IGNORE_SECURITY);
		mMediaFileLoader = new MediaFileLoader(mCtx);
	}
	
	
//	/**
//	 * test if files exist
//	 */
//	public void testLoadPictureContent(){
//		mMediaFileLoader.loadPictureContent();
//		ArrayList<HashMap<String, String>> content =  mMediaFileLoader.getPictureContent();
//		File file;
//		
//		Log.d("TEST", "number of image files: "+content.size());
//		assertNotNull(content);
//		
//		for(int i = 0; i < content.size(); i++){
//			file = new File(content.get(i).get(MediaFileLoader.PICTURE_PATH));
//			assertTrue(file.exists());
//			assertNotNull(BitmapFactory.decodeFile(content.get(i).get(MediaFileLoader.PICTURE_PATH)));
////			Log.d("TEST", content.get(i).get(MediaFileLoader.PICTURE_NAME));
////			Log.d("TEST", content.get(i).get(MediaFileLoader.PICTURE_PATH));
//		}
//		
//	}
	
	public void testLoadSoundContent(){
		mMediaFileLoader.loadSoundContent();
		ArrayList<HashMap<String, String>> content =  mMediaFileLoader.getSoundContent();
		File file;
		
		Log.d("TEST", "number of sound files: "+content.size());
		assertNotNull( content);
		
		for(int i = 0; i < content.size(); i++){
			file = new File(content.get(i).get(MediaFileLoader.SOUND_PATH));
			assertTrue(file.exists());
			
			Log.d("TEST", content.get(i).get(MediaFileLoader.SOUND_NAME));
			Log.d("TEST", content.get(i).get(MediaFileLoader.SOUND_PATH));
		}
	}
}
