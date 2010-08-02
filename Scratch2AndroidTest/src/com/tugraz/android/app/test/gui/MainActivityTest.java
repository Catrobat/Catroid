package com.tugraz.android.app.test.gui;

import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.BrickDefine;
import com.tugraz.android.app.MainActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

	private MainActivity mActivity;
	
	private ListView mListView;
	private ListView mMenu;
	
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
        
        //mActivity.mList = list;
        
		
        mListView = (ListView) mActivity.findViewById(com.tugraz.android.app.R.id.MainListView);
        mMenu = (ListView) mActivity.findViewById(com.tugraz.android.app.R.menu.constructionsitemenu);
        
	}
	
//	public void testPreConditions() {
//		// we don't have preconditions atm
//	}
	
	/**
	 * NO AUTOMATED TEST
	 */
	public void testContextMenuOnMainList() { //TODO der test funktioniert nicht, wenn noch kein brick gesetzt wurde! ausbessern!
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						mListView.requestFocus();	
						
					}
				}
		);
		View view = (View) mListView.getChildAt(0);	
		TouchUtils.longClickView(this, view);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
	}
	
	/**
	 * NO AUTOMATED TEST
	 */
	public void testMenuShown(){

		this.sendKeys(KeyEvent.KEYCODE_MENU);
		
		this.sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
	}
	

}
