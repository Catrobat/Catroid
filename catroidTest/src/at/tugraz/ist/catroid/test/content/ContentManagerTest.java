/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//package at.tugraz.ist.catroid.test.content;
//
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import android.content.Context;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.test.AndroidTestCase;
//import android.util.Pair;
//import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
//import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
//import at.tugraz.ist.catroid.test.utils.TestDefines;
//import at.tugraz.ist.catroid.utils.Utils;
//import at.tugraz.ist.catroid.utils.filesystem.FileSystem;
//
//public class ContentManagerTest extends AndroidTestCase {
//	
//	private ContentManager mContentManager;
//	
//	private ArrayList<HashMap<String, String>> mCurrentSpriteList;
//	private ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> mAllContentArrayList;
//	private ArrayList<String> mAllContentNameList;
//	
//	
//	private Context mContext;
//	private String FILENAME = "cmanagerfile.spf";
//	
//	
//	@Override
//	protected void setUp() throws Exception {
//		mCurrentSpriteList = new ArrayList<HashMap<String,String>>();
//		
//		mAllContentArrayList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();
//		mAllContentNameList = new ArrayList<String>();
//		
//		try {
//			mContext = getContext().createPackageContext("at.tugraz.ist.catroid", Context.CONTEXT_IGNORE_SECURITY);
//			mContentManager = new ContentManager(mContext);	
//		} catch (NameNotFoundException e) {
//			assertFalse(true);
//		}
//		
//		
//		super.setUp();
//	}
//	
//	
//
//	@Override
//	protected void tearDown() throws Exception {
//		File testFile = new File(Utils.concatPaths("/sdcard/",FILENAME));
//		if(testFile.exists())
//			testFile.delete();
//		super.tearDown();
//	}
//
//	private void addTestDataToContentArrayList(){
//		HashMap<String, String> map = new HashMap<String, String>();
//        map.put(BrickDefine.BRICK_ID, "1");
//        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
//        map.put(BrickDefine.BRICK_NAME, "Test1");
//        map.put(BrickDefine.BRICK_VALUE, "");
//        mCurrentSpriteList.add(map);
//        map = new HashMap<String, String>();
//        map.put(BrickDefine.BRICK_ID, "2");
//        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
//        map.put(BrickDefine.BRICK_NAME, "Test2");
//        map.put(BrickDefine.BRICK_VALUE, "");
//        mCurrentSpriteList.add(map);
//	}
//
//	public void testResetCurrentSpriteCommandList()
//	{
//		mCurrentSpriteList = mContentManager.getCurrentSpriteCommandList();
//		addTestDataToContentArrayList();
//        mContentManager.resetContent();
//
//        assertTrue(mContentManager.getCurrentSpriteCommandList() == null);
//	}
//
//	public void testAddSprite(){
//		
//		mCurrentSpriteList = new ArrayList<HashMap<String,String>>();
//        mContentManager.loadAllSpriteNameList(); //quick fix: before that stage was not in mAllContentArrayList, but in normal execution it works correctly
//		
//        mContentManager.addSprite(new Pair<String, ArrayList<HashMap<String,String>>>("FirstSprite", mCurrentSpriteList));     
//        mAllContentNameList = mContentManager.getAllSpriteNameList();
//        mAllContentArrayList = mContentManager.getAllSpriteCommandList();
//        
//        assertEquals(mAllContentNameList.size(), 2);
//        assertEquals(mAllContentArrayList.get(1).second, mCurrentSpriteList);
//	}
//	
//	public void testSwitchSprite(){
//		
//		mCurrentSpriteList = new ArrayList<HashMap<String,String>>();    
//        mContentManager.addSprite(new Pair<String, ArrayList<HashMap<String,String>>>("FirstSprite", mCurrentSpriteList));
//        
//        assertEquals(mCurrentSpriteList, mContentManager.getCurrentSpriteCommandList());
//	}
//	
//	public void testRemoveBrick(){
//		mCurrentSpriteList = mContentManager.getCurrentSpriteCommandList();
//		addTestDataToContentArrayList();
//		int size = mCurrentSpriteList.size();	
//        mContentManager.removeBrick(0);
//        int new_size = mCurrentSpriteList.size();
//
//        assertEquals(size - 1 , new_size);
//	}
//	
//	public void testAddBrick(){
//		mCurrentSpriteList = mContentManager.getCurrentSpriteCommandList();
//		addTestDataToContentArrayList();
//		int size = mCurrentSpriteList.size();	
//		
//		HashMap<String, String> map = new HashMap<String, String>();
//        map.put(BrickDefine.BRICK_ID, "1");
//        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
//        map.put(BrickDefine.BRICK_NAME, "Test1");
//        map.put(BrickDefine.BRICK_VALUE, "");
//        
//        mContentManager.addBrick(map);
//        int new_size = mCurrentSpriteList.size();
//
//        assertEquals(size + 1 , new_size);    
//	}
//
//	private HashMap<String, String> createSingleTestBrick(){
//		HashMap<String, String> map = new HashMap<String, String>();
//        map.put(BrickDefine.BRICK_ID, "1");
//        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
//        map.put(BrickDefine.BRICK_NAME, "Test1");
//        map.put(BrickDefine.BRICK_VALUE, "");
//        map.put(BrickDefine.BRICK_VALUE_1, "");
//        
//        return map;
//	}
//	
//	public void testIdAddBrick()
//	{
//		
//        mContentManager.addBrick(createSingleTestBrick());
//        mContentManager.addBrick(createSingleTestBrick());
//        mContentManager.addBrick(createSingleTestBrick());
//        
//        mContentManager.addSprite(new Pair<String, ArrayList<HashMap<String,String>>>("Sprite", new ArrayList<HashMap<String, String>>()));
//        mContentManager.addBrick(createSingleTestBrick());
//        mContentManager.addBrick(createSingleTestBrick());
//        mContentManager.addBrick(createSingleTestBrick());
//        
//        assertEquals(mContentManager.getBrickIdCounter(), 6);
//	    assertEquals(mContentManager.getCurrentSpriteCommandList().get(2).get(BrickDefine.BRICK_ID), "5");
//	}
//	
//	///////////////////////////////
//
//	public void testLoadContent(){
//		
//		FileSystem filesystem = new FileSystem();
//		 try {     
//             // ##### Write a file to the disk #####
//             /* We have to use the openFileOutput()-method
//              * the ActivityContext provides, to
//              * protect your file from others and
//              * This is done for security-reasons.
//              * We chose MODE_WORLD_READABLE, because
//              *  we have nothing to hide in our file */ 
//             FileOutputStream fOut = filesystem.createOrOpenFileOutput(Utils.concatPaths("/sdcard/",FILENAME),mContext);
//             DataOutputStream ps = new DataOutputStream(fOut);
//         
//             // Write the string to the file
//             ps.write(new TestDefines().getTestXml(getContext()).getBytes());
//             /* ensure that everything is
//              * really written out and close */
//             ps.flush();
//             ps.close();
//             fOut.close();
//             
//		 }catch(Exception ex){assertFalse(true);}
//		 //load content that was saved before in file
//		 mContentManager.loadContent(Utils.concatPaths("/sdcard/",FILENAME));
//		 //check if the content is the same as written to file
//		 mCurrentSpriteList = mContentManager.getCurrentSpriteCommandList();
//		 assertEquals(mCurrentSpriteList.get(0).get(BrickDefine.BRICK_TYPE), String.valueOf(BrickDefine.SET_BACKGROUND));
//		 assertEquals(mCurrentSpriteList.get(1).get(BrickDefine.BRICK_TYPE), String.valueOf(BrickDefine.WAIT));
//		 assertEquals(mCurrentSpriteList.get(2).get(BrickDefine.BRICK_TYPE), String.valueOf(BrickDefine.PLAY_SOUND));
//	}
//	
//	public void testSaveContentLoadContent(){
//		
//		mContentManager.addBrick(createSingleTestBrick());
//		File testFile = new File(Utils.concatPaths("/sdcard/",FILENAME));
//		try {
//			testFile.createNewFile();
//		} catch (IOException e) {
//		}
//        mContentManager.saveContent(testFile.getAbsolutePath());
//        mCurrentSpriteList = mContentManager.getCurrentSpriteCommandList();
//        mContentManager = new ContentManager(mContext); 
//        mContentManager.loadContent(Utils.concatPaths("/sdcard/",FILENAME));
//        assertEquals(mCurrentSpriteList, mContentManager.getCurrentSpriteCommandList());
//	}
//	
//	public void testReferences(){
//		ArrayList<String> nameList = mContentManager.getAllSpriteNameList();
//		
//		mContentManager.loadAllSpriteNameList();
//		ArrayList<String> newNameList = mContentManager.getAllSpriteNameList();
//		assertEquals(nameList, newNameList);
//		
//		mContentManager.resetContent();
//		newNameList = mContentManager.getAllSpriteNameList();
//		assertEquals(nameList, newNameList);
//		
//		Pair<String, ArrayList<HashMap<String, String>>> sprite = new Pair<String, ArrayList<HashMap<String, String>>>("sprite", new ArrayList<HashMap<String,String>>());
//		mContentManager.addSprite(sprite);
//		assertEquals(nameList, newNameList);
//	}
//}

