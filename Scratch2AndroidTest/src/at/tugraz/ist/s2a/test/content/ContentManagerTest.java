package at.tugraz.ist.s2a.test.content;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;
import at.tugraz.ist.s2a.utils.filesystem.FileSystem;

public class ContentManagerTest extends AndroidTestCase {
	
	private ContentManager mContentManager;
	
	private ArrayList<HashMap<String, String>> mContentArrayList;
	private TreeMap<String, ArrayList<HashMap<String, String>>> mSpritesAndBackgroundList;
	private ArrayList<ArrayList<HashMap<String, String>>> mAllContentArrayList;
	private ArrayList<String> mAllContentNameList;
	
	
	private Context mCtx;
	private String FILENAME = "cmanagerfile.spf";
	
	
	@Override
	protected void setUp() throws Exception {
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		mSpritesAndBackgroundList = new TreeMap<String, ArrayList<HashMap<String, String>>>();
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



	public void testRemove(){
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
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "10");
        mContentArrayList.add(map);
        
        //3 elements added
        mContentManager.setContentArrayList(mContentArrayList);
        mContentManager.removeBrick(1);
        //last element should be on position 1
        assertEquals(mContentArrayList.get(1).get(BrickDefine.BRICK_ID), "3");
	}
	
	
	public void testClearSprites()
	{
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
        mSpritesAndBackgroundList.put("SomeName", mContentArrayList);
		
        mContentManager.resetContent();
        assertEquals(mContentManager.getContentArrayList().size(), 0);
        assertEquals(mSpritesAndBackgroundList.size(), 1);
	}
	
	public void testResetContent(){
		
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
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "10");
        mContentArrayList.add(map);
        mContentManager.setContentArrayList(mContentArrayList);
        //3 elements added
        mContentManager.resetContent();
        assertEquals(mContentArrayList.size(), 0);
	}
	
	public void testAddBrick(){
		mContentManager.setContentArrayList(mContentArrayList);
		
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");

        mContentManager.addBrick(map);
        mContentArrayList = mContentManager.getContentArrayList();
        assertEquals(mContentArrayList.size(), 1);
        assertEquals(mContentArrayList.get(0), map);
        
	}
	
	public void testAddSprite(){
			
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
        
        mContentManager.addSprite("FirstSprite", mContentArrayList);
        
        mAllContentNameList = mContentManager.getSpritelist();
        mAllContentArrayList = mContentManager.getAllContentList();
        
        assertEquals(mAllContentNameList.size(), 2);
        assertEquals(mAllContentArrayList.get(1), mContentArrayList);
	}
	
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
             FileOutputStream fOut = filesystem.createOrOpenFileOutput("/sdcard/"+FILENAME,mCtx);
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
		 mContentManager.loadContent("/sdcard/"+FILENAME);
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
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "10");
        mContentArrayList.add(map);
        mContentManager.setContentArrayList(mContentArrayList);
        mSpritesAndBackgroundList.put("FirstSprite", mContentArrayList);
        
        mContentManager.setSpritesAndBackgroundList(mSpritesAndBackgroundList);
        
        mContentManager.saveContent();
        mContentManager.resetContent();
        mContentManager.loadContent();
        assertEquals(mContentArrayList, mContentManager.getContentArrayList());
	}
	
	public void testSwitchSprite(){
		
		mContentArrayList = new ArrayList<HashMap<String,String>>();
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
        mSpritesAndBackgroundList.put("FirstSprite", mContentArrayList);
        mContentManager.setContentArrayList(mContentArrayList);
        
        mContentArrayList = new ArrayList<HashMap<String,String>>();
		map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "haha");
        map.put(BrickDefine.BRICK_VALUE, "10");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "4");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "hoho");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
        mSpritesAndBackgroundList.put("SecondSprite", mContentArrayList);
        
        mContentManager.setSpritesAndBackgroundList(mSpritesAndBackgroundList);
		
	    mContentManager.switchSprite(1);
	    
	    assertEquals(mContentManager.getContentArrayList().get(1).get(BrickDefine.BRICK_ID), "4");
	    assertEquals(mContentManager.getCurrentSprite(), "SecondSprite");
	
	}
	
	public void testIdAddBrick()
	{
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentManager.addBrick(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentManager.addBrick(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "10");
        mContentManager.addBrick(map);
        
        mContentManager.addSprite("Sprite", new ArrayList<HashMap<String, String>>());
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentManager.addBrick(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentManager.addBrick(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "10");
        mContentManager.addBrick(map);
        
        assertEquals(mContentManager.getIdCounter(), 6);
	    assertEquals(mContentManager.getContentArrayList().get(2).get(BrickDefine.BRICK_ID), "5");
	}

	public void testIdLoad()
	{
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "0");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "13");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "10");
        mContentArrayList.add(map);
        mContentManager.setContentArrayList(mContentArrayList);
        mSpritesAndBackgroundList.put("FirstSprite", mContentArrayList);
        
        mContentManager.setSpritesAndBackgroundList(mSpritesAndBackgroundList);
        
        mContentManager.saveContent();
        mContentManager.resetContent();
        mContentManager.loadContent();
        
        assertEquals(mContentManager.getIdCounter(), 13);
	}
	
}

