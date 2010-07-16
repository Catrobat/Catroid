package com.tugraz.android.app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;


import com.tugraz.android.app.filesystem.FileSystem;
import com.tugraz.android.app.parser.Parser;

/**
 * provides content
 * @author alex, niko
 *
 */
public class ContentManager extends Observable{
	
	public ArrayList<HashMap<String, String>> mContentArrayList;
	private FileSystem mFilesystem;
	private Parser mParser;
	private Context mCtx;
	private static final String tempFile = "tempFile.txt";
	
	public ArrayList<HashMap<String, String>> getContentArrayList(){
		return mContentArrayList;
	}
	
	public void remove(int position){
		mContentArrayList.remove(position);
		setChanged();
		notifyObservers();
	}
	
	public void clear(){
		mContentArrayList.clear();
        setChanged();
		notifyObservers();
		
	}
	
	public void add(HashMap<String, String> map){
		mContentArrayList.add(map);
		setChanged();
		notifyObservers();
	}
	
	public ContentManager(){
		mContentArrayList = new ArrayList<HashMap<String, String>>();
		mFilesystem = new FileSystem();
		mParser = new Parser();
	}
	
	/**
	 * load content into data structure
	 */
	public void loadContent(){
		//load
		FileInputStream scratch = mFilesystem.createOrOpenFileInput(tempFile, mCtx);
        
		//parse
		mContentArrayList.clear();
		mContentArrayList.addAll(mParser.parse(scratch));

        try {
			scratch.close();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
		
	  testSet();
	  
        setChanged();
        notifyObservers();
	}
	/**
	 * load content into data structure
	 */
	public void loadContent(String file){
		//load
		FileInputStream scratch = mFilesystem.createOrOpenFileInput(file, mCtx);
        
        //parse
		mContentArrayList.clear();
        mContentArrayList.addAll((mParser.parse(scratch)));
        
        try {
			scratch.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        setChanged();
        notifyObservers();
	}

	/**
	 * save content
	 */
	public void saveContent(){
		FileOutputStream fd = mFilesystem.createOrOpenFileOutput(tempFile, mCtx);
	    
		String xml = mParser.toXml(mContentArrayList);
		
		try {
			fd.write(xml.getBytes());
			fd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * save content
	 */
	public void saveContent(String file){
		FileOutputStream fd = mFilesystem.createOrOpenFileOutput(file, mCtx);
	    
		String xml = mParser.toXml(mContentArrayList);
		
		try {
			fd.write(xml.getBytes());
			fd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	/**
	 * test method
	 *
	 */
	public void testSet(){
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
	 */
	public void setArrayList(ArrayList<HashMap<String, String>> list){
		mContentArrayList = list;
		setChanged();
		notifyObservers();
	}
	
	public void setObserver(Observer observer)
	{
		addObserver(observer);
	}
	public void setContext(Context context)
	{
		 mCtx = context;
	}

}
