package at.tugraz.ist.s2a.test.gui;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Dialog;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.tugraz.ist.s2a.ConstructionSiteActivity;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;

public class ConstructionSiteActivityTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{

	private ConstructionSiteActivity mActivity;
	
	private ListView mListView;

	private Button mShowToolBoxButton;
	private Dialog mToolBoxDialog;
	
	private Button mShowObjectDialogButton;
	private Dialog mObjectDialog;
	
	public ConstructionSiteActivityTest() {
		super("at.tugraz.ist.s2a", ConstructionSiteActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		//you have to turn this off if any of the test methods send key events to the application
		setActivityInitialTouchMode(false); 
		mActivity = getActivity();
        mListView = (ListView) mActivity.findViewById(at.tugraz.ist.s2a.R.id.MainListView);
        
        mShowToolBoxButton = (Button) mActivity.findViewById(at.tugraz.ist.s2a.R.id.toolbar_button);
        
        
        mShowObjectDialogButton = (Button) mActivity.findViewById(at.tugraz.ist.s2a.R.id.toolbar_button);
        
	}
	
//	public void testToolboxShowingUp() {
	
		
		//TODO redesign test case
//		assertTrue(false);
		
//		mActivity.runOnUiThread(
//				new Runnable() {
//					public void run() {
//						mShowToolBoxButton.requestFocus();	
//					}
//				}
//		);
//
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
//		
//		try {
//			mToolBoxDialog = (mActivity.getToolboxDialog());
//			assertTrue(mToolBoxDialog.isShowing());
//		} catch (Exception e) {
//			assertTrue(false);
//		}
		
//	}
	
//	public void testContextMenuOnMainList() {
//		mActivity.runOnUiThread(
//				new Runnable() {
//					public void run() {
//						mListView.requestFocus();	
//						
//					}
//				}
//		);
//		if(mListView.getChildCount()>0)
//		{
//		View view = (View) mListView.getChildAt(0);	
//		TouchUtils.longClickView(this, view);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
//		}
		//TODO redesign test case
//		assertTrue(false);

		
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
	
//	}
	
	

}
