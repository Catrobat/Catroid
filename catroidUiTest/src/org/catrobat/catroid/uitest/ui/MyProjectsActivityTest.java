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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.fragment.ProjectsListFragment.ProjectData;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class MyProjectsActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private final String INVALID_PROJECT_MODIFIER = "invalidProject";
	private final int IMAGE_RESOURCE_1 = org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int IMAGE_RESOURCE_2 = org.catrobat.catroid.uitest.R.drawable.background_white;
	private final int IMAGE_RESOURCE_3 = org.catrobat.catroid.uitest.R.drawable.background_black;
	private final int IMAGE_RESOURCE_4 = org.catrobat.catroid.uitest.R.drawable.background_green;
	private final int IMAGE_RESOURCE_5 = org.catrobat.catroid.uitest.R.drawable.background_red;
	private final static String MY_PROJECTS_ACTIVITY_TEST_TAG = MyProjectsActivityTest.class.getSimpleName();
	private final String ZIPFILE_NAME = "testzip";

	private Solo solo;
	private File renameDirectory = null;
	private boolean unzip;
	private boolean deleteCacheProjects = false;
	private int numberOfCacheProjects = 27;
	private String cacheProjectName = "cachetestProject";

	// temporarily removed - because of upcoming release, and bad performance of projectdescription	
	//	private final String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus consequat lacinia ante, ut sollicitudin est hendrerit ut. Nunc at hendrerit mauris. Morbi tincidunt eleifend ligula, eget gravida ante fermentum vitae. Cras dictum nunc non quam posuere dignissim. Etiam vel gravida lacus. Vivamus facilisis, nunc sit amet placerat rutrum, nisl orci accumsan odio, vitae pretium ipsum urna nec ante. Donec scelerisque viverra felis a varius. Sed lacinia ultricies mi, eu euismod leo ultricies eu. Nunc eleifend dignissim nulla eget dictum. Quisque mi eros, faucibus et pretium a, tempor et libero. Etiam dui felis, ultrices id gravida quis, tempor a turpis.Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Aliquam consequat velit eu elit adipiscing eu feugiat sapien euismod. Nunc sollicitudin rhoncus velit nec malesuada. Donec velit quam, luctus in sodales eu, viverra vitae massa. Aenean sed dolor sapien, et lobortis lacus. Proin a est vitae metus fringilla malesuada. Pellentesque eu adipiscing diam. Maecenas massa ante, tincidunt volutpat dapibus vitae, mollis in enim. Sed dictum dolor ultricies metus varius sit amet scelerisque lacus convallis. Nullam dui nisl, mollis a molestie non, tempor vitae arcu. Phasellus vitae metus pellentesque ligula scelerisque adipiscing vitae sed quam. Quisque porta rhoncus magna a porttitor. In ac magna nulla. Donec quis lacus felis, in bibendum massa. ";
	private final String lorem = "Lorem ipsum dolor sit amet";

	public MyProjectsActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		unzip = false;
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		ProjectManager.getInstance().deleteCurrentProject();
		UiTestUtils.clearAllUtilTestProjects();
		if (renameDirectory != null && renameDirectory.isDirectory()) {
			UtilFile.deleteDirectory(renameDirectory);
			renameDirectory = null;
		}
		if (deleteCacheProjects) {
			for (int i = 0; i < numberOfCacheProjects; i++) {
				File directory = new File(Utils.buildProjectPath(cacheProjectName + i));
				UtilFile.deleteDirectory(directory);
			}
			deleteCacheProjects = false;
		}

		if (unzip) {
			unzipProjects();
		}
		super.tearDown();
		solo = null;
	}

	public void saveProjectsToZip() {
		File directory;
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		String[] paths = rootDirectory.list();

		if (paths == null) {
			fail("could not determine catroid directory");
		}

		for (int i = 0; i < paths.length; i++) {
			paths[i] = Utils.buildPath(rootDirectory.getAbsolutePath(), paths[i]);
		}
		try {
			String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);
			File zipFile = new File(zipFileString);
			if (zipFile.exists()) {
				zipFile.delete();
			}
			zipFile.getParentFile().mkdirs();
			zipFile.createNewFile();
			if (!UtilZip.writeToZipFile(paths, zipFileString)) {
				zipFile.delete();
			}
		} catch (IOException e) {
			fail("IOException while zipping projects");
		}

		for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
			directory = new File(Constants.DEFAULT_ROOT + "/" + projectName);
			if (directory.exists()) {
				UtilFile.deleteDirectory(directory);
			}
		}
	}

	public void unzipProjects() {
		String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);
		File zipFile = new File(zipFileString);
		UtilZip.unZipFile(zipFileString, Constants.DEFAULT_ROOT);
		zipFile.delete();
	}

	public void testOrientation() throws NameNotFoundException {
		/// Method 1: Assert it is currently in portrait mode.
		solo.waitForActivity(MainMenuActivity.class.getSimpleName(), 1000);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertEquals("MyProjectsActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, solo
				.getCurrentActivity().getResources().getConfiguration().orientation);

		/// Method 2: Retreive info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = solo.getCurrentActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(solo.getCurrentActivity().getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);

		assertEquals(MyProjectsActivity.class.getSimpleName()
				+ " not set to be in portrait mode in AndroidManifest.xml!", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
				activityInfo.screenOrientation);
	}

	public void testDeleteSprite() {
		try {
			StandardProjectHandler.createAndSaveStandardProject(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Standard Project not created");
		}

		Project activeProject = ProjectManager.INSTANCE.getCurrentProject();
		ArrayList<LookData> catroidLookList = activeProject.getSpriteList().get(1).getLookDataList();

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.clickOnTextInList(solo, solo.getString(R.string.default_project_name));
		solo.sleep(200);
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_pocketcode_name));
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.delete));
		solo.sleep(1000);

		File imageFile;

		for (LookData currentLookData : catroidLookList) {
			imageFile = new File(currentLookData.getAbsolutePath());
			assertFalse("Imagefile should be deleted", imageFile.exists());
		}
	}

	public void testInvalidProject() {
		unzip = true;
		saveProjectsToZip();
		try {
			StandardProjectHandler.createAndSaveStandardProject(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Standard Project not created");
		}
		UiTestUtils.createTestProject();
		solo.sleep(200);

		String myProjectsText = solo.getString(R.string.main_menu_programs);
		solo.clickOnButton(myProjectsText);
		UiTestUtils.clickOnTextInList(solo, solo.getString(R.string.default_project_name));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		if (!solo.waitForText(solo.getString(R.string.new_sprite_dialog_default_sprite_name), 0, 5000)) {
			fail("Edit-Dialog not shown in 5 secs!");
		}
		solo.enterText(0, "testSprite");
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(500);
		solo.goBack();

		corruptProjectXML(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		solo.sleep(200);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName(), 1000);
		solo.clickOnButton(myProjectsText);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		solo.clickOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		solo.sleep(200);
		assertTrue("No error message was shown", solo.searchText(solo.getString(R.string.error_load_project)));

		solo.clickOnButton(0);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertTrue("Default Project should not be overwritten", spriteList.size() == 3);
	}

	public void testDeleteStandardProject() {
		unzip = true;
		saveProjectsToZip();
		try {
			StandardProjectHandler.createAndSaveStandardProject(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Standard Project not created");
		}
		UiTestUtils.createTestProject();
		solo.sleep(200);

		String myProjectsText = solo.getString(R.string.main_menu_programs);
		String standardProjectName = solo.getString(R.string.default_project_name);
		solo.clickOnButton(myProjectsText);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("click on project '" + standardProjectName + "' in list not successful",
				UiTestUtils.clickOnTextInList(solo, standardProjectName));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		if (!solo.waitForText(solo.getString(R.string.new_sprite_dialog_default_sprite_name), 0, 5000)) {
			fail("Edit-Dialog not shown in 5 secs!");
		}
		solo.enterText(0, "testSprite");
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(500);
		solo.goBack();

		corruptProjectXML(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		solo.sleep(200);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName(), 1000);
		solo.clickOnButton(myProjectsText);

		assertTrue("longclick on project '" + standardProjectName + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, standardProjectName));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("delete dialog not closed in time", solo.waitForDialogToClose(5000));

		if (!solo.waitForView(ListView.class, 0, 5000)) {
			fail("ListView not shown in 5 secs!");
		}

		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnButton(myProjectsText);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		solo.sleep(200);
		assertTrue("click on project '" + standardProjectName + "' in list not successful",
				UiTestUtils.clickOnTextInList(solo, standardProjectName));

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertTrue("Standard Project should be restored", spriteList.size() == 2);
	}

	public void testProjectsAndImagesVisible() {
		createProjects();
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("activity doesn't show the project " + UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 1, true));
		assertTrue("activity doesn't show the project " + UiTestUtils.PROJECTNAME1,
				solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));

		int currentViewID;
		int pixelColor;
		int expectedImageWidth = getActivity().getResources().getDimensionPixelSize(R.dimen.project_thumbnail_width);
		int expectedImageHeigth = getActivity().getResources().getDimensionPixelSize(R.dimen.project_thumbnail_height);
		int imageViewID = R.id.my_projects_activity_project_image;
		Bitmap viewBitmap;
		int counter = 0;
		for (View viewToTest : solo.getCurrentViews()) {
			currentViewID = viewToTest.getId();
			if (imageViewID == currentViewID) {
				counter++;
				viewToTest.buildDrawingCache();
				viewBitmap = viewToTest.getDrawingCache();
				switch (counter) {
					case 1:
						pixelColor = viewBitmap.getPixel(1, 1);
						assertEquals("Image color should be white",
								solo.getCurrentActivity().getResources().getColor(R.color.white), pixelColor);

						assertEquals("Image is not scaled right", expectedImageWidth, viewBitmap.getWidth());
						assertEquals("Image is not scaled right", expectedImageHeigth, viewBitmap.getHeight());
						break;
					case 2:
						pixelColor = viewBitmap.getPixel(1, 1);
						assertEquals("Image color should be black",
								solo.getCurrentActivity().getResources().getColor(R.color.solid_black), pixelColor);
						assertEquals("Image is not scaled right", expectedImageWidth, viewBitmap.getWidth());
						assertEquals("Image is not scaled right", expectedImageHeigth, viewBitmap.getHeight());
						break;
					default:
						break;
				}
			}
		}
		if (counter == 0) {
			fail("no imageviews tested");
		}
	}

	public void testImageCache() {
		deleteCacheProjects = true;

		//create first cache test project and set it as current project 
		Project firstCacheTestProject = new Project(getActivity(), "cachetestProject" + 0);
		StorageHandler.getInstance().saveProject(firstCacheTestProject);
		UiTestUtils.saveFileToProject(cacheProjectName + 0, "screenshot.png", IMAGE_RESOURCE_2, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.ROOT);
		ProjectManager.getInstance().setProject(firstCacheTestProject);

		for (int i = 1; i < numberOfCacheProjects; i++) {
			StorageHandler.getInstance().saveProject(new Project(getActivity(), "cachetestProject" + i));
			UiTestUtils.saveFileToProject(cacheProjectName + i, "screenshot.png", IMAGE_RESOURCE_2,
					getInstrumentation().getContext(), UiTestUtils.FileTypes.ROOT);
		}

		UiTestUtils.saveFileToProject(cacheProjectName + 24, "screenshot.png", IMAGE_RESOURCE_2, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.ROOT);
		ProjectManager.getInstance().setProject(firstCacheTestProject);
		UiTestUtils.saveFileToProject(cacheProjectName + 26, "screenshot.png", IMAGE_RESOURCE_3, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.ROOT);

		Log.v(MY_PROJECTS_ACTIVITY_TEST_TAG, "before sleep");
		solo.sleep(100);
		Log.v(MY_PROJECTS_ACTIVITY_TEST_TAG, "after sleep");
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		Log.v(MY_PROJECTS_ACTIVITY_TEST_TAG, "after intent");
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		Log.v(MY_PROJECTS_ACTIVITY_TEST_TAG, "activity visible");

		solo.scrollToBottom();
		Log.v(MY_PROJECTS_ACTIVITY_TEST_TAG, "scroll bottom");
		solo.scrollToTop();
		Log.v(MY_PROJECTS_ACTIVITY_TEST_TAG, "scroll up");
		solo.sleep(300);
		int currentViewID;
		int pixelColor;
		int imageViewID = R.id.my_projects_activity_project_image;
		Bitmap viewBitmap;
		int counter = 0;
		for (View viewToTest : solo.getCurrentViews()) {
			currentViewID = viewToTest.getId();
			if (imageViewID == currentViewID) {
				counter++;
				viewToTest.buildDrawingCache();
				viewBitmap = viewToTest.getDrawingCache();
				switch (counter) {
					case 1:
						pixelColor = viewBitmap.getPixel(1, 1);
						assertEquals("Image color should be white",
								solo.getCurrentActivity().getResources().getColor(R.color.white), pixelColor);
						break;
					case 2:
						pixelColor = viewBitmap.getPixel(1, 1);
						assertEquals("Image color should be black",
								solo.getCurrentActivity().getResources().getColor(R.color.solid_black), pixelColor);
						break;
					case 3:
						pixelColor = viewBitmap.getPixel(1, 1);
						assertEquals("Image color should be white",
								solo.getCurrentActivity().getResources().getColor(R.color.white), pixelColor);
						break;
					default:
						break;
				}
			}
		}
	}

	public void testDeleteProject() {
		createProjects();
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("delete dialog not closed in time", solo.waitForDialogToClose(5000));

		assertFalse("project " + UiTestUtils.PROJECTNAME1 + " is still visible",
				solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		ArrayList<String> projectList = (ArrayList<String>) UtilFile.getProjectNames(rootDirectory);
		boolean projectDeleted = true;
		for (String project : projectList) {
			if (project.equalsIgnoreCase(UiTestUtils.PROJECTNAME1)) {
				projectDeleted = false;
			}
		}
		assertTrue("project " + UiTestUtils.PROJECTNAME1 + " not deleted", projectDeleted);
	}

	public void testDeleteCurrentProject() {
		createProjects();
		solo.sleep(200);
		//current project is UiTestUtils.DEFAULT_TEST_PROJECT_NAME
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("delete dialog not closed in time", solo.waitForDialogToClose(5000));

		assertFalse("project " + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + " is still visible",
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 1, true));
		assertTrue("project " + UiTestUtils.PROJECTNAME1 + " is not visible anymore",
				solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));
		assertNotSame("the deleted project is still the current project", UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				ProjectManager.INSTANCE.getCurrentProject().getName());
	}

	public void testDeleteAllProjects() {
		unzip = true;
		saveProjectsToZip();
		createProjects();
		solo.sleep(200);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);

		String defaultProjectName = solo.getString(R.string.default_project_name);
		String buttonDeleteText = solo.getString(R.string.delete);

		//delete default project if exists:
		if (UiTestUtils.longClickOnTextInList(solo, defaultProjectName)) {
			solo.clickOnText(buttonDeleteText);
			assertTrue("delete dialog not closed in time", solo.waitForDialogToClose(5000));
		}

		//delete first project
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(buttonDeleteText);
		assertTrue("delete dialog not closed in time", solo.waitForDialogToClose(5000));
		ProjectManager projectManager = ProjectManager.getInstance();
		assertFalse("project " + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + " is still visible",
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 1));
		assertTrue("project " + UiTestUtils.PROJECTNAME1 + " is not visible anymore",
				solo.searchText(UiTestUtils.PROJECTNAME1, 1));
		assertNotSame("the deleted project is still the current project", UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				projectManager.getCurrentProject().getName());
		assertEquals(UiTestUtils.PROJECTNAME1 + " should be the current project", UiTestUtils.PROJECTNAME1,
				projectManager.getCurrentProject().getName());

		//delete second project
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(buttonDeleteText);
		assertTrue("delete dialog not closed in time", solo.waitForDialogToClose(5000));
		assertFalse("project " + UiTestUtils.PROJECTNAME1 + " is still visible",
				solo.searchText(UiTestUtils.PROJECTNAME1, 1));
		assertNotSame("the deleted project is still the current project", UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				projectManager.getCurrentProject().getName());

		assertTrue("default project not visible", solo.searchText(defaultProjectName));
		assertEquals("the current project is not the default project", defaultProjectName, projectManager
				.getCurrentProject().getName());
	}

	public void testRenameProject() {
		createProjects();
		solo.sleep(200);
		String buttonPositiveText = solo.getString(R.string.ok);
		String actionRenameText = solo.getString(R.string.rename);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(actionRenameText);
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.PROJECTNAME3);
		solo.clickOnText(buttonPositiveText);
		solo.sleep(300);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME3, 1, true));
		assertFalse("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));
		assertEquals("the renamed project is not first in list", ((ProjectData) (solo.getCurrentListViews().get(0)
				.getAdapter().getItem(0))).projectName, UiTestUtils.PROJECTNAME3);

		solo.scrollToTop();
		solo.sleep(300);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(actionRenameText);
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.PROJECTNAME1);
		solo.clickOnText(buttonPositiveText);
		solo.sleep(300);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));
		assertFalse("rename wasnt successfull", solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 1, true));
		assertEquals("the renamed project is not first in list", ((ProjectData) (solo.getCurrentListViews().get(0)
				.getAdapter().getItem(0))).projectName, UiTestUtils.PROJECTNAME1);
	}

	public void testRenameCurrentProject() {
		createProjects();
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.PROJECTNAME3);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.sleep(300);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME3, 1, true));
		solo.goBack();
		assertEquals("current project not updated", UiTestUtils.PROJECTNAME3, ProjectManager.getInstance()
				.getCurrentProject().getName());
	}

	public void testRenameCurrentProjectMixedCase() {
		createProjects();
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.DEFAULT_TEST_PROJECT_NAME_MIXED_CASE);
		solo.sendKey(Solo.ENTER);
		solo.sleep(300);

		assertTrue("rename to Mixed Case was not successfull",
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME_MIXED_CASE, 1, true));
		solo.sleep(200);
		solo.goBack();
		assertEquals("current project not updated", UiTestUtils.DEFAULT_TEST_PROJECT_NAME_MIXED_CASE, ProjectManager
				.getInstance().getCurrentProject().getName());
	}

	public void testRenameToSameName() {
		createProjects();
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		solo.assertCurrentActivity("Should be My Projects Activity", MyProjectsActivity.class);
		assertEquals("Should not be renamed", UiTestUtils.DEFAULT_TEST_PROJECT_NAME, ProjectManager.getInstance()
				.getCurrentProject().getName());
	}

	public void testRenameWithNoInput() {
		createProjects();
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		String errorMessageInvalidInput = solo.getString(R.string.notification_invalid_text_entered);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageInvalidInput));
		solo.clickOnButton(solo.getString(R.string.close));
	}

	public void testRenameProjectWithWhitelistedCharacters() {
		createProjects();
		solo.sleep(200);
		final String renameString = "[Hey+, =lo_ok. I'm; -special! too!]";
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.enterText(0, renameString);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForDialogToClose(500);
		renameDirectory = new File(Utils.buildProjectPath(renameString));
		assertTrue("Rename with whitelisted characters was not successfull", renameDirectory.isDirectory());
	}

	public void testRenameProjectWithBlacklistedCharacters() {
		createProjects();
		solo.sleep(200);
		final String renameString = "<H/ey,\", :I'\\m s*pe?ci>al! ?äö|üß<>";
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.enterText(0, renameString);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForDialogToClose(500);
		renameDirectory = new File(Utils.buildProjectPath(renameString));
		assertTrue("Rename with blacklisted characters was not successfull", renameDirectory.isDirectory());
	}

	public void testRenameProjectWithOnlyBlacklistedCharacters() {
		createProjects();
		solo.sleep(200);
		final String renameString = "<>?*|";
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.enterText(0, renameString);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForDialogToClose(500);
		String errorMessageProjectExists = solo.getString(R.string.error_project_exists);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageProjectExists));
		solo.clickOnButton(solo.getString(R.string.close));
	}

	public void testRenameToExistingProjectMixedCase() {
		createProjects();
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.DEFAULT_TEST_PROJECT_NAME_MIXED_CASE);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.waitForDialogToClose(500);
		String errorMessageProjectExists = solo.getString(R.string.error_project_exists);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageProjectExists));
		solo.goBack();
	}

	public void testAddNewProject() {
		createProjects();
		solo.sleep(200);
		String buttonMyProjectsText = solo.getString(R.string.main_menu_programs);
		String buttonOkText = solo.getString(R.string.ok);
		String buttonCloseText = solo.getString(R.string.close);

		solo.clickOnButton(buttonMyProjectsText);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);

		EditText addNewProjectEditText = solo.getEditText(0);
		assertEquals("Not the proper hint set", solo.getString(R.string.new_project_dialog_hint),
				addNewProjectEditText.getHint());
		assertEquals("There should no text be set", "", addNewProjectEditText.getText().toString());
		solo.sleep(100);

		solo.enterText(0, UiTestUtils.PROJECTNAME1);
		solo.sleep(100);
		solo.clickOnButton(buttonOkText);

		solo.sleep(200);
		String errorMessageProjectExists = solo.getString(R.string.error_project_exists);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageProjectExists));
		solo.sleep(100);
		solo.clickOnButton(buttonCloseText);
		solo.sleep(100);

		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.PROJECTNAME2);
		solo.sleep(200);
		solo.clickOnButton(buttonOkText);

		solo.sleep(200);
		solo.assertCurrentActivity("not in projectactivity", ProjectActivity.class);
		assertEquals("current project not updated", UiTestUtils.PROJECTNAME2, ProjectManager.getInstance()
				.getCurrentProject().getName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("not in MainMenuActivity after goBack from ProjectActivity", MainMenuActivity.class);
		solo.clickOnButton(buttonMyProjectsText);
		assertTrue("project " + UiTestUtils.PROJECTNAME2 + " was not added",
				solo.searchText(UiTestUtils.PROJECTNAME2, 1, true));
	}

	public void testAddNewProjectMixedCase() {
		createProjects();
		solo.sleep(200);
		String buttonOkText = solo.getString(R.string.ok);
		String buttonCloseText = solo.getString(R.string.close);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.sleep(200);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);
		solo.enterText(0, UiTestUtils.DEFAULT_TEST_PROJECT_NAME_MIXED_CASE);
		solo.sleep(200);
		solo.clickOnButton(buttonOkText);

		solo.sleep(200);
		assertTrue("No or wrong error message shown", solo.searchText(solo.getString(R.string.error_project_exists)));
		solo.sleep(100);
		solo.clickOnButton(buttonCloseText);
		solo.sleep(100);
		solo.clickOnButton(buttonOkText);
		assertTrue("No or wrong error message shown", solo.searchText(solo.getString(R.string.error_project_exists)));
		solo.clickOnButton(buttonCloseText);
	}

	public void testSetDescriptionCurrentProject() {
		createProjects();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(300);
		String actionSetDescriptionText = solo.getString(R.string.set_description);
		String setDescriptionDialogTitle = solo.getString(R.string.description);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		assertTrue("context menu not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clickOnText(actionSetDescriptionText);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		solo.clearEditText(0);
		solo.enterText(0, lorem);
		solo.sleep(300);
		solo.sendKey(Solo.ENTER);
		solo.sendKey(Solo.ENTER);
		solo.waitForDialogToClose(500);

		// temporarily removed - should be added when displaying projectdescription
		//		assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum"));
		//		assertTrue("description is not shown in activity", solo.searchText("ultricies"));
		UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		assertTrue("context menu not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clickOnText(actionSetDescriptionText);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum"));
		assertTrue("description is not set in project", ProjectManager.INSTANCE.getCurrentProject().getDescription()
				.equalsIgnoreCase(lorem));
	}

	public void testSetDescription() {
		createProjects();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(300);
		String actionSetDescriptionText = solo.getString(R.string.set_description);
		String setDescriptionDialogTitle = solo.getString(R.string.description);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1);
		assertTrue("context menu not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clickOnText(actionSetDescriptionText);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		solo.clearEditText(0);
		solo.enterText(0, lorem);
		solo.sleep(300);
		solo.sendKey(Solo.ENTER);
		solo.sendKey(Solo.ENTER);
		solo.waitForDialogToClose(500);

		// temporarily removed - should be added when displaying projectdescription
		//		assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum"));
		//		assertTrue("description is not shown in activity", solo.searchText("ultricies"));
		assertEquals("The project is not first in list",
				((ProjectData) (solo.getCurrentListViews().get(0).getAdapter().getItem(0))).projectName,
				UiTestUtils.PROJECTNAME1);

		UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1);
		assertTrue("context menu not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clickOnText(actionSetDescriptionText);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		assertTrue("description is not shown in edittext", solo.searchText("Lorem ipsum"));
		ProjectManager.INSTANCE.loadProject(UiTestUtils.PROJECTNAME1, getActivity(), true);
		assertTrue("description is not set in project", ProjectManager.INSTANCE.getCurrentProject().getDescription()
				.equalsIgnoreCase(lorem));
	}

	public void testCopyCurrentProject() {
		createProjects();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(200);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("click on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.clickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(solo.getString(R.string.new_sprite_dialog_title));
		solo.clearEditText(0);
		solo.enterText(0, "testSprite");
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		solo.sleep(300);

		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.copy));
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.COPIED_PROJECT_NAME);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		Project oldProject = ProjectManager.getInstance().getCurrentProject();
		ArrayList<LookData> lookDataListOldProject = oldProject.getSpriteList().get(1).getLookDataList();
		LookData lookDataOldProject = lookDataListOldProject.get(0);
		String oldChecksum = lookDataOldProject.getChecksum();

		solo.sleep(200);
		assertTrue("click on project '" + UiTestUtils.COPIED_PROJECT_NAME + "' in list not successful",
				UiTestUtils.clickOnTextInList(solo, UiTestUtils.COPIED_PROJECT_NAME));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		assertTrue("project " + UiTestUtils.COPIED_PROJECT_NAME + " was not added",
				solo.searchText(UiTestUtils.COPIED_PROJECT_NAME, 1, true));
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertTrue("The copied project should have all sprites from the source", spriteList.size() == 3);
		assertTrue("The sprite name should be: 'testSprite'", solo.searchText("testSprite", 1, false));

		Project copiedProject = ProjectManager.getInstance().getCurrentProject();
		ArrayList<LookData> lookDataListCopiedProject = copiedProject.getSpriteList().get(1).getLookDataList();
		LookData lookDataCopiedProject = lookDataListCopiedProject.get(0);
		String copiedLookChecksum = lookDataCopiedProject.getChecksum();

		assertTrue("Checksum should be the same", oldChecksum.equals(copiedLookChecksum));
	}

	public void testCopyProject() {
		createProjects();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(200);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(solo.getString(R.string.copy));
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.COPIED_PROJECT_NAME);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		assertTrue("Did not copy the selected project", solo.searchText(UiTestUtils.COPIED_PROJECT_NAME, true));
	}

	public void testCopyProjectMixedCase() {
		createProjects();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(200);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.copy));
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.DEFAULT_TEST_PROJECT_NAME_MIXED_CASE);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		String errorMessageProjectExists = solo.getString(R.string.error_project_exists);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageProjectExists));
		solo.clickOnButton(solo.getString(R.string.close));
	}

	public void testCopyProjectNoName() {
		createProjects();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(200);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME));
		solo.clickOnText(solo.getString(R.string.copy));
		solo.clearEditText(0);
		solo.enterText(0, " ");
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		String notificationEmptyString = solo.getString(R.string.notification_invalid_text_entered);
		assertTrue("No or wrong error message shown", solo.searchText(notificationEmptyString));
		solo.clickOnButton(solo.getString(R.string.close));
	}

	public void testCopyProjectWithOnlyBlacklistedCharacters() {
		createProjects();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(200);
		final String copyProjectString = "<>?*|";
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("longclick on project '" + UiTestUtils.PROJECTNAME1 + "' in list not successful",
				UiTestUtils.longClickOnTextInList(solo, UiTestUtils.PROJECTNAME1));
		solo.clickOnText(solo.getString(R.string.copy));
		solo.clearEditText(0);
		solo.enterText(0, copyProjectString);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.sleep(200);
		String errorMessageProjectExists = solo.getString(R.string.error_project_exists);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageProjectExists));
		solo.clickOnButton(solo.getString(R.string.close));
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
				IMAGE_RESOURCE_1, getActivity(), UiTestUtils.FileTypes.IMAGE);

		ArrayList<LookData> lookDataList = projectManager.getCurrentSprite().getLookDataList();
		LookData lookData = new LookData();
		lookData.setLookFilename(imageFile.getName());
		lookData.setLookName("testname");
		lookDataList.add(lookData);
		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		StorageHandler.getInstance().saveProject(project1);

		//-------------------------------------------------

		Project project2 = new Project(getActivity(), UiTestUtils.PROJECTNAME1);
		StorageHandler.getInstance().saveProject(project2);

		UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "screenshot.png", IMAGE_RESOURCE_2,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.ROOT);

		UiTestUtils.saveFileToProject(UiTestUtils.PROJECTNAME1, "screenshot.png", IMAGE_RESOURCE_3,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.ROOT);
	}

	private void playTheProject(boolean switchGreenToRed, boolean switchRedToGreen, boolean makeScreenshot) {

		solo.clickOnText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.scripts));
		if (switchGreenToRed) {
			solo.clickOnText("backgroundGreen");
			solo.clickOnText("backgroundRed");
		}

		if (switchRedToGreen) {
			solo.clickOnText("backgroundRed");
			solo.clickOnText("backgroundGreen");
		}

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		if (makeScreenshot) {
			solo.goBack();
			solo.clickOnText(solo.getString(R.string.stage_dialog_screenshot));
			solo.goBack();
		} else {
			solo.goBack();
			solo.goBack();
		}

		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.sleep(500);
	}

	private int createScreenshotBitmap() {

		Bitmap viewBitmap;
		int currentViewID;
		int imageViewID = R.id.my_projects_activity_project_image;
		int pixel = -1;

		ArrayList<View> currentViews = solo.getCurrentViews();
		int viewSize = currentViews.size();

		for (int i = 0; i < viewSize; i++) {
			View viewToTest = currentViews.get(i);
			currentViewID = viewToTest.getId();
			if (currentViewID == imageViewID) { // Only stop at Image View...
				TextView textView = (TextView) currentViews.get(i + 1);
				if (textView.getText().equals(UiTestUtils.DEFAULT_TEST_PROJECT_NAME)) { // ...and check if it belongs to the test project

					viewToTest.buildDrawingCache();
					viewBitmap = viewToTest.getDrawingCache();
					pixel = viewBitmap.getPixel(1, viewBitmap.getHeight() / 2);
					viewToTest.destroyDrawingCache();
				}
			}
		}
		return pixel;
	}

	public void testScreenshotUpdate() {
		createProjectWithBackgrounds();

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		playTheProject(false, false, false); // green to green
		int greenPixel1 = createScreenshotBitmap();
		String greenHexValue = "ff00ff00";
		String redHexValue = "ffff0000";
		String pixelHexValue = Integer.toHexString(greenPixel1);
		assertEquals("The extracted pixel was not green", greenHexValue, pixelHexValue);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		playTheProject(true, false, false); // green to red
		int redPixel1 = createScreenshotBitmap();
		pixelHexValue = Integer.toHexString(redPixel1);
		assertEquals("The extracted pixel was not red", redHexValue, pixelHexValue);
		assertFalse("The screenshot has not been changed", greenPixel1 == redPixel1);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		playTheProject(false, true, true);// red to green + screenshot
		int greenPixel2 = createScreenshotBitmap();
		pixelHexValue = Integer.toHexString(greenPixel2);
		assertEquals("The extracted pixel was not green", greenHexValue, pixelHexValue);
		assertFalse("The screenshot has not been changed", redPixel1 == greenPixel2);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		playTheProject(true, false, false); // green to red, screenshot must stay green
		int greenPixel3 = createScreenshotBitmap();
		pixelHexValue = Integer.toHexString(greenPixel3);
		assertEquals("The extracted pixel was not green", greenHexValue, pixelHexValue);
		assertTrue("The screenshot has not been changed", greenPixel2 == greenPixel3);
	}

	private void createProjectWithBackgrounds() {

		LookData backgroundGreen;
		LookData backgroundRed;
		ProjectManager projectManager = ProjectManager.getInstance();

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createEmptyProject();

		File imageFile1 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "screenshot.png",
				IMAGE_RESOURCE_4, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		File imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "screenshot.png",
				IMAGE_RESOURCE_5, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);

		ArrayList<LookData> lookDataList = projectManager.getCurrentSprite().getLookDataList();

		backgroundGreen = new LookData();
		backgroundGreen.setLookFilename(imageFile1.getName());
		backgroundGreen.setLookName("backgroundGreen");
		lookDataList.add(backgroundGreen);

		projectManager.getFileChecksumContainer().addChecksum(backgroundGreen.getChecksum(),
				backgroundGreen.getAbsolutePath());

		backgroundRed = new LookData();
		backgroundRed.setLookFilename(imageFile2.getName());
		backgroundRed.setLookName("backgroundRed");
		lookDataList.add(backgroundRed);

		projectManager.getFileChecksumContainer().addChecksum(backgroundRed.getChecksum(),
				backgroundRed.getAbsolutePath());

		SetLookBrick setBackgroundBrick = new SetLookBrick(projectManager.getCurrentSprite());
		projectManager.getCurrentScript().addBrick(setBackgroundBrick);
		setBackgroundBrick.setLook(backgroundGreen);
	}

	private void corruptProjectXML(String projectName) {
		String projectPath = Utils.buildPath(Constants.DEFAULT_ROOT, projectName, Constants.PROJECTCODE_NAME);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(projectPath);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			outputStreamWriter.write(INVALID_PROJECT_MODIFIER);
			outputStreamWriter.flush();
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("CATROID", e.toString());
			fail("corrupting project failed due to IOException");
		}
	}

}
