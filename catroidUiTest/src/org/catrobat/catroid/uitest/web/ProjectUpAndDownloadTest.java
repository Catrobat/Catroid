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
package org.catrobat.catroid.uitest.web;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.web.ServerCalls;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public class ProjectUpAndDownloadTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final String TEST_FILE_DOWNLOAD_URL = "http://catroidtest.ist.tugraz.at/catroid/download/";
	private static final int LONG_TEST_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;

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
		solo = null;
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

		//TODO: refactor test method downloadProject()
		//downloadProject();
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

	public void testUpAndDownloadJapaneseUnicodeProject() throws Throwable {
		setServerURLToTestUrl();

		String testProject = UiTestUtils.JAPANESE_PROJECT_NAME;
		createTestProject(testProject);

		//intent to the main activity is sent since changing activity orientation is not working
		//after executing line "UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);" 
		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		getActivity().startActivity(intent);

		UiTestUtils.createValidUser(getActivity());

		uploadProject(testProject, "");
		solo.sleep(5000);

		Project uploadProject = StorageHandler.getInstance().loadProject(testProject);
		String DeserializedProjectName = uploadProject.getName();
		assertTrue("Deserialized project name was changed", DeserializedProjectName.equalsIgnoreCase(testProject));

		UiTestUtils.clearAllUtilTestProjects();

		downloadProjectAndReplace(testProject);
		Project downloadedProject = StorageHandler.getInstance().loadProject(testProject);

		String serverProjectName = downloadedProject.getName();
		assertTrue("Project name on server was changed", serverProjectName.equalsIgnoreCase(testProject));
	}

	public void testDownload() throws Throwable {
		setServerURLToTestUrl();

		String projectName = UiTestUtils.DEFAULT_TEST_PROJECT_NAME;
		UiTestUtils.createTestProject();

		//Adds a sufficient number of media files so that the project is big enough (16 files ~1MB) for download-testing
		int numberMediaFiles = 10;
		String soundName = "testSound";

		ArrayList<SoundInfo> soundInfoList = ProjectManager.INSTANCE.getCurrentSprite().getSoundList();
		for (int number = 0; number < numberMediaFiles; number++) {
			File soundFile = UiTestUtils.saveFileToProject(projectName,
					"longsound" + Integer.toString(number) + ".mp3", LONG_TEST_SOUND,
					getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setSoundFileName(soundFile.getName());
			soundInfo.setTitle(soundName + Integer.toString(number));
			soundInfoList.add(soundInfo);
			ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(),
					soundInfo.getAbsolutePath());
		}
		ProjectManager.INSTANCE.saveProject();
		Project newProject = StorageHandler.getInstance().loadProject(projectName);
		ProjectManager.INSTANCE.setProject(newProject);

		UiTestUtils.createValidUser(getActivity());
		uploadProject(projectName, "");

		Project uploadProject = StorageHandler.getInstance().loadProject(projectName);
		String deserializedProjectName = uploadProject.getName();
		assertTrue("Project was successfully uploaded", deserializedProjectName.equalsIgnoreCase(projectName));
		UiTestUtils.clearAllUtilTestProjects();

		downloadProjectAndReplace(projectName);
		Project downloadedProject = StorageHandler.getInstance().loadProject(projectName);
		String serverProjectName = downloadedProject.getName();
		assertTrue("Project was successfully downloaded", serverProjectName.equalsIgnoreCase(projectName));
	}

	private void createTestProject(String projectToCreate) {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectToCreate);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		assertFalse("testProject was not deleted!", directory.exists());

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.enterText(0, projectToCreate);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(2000);

		File file = new File(Constants.DEFAULT_ROOT + "/" + projectToCreate + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(projectToCreate + " was not created!", file.exists());
	}

	private void addABrickToProject() {
		solo.clickInList(0);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		UiTestUtils.goToHomeActivity(getActivity());
	}

	private void uploadProject(String uploadProjectName, String uploadProjectDescription) {
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.sleep(500);

		// enter a new title
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, uploadProjectName);

		// enter a description
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, uploadProjectDescription);

		solo.clickOnButton(solo.getString(R.string.upload_button));
		solo.sleep(500);

		try {
			boolean success = solo.waitForText(solo.getString(R.string.success_project_upload));
			assertTrue("Upload failed. Internet connection?", success);
			String resultString = (String) UiTestUtils.getPrivateField("resultString", ServerCalls.getInstance());
			JSONObject jsonObject;
			jsonObject = new JSONObject(resultString);
			serverProjectId = jsonObject.optInt("projectId");
			Log.v("serverID=", "" + serverProjectId);

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
		launchActivityWithIntent("org.catrobat.catroid", MainMenuActivity.class, intent);
		solo.sleep(500);
		assertTrue("OverwriteRenameDialog not shown.", solo.searchText(solo.getString(R.string.overwrite_text)));
		solo.clickOnText(solo.getString(R.string.overwrite_replace));
		solo.clickOnButton(solo.getString(R.string.ok));

		boolean waitResult = solo.waitForActivity("MainMenuActivity", 10000);
		assertTrue("Download takes too long.", waitResult);
		assertTrue("Download not successful.", solo.searchText(solo.getString(R.string.success_project_download)));
		assertEquals("Testproject not loaded.", projectName, ProjectManager.getInstance().getCurrentProject().getName());

		String projectPath = Constants.DEFAULT_ROOT + "/" + projectName;
		File downloadedDirectory = new File(projectPath);
		File downloadedProjectFile = new File(projectPath + "/" + Constants.PROJECTCODE_NAME);
		assertTrue("Original Directory does not exist.", downloadedDirectory.exists());
		assertTrue("Original Project File does not exist.", downloadedProjectFile.exists());
	}

	@SuppressWarnings("unused")
	private void downloadProject() {
		String downloadUrl = TEST_FILE_DOWNLOAD_URL + serverProjectId + Constants.CATROID_EXTENTION;
		downloadUrl += "?fname=" + newTestProject;

		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(downloadUrl));
		launchActivityWithIntent("org.catrobat.catroid", MainMenuActivity.class, intent);

		solo.sleep(5000);
		assertTrue("OverwriteRenameDialog not shown.", solo.searchText(solo.getString(R.string.overwrite_text)));
		solo.clickOnText(solo.getString(R.string.overwrite_rename));
		assertTrue("No text field to enter new name.", solo.searchEditText(newTestProject));

		/*
		 * TODO: Does not work when testing, but it works in practice
		 * solo.clickOnButton(solo.getString(R.string.ok));
		 * solo.sleep(500);
		 * assertTrue("No error shown because of duplicate names.",
		 * solo.searchText(solo.getString(R.string.error_project_exists)));
		 * solo.sleep(500);
		 * solo.clickOnButton(solo.getString(R.string.close));
		 */

		solo.sleep(500);
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.clickOnButton(solo.getString(R.string.ok));

		boolean waitResult = solo.waitForActivity("MainMenuActivity", 10000);
		assertTrue("Download takes too long.", waitResult);
		assertTrue("Download not successful.", solo.searchText(solo.getString(R.string.success_project_download)));
		assertTrue("Testproject2 not loaded.", solo.searchText(newTestProject));

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
