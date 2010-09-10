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
import android.util.Pair;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;
import at.tugraz.ist.s2a.test.utils.TestDefines;
import at.tugraz.ist.s2a.utils.Utils;
import at.tugraz.ist.s2a.utils.filesystem.FileSystem;

public class ContentManagerTest extends AndroidTestCase {
	
	private ContentManager mContentManager;
	
	private ArrayList<HashMap<String, String>> mCurrentSpriteList;
	private ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> mAllContentArrayList;
	private ArrayList<String> mAllContentNameList;
	
	
	private Context mCtx;
	private String FILENAME = "cmanagerfile.spf";
	
	
	@Override
	protected void setUp() throws Exception {
		mCurrentSpriteList = new ArrayList<HashMap<String,String>>();
		
		mAllContentArrayList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();
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
        mCurrentSpriteList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "");
        mCurrentSpriteList.add(map);
	}

	public void testClear()
	{
		mCurrentSpriteList = mContentManager.getCurrentSpriteList();
		addTestDataToContentArrayList();
        mContentManager.resetContent();

        assertTrue(mContentManager.getCurrentSpriteList() == null);
	}

	public void testAddSprite(){
		
		mCurrentSpriteList = new ArrayList<HashMap<String,String>>();
        mContentManager.loadAllContentNameList(); //quick fix: before that stage was not in mAllContentArrayList, but in normal execution it works correctly
		
        mContentManager.addSprite(new Pair<String, ArrayList<HashMap<String,String>>>("FirstSprite", mCurrentSpriteList));     
        mAllContentNameList = mContentManager.getAllContentNameList();
        mAllContentArrayList = mContentManager.getAllContentList();
        
        assertEquals(mAllContentNameList.size(), 2);
        assertEquals(mAllContentArrayList.get(1).second, mCurrentSpriteList);
	}
	
	public void testSwitchSprite(){
		
		mCurrentSpriteList = new ArrayList<HashMap<String,String>>();    
        mContentManager.addSprite(new Pair<String, ArrayList<HashMap<String,String>>>("FirstSprite", mCurrentSpriteList));
        
        assertEquals(mCurrentSpriteList, mContentManager.getCurrentSpriteList());
	}
	
	public void testRemoveBrick(){
		mCurrentSpriteList = mContentManager.getCurrentSpriteList();
		addTestDataToContentArrayList();
		int size = mCurrentSpriteList.size();	
        mContentManager.removeBrick(0);
        int new_size = mCurrentSpriteList.size();

        assertEquals(size - 1 , new_size);
	}
	
	public void testAddBrick(){
		mCurrentSpriteList = mContentManager.getCurrentSpriteList();
		addTestDataToContentArrayList();
		int size = mCurrentSpriteList.size();	
		
		HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "");
        
        mContentManager.addBrick(map);
        int new_size = mCurrentSpriteList.size();

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
        
        mContentManager.addSprite(new Pair<String, ArrayList<HashMap<String,String>>>("Sprite", new ArrayList<HashMap<String, String>>()));
        mContentManager.addBrick(createSingleTestBrick());
        mContentManager.addBrick(createSingleTestBrick());
        mContentManager.addBrick(createSingleTestBrick());
        
        assertEquals(mContentManager.getIdCounter(), 6);
	    assertEquals(mContentManager.getCurrentSpriteList().get(2).get(BrickDefine.BRICK_ID), "5");
	}
	
	///////////////////////////////

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
             ps.write(TestDefines.TEST_XML.getBytes());
             /* ensure that everything is
              * really written out and close */
             ps.flush();
             ps.close();
             fOut.close();
             
		 }catch(Exception ex){assertFalse(true);}
		 //load content that was saved before in file
		 mContentManager.loadContent(Utils.concatPaths("/sdcard/",FILENAME));
		 //check if the content is the same as written to file
		 mCurrentSpriteList = mContentManager.getCurrentSpriteList();
		 assertEquals(mCurrentSpriteList.get(0).get(BrickDefine.BRICK_TYPE), String.valueOf(BrickDefine.SET_BACKGROUND));
		 assertEquals(mCurrentSpriteList.get(1).get(BrickDefine.BRICK_TYPE), String.valueOf(BrickDefine.WAIT));
		 assertEquals(mCurrentSpriteList.get(2).get(BrickDefine.BRICK_TYPE), String.valueOf(BrickDefine.PLAY_SOUND));
	}
	
	public void testSaveContentLoadContent(){
		
		mContentManager.addBrick(createSingleTestBrick());
		File testFile = new File(Utils.concatPaths("/sdcard/",FILENAME));
		try {
			testFile.createNewFile();
		} catch (IOException e) {
		}
        mContentManager.saveContent(testFile.getAbsolutePath());
        mCurrentSpriteList = mContentManager.getCurrentSpriteList();
        mContentManager = new ContentManager(mCtx); 
        mContentManager.loadContent(Utils.concatPaths("/sdcard/",FILENAME));
        assertEquals(mCurrentSpriteList, mContentManager.getCurrentSpriteList());
	}
	
	public void testReferences(){
		ArrayList<String> nameList = mContentManager.getAllContentNameList();
		
		mContentManager.loadAllContentNameList();
		ArrayList<String> newNameList = mContentManager.getAllContentNameList();
		assertEquals(nameList, newNameList);
		
		mContentManager.resetContent();
		newNameList = mContentManager.getAllContentNameList();
		assertEquals(nameList, newNameList);
		
		Pair<String, ArrayList<HashMap<String, String>>> sprite = new Pair<String, ArrayList<HashMap<String, String>>>("sprite", new ArrayList<HashMap<String,String>>());
		mContentManager.addSprite(sprite);
		assertEquals(nameList, newNameList);
	}
}

