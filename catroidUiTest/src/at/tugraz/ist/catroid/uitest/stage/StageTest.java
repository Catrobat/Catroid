/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.stage;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ConstructionSiteActivity;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class StageTest extends
ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
	private Solo solo;
	
	public StageTest() {
		super("at.tugraz.ist.catroid",
				ConstructionSiteActivity.class);

		//delete the whole catroid directory so we can start from scratch
		//File catroidDirectory = new File("/sdcard/catroid");
		//deleteDirectory(catroidDirectory);
	}
	
	@Override
    public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
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
	

	/*
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
	  */
	
	@Smoke
	public void testDemoApplicationLoaded(){
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
