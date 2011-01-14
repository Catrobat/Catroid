package at.tugraz.ist.paintroid.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.paintroid.MainActivity;

import com.jayway.android.robotium.solo.Solo;


public class ButtonFunctionTests extends ActivityInstrumentationTestCase2<MainActivity>{
	private Solo solo;
	private MainActivity mainActivity;
	
	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int FILE = 7;
	
	

	public ButtonFunctionTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Smoke
	/**
	 * Check if Buttons change their background when they have been clicked
	 */
	public void testChangedButtonBackground() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		
		solo.clickOnImageButton(MAGNIFIY);
		assertEquals(mainActivity.getImageButtonBackground(MAGNIFIY),solo.getImageButton(MAGNIFIY).getContext());
		solo.clickOnImageButton(HAND);
		assertEquals(mainActivity.getImageButtonBackground(HAND),solo.getImageButton(HAND).getContext());
		solo.clickOnImageButton(BRUSH);
		assertEquals(mainActivity.getImageButtonBackground(BRUSH),solo.getImageButton(BRUSH).getContext());
		solo.clickOnImageButton(EYEDROPPER);
		assertEquals(mainActivity.getImageButtonBackground(EYEDROPPER),solo.getImageButton(EYEDROPPER).getContext());
		solo.clickOnImageButton(WAND);
		assertEquals(mainActivity.getImageButtonBackground(WAND),solo.getImageButton(WAND).getContext());
		
	}
		
	public void testColorPicker() throws Exception{
		/**
		 * Has to be done in Robotium
		 * ToDo:
		 * 1. Click on the SelectedColorButton
		 * 2. choose a Color
		 * 3. Accept Color
		 * 4. Check if SelectedColorButton has now the selected Color
		 */
		
	}
	
	public void testBrushShape() throws Exception{
		/**
		 * Has to be done in Robotium
		 * ToDo:
		 * 1. Click on the brushStrokeButton
		 * 2. choose a Shape and Size
		 * 4. Check if Shape and Size has been changed
		 */
	}

	/**
	 * Tests if there is a new Bitmap created
	 * 
	 */
	public void testNewDrawing() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		assertNotNull(mainActivity.getCurrentImage());
		
	}
	

	/**
	 * Tests if the Bitmap(DrawingSurface) is now cleared
	 * @throws Exception
	 */
	public void testClearDrawing() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnMenuItem("Clear Drawing");
		assertNull(mainActivity.getCurrentImage());
//		assertNull(mainActivity.getCurrentImage().getNinePatchChunk());
		
	}

	/**
	 * Tests if reset of ZoomValue works
	 * 
	 */
	public void testResetZoom() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(MAGNIFIY);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.drag(66, 500, 700, 55, 100);
				
		//TODO could be that it depends on the device (resolution, etc.)
//		assertEquals(mainActivity.getZoomLevel(), String.valueOf(12.626037));
		assertFalse(mainActivity.getZoomLevel().equals(String.valueOf(1.0)));
		
		solo.clickOnMenuItem("Reset Zoom");
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		assertEquals(mainActivity.getZoomLevel(), String.valueOf(1.0));
	}
	
	public void testScroll() throws Exception{
		
	}
	
	/**
	 * Tests if Zooming works
	 * 
	 */
	public void testZoom() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(MAGNIFIY);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.drag(66, 500, 700, 55, 100);
		
		//TODO could be that it depends on the device (resolution, etc.)
//		assertEquals(mainActivity.getZoomLevel(), String.valueOf(12.626037));
		assertFalse(mainActivity.getZoomLevel().equals(String.valueOf(1.0)));
	}
	
	public void testUndo() throws Exception{
		/**
		 * Not implemented yet
		 */
	}
	
	
	public void testWarningWhenNoBitmap() throws Exception{
		/**
		 * this has to be tested with Robotium
		 * for now its just an straight forward test for complete
		 * Story Card Testcase 1
		 */
		solo.clickOnImageButton(BRUSH);
		solo.clickOnButton("Cancel");
		solo.clickOnMenuItem("Quit");
	}
	
	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
}
