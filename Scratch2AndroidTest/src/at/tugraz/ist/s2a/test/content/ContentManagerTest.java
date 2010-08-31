package at.tugraz.ist.s2a.test.content;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;
import at.tugraz.ist.s2a.utils.Utils;
import at.tugraz.ist.s2a.utils.filesystem.FileSystem;

public class ContentManagerTest extends AndroidTestCase {
	
	private ContentManager mContentManager;
	
	private ArrayList<HashMap<String, String>> mContentArrayList;
	private ArrayList<ArrayList<HashMap<String, String>>> mAllContentArrayList;
	private ArrayList<String> mAllContentNameList;
	
	
	private Context mCtx;
	private String FILENAME = "cmanagerfile.spf";
	
	
	@Override
	protected void setUp() throws Exception {
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		
		mAllContentArrayList = new ArrayList<ArrayList<HashMap<String, String>>>();
		mAllContentNameList = new ArrayList<String>();
		
		try {
			mCtx = getContext().createPackageContext("at.tugraz.ist.s2a", Context.CONTEXT_IGNORE_SECURITY);
			mContentManager = new ContentManager(mCtx);	
		} catch (NameNotFoundException e) {
			assertFalse(true);
		}
		
		
		super.setUp();
	}
	
	

	@Override
	protected void tearDown() throws Exception {
		File testFile = new File(Utils.concatPaths("/sdcard/",FILENAME));
		if(testFile.exists())
			testFile.delete();
		super.tearDown();
	}

	private void addTestDataToContentArrayList(){
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
	}

	public void testClear()
	{
		mContentArrayList = mContentManager.getContentArrayList();
		addTestDataToContentArrayList();
        mContentManager.resetContent();

        assertTrue(mContentManager.getContentArrayList() == null);
	}

	public void testAddSprite(){
		
		mContentArrayList = new ArrayList<HashMap<String,String>>();
        
        mContentManager.addSprite("FirstSprite", mContentArrayList);     
        mAllContentNameList = mContentManager.getAllContentNameList();
        mAllContentArrayList = mContentManager.getAllContentList();
        
        assertEquals(mAllContentNameList.size(), 2);
        assertEquals(mAllContentArrayList.get(1), mContentArrayList);
	}
	
	public void testSwitchSprite(){
		
		mContentArrayList = new ArrayList<HashMap<String,String>>();    
        mContentManager.addSprite("FirstSprite", mContentArrayList);
        
        assertEquals(mContentArrayList, mContentManager.getContentArrayList());
	}
	
	public void testRemoveBrick(){
		mContentArrayList = mContentManager.getContentArrayList();
		addTestDataToContentArrayList();
		int size = mContentArrayList.size();	
        mContentManager.removeBrick(0);
        int new_size = mContentArrayList.size();

        assertEquals(size - 1 , new_size);
	}
	
	public void testAddBrick(){
		mContentArrayList = mContentManager.getContentArrayList();
		addTestDataToContentArrayList();
		int size = mContentArrayList.size();	
		
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        
        mContentManager.addBrick(map);
        int new_size = mContentArrayList.size();

        assertEquals(size + 1 , new_size);    
	}

	private HashMap<String, String> createSingleTestBrick(){
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        map.put(BrickDefine.BRICK_VALUE_1, "");
        
        return map;
	}
	
	public void testIdAddBrick()
	{
		
        mContentManager.addBrick(createSingleTestBrick());
        mContentManager.addBrick(createSingleTestBrick());
        mContentManager.addBrick(createSingleTestBrick());
        
        mContentManager.addSprite("Sprite", new ArrayList<HashMap<String, String>>());
        mContentManager.addBrick(createSingleTestBrick());
        mContentManager.addBrick(createSingleTestBrick());
        mContentManager.addBrick(createSingleTestBrick());
        
        assertEquals(mContentManager.getIdCounter(), 6);
	    assertEquals(mContentManager.getContentArrayList().get(2).get(BrickDefine.BRICK_ID), "5");
	}
	
	///////////////////////////////
	private String TESTXML =
		"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"+
		"<project>"+
		"<stage name=\"stage\">"+
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
		"<object name=\"sprite\">"+
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
		"</object>"+
		"</project>";
	

	public void testLoadContent(){
		
		FileSystem filesystem = new FileSystem();
		 try {     
             // ##### Write a file to the disk #####
             /* We have to use the openFileOutput()-method
              * the ActivityContext provides, to
              * protect your file from others and
              * This is done for security-reasons.
              * We chose MODE_WORLD_READABLE, because
              *  we have nothing to hide in our file */ 
             FileOutputStream fOut = filesystem.createOrOpenFileOutput(Utils.concatPaths("/sdcard/",FILENAME),mCtx);
             DataOutputStream ps = new DataOutputStream(fOut);
         
             // Write the string to the file
             ps.write(TESTXML.getBytes());
             /* ensure that everything is
              * really written out and close */
             ps.flush();
             ps.close();
             fOut.close();
             
		 }catch(Exception ex){assertFalse(true);}
		 //load content that was saved before in file
		 mContentManager.loadContent(Utils.concatPaths("/sdcard/",FILENAME));
		 //check if the content is the same as written to file
		 mContentArrayList = mContentManager.getContentArrayList();
		 assertEquals(mContentArrayList.get(0).get(BrickDefine.BRICK_TYPE), "1001");
		 assertEquals(mContentArrayList.get(1).get(BrickDefine.BRICK_TYPE), "1002");
		 assertEquals(mContentArrayList.get(2).get(BrickDefine.BRICK_TYPE), "2001");
	}
	
	public void testSaveContentLoadContent(){
		
		mContentManager.addBrick(createSingleTestBrick());
		File testFile = new File(Utils.concatPaths("/sdcard/",FILENAME));
		try {
			testFile.createNewFile();
		} catch (IOException e) {
		}
        mContentManager.saveContent(testFile.getAbsolutePath());
        mContentArrayList = mContentManager.getContentArrayList();
        mContentManager = new ContentManager(mCtx); 
        mContentManager.loadContent(Utils.concatPaths("/sdcard/",FILENAME));
        assertEquals(mContentArrayList, mContentManager.getContentArrayList());
	}
}

