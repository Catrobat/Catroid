package at.tugraz.ist.catroid.uitest.stage;

import java.io.File;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class ProjectTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{
	private Solo solo;
	
	public ProjectTest() {
		super("at.tugraz.ist.catroid",
				ConstructionSiteActivity.class);
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
		
        File projectRoot = new File(Consts.DEFAULT_ROOT + "/testProject/");
		UtilFile.deleteDirectory(projectRoot);
	}
	
	@Smoke
	public void testCreateNewProject(){
		solo.clickOnMenuItem(getActivity().getString(R.string.new_project));
		solo.clearEditText(0);
		solo.enterText(0, "testProject");
		solo.clickOnButton(0);
		// now there should be an empty new project
		
		solo.clickOnButton(getActivity().getString(R.string.stage));
		ListView spritesList = solo.getCurrentListViews().get(0);
		assertEquals("After creating a new project there is only the stage available.", 1, spritesList.getChildCount());
		solo.goBack();
		
//		ListView brickList = (ListView) getActivity().findViewById(R.id.MainListView);
//		assertEquals("After creating a new project there is no brick in construction site.", 0, brickList.getChildCount());
		
		
	}
	
}
