package at.tugraz.ist.s2a.test.gui;

import android.app.Dialog;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;
import at.tugraz.ist.s2a.ConstructionSiteActivity;

public class ToolboxDialogTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{

	private ConstructionSiteActivity mActivity;

	private Button mButton;
	private Dialog mDialog;
	
	public ToolboxDialogTest() {
		super("com.tugraz.android.app", ConstructionSiteActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false); //you have to turn this of if any of the test methods send key events to the application
		mActivity = getActivity();

		
		mButton = (Button) mActivity.findViewById(at.tugraz.ist.s2a.R.id.toolbar_button);
        
	}
	
	/**
	 * clicks on the toolbox button and checks if the toolbox is showing up
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
		
		mDialog = (mActivity.getToolboxDialog());
		assertTrue(mDialog.isShowing());
		
	}

//	public void testSetNewBrick() {
//		mActivity.runOnUiThread(
//				new Runnable() {
//					public void run() {
//						mButton.requestFocus();	
//						
//					}
//				}
//		);
////		View view = (View) mListView.getChildAt(0);	
////		TouchUtils.longClickView(this, view);
////		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
//		
//		mDialog = mActivity.getToolboxDialog();
//		ListView view = (ListView) mActivity.findViewById(R.id.toolboxListView);
//		View item = view.getChildAt(0);
//		item.requestFocus();
//		
//		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
//		
////		mActivity.runOnUiThread(
////				new Runnable() {
////					public void run() {
////						item.requestFocus();	
////						
////					}
////				}
////		);
//		
//		
//		assertTrue(true);
//	}
	

}
