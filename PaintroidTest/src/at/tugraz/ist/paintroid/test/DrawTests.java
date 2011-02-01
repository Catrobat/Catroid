package at.tugraz.ist.paintroid.test;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class DrawTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;

	// Buttonindexes
	final int COLORPICKER = 0;
	final int STROKE = 0;
	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int FILE = 7;
	
	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;

	public DrawTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	/**
	 * Test if background is transparent
	 * 
	 */
	@Smoke
	public void testDrawTRANSPARENTOnCanvas() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if(view instanceof DialogColorPicker.ColorPickerView)
			{
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+145, colorPickerViewCoordinates[1]+33);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+200, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.TRANSPARENT), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnImageButton(BRUSH);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
				.getDefaultDisplay().getHeight();

		int halfScreenWidth = (int) (screenWidth * 0.5);
		int halfScreenHeight = (int) (screenHeight * 0.5);

		solo.clickLongOnScreen(halfScreenWidth, halfScreenHeight);

		Bitmap currentImage = mainActivity.getCurrentImage();

		assertNotNull(currentImage);

		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		int pixel = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
		
		// 2.1 returns not completely transparent pixels here
		if(Build.VERSION.SDK_INT == Build.VERSION_CODES.ECLAIR_MR1)
		{
			assertEquals(1, Color.alpha(pixel));
		}
		else
		{
			assertEquals(Color.TRANSPARENT, pixel);
		}
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "test_drawTransparent");
		solo.clickOnButton("Done");
		solo.clickOnMenuItem("Clear Drawing");

    	solo.clickOnMenuItem("Quit");
	}
	

	
	/**
	 * Test if Brush function works
	 * 
	 */
	public void testBrush() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		solo.clickOnImageButton(BRUSH);
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if(view instanceof DialogColorPicker.ColorPickerView)
			{
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+145, colorPickerViewCoordinates[1]+33);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+200, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.TRANSPARENT), mainActivity.getCurrentSelectedColor());
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
		  .getDefaultDisplay().getWidth();
		
		ImageButton strokePickerButton = solo.getImageButton(STROKE);
		int[] locationstrokePickerButton = new int[2];
		strokePickerButton.getLocationOnScreen(locationstrokePickerButton);
		locationstrokePickerButton[0] += strokePickerButton.getMeasuredWidth();
		ImageButton handButton = solo.getImageButton(HAND);
		int[] locationHandButton = new int[2];
		handButton.getLocationOnScreen(locationHandButton);
		locationHandButton[1] -= handButton.getMeasuredHeight();
		
		actual_views = solo.getViews();
		View surfaceView = null;
		for (View view : actual_views) {
			if(view instanceof DrawingSurface)
			{
				surfaceView = view;
			}
		}
		assertNotNull(surfaceView);
		int[] coords = new int[2];
		surfaceView.getLocationOnScreen(coords);
		
		float min_x = locationstrokePickerButton[0];
		float min_y = coords[1];
		float max_x = screenWidth;
		float max_y = locationHandButton[1];
		// Get coordinates of begin of drag
		solo.clickLongOnScreen(min_x, min_y);
		float[] coordinatesOfFirstClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfFirstClick);
		
		solo.drag(min_x, max_x, min_y, max_y, 50);
		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		
		//Change coordinates to real clicked ones
		min_x = coordinatesOfFirstClick[0];
		max_x = coordinatesOfLastClick[0];
		min_y = coordinatesOfFirstClick[1];
		max_y = coordinatesOfLastClick[1];
		
		float ratioYX = ((float)max_y-(float)min_y)/((float)max_x-(float)min_x);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();

		int testPixel1 = mainActivity.getPixelFromScreenCoordinates(min_x+20, min_y+Math.round(20*ratioYX));
		int testPixel2 = mainActivity.getPixelFromScreenCoordinates(max_x-20, max_y-Math.round(20*ratioYX));
		int testPixel3 = mainActivity.getPixelFromScreenCoordinates(min_x+(max_x-min_x)/2, min_y+Math.round((max_x-min_x)/2*ratioYX));
		int testPixel4 = mainActivity.getPixelFromScreenCoordinates(min_x+20, min_y+max_y/2);
		int testPixel5 = mainActivity.getPixelFromScreenCoordinates(min_x+max_x/2, min_y+Math.round(20*ratioYX));

		assertEquals(testPixel1, Color.TRANSPARENT);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
		assertNotSame(testPixel4, Color.TRANSPARENT);
		assertNotSame(testPixel5, Color.TRANSPARENT);
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		solo.clickLongOnScreen(35, 400);
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		int pixel = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
		assertEquals(Color.RED, pixel);
		
	}
	
	/**
	 * Test different brush sizes
	 * 
	 */
	public void testBrushSizes() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		solo.clickOnImageButton(BRUSH);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if(view instanceof DialogColorPicker.ColorPickerView)
			{
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKECIRLCE);
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE1);
		solo.waitForDialogToClose(200);
		solo.clickLongOnScreen(100, 400);
		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		
		int brushWidth = mainActivity.getCurrentBrushWidth();
		Cap brushType = mainActivity.getCurrentBrush();
		assertEquals(1, brushWidth);
		assertEquals(Cap.ROUND, brushType);
		
		assertEquals(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])-brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1])-brushWidth);
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKERECT);
		solo.waitForDialogToClose(200);
		solo.clickLongOnScreen(100, 350);
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		brushWidth = mainActivity.getCurrentBrushWidth();
		brushType = mainActivity.getCurrentBrush();
		assertEquals(1, brushWidth);
		assertEquals(Cap.SQUARE, brushType);
		
		assertEquals(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])-brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1])-brushWidth);
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE3);
		solo.waitForDialogToClose(200);
		solo.clickLongOnScreen(100, 300);
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		brushWidth = mainActivity.getCurrentBrushWidth();
		brushType = mainActivity.getCurrentBrush();
		assertEquals(15, brushWidth);
		assertEquals(Cap.SQUARE, brushType);
		
		for (int count_x = 0; count_x < brushWidth; count_x++) {
			for (int count_y = 0; count_y < brushWidth; count_y++) {
				assertEquals(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+count_x, coordinatesOfLastClick[1]+count_y));
				assertEquals(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-count_x, coordinatesOfLastClick[1]-count_y));
			}
		}
		
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])-brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1])-brushWidth);
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKECIRLCE);
		solo.waitForDialogToClose(200);
		solo.clickLongOnScreen(100, 200);
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		brushWidth = mainActivity.getCurrentBrushWidth();
		brushType = mainActivity.getCurrentBrush();
		assertEquals(15, brushWidth);
		assertEquals(Cap.ROUND, brushType);
		
		for (int count_x = 0; count_x < brushWidth; count_x++) {
			for (int count_y = 0; count_y < brushWidth; count_y++) {
				if(! ((Math.pow(count_x, 2)+Math.pow(count_y, 2)) == Math.pow(brushWidth, 2)))
				{
					assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+count_x, coordinatesOfLastClick[1]+count_y));
					assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-count_x, coordinatesOfLastClick[1]-count_y));
				}
				else
				{
					assertEquals(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+count_x, coordinatesOfLastClick[1]+count_y));
					assertEquals(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-count_x, coordinatesOfLastClick[1]-count_y));
				}
			}
		}
		
		assertEquals(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+brushWidth, coordinatesOfLastClick[1])+brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1]));
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1])-brushWidth);
		assertNotSame(Color.RED, mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]-brushWidth, coordinatesOfLastClick[1])-brushWidth);
		
		
		
	}
	
	/**
	 * Test if MagicWand function works
	 * 
	 */
	public void testMagicWand() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if(view instanceof DialogColorPicker.ColorPickerView)
			{
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+145, colorPickerViewCoordinates[1]+33);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+200, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.TRANSPARENT), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnImageButton(WAND);
		
		solo.clickLongOnScreen(35, 400);		

		int testPixel1 = mainActivity.getPixelFromScreenCoordinates(35, 350);
		int testPixel2 = mainActivity.getPixelFromScreenCoordinates(25, 255);
		int testPixel3 = mainActivity.getPixelFromScreenCoordinates(40, 360);
		
		assertEquals(testPixel1, Color.TRANSPARENT);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnImageButton(BRUSH);
		solo.clickLongOnScreen(35, 400);
		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		int testPixelRed = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
		assertEquals(Color.RED, testPixelRed);
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+297);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.BLACK), mainActivity.getCurrentSelectedColor());
		solo.clickOnImageButton(WAND);
		
		solo.clickLongOnScreen(200, 200);
		
		testPixel1 = mainActivity.getPixelFromScreenCoordinates(200, 200);
		testPixel2 = mainActivity.getPixelFromScreenCoordinates(150, 200);
		testPixel3 = mainActivity.getPixelFromScreenCoordinates(10, 10);
		
		assertEquals(testPixel1, Color.BLACK);
		assertEquals(testPixel2, Color.BLACK);
		assertEquals(testPixel3, Color.BLACK);
		assertEquals(testPixelRed, Color.RED);
	}
	
	/**
	 * Test if EyeDropper function works
	 * 
	 */
	public void testEyeDropper() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		solo.clickOnImageButton(EYEDROPPER);
		
		solo.clickLongOnScreen(35, 400);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		float[] coordinatesOfClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfClick);

		int testPixel = mainActivity.getPixelFromScreenCoordinates(coordinatesOfClick[0], coordinatesOfClick[1]);
		
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
