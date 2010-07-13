package com.tugraz.android.app.test.gui;

import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.BrickDefine;
import com.tugraz.android.app.MainActivity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

	private MainActivity mActivity;
	
	public MainActivityTest() {
		super("com.tugraz.android.app", MainActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false); //you have to turn this of if any of the test methods send key events to the application
		mActivity = getActivity();
		
		//setting up test data
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        list.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        list.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "blabla2");
        list.add(map);
        
        mActivity.mList = list;
        
        //TODO hier mehr machen um testdaten korrekt zu laden?
		
	}
	
	public void testPreConditions() {
		// we don't have preconditions atm
	}


}
