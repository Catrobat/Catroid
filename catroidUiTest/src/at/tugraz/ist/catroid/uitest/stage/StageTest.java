package at.tugraz.ist.catroid.uitest.stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.jayway.android.robotium.solo.Solo;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.Surface;
import android.view.View;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.stage.StageView;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class StageTest extends
ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
	private Solo solo;
	
	public StageTest() {
		super("at.tugraz.ist.catroid.test.construction_site",
				ConstructionSiteActivity.class);

		//delete the whole catroid directory so we can start from scratch
		//File catroidDirectory = new File("/sdcard/catroid");
		//deleteDirectory(catroidDirectory);
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void tearDown() throws Exception {
		try {	
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	

	
	private boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }

	
	@Smoke
	public void testDemoApplicationLoaded(){
		//TODO load demo program and look if it's sowing correctly on stage
//		solo.clickOnMenuItem(getActivity().getString(R.string.construction_site_play));
//		solo.assertCurrentActivity("Now stage activity is active", "StageActivity");
//		
//		solo.clickOnScreen(200, 200);
//		
//		ArrayList<View> views = solo.getViews();
//		StageView stage = (StageView) views.get(views.size()-1);
//		Bitmap bitmap = stage.getDrawingCache();
//		solo.getCurrentActivity().
//		
//		int color = bitmap.getPixel(20, 20);
//		int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
//		//int[] pixels = null;
//		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//		
//		
//		
		//TODO make after refactoring from stage
		
		
	}
	

}
