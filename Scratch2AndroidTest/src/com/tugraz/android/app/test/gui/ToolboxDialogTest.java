package com.tugraz.android.app.test.gui;


import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.BrickDefine;
import com.tugraz.android.app.MainActivity;
import com.tugraz.android.app.ToolboxDialog;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ToolboxDialogTest extends ActivityInstrumentationTestCase2<MainActivity>{

	private MainActivity mActivity;

	private Button mButton;
	private ToolboxDialog mDialog;
	
	public ToolboxDialogTest() {
		super("com.tugraz.android.app", MainActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false); //you have to turn this of if any of the test methods send key events to the application
		mActivity = getActivity();

		
		mButton = (Button) mActivity.findViewById(com.tugraz.android.app.R.id.toolbar_button);
        //mDialog = mActivity.getToolboxDialog(); //TODO kriegen wir da das richtige??
        
	}
	
//	public void testPreConditions() {
//		// we don't have preconditions atm
//	}
	
	/**
	 * NO AUTOMATED TEST
	 */
	public void testToolboxShowingUp() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						mButton.requestFocus();	
						
					}
				}
		);
//		View view = (View) mListView.getChildAt(0);	
//		TouchUtils.longClickView(this, view);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
		mDialog = mActivity.getToolboxDialog();
		assertTrue(mDialog.isShowing());
		
	}

	public void testSetNewBrick() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						mButton.requestFocus();	
						
					}
				}
		);
//		View view = (View) mListView.getChildAt(0);	
//		TouchUtils.longClickView(this, view);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
		mDialog = mActivity.getToolboxDialog();
		
		int sizeBeforeClick = mDialog.mContentArrayList.size();
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		int sizeAfterClick = mDialog.mContentArrayList.size();
		
		assertTrue(sizeBeforeClick==(sizeAfterClick));
	}
	

}
