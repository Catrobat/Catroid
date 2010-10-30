package at.tugraz.ist.catroid.uitest.web;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.download.tasks.ProjectDownloadTask;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Thomas Holzmann
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
	
	@Smoke
	public void testUpProject() throws InterruptedException {
		String projectName = "testUploadProject"+System.currentTimeMillis();
		
		solo.clickOnMenuItem(getActivity().getString(R.string.new_project_main));
		solo.clearEditText(0);
		solo.enterText(0, projectName);  
		solo.clickOnButton(0);  
		// now there should be an empty new project
		
		solo.clickOnMenuItem(getActivity().getString(R.string.upload_project), true); 
		
		Thread.sleep(8000);
		
		assertTrue("upload failed.", 
					solo.searchText(getActivity().getString(R.string.success_project_upload)));  
		
		solo.clickOnButton(0);
		// TODO: delete and download project
		// TODO: check if downloaded project can be opened
		
	}
	
}
