package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * provides content
 * @author alex, niko
 *
 */
public class ContentManager extends Observable{
	
	private ArrayList<HashMap<String, String>> mContentArrayList;
	
	public ArrayList<HashMap<String, String>> getContentArrayList(){
		return mContentArrayList;
	}
	
	public void remove(int position){
		
	}
	
	public void clear(){
		
	}
	
	public void add(HashMap<String, String> map){
		
	}
	
	public ContentManager(){
		mContentArrayList = new ArrayList<HashMap<String, String>>();
	}
	
	/**
	 * load content into data structure
	 */
	public void loadContent(){
        
        //Bsp.: List; Testdaten
               
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
	}
	
	/**
	 * test method
	 * @param filename
	 */
	public void loadContent(String filename){
		
	}
	
	
	public void setArrayList(){
		
	}
	
	/**
	 * save content
	 */
	public void saveContent(){
		
	}


}
