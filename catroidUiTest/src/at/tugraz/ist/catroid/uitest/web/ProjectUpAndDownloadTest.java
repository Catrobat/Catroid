package at.tugraz.ist.catroid.uitest.web;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 *
 */
public class ProjectUpAndDownloadTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{
	private Solo solo;
	
	public ProjectUpAndDownloadTest() {
		super("at.tugraz.ist.catroid.test.construction_site",
				ConstructionSiteActivity.class);
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
	
	public void testUploadProject() throws InterruptedException {
//		
//		solo.clickOnMenuItem(getActivity().getString(R.string.upload_project), true); 
//		Thread.sleep(8000);
//		
//		assertTrue("Upload failed. Internet connection?", 
//					solo.searchText(getActivity().getString(R.string.success_project_upload)));  
//	
//		solo.clickOnButton(0);   
		
	}
	
}
