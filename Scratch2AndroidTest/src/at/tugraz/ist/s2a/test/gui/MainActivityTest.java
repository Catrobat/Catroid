package at.tugraz.ist.s2a.test.gui;

import java.util.ArrayList;
import java.util.HashMap;


import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import at.tugraz.ist.s2a.ConstructionSiteActivity;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;

public class MainActivityTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{

	private ConstructionSiteActivity mActivity;
	
	private ListView mListView;
	private ListView mMenu;
	
	public MainActivityTest() {
		super("at.tugraz.ist.s2a", ConstructionSiteActivity.class);
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

		
        mListView = (ListView) mActivity.findViewById(at.tugraz.ist.s2a.R.id.MainListView);
        mMenu = (ListView) mActivity.findViewById(at.tugraz.ist.s2a.R.menu.construction_site_menu);
        
	}
	
	
	/**
	 * NO AUTOMATED TEST
	 */
	public void testContextMenuOnMainList() { //TODO der test funktioniert nicht, wenn noch kein brick gesetzt wurde! ausbessern!
		
		//TODO redesign test case
		assertTrue(false);

		
//		mActivity.runOnUiThread(
//				new Runnable() {
//					public void run() {
//						mListView.requestFocus();	
//						
//					}
//				}
//		);
//		View view = (View) mListView.getChildAt(0);	
//		TouchUtils.longClickView(this, view);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	
	}
	
	/**
	 * NO AUTOMATED TEST
	 */
	public void testMenuShown(){

		//TODO redesign test case
		assertTrue(false);
		
//		this.sendKeys(KeyEvent.KEYCODE_MENU);
//		
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
	}
	

}
