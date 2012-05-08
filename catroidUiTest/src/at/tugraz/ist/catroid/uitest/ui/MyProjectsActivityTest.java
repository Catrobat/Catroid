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
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.MyProjectsActivity.ProjectData;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class MyProjectsActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private final int IMAGE_RESOURCE_1 = at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int IMAGE_RESOURCE_2 = at.tugraz.ist.catroid.uitest.R.drawable.background_white;
	private final int IMAGE_RESOURCE_3 = at.tugraz.ist.catroid.uitest.R.drawable.background_black;
	private Solo solo;
	private final String ZIPFILE_NAME = "testzip";
	private File renameDirectory = null;
	private boolean unzip;
	private boolean deleteCacheProjects = false;
	private int numberOfCacheProjects = 27;
	private String cacheProjectName = "cachetestProject";

	// temporarily removed - because of upcoming release, and bad performance of projectdescription	
	//	private final String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus consequat lacinia ante, ut sollicitudin est hendrerit ut. Nunc at hendrerit mauris. Morbi tincidunt eleifend ligula, eget gravida ante fermentum vitae. Cras dictum nunc non quam posuere dignissim. Etiam vel gravida lacus. Vivamus facilisis, nunc sit amet placerat rutrum, nisl orci accumsan odio, vitae pretium ipsum urna nec ante. Donec scelerisque viverra felis a varius. Sed lacinia ultricies mi, eu euismod leo ultricies eu. Nunc eleifend dignissim nulla eget dictum. Quisque mi eros, faucibus et pretium a, tempor et libero. Etiam dui felis, ultrices id gravida quis, tempor a turpis.Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Aliquam consequat velit eu elit adipiscing eu feugiat sapien euismod. Nunc sollicitudin rhoncus velit nec malesuada. Donec velit quam, luctus in sodales eu, viverra vitae massa. Aenean sed dolor sapien, et lobortis lacus. Proin a est vitae metus fringilla malesuada. Pellentesque eu adipiscing diam. Maecenas massa ante, tincidunt volutpat dapibus vitae, mollis in enim. Sed dictum dolor ultricies metus varius sit amet scelerisque lacus convallis. Nullam dui nisl, mollis a molestie non, tempor vitae arcu. Phasellus vitae metus pellentesque ligula scelerisque adipiscing vitae sed quam. Quisque porta rhoncus magna a porttitor. In ac magna nulla. Donec quis lacus felis, in bibendum massa. ";
	private final String lorem = "Lorem ipsum dolor sit amet";

	public MyProjectsActivityTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() {
		unzip = false;
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
			if (!zipFile.exists()) {
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
			}
			if (!UtilZip.writeToZipFile(paths, zipFileString)) {
				zipFile.delete();
			}
		} catch (IOException e) {
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

	public void testProjectsAndImagesVisible() {
		createProjects();
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
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
		solo.sleep(100);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);

		while (solo.scrollDown()) {
			;
		}
		while (solo.scrollUp()) {
			;
		}
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
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete));
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
		//current project is UiTestUtils.DEFAULT_TEST_PROJECT_NAME
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete));
		ProjectManager projectManager = ProjectManager.getInstance();
		assertFalse("project " + UiTestUtils.DEFAULT_TEST_PROJECT_NAME + " is still visible",
				solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 1, true));
		assertTrue("project " + UiTestUtils.PROJECTNAME1 + " is not visible anymore",
				solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));
		assertNotSame("the deleted project is still the current project", UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				projectManager.getCurrentProject().getName());
	}

	public void testDeleteAllProjects() {
		unzip = true;
		saveProjectsToZip();
		createProjects();

		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);

		String defaultProjectName = getActivity().getString(R.string.default_project_name);

		//delete default project if exists:
		if (solo.searchText(defaultProjectName, 1)) {
			solo.clickLongOnText(defaultProjectName);
			solo.sleep(100);
			solo.clickOnText(getActivity().getString(R.string.delete));
		}

		//delete first project
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete), 1);
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
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1, 2);
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.delete), 1);
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
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1, 1, true);
		solo.clickOnText(getActivity().getString(R.string.rename), 1, true);
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME3);
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.ok), 1, true);
		solo.sleep(200);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME3, 1, true));
		assertFalse("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));
		assertEquals("the renamed project is not first in list", ((ProjectData) (solo.getCurrentListViews().get(0)
				.getAdapter().getItem(0))).projectName, UiTestUtils.PROJECTNAME3);

		while (solo.scrollUp()) {
			;
		}

		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2, true);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME1);
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.ok), 1, true);
		solo.sleep(200);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME1, 1, true));
		assertFalse("rename wasnt successfull", solo.searchText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 1, true));
		assertEquals("the renamed project is not first in list", ((ProjectData) (solo.getCurrentListViews().get(0)
				.getAdapter().getItem(0))).projectName, UiTestUtils.PROJECTNAME1);
	}

	public void testRenameCurrentProject() {
		createProjects();
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(300);
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2, true);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME3);
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.ok));
		solo.sleep(200);
		assertTrue("rename wasnt successfull", solo.searchText(UiTestUtils.PROJECTNAME3, 1, true));
		solo.goBack();
		assertEquals("current project not updated", UiTestUtils.PROJECTNAME3, ProjectManager.getInstance()
				.getCurrentProject().getName());
	}

	public void testRenameProjectWithWhitelistedCharacters() {
		createProjects();
		final String renameString = "[Hey+, =lo_ok. I'm; -special! too!]";
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1, 1, true);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, renameString);
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.ok));
		solo.waitForDialogToClose(2000);
		renameDirectory = new File(Utils.buildProjectPath(renameString));
		assertTrue("Rename with whitelisted characters was not successfull", renameDirectory.isDirectory());
	}

	public void testRenameProjectWithBlacklistedCharacters() {
		createProjects();
		final String renameString = "<H/ey,\", :I'\\m s*pe?ci>al! ?äö|üß<>";
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1, 1, true);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, renameString);
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.ok));
		solo.waitForDialogToClose(2000);
		renameDirectory = new File(Utils.buildProjectPath(renameString));
		assertTrue("Rename with blacklisted characters was not successfull", renameDirectory.isDirectory());
	}

	public void testAddNewProject() {
		createProjects();
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.sleep(200);
		EditText addNewProjectEditText = solo.getEditText(0);
		assertEquals("Not the proper hint set", getActivity().getString(R.string.new_project_dialog_hint),
				addNewProjectEditText.getHint());
		assertEquals("There should no text be set", "", addNewProjectEditText.getText().toString());
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		assertTrue("Dialog is not visible", solo.searchText(getActivity().getString(R.string.ok)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME2);
		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(200);
		solo.assertCurrentActivity("not in projectactivity", ProjectActivity.class);
		assertEquals("current project not updated", UiTestUtils.PROJECTNAME2, ProjectManager.getInstance()
				.getCurrentProject().getName());
		solo.goBack();
		solo.sleep(200);
		solo.assertCurrentActivity("not in MainMenuActivity after goBack from ProjectActivity", MainMenuActivity.class);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		assertTrue("project " + UiTestUtils.PROJECTNAME2 + " was not added",
				solo.searchText(UiTestUtils.PROJECTNAME2, 1, true));
	}

	public void testAddNewProject2() {
		createProjects();
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.sleep(200);
		UiTestUtils.enterText(solo, 0, UiTestUtils.PROJECTNAME1);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		assertTrue("No or wrong error message shown",
				solo.searchText(getActivity().getString(R.string.error_project_exists)));
		solo.sleep(100);
		solo.clickOnButton(getActivity().getString(R.string.close));
		solo.goBack();
		solo.sleep(100);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("No or wrong error message shown",
				solo.searchText(getActivity().getString(R.string.error_project_exists)));
	}

	public void testSetDescriptionCurrentProject() {
		createProjects();
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.set_description));
		solo.sleep(200);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		UiTestUtils.enterText(solo, 0, lorem);
		solo.clickOnButton(0);
		solo.sleep(500);
		ProjectManager projectManager = ProjectManager.getInstance();
		// temporarily removed - should be added when displaying projectdescription
		//		assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum"));
		//		assertTrue("description is not shown in activity", solo.searchText("ultricies"));
		solo.clickLongOnText(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, 2);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.set_description));
		solo.sleep(200);
		assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum"));
		assertTrue("description is not set in project",
				projectManager.getCurrentProject().description.equalsIgnoreCase(lorem));
	}

	public void testSetDescription() {
		createProjects();
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(200);
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1, 1);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.set_description));
		solo.sleep(200);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		UiTestUtils.enterText(solo, 0, lorem);
		solo.sleep(300);
		solo.clickOnButton(0);
		solo.sleep(500);
		ProjectManager projectManager = ProjectManager.getInstance();
		// temporarily removed - should be added when displaying projectdescription
		//		assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum"));
		//		assertTrue("description is not shown in activity", solo.searchText("ultricies"));
		solo.clickLongOnText(UiTestUtils.PROJECTNAME1, 1);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.set_description));
		solo.sleep(200);
		assertTrue("description is not shown in activity", solo.searchText("Lorem ipsum"));
		projectManager.loadProject(UiTestUtils.PROJECTNAME1, getActivity(), true);
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
				IMAGE_RESOURCE_1, getActivity(), UiTestUtils.FileTypes.IMAGE);

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

		UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "screenshot.png", IMAGE_RESOURCE_2,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.ROOT);

		UiTestUtils.saveFileToProject(UiTestUtils.PROJECTNAME1, "screenshot.png", IMAGE_RESOURCE_3,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.ROOT);
	}
}
