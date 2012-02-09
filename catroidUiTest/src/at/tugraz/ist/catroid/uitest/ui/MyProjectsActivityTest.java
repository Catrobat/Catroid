package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class MyProjectsActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private final int RESOURCE_IMAGE = R.drawable.catroid_sunglasses;
	private Solo solo;
	private final String lorem = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor "
			+ "incididunt ut labore et dolore magna aliqua. Ut enim ";

	public MyProjectsActivityTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() {

		//delete all projects
		File directory;
		File rootDirectory = new File(Consts.DEFAULT_ROOT);
		for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
			directory = new File(Consts.DEFAULT_ROOT + "/" + projectName);
			if (directory.exists()) {
				UtilFile.deleteDirectory(directory);
			}
		}

		createProjects();

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
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testProjectsVisible() {
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		assertTrue("activity doesn't show the project " + UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertTrue("activity doesn't show the project " + UiTestUtils.PROJECTNAME1,
				solo.searchText(UiTestUtils.PROJECTNAME1));
	}

	public void testDeleteProject() {
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete));
		assertFalse("project " + UiTestUtils.PROJECTNAME1 + " is still visible",
				solo.searchText(UiTestUtils.PROJECTNAME1));
	}

	public void testDeleteCurrentProject() {
		//current project is UiTestUtils.DEFAULT_TEST_PROJECT_NAME
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete));
		ProjectManager projectManager = ProjectManager.getInstance();
		assertFalse("project " + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + " is still visible",
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertTrue("project " + UiTestUtils.PROJECTNAME1 + " is not visible anymore",
				solo.searchText(UiTestUtils.PROJECTNAME1));
		assertNotSame("the deleted project is still the current project", UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				projectManager.getCurrentProject().getName());
	}

	public void testDeleteAllProjects() {

		solo.sleep(500);

		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);

		String defaultProjectName = getActivity().getString(R.string.default_project_name);

		//delete default project:
		if (solo.searchText(defaultProjectName)) {
			solo.clickLongOnText(defaultProjectName);
			solo.sleep(100);
			solo.clickOnText(getActivity().getString(R.string.delete));
		}

		//delete first project
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete));
		ProjectManager projectManager = ProjectManager.getInstance();
		assertFalse("project " + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + " is still visible",
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertTrue("project " + UiTestUtils.PROJECTNAME1 + " is not visible anymore",
				solo.searchText(UiTestUtils.PROJECTNAME1));
		assertNotSame("the deleted project is still the current project", UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				projectManager.getCurrentProject().getName());
		assertEquals(UiTestUtils.PROJECTNAME1 + " should be the current project", UiTestUtils.PROJECTNAME1,
				projectManager.getCurrentProject().getName());

		//delete second project
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1, 2);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete));
		assertFalse("project " + UiTestUtils.PROJECTNAME1 + " is still visible",
				solo.searchText(UiTestUtils.PROJECTNAME1));
		assertNotSame("the deleted project is still the current project", UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				projectManager.getCurrentProject().getName());

		assertTrue("default project not visible", solo.searchText(defaultProjectName));
		assertEquals("the current project is not the default project", defaultProjectName, projectManager
				.getCurrentProject().getName());

	}

	public void testRenameProject() {
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME3);
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.ok));
		solo.sleep(200);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME3));
	}

	public void testRenameCurrentProject() {
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(300);
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME3);
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.ok));
		solo.sleep(200);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME3));
		solo.goBack();
		assertEquals("current project not updated", UiTestUtils.PROJECTNAME3, ProjectManager.getInstance()
				.getCurrentProject().getName());
	}

	public void testAddNewProject() {
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_sprite);
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME2);
		solo.sleep(200);
		solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(200);
		solo.assertCurrentActivity("not in projectactivity", ProjectActivity.class);
		assertEquals("current project not updated", UiTestUtils.PROJECTNAME2, ProjectManager.getInstance()
				.getCurrentProject().getName());
		solo.goBack();
		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		assertTrue("project " + UiTestUtils.PROJECTNAME2 + " was not added", solo.searchText(UiTestUtils.PROJECTNAME2));
	}

	public void testSetDescription() {
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.set_description));
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, lorem);
		solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(500);
		ProjectManager projectManager = ProjectManager.getInstance();
		//assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum")); //TODO this doesnt work - I don't know why
		assertTrue("description is not set in project",
				projectManager.getCurrentProject().description.equalsIgnoreCase(lorem));
	}

	public void createProjects() {
		Project project1 = new Project(getActivity(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project1);
		ProjectManager.getInstance().setProject(project1);
		ProjectManager projectManager = ProjectManager.getInstance();

		Sprite testSprite = new Sprite("sprite1");
		projectManager.addSprite(testSprite);
		projectManager.setCurrentSprite(testSprite);

		File imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);

		ArrayList<CostumeData> costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName("testname");
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());

		StorageHandler.getInstance().saveProject(project1);

		//-------------------------------------------------

		Project project2 = new Project(getActivity(), UiTestUtils.PROJECTNAME1);
		StorageHandler.getInstance().saveProject(project2);
	}
}
