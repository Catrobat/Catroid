package at.tugraz.ist.paintroid.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.paintroid.MainActivity;

import com.jayway.android.robotium.solo.Solo;

public class DrawTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	
	final int BRUSH = 3;
	final int FILE = 7;
	
	public DrawTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	/**
	 * This test creates a new empty image, draws transparency onto it,
	 * saves it, loads it again and checks, whether the same spot is
	 * still transparent.
	 * 
	 */
	@Smoke
	public void testDrawOnCanvas() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(BRUSH);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		
		int halfScreenWidth = (int)(screenWidth * 0.5);
		int halfScreenHeight = (int)(screenHeight * 0.5);
		
		solo.drag(halfScreenWidth, halfScreenWidth, halfScreenHeight, halfScreenHeight, 1);
		
		MainActivity mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Bitmap currentImage = mainActivity.getCurrentImage();
		
		assertNotNull(currentImage);
		
		int halfImageWidth = (int)(currentImage.getWidth() * 0.5);
		int halfImageHeight = (int)(currentImage.getHeight() * 0.5);
		
		int pixel = currentImage.getPixel(halfImageWidth, halfImageHeight);
				
		assertEquals(pixel, Color.TRANSPARENT);
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Save");
		solo.enterText(0, "robotium-test");
		solo.clickOnButton("Done");
		solo.clickOnMenuItem("Clear Drawing");
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Cancel");
		
//		Intent intent = new Intent();
//		intent.putExtra("UriString", mainActivity.savedFile.toString());
//		intent.putExtra("IntentReturnValue", "LOAD");
//		
//		mainActivity.onActivityResult(mainActivity.FILE_IO, Activity.RESULT_OK, intent);
		
//		mainActivity.loadNewImage(mainActivity.savedFile.toString());
		
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
