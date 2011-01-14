package at.tugraz.ist.paintroid.test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.paintroid.MainActivity;

import com.jayway.android.robotium.solo.Solo;

public class DrawTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;

	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int FILE = 7;

	public DrawTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	/**
	 * Tests if Color background Color is Really Transparent
	 * 
	 */
	@Smoke
	public void testDrawTRANSPARENTOnCanvas() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(BRUSH);

		int screenWidth = solo.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getHeight();

		int halfScreenWidth = (int) (screenWidth * 0.5);
		int halfScreenHeight = (int) (screenHeight * 0.5);

		solo.drag(halfScreenWidth, halfScreenWidth, halfScreenHeight,
				halfScreenHeight, 1);

		mainActivity = (MainActivity) solo.getCurrentActivity();

		Bitmap currentImage = mainActivity.getCurrentImage();

		assertNotNull(currentImage);

		int halfImageWidth = (int) (currentImage.getWidth() * 0.5);
		int halfImageHeight = (int) (currentImage.getHeight() * 0.5);

		int pixel = currentImage.getPixel(halfImageWidth, halfImageHeight);
		
		//TODO could it be that it depends on the device (resolution, etc.)
		assertEquals(pixel, Color.TRANSPARENT);

		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "test_drawTransparent");
		solo.clickOnButton("Done");
		solo.clickOnMenuItem("Clear Drawing");

    	solo.clickOnMenuItem("Quit");
	}
	

	
	/**
	 * Testing if Brush function works
	 * @throws Exception
	 */
	public void testBrush() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(BRUSH);
		
		solo.drag(0, 400, 600, 0, 50);
		solo.drag(400, 23, 50, 600, 50);
		solo.drag(400, 50, 80, 600, 50);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();

		int testPixel1 = mainActivity.getCurrentImage().getPixel(35, 350);
		int testPixel2 = mainActivity.getCurrentImage().getPixel(25, 255);
		int testPixel3 = mainActivity.getCurrentImage().getPixel(40, 360);

		//TODO could be that it depends on the device (resolution, etc.)
		assertEquals(testPixel1, Color.TRANSPARENT);
		assertNotSame(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
	}
	
	/**
	 * Testing if MagicWand function works
	 * @throws Exception
	 */
	public void testMagicWand() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(WAND);
		
		solo.clickLongOnScreen(35, 400);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();

		int testPixel1 = mainActivity.getCurrentImage().getPixel(35, 350);
		int testPixel2 = mainActivity.getCurrentImage().getPixel(25, 255);
		int testPixel3 = mainActivity.getCurrentImage().getPixel(40, 360);
		
		assertEquals(testPixel1, Color.TRANSPARENT);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
	}
	
	public void testEyeDropper() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(EYEDROPPER);
		
		solo.clickLongOnScreen(35, 400);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();

		int testPixel = mainActivity.getCurrentImage().getPixel(35, 350);
		
		assertEquals(mainActivity.getCurrentSelectedColor(), String.valueOf(testPixel));
		
		
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
