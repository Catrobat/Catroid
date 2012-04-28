package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class DeleteDialogTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;
	private String costumeName = "costumeNametest";
	private File imageFile;
	private File imageFile2;
	private File paintroidImageFile;
	private ArrayList<CostumeData> costumeDataList;
	private final int RESOURCE_IMAGE = R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;
	private ProjectManager projectManager = ProjectManager.getInstance();

	public DeleteDialogTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);

		paintroidImageFile = UiTestUtils.createTestMediaFile(Consts.DEFAULT_ROOT + "/testFile.png",
				R.drawable.catroid_banzai, getActivity());

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile2.getName());
		costumeData.setCostumeName("costumeNameTest2");
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testDeleteCostumes() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		assertTrue("No ok button found", solo.searchButton(getActivity().getString(R.string.ok)));
		assertTrue("No cancel button found", solo.searchButton(getActivity().getString(R.string.cancel_button)));
		solo.sleep(5000);
	}
}
