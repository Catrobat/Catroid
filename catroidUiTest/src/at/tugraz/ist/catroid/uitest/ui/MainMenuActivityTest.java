/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.MyProjectsActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private String testProject = UiTestUtils.PROJECTNAME1;
	private String testProject2 = UiTestUtils.PROJECTNAME2;
	private String testProject3 = UiTestUtils.PROJECTNAME3;
	private String projectNameWithBlacklistedCharacters = "<H/ey, lo\"ok, :I'\\m s*pe?ci>al! ?äö|üß<>";
	private String projectNameWithWhitelistedCharacters = "[Hey+, =lo_ok. I'm; -special! ?äöüß<>]";

	public MainMenuActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameWithBlacklistedCharacters)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameWithWhitelistedCharacters)));
		super.tearDown();
	}

	public void testCreateNewProject() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject);
		UtilFile.deleteDirectory(directory);
		assertFalse("testProject was not deleted!", directory.exists());

		String hintNewProjectText = solo.getString(R.string.new_project_dialog_hint);

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.waitForText(hintNewProjectText);
		EditText addNewProjectEditText = solo.getEditText(0);
		//check if hint is set
		assertEquals("Not the proper hint set", hintNewProjectText, addNewProjectEditText.getHint());
		assertEquals("There should no text be set", "", addNewProjectEditText.getText().toString());
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(testProject));
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Constants.DEFAULT_ROOT + "/" + testProject + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(testProject + " was not created!", file.exists());
	}

	public void testCreateNewProjectErrors() {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, "");
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(""));
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.sendKey(Solo.ENTER);

		assertTrue("No error message was displayed upon creating a project with an empty name.",
				solo.searchText(getActivity().getString(R.string.error_no_name_entered)));
		solo.clickOnButton(0);

		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject);
		directory.mkdirs();
		solo.sleep(50);

		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(testProject));
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clickOnButton(0);

		assertTrue("No error message was displayed upon creating a project with the same name twice.",
				solo.searchText(getActivity().getString(R.string.error_project_exists)));
		solo.clickOnButton(0);

		directory = new File(Utils.buildProjectPath("te?st"));
		String name = "te/st:";
		directory.mkdirs();
		solo.sleep(50);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, name);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(name));
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clickOnButton(0);

		assertTrue("No error message was displayed upon creating a project with the same name twice.",
				solo.searchText(getActivity().getString(R.string.error_project_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));

		UtilFile.deleteDirectory(directory);
	}

	public void testCreateNewProjectWithBlacklistedCharacters() {
		String directoryPath = Utils.buildProjectPath(projectNameWithBlacklistedCharacters);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, projectNameWithBlacklistedCharacters);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project with blacklisted characters was not created!", file.exists());
	}

	public void testCreateNewProjectWithWhitelistedCharacters() {
		String directoryPath = Utils.buildProjectPath(projectNameWithWhitelistedCharacters);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, projectNameWithWhitelistedCharacters);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file with whitelisted characters was not created!", file.exists());
	}

	public void testLoadProject() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject2);
		UtilFile.deleteDirectory(directory);
		assertFalse(testProject2 + " was not deleted!", directory.exists());

		createTestProject(testProject2);
		solo.sleep(200);

		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.clickOnText(testProject2);
		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite first = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite at index 1 is not \"cat\"!", "cat", first.getName());
		Sprite second = (Sprite) spritesList.getItemAtPosition(2);
		assertEquals("Sprite at index 2 is not \"dog\"!", "dog", second.getName());
		Sprite third = (Sprite) spritesList.getItemAtPosition(3);
		assertEquals("Sprite at index 3 is not \"horse\"!", "horse", third.getName());
		Sprite fourth = (Sprite) spritesList.getItemAtPosition(4);
		assertEquals("Sprite at index 4 is not \"pig\"!", "pig", fourth.getName());
	}

	public void testResume() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject3);
		UtilFile.deleteDirectory(directory);
		assertFalse(testProject3 + " was not deleted!", directory.exists());

		createTestProject(testProject3);
		solo.sleep(200);

		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.clickOnText(testProject3);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite first = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite at index 1 is not \"cat\"!", "cat", first.getName());
		Sprite second = (Sprite) spritesList.getItemAtPosition(2);
		assertEquals("Sprite at index 2 is not \"dog\"!", "dog", second.getName());
		Sprite third = (Sprite) spritesList.getItemAtPosition(3);
		assertEquals("Sprite at index 3 is not \"horse\"!", "horse", third.getName());
		Sprite fourth = (Sprite) spritesList.getItemAtPosition(4);
		assertEquals("Sprite at index 4 is not \"pig\"!", "pig", fourth.getName());
	}

	public void testAboutCatroid() {
		solo.clickOnButton(getActivity().getString(R.string.about));
		solo.sleep(200);
		ArrayList<TextView> textViewList = solo.getCurrentTextViews(null);

		assertEquals("Title is not correct!", getActivity().getString(R.string.about_title), textViewList.get(0)
				.getText().toString());
		assertEquals("About text not correct!", getActivity().getString(R.string.about_text), textViewList.get(1)
				.getText().toString());
		assertEquals("Link text is not correct!", getActivity().getString(R.string.about_catroid_license_link_text),
				textViewList.get(2).getText().toString());
		solo.goBack();
	}

	public void testShouldDisplayDialogIfVersionNumberTooHigh() throws Throwable {
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		// Prevent Utils from returning true in isApplicationDebuggable
		UiTestUtils.setPrivateField2(Utils.class, null, "isUnderTest", true);

		boolean result = UiTestUtils.createTestProjectOnLocalStorageWithVersionCode(Integer.MAX_VALUE);
		assertTrue("Could not create test project.", result);

		runTestOnUiThread(new Runnable() {
			public void run() {
				ProjectManager.INSTANCE.loadProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, getActivity(), true);
			}
		});

		solo.getText(solo.getString(R.string.error_project_compatability), true);
		solo.clickOnButton(0);
		solo.waitForDialogToClose(500);
	}

	public void testPlayButton() {
		// FIXME
		//		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		//		solo.assertCurrentActivity("StageActivity not showing!", StageActivity.class);
	}

	// TODO edit this to work with login dialog

	//	public void testRenameToExistingProject() {
	//		createTestProject(existingProject);
	//		solo.clickOnButton(getActivity().getString(R.string.upload_project));
	//		solo.clickOnEditText(0);
	//		solo.enterText(0, "");
	//		solo.enterText(0, existingProject);
	//		solo.goBack();
	//		solo.clickOnEditText(1);
	//		solo.goBack();
	//		solo.clickOnButton(getActivity().getString(R.string.upload_button));
	//		assertTrue("No error message was displayed upon renaming the project to an existing one.",
	//				solo.searchText(getActivity().getString(R.string.error_project_exists)));
	//	}

	//	public void testDefaultProject() throws IOException {
	//		File directory = new File(Constants.DEFAULT_ROOT + "/" + getActivity().getString(R.string.default_project_name));
	//		UtilFile.deleteDirectory(directory);
	//
	//		StorageHandler handler = StorageHandler.getInstance();
	//		ProjectManager project = ProjectManager.getInstance();
	//		project.setProject(handler.createDefaultProject(solo.getCurrentActivity()));
	//		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
	//		solo.sleep(8000);
	//		Bitmap bitmap = project.getCurrentProject().getSpriteList().get(1).getCostume().getBitmap();
	//		assertNotNull("Bitmap is null", bitmap);
	//		assertTrue("Sprite not visible", project.getCurrentProject().getSpriteList().get(1).isVisible());
	//
	//		directory = new File(Constants.DEFAULT_ROOT + "/" + getActivity().getString(R.string.default_project_name));
	//		UtilFile.deleteDirectory(directory);
	//	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();

		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");
		Script testScript = new StartScript(firstSprite);
		Script otherScript = new StartScript(secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(secondSprite, size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

		// adding Bricks: ----------------
		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(setSizeToBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick); // secondSprite
		otherScript.setPaused(true);
		// -------------------------------

		firstSprite.addScript(testScript);
		secondSprite.addScript(otherScript);

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		storageHandler.saveProject(project);
	}
}
