/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui;

import java.io.File;
import java.io.IOException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class MainMenuActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String testProject = UiTestUtils.PROJECTNAME1;
	private String testProject2 = UiTestUtils.PROJECTNAME2;
	private String testProject3 = UiTestUtils.PROJECTNAME3;
	private String projectNameWithBlacklistedCharacters = "<H/ey, lo\"ok, :I'\\m s*pe?ci>al! ?äö|üß<>";
	private String projectNameWithWhitelistedCharacters = "[Hey+, =lo_ok. I'm; -special! ?äöüß<>]";

	private static final float CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED = 0.0f;

	public MainMenuActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void tearDown() throws Exception {
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameWithBlacklistedCharacters)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameWithWhitelistedCharacters)));
		super.tearDown();
	}

	public void testCreateNewProject() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject);
		UtilFile.deleteDirectory(directory);
		assertFalse("testProject was not deleted!", directory.exists());

		String hintNewProjectText = solo.getString(R.string.new_project_dialog_hint);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.waitForText(hintNewProjectText);
		EditText addNewProjectEditText = solo.getEditText(0);
		//check if hint is set
		assertEquals("Not the proper hint set", hintNewProjectText, addNewProjectEditText.getHint());
		assertEquals("There should no text be set", "", addNewProjectEditText.getText().toString());
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		String buttonOKText = solo.getString(R.string.ok);
		solo.goBack();
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Constants.DEFAULT_ROOT + "/" + testProject + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(testProject + " was not created!", file.exists());

		solo.goBack();
		assertFalse("New project dialog shouldn't show up again!",
				solo.searchText(solo.getString(R.string.new_project_dialog_title)));
	}

	public void testCreateNewProjectErrors() {
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, "");
		solo.goBack();

		Button okButton = solo.getButton(getActivity().getString(R.string.ok));

		assertFalse("New project ok button is enabled!", okButton.isEnabled());
		solo.clickOnButton(getActivity().getString(R.string.ok));

		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject);
		directory.mkdirs();
		solo.sleep(50);
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("No error message was displayed upon creating a project with the same name twice.",
				solo.searchText(solo.getString(R.string.error_project_exists)));
		solo.clickOnButton(0);

		directory = new File(Utils.buildProjectPath("te?st"));
		directory.mkdirs();
		String name = "te/st:";
		solo.sleep(50);
		solo.clearEditText(0);
		solo.enterText(0, name);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("No error message was displayed upon creating a project with the same name twice.",
				solo.searchText(solo.getString(R.string.error_project_exists)));
		solo.clickOnButton(solo.getString(R.string.close));

		UtilFile.deleteDirectory(directory);
	}

	public void testCreateNewProjectWithBlacklistedCharacters() {
		String directoryPath = Utils.buildProjectPath(projectNameWithBlacklistedCharacters);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameWithBlacklistedCharacters);
		solo.goBack();
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

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameWithWhitelistedCharacters);
		solo.goBack();
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file with whitelisted characters was not created!", file.exists());
	}

	public void testOrientation() throws NameNotFoundException {
		/// Method 1: Assert it is currently in portrait mode.
		assertEquals("MainMenuActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, getActivity()
				.getResources().getConfiguration().orientation);

		/// Method 2: Retreive info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = getActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(getActivity().getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);

		assertEquals(
				MainMenuActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}

	public void testLoadProject() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject2);
		UtilFile.deleteDirectory(directory);
		assertFalse(testProject2 + " was not deleted!", directory.exists());

		createTestProject(testProject2);
		solo.sleep(200);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.clickOnText(testProject2);
		solo.waitForFragmentById(R.id.fragment_sprites_list);
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

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.clickOnText(testProject3);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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

	public void testShouldDisplayDialogIfVersionNumberTooHigh() throws Throwable {
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		// Prevent Utils from returning true in isApplicationDebuggable
		Reflection.setPrivateField(Utils.class, "isUnderTest", true);

		boolean result = UiTestUtils
				.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED);
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

	public void testOverrideMyFirstProject() {
		String standardProjectName = solo.getString(R.string.default_project_name);
		Project standardProject = null;

		try {
			standardProject = StandardProjectHandler.createAndSaveStandardProject(standardProjectName,
					getInstrumentation().getTargetContext());
		} catch (IOException e) {
			fail("Could not create standard project");
			e.printStackTrace();
		}

		if (standardProject == null) {
			fail("Could not create standard project");
		}
		ProjectManager.INSTANCE.setProject(standardProject);
		StorageHandler.getInstance().saveProject(standardProject);

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());

		Sprite backgroundSprite = standardProject.getSpriteList().get(0);
		Script startingScript = backgroundSprite.getScript(0);
		assertEquals("Number of bricks in background sprite was wrong", 3, backgroundSprite.getNumberOfBricks());
		startingScript.addBrick(new SetLookBrick(backgroundSprite));
		startingScript.addBrick(new SetLookBrick(backgroundSprite));
		startingScript.addBrick(new SetLookBrick(backgroundSprite));
		assertEquals("Number of bricks in background sprite was wrong", 6, backgroundSprite.getNumberOfBricks());
		ProjectManager.INSTANCE.setCurrentSprite(backgroundSprite);
		ProjectManager.INSTANCE.setCurrentScript(startingScript);
		StorageHandler.getInstance().saveProject(standardProject);

		UiTestUtils.goBackToHome(getInstrumentation());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(300);
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation()
				.getTargetContext());
		assertEquals("Standard project was not set in shared preferences", standardProjectName,
				defaultSharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null));

		Utils.saveToPreferences(getInstrumentation().getTargetContext(), Constants.PREF_PROJECTNAME_KEY, null);
		assertEquals("Standard project was not reset to null in shared preferences", null,
				defaultSharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null));

		Intent intent = new Intent(solo.getCurrentActivity(), ProjectActivity.class);
		ProjectManager.INSTANCE.setProject(null);
		solo.getCurrentActivity().startActivity(intent);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.waitForText(solo, solo.getString(R.string.default_project_backgroundname));
		assertEquals("Number of bricks in background sprite was wrong - standard project was overwritten", 6,
				ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfBricks());
	}

	public void testProjectNameVisible() {
		createTestProject(testProject);
		createTestProject(testProject2);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.clickOnText(testProject);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		assertTrue("The name of the current project is not displayed on the continue button", solo.getButton(0)
				.getText().toString().endsWith(testProject));

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.clickOnText(testProject2);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		assertTrue("The name of the current project is not displayed on the continue button", solo.getButton(0)
				.getText().toString().endsWith(testProject2));
	}
}
