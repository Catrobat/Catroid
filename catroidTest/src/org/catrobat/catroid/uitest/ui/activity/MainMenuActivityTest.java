/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.robotium.solo.Solo;

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
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;

public class MainMenuActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = MainMenuActivityTest.class.getSimpleName();

	private String testProject = UiTestUtils.PROJECTNAME1;
	private String testProject2 = UiTestUtils.PROJECTNAME2;
	private String testProject3 = UiTestUtils.PROJECTNAME3;
	private String projectNameWithNormalAndSpecialChars = UiTestUtils.NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME;
	private String projectNameWithNormalAndSpecialChars2 = UiTestUtils.NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME2;
	private String projectNameJustSpecialChars = UiTestUtils.JUST_SPECIAL_CHAR_PROJECT_NAME;
	private String projectNameJustSpecialChars2 = UiTestUtils.JUST_SPECIAL_CHAR_PROJECT_NAME2;
	private String projectNameJustOneDot = UiTestUtils.JUST_ONE_DOT_PROJECT_NAME;
	private String projectNameJustTwoDots = UiTestUtils.JUST_TWO_DOTS_PROJECT_NAME;

	private static final float CATROBAT_LANGUAGE_VERSION_TOO_LOW = 0.0f;

	public MainMenuActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void tearDown() throws Exception {
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameWithNormalAndSpecialChars)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameWithNormalAndSpecialChars2)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameJustSpecialChars)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameJustSpecialChars2)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameJustOneDot)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectNameJustTwoDots)));
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
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOKText);
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

		directory = new File(Utils.buildProjectPath(projectNameWithNormalAndSpecialChars2 + "_TWO"));
		directory.mkdirs();
		String name = projectNameWithNormalAndSpecialChars2 + "_TWO";
		solo.sleep(50);
		solo.clearEditText(0);
		solo.enterText(0, name);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("No error message was displayed upon creating a project with the same name twice.",
				solo.searchText(solo.getString(R.string.error_project_exists)));
		solo.clickOnButton(solo.getString(R.string.close));

		UtilFile.deleteDirectory(directory);
	}

	public void testCreateNewProjectWithNormalAndSpecialChars() {
		String directoryPath = Utils.buildProjectPath(projectNameWithNormalAndSpecialChars);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameWithNormalAndSpecialChars);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file with normal and special characters was not created!", file.exists());
	}

	public void testCreateNewProjectsWithNormalAndSpecialCharsTwo() {
		String directoryPath = Utils.buildProjectPath(projectNameWithNormalAndSpecialChars2);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameWithNormalAndSpecialChars2);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file with special characters two was not created!", file.exists());
	}

	public void testCreateNewProjectsJustSpecialChars() {
		String directoryPath = Utils.buildProjectPath(projectNameJustSpecialChars);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameJustSpecialChars);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file with just special characters was not created!", file.exists());
	}

	public void testCreateNewProjectsJustSpecialCharsTwo() {
		String directoryPath = Utils.buildProjectPath(projectNameJustSpecialChars2);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameJustSpecialChars2);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file with just special characters two was not created!", file.exists());
	}

	public void testCreateNewProjectJustOneDot() {
		String directoryPath = Utils.buildProjectPath(projectNameJustOneDot);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameJustOneDot);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file just one dot was not created!", file.exists());
	}

	public void testCreateNewProjectJustTwoDots() {
		String directoryPath = Utils.buildProjectPath(projectNameJustTwoDots);
		File directory = new File(directoryPath);
		UtilFile.deleteDirectory(directory);

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.clearEditText(0);
		solo.enterText(0, projectNameJustTwoDots);
		String buttonOKText = solo.getString(R.string.ok);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOKText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		File file = new File(Utils.buildPath(directoryPath, Constants.PROJECTCODE_NAME));
		assertTrue("Project file just two dots was not created!", file.exists());
	}

	public void testOrientation() throws NameNotFoundException {
		/// Method 1: Assert it is currently in portrait mode.
		assertEquals("MainMenuActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, getActivity()
				.getResources().getConfiguration().orientation);

		/// Method 2: Retrieve info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = getActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(getActivity().getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);

		assertEquals(MainMenuActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}

	public void testBottombarElementsVisibilty() {
		assertFalse("Add button is visible", solo.searchButton(solo.getString(R.id.button_add)));
		assertFalse("Play button is visible", solo.searchButton(solo.getString(R.id.button_play)));
	}

	public void testLoadProject() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject2);
		UtilFile.deleteDirectory(directory);
		assertFalse(testProject2 + " was not deleted!", directory.exists());

		createTestProject(testProject2);
		solo.sleep(200);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		assertTrue("MyProjectsActivity not shown", solo.waitForActivity(MyProjectsActivity.class.getSimpleName()));
		solo.clickOnText(testProject2);
		assertTrue("ProjectActivity not shown", solo.waitForActivity(ProjectActivity.class.getSimpleName()));
		assertTrue("SpritesListFragment not shown", solo.waitForFragmentById(R.id.fragment_sprites_list));

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
		assertTrue("MyProjectsActivity not shown", solo.waitForActivity(MyProjectsActivity.class.getSimpleName()));
		solo.clickOnText(testProject3);
		assertTrue("ProjectActivity not shown", solo.waitForActivity(ProjectActivity.class.getSimpleName()));
		solo.goBack();
		assertTrue("MyProjectsActivity not shown", solo.waitForActivity(MyProjectsActivity.class.getSimpleName()));
		solo.goBack();
		assertTrue("MainMenuActivity not shown", solo.waitForActivity(MainMenuActivity.class.getSimpleName()));
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		assertTrue("ProjectActivity not shown", solo.waitForActivity(ProjectActivity.class.getSimpleName()));

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

	public void testRateAppMenuExists() {
		solo.sendKey(Solo.MENU);
		assertTrue("App rating menu not found in overflow menu!",
				solo.searchText(solo.getString(R.string.main_menu_rate_app)));
		solo.goBack();
	}

	public void testShouldDisplayDialogIfVersionNumberTooLow() throws Throwable {
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		boolean result = UiTestUtils
				.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_TOO_LOW);
		assertTrue("Could not create test project.", result);

		runTestOnUiThread(new Runnable() {
			public void run() {
				try {
					ProjectManager.getInstance().loadProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, getActivity());
					fail("Load project didn't fail");
				} catch (CompatibilityProjectException compatibilityException) {
					Utils.showErrorDialog(getActivity(), R.string.error_project_compatability);
				} catch (ProjectException projectException) {
					fail("Incompatible project not identified");
				}
			}
		});

		solo.getText(solo.getString(R.string.error_project_compatability), true);
		solo.clickOnButton(0);
		solo.waitForDialogToClose(500);
	}

	public void createTestProject(String projectName) {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");
		Script testScript = new StartScript();
		Script otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(xPosition, yPosition);

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

		ProjectManager.getInstance().setProject(project);
		StorageHandler.getInstance().saveProject(project);
	}

	public void testOverrideMyFirstProject() {
		String standardProjectName = solo.getString(R.string.default_project_name);
		File directory = new File(Constants.DEFAULT_ROOT + "/" + standardProjectName);
		UtilFile.deleteDirectory(directory);
		assertFalse(standardProjectName + " was not deleted!", directory.exists());

		Project standardProject = null;

		try {
			standardProject = StandardProjectHandler.createAndSaveStandardProject(standardProjectName,
					getInstrumentation().getTargetContext());
		} catch (IOException e) {
			Log.e(TAG, "Could not create standard project", e);
			fail("Could not create standard project");
		}

		if (standardProject == null) {
			fail("Could not create standard project");
		}
		ProjectManager.getInstance().setProject(standardProject);
		StorageHandler.getInstance().saveProject(standardProject);

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());

		Sprite backgroundSprite = standardProject.getSpriteList().get(0);
		Script startingScript = backgroundSprite.getScript(0);
		assertEquals("Number of bricks in background sprite was wrong", 3, backgroundSprite.getNumberOfBricks());
		startingScript.addBrick(new SetLookBrick());
		startingScript.addBrick(new SetLookBrick());
		startingScript.addBrick(new SetLookBrick());
		assertEquals("Number of bricks in background sprite was wrong", 6, backgroundSprite.getNumberOfBricks());
		ProjectManager.getInstance().setCurrentSprite(backgroundSprite);
		ProjectManager.getInstance().setCurrentScript(startingScript);
		StorageHandler.getInstance().saveProject(standardProject);

		solo.goBack();
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
		ProjectManager.getInstance().setProject(null);
		solo.getCurrentActivity().startActivity(intent);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.waitForText(solo, solo.getString(R.string.default_project_backgroundname));
		assertEquals("Number of bricks in background sprite was wrong - standard project was overwritten", 6,
				ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getNumberOfBricks());
	}

	public void testProjectNameVisible() {
		createTestProject(testProject);
		createTestProject(testProject2);

		solo.clickOnText(solo.getString(R.string.main_menu_programs));

		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		UiTestUtils.clickOnExactText(solo, testProject);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		assertTrue("The name of the current testProject is not displayed on the continue button", solo.getButton(0)
				.getText().toString().endsWith(testProject));

		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());

		solo.clickOnText(testProject2, 1, true);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		assertTrue("The name of the current testProject2 is not displayed on the continue button", solo.getButton(0)
				.getText().toString().endsWith(testProject2));
	}

	public void testProjectNameWithNormalAndSpecialCharsVisible() {
		createTestProject(projectNameJustSpecialChars);
		createTestProject(projectNameWithNormalAndSpecialChars2);

		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		UiTestUtils.clickOnExactText(solo, projectNameJustSpecialChars);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		assertTrue("The name of the current projectNameJustSpecialChars is not displayed on the continue button", solo
				.getButton(0).getText().toString().endsWith(projectNameJustSpecialChars));

		assertTrue("The name of the current projectNameJustSpecialChars is not displayed on the continue button", solo.searchText(projectNameJustSpecialChars));

		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		UiTestUtils.clickOnExactText(solo, projectNameWithNormalAndSpecialChars2);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		assertTrue(
				"The name of the current projectNameWithNormalAndSpecialChars2 is not displayed on the continue button",
				solo.getButton(0).getText().toString().endsWith(projectNameWithNormalAndSpecialChars2));
	}

	public void testProjectNameWithDotsVisible() {
		createTestProject(projectNameJustOneDot);
		createTestProject(projectNameJustTwoDots);

		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		UiTestUtils.clickOnExactText(solo, projectNameJustOneDot);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(800);
		assertTrue("The name of the current projectNameJustOneDot is not displayed on the continue button", solo
				.getButton(0).getText().toString().endsWith(projectNameJustOneDot));

		solo.sleep(400);
		assertTrue("The name of the current projectNameJustOneDot is not displayed on the continue button", solo.searchText(projectNameJustOneDot));

		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		UiTestUtils.clickOnExactText(solo, projectNameJustTwoDots);
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(800);
		assertTrue("The name of the current projectNameJustTwoDots is not displayed on the continue button", solo
				.getButton(0).getText().toString().endsWith(projectNameJustTwoDots));
	}
}
