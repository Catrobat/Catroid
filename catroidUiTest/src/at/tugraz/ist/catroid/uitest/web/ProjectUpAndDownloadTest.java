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
package at.tugraz.ist.catroid.uitest.web;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.web.ServerCalls;

import com.jayway.android.robotium.solo.Solo;

public class ProjectUpAndDownloadTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final String TEST_FILE_DOWNLOAD_URL = "http://catroidtest.ist.tugraz.at/catroid/download/";

	private Solo solo;
	private String testProject = UiTestUtils.PROJECTNAME1;
	private String newTestProject = UiTestUtils.PROJECTNAME2;
	private String testDescription = UiTestUtils.PROJECTDESCRIPTION1;
	private String newTestDescription = UiTestUtils.PROJECTDESCRIPTION2;
	private String saveToken;
	private int serverProjectId;

	public ProjectUpAndDownloadTest() {
		super(MainMenuActivity.class);
		UiTestUtils.clearAllUtilTestProjects();
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Constants.TOKEN, "0");
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, saveToken).commit();
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private void setServerURLToTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	public void testUploadProjectSuccess() throws Throwable {
		setServerURLToTestUrl();

		createTestProject(testProject);
		addABrickToProject();

		//intent to the main activity is sent since changing activity orientation is not working
		//after executing line "UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);" 
		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		getActivity().startActivity(intent);

		UiTestUtils.createValidUser(getActivity());

		uploadProject(newTestProject, newTestDescription);

		UiTestUtils.clearAllUtilTestProjects();

		downloadProject();
	}

	public void testRenameProjectNameAndDescriptionWhenUploading() throws Throwable {
		setServerURLToTestUrl();

		String originalProjectName = testProject;
		String originalProjectDescription = testDescription;
		createTestProject(originalProjectName);
		ProjectManager.INSTANCE.getCurrentProject().setDescription(originalProjectDescription);

		//intent to the main activity is sent since changing activity orientation is not working
		//after executing line "UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);" 
		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		getActivity().startActivity(intent);

		UiTestUtils.createValidUser(getActivity());

		//Project name and description are renamed to testproject2 and testdescription2 in uploadProject()
		String projectNameSetWhenUploading = newTestProject;
		String projectDescriptionSetWhenUploading = newTestDescription;
		uploadProject(newTestProject, newTestDescription);
		solo.sleep(5000);

		Project uploadProject = StorageHandler.getInstance().loadProject(newTestProject);

		String DeserializedProjectName = uploadProject.getName();
		String DeserializedProjectDescription = uploadProject.getDescription();
		assertTrue("Deserialized project name was not renamed correctly",
				DeserializedProjectName.equalsIgnoreCase(projectNameSetWhenUploading));
		assertTrue("Deserialized project description was not renamed correctly",
				DeserializedProjectDescription.equalsIgnoreCase(projectDescriptionSetWhenUploading));

		UiTestUtils.clearAllUtilTestProjects();

		//Download replaces project. Name and description should be testproject2 and testdescription2
		downloadProjectAndReplace(newTestProject);
		Project downloadedProject = StorageHandler.getInstance().loadProject(newTestProject);

		String serverProjectName = downloadedProject.getName();
		String serverProjectDescription = downloadedProject.getDescription();
		assertTrue("Project name on server was not correctly renamed",
				serverProjectName.equalsIgnoreCase(projectNameSetWhenUploading));
		assertTrue("Project name on server was not correctly renamed",
				serverProjectDescription.equalsIgnoreCase(projectDescriptionSetWhenUploading));
	}

	public void testRenameProjectDescriptionWhenUploading() throws Throwable {
		setServerURLToTestUrl();

		String projectName = testProject;
		String originalProjectDescription = testDescription;
		createTestProject(projectName);
		ProjectManager.INSTANCE.getCurrentProject().setDescription(originalProjectDescription);

		//intent to the main activity is sent since changing activity orientation is not working
		//after executing line "UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);" 
		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		getActivity().startActivity(intent);

		UiTestUtils.createValidUser(getActivity());

		//Project description is changed to testdescription2 in uploadProject()
		String projectDescriptionSetWhenUploading = newTestDescription;
		uploadProject(projectName, newTestDescription);
		solo.sleep(5000);

		Project uploadProject = StorageHandler.getInstance().loadProject(projectName);

		String DeserializedProjectName = uploadProject.getName();
		String DeserializedProjectDescription = uploadProject.getDescription();
		assertTrue("Deserialized project name was changed", DeserializedProjectName.equalsIgnoreCase(projectName));
		assertTrue("Deserialized project description was not renamed correctly",
				DeserializedProjectDescription.equalsIgnoreCase(projectDescriptionSetWhenUploading));

		UiTestUtils.clearAllUtilTestProjects();

		//Download replaces project. Name and description should be testproject1 and testdescription2
		downloadProjectAndReplace(projectName);
		Project downloadedProject = StorageHandler.getInstance().loadProject(projectName);

		String serverProjectName = downloadedProject.getName();
		String serverProjectDescription = downloadedProject.getDescription();
		assertTrue("Project name on server was changed", serverProjectName.equalsIgnoreCase(projectName));
		assertTrue("Project name on server was not correctly renamed",
				serverProjectDescription.equalsIgnoreCase(projectDescriptionSetWhenUploading));
	}

	private void createTestProject(String projectToCreate) {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectToCreate);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		assertFalse("testProject was not deleted!", directory.exists());

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.enterText(0, projectToCreate);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(2000);

		File file = new File(Constants.DEFAULT_ROOT + "/" + projectToCreate + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(projectToCreate + " was not created!", file.exists());
	}

	private void addABrickToProject() {
		solo.clickInList(0);
		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		UiTestUtils.goToHomeActivity(getActivity());
	}

	private void uploadProject(String uploadProjectName, String uploadProjectDescription) {
		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(500);

		// enter a new title
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, uploadProjectName);

		// enter a description
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, uploadProjectDescription);

		//		solo.setActivityOrientation(Solo.LANDSCAPE);

		solo.clickOnButton(getActivity().getString(R.string.upload_button));

		solo.sleep(500);

		try {
			solo.setActivityOrientation(Solo.LANDSCAPE);

			solo.waitForDialogToClose(10000);
			assertTrue("Upload failed. Internet connection?",
					solo.searchText(getActivity().getString(R.string.success_project_upload)));
			String resultString = (String) UiTestUtils.getPrivateField("resultString", ServerCalls.getInstance());
			JSONObject jsonObject;
			jsonObject = new JSONObject(resultString);
			serverProjectId = jsonObject.optInt("projectId");

			solo.clickOnButton(0);
		} catch (JSONException e) {
			fail("JSON exception orrured");
		}
	}

	private void downloadProjectAndReplace(String projectName) {
		String downloadUrl = TEST_FILE_DOWNLOAD_URL + serverProjectId + Constants.CATROID_EXTENTION;
		downloadUrl += "?fname=" + projectName;

		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(downloadUrl));
		launchActivityWithIntent("at.tugraz.ist.catroid", MainMenuActivity.class, intent);

		boolean waitResult = solo.waitForActivity("MainMenuActivity", 10000);
		assertTrue("Download takes too long.", waitResult);
		assertTrue("Testproject not loaded.", solo.searchText(projectName));
		assertTrue("OverwriteRenameDialog not showed.",
				solo.searchText(getActivity().getString(R.string.overwrite_text)));

		solo.clickOnText(getActivity().getString(R.string.overwrite_replace));
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("Download not successful.",
				solo.searchText(getActivity().getString(R.string.success_project_download)));

		String projectPath = Constants.DEFAULT_ROOT + "/" + projectName;
		File downloadedDirectory = new File(projectPath);
		File downloadedProjectFile = new File(projectPath + "/" + Constants.PROJECTCODE_NAME);
		assertTrue("Original Directory does not exist.", downloadedDirectory.exists());
		assertTrue("Original Project File does not exist.", downloadedProjectFile.exists());
	}

	private void downloadProject() {
		String downloadUrl = TEST_FILE_DOWNLOAD_URL + serverProjectId + Constants.CATROID_EXTENTION;
		downloadUrl += "?fname=" + newTestProject;

		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(downloadUrl));
		launchActivityWithIntent("at.tugraz.ist.catroid", MainMenuActivity.class, intent);

		boolean waitResult = solo.waitForActivity("MainMenuActivity", 10000);
		assertTrue("Download takes too long.", waitResult);
		assertTrue("Testproject2 not loaded.", solo.searchText(newTestProject));
		assertTrue("OverwriteRenameDialog not showed.",
				solo.searchText(getActivity().getString(R.string.overwrite_text)));

		solo.clickOnText(getActivity().getString(R.string.overwrite_rename));
		assertTrue("No text field to enter new name.", solo.searchEditText(newTestProject));
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("No error showed because of duplicate names.",
				solo.searchText(getActivity().getString(R.string.error_project_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("Download not successful.",
				solo.searchText(getActivity().getString(R.string.success_project_download)));

		String projectPath = Constants.DEFAULT_ROOT + "/" + testProject;
		File downloadedDirectory = new File(projectPath);
		File downloadedProjectFile = new File(projectPath + "/" + Constants.PROJECTCODE_NAME);
		assertTrue("Downloaded Directory does not exist.", downloadedDirectory.exists());
		assertTrue("Downloaded Project File does not exist.", downloadedProjectFile.exists());

		projectPath = Constants.DEFAULT_ROOT + "/" + newTestProject;
		downloadedDirectory = new File(projectPath);
		downloadedProjectFile = new File(projectPath + "/" + Constants.PROJECTCODE_NAME);
		assertTrue("Original Directory does not exist.", downloadedDirectory.exists());
		assertTrue("Original Project File does not exist.", downloadedProjectFile.exists());
	}
}
