package com.tugraz.android.app.test.contentManager;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.tugraz.android.app.BrickDefine;
import com.tugraz.android.app.ContentManager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;

public class ContentManagerTest extends AndroidTestCase {
	
	private ContentManager mContentManager;
	
	private ArrayList<HashMap<String, String>> mContentArrayList;
	private TreeMap<String, ArrayList<HashMap<String, String>>> mSpritesAndBackgroundList;
	
	private Context mCtx;
	private String FILENAME = "cmanagerfile.txt";
	
	
	@Override
	protected void setUp() throws Exception {
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		mSpritesAndBackgroundList = new TreeMap<String, ArrayList<HashMap<String, String>>>();
		mContentManager = new ContentManager();
		try {
			mCtx = getContext().createPackageContext("com.tugraz.android.app", Context.CONTEXT_IGNORE_SECURITY);
			mContentManager.setContext(mCtx);
		} catch (NameNotFoundException e) {
			assertFalse(true);
		}
		
		
		super.setUp();
	}



	public void testRemove(){
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "blabla2");
        mContentArrayList.add(map);
        
        //3 elements added
        mContentManager.setContentArrayList(mContentArrayList);
        mContentManager.remove(1);
        //last element should be on position 1
        assertEquals(mContentArrayList.get(1).get(BrickDefine.BRICK_ID), "3");
	}
	
	public void testRemoveSprite(){
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mContentArrayList.add(map);
        mSpritesAndBackgroundList.put("FirstSprite", mContentArrayList);
		
        mContentArrayList = new ArrayList<HashMap<String, String>>();
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Wait");
        map.put(BrickDefine.BRICK_VALUE, "Sound");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "4");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Wait");
        map.put(BrickDefine.BRICK_VALUE, "1");
        mContentArrayList.add(map);
        mSpritesAndBackgroundList.put("SecondSprite", mContentArrayList);
		
        mContentManager.setSpritesAndBackgroundList(mSpritesAndBackgroundList);
        mContentManager.removeSprite("FirstSprite");
        
        assertEquals(mSpritesAndBackgroundList.get("SecondSprite").get(1).get(BrickDefine.BRICK_ID), "4");
        
	}
	
	public void testClearSprites()
	{
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mContentArrayList.add(map);
        mSpritesAndBackgroundList.put("SomeName", mContentArrayList);
		
        mContentManager.clearSprites();
        assertEquals(mContentManager.getContentArrayList().size(), 0);
        assertEquals(mSpritesAndBackgroundList.size(), 1);
	}
	
	public void testClear(){
		
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "blabla2");
        mContentArrayList.add(map);
        mContentManager.setContentArrayList(mContentArrayList);
        //3 elements added
        mContentManager.clear();
        assertEquals(mContentArrayList.size(), 0);
	}
	
	public void testAddBrick(){
		mContentManager.setContentArrayList(mContentArrayList);
		
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");

        mContentManager.add(map);
        mContentArrayList = mContentManager.getContentArrayList();
        assertEquals(mContentArrayList.size(), 1);
        assertEquals(mContentArrayList.get(0), map);
        
	}
	
	public void testAddSprite(){
		
		mContentManager.setContentArrayList(mContentArrayList);
		mContentManager.setSpritesAndBackgroundList(mSpritesAndBackgroundList);
		
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        
        mContentManager.addSprite("FirstSprite", mContentArrayList);
        
        mSpritesAndBackgroundList = mContentManager.getSpritesAndBackground();
        
        assertEquals(mSpritesAndBackgroundList.size(), 2);
        assertEquals(mSpritesAndBackgroundList.get("FirstSprite"), mContentArrayList);
	}
	
	private String TESTXML =
		"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"+
		"<project>"+
		"<stage name=\"Stage\" />"+
		  "<command id=\"1001\">"+
		    "<image path=\"bla.jpg\" />"+
		  "</command>"+
		  "<command id=\"1002\">"+
		    "5"+
		  "</command>"+
		  "<command id=\"2001\">"+
		    "<sound path=\"bla.mp3\" />"+
		  "</command>"+
		"</stage>"+
		"<sprite name=\"Stage\" /"+
		  "<command id=\"4003\">"+
		    "<image path=\"bla.jpg\" />"+
		  "</command>"+
		  "<command id=\"3001\">"+
		  	"<x>5</x>"+
		  	"<y>7</y>"+
		  "</command>"+
		  "<command id=\"4001\" />"+
		  "<command id=\"4002\" />"+
		  "<command id=\"4003\">"+
		    "<image path=\"bla.jpg\" />"+
		  "</command>"+
		"</sprite>"+
		"</project>";
	

	public void testLoadContent(){
		
		 try {     
             // ##### Write a file to the disk #####
             /* We have to use the openFileOutput()-method
              * the ActivityContext provides, to
              * protect your file from others and
              * This is done for security-reasons.
              * We chose MODE_WORLD_READABLE, because
              *  we have nothing to hide in our file */ 
             FileOutputStream fOut = mCtx.openFileOutput(FILENAME, Activity.MODE_WORLD_READABLE);
             OutputStreamWriter osw = new OutputStreamWriter(fOut); 

             // Write the string to the file
             osw.write(TESTXML);
             /* ensure that everything is
              * really written out and close */
             osw.flush();
             osw.close();
             
		 }catch(Exception ex){assertFalse(true);}
		 //load content that was saved before in file
		 mContentManager.loadContent(FILENAME);
		 //check if the content is the same as written to file
		 mContentArrayList = mContentManager.getContentArrayList();
		 assertEquals(mContentArrayList.get(0).get(BrickDefine.BRICK_VALUE), "bla.jpg");
		 assertEquals(mContentArrayList.get(1).get(BrickDefine.BRICK_VALUE), "5");
		 assertEquals(mContentArrayList.get(2).get(BrickDefine.BRICK_TYPE), "2001");
	}
	
	public void testSaveContentLoadContent(){

		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "blabla2");
        mContentArrayList.add(map);
        mContentManager.setContentArrayList(mContentArrayList);
        mSpritesAndBackgroundList.put("FirstSprite", mContentArrayList);
        
        mContentManager.setSpritesAndBackgroundList(mSpritesAndBackgroundList);
        
        mContentManager.saveContent();
        mContentManager.clear();
        mContentManager.loadContent();
        assertEquals(mContentArrayList, mContentManager.getContentArrayList());
	}
	
	public void testSwitchSprite(){
		
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mContentArrayList.add(map);
        mSpritesAndBackgroundList.put("FirstSprite", mContentArrayList);
        mContentManager.setContentArrayList(mContentArrayList);
        
        mContentArrayList = new ArrayList<HashMap<String,String>>();
		map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "haha");
        map.put(BrickDefine.BRICK_VALUE, "hihi");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "4");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "hoho");
        map.put(BrickDefine.BRICK_VALUE, "huhu");
        mContentArrayList.add(map);
        mSpritesAndBackgroundList.put("SecondSprite", mContentArrayList);
        
        mContentManager.setSpritesAndBackgroundList(mSpritesAndBackgroundList);
		
	    mContentManager.switchSprite("SecondSprite");
	    
	    assertEquals(mContentManager.getContentArrayList().get(1).get(BrickDefine.BRICK_ID), "4");
	    assertEquals(mContentManager.getCurrentSprite(), "SecondSprite");
	
	}
	
	
}

