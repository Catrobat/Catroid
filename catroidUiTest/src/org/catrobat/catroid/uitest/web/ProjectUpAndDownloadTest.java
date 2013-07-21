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
import java.io.IOException;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.web.ServerCalls;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.FlakyTest;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.EditText;

public class ProjectUpAndDownloadTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TEST_FILE_DOWNLOAD_URL = "http://catroidtest.ist.tugraz.at/catroid/download/";
	private static final int LONG_TEST_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;

	private String testProject = UiTestUtils.PROJECTNAME1;
	private String newTestProject = UiTestUtils.PROJECTNAME2;
	private String testDescription = UiTestUtils.PROJECTDESCRIPTION1;
	private String newTestDescription = UiTestUtils.PROJECTDESCRIPTION2;
	private String saveToken;
	private String uploadDialogTitle;
	private int serverProjectId;

	private Project standardProject;

	public ProjectUpAndDownloadTest() {
		super(MainMenuActivity.class);
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		super.setUp();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Constants.TOKEN, Constants.NO_TOKEN);
		uploadDialogTitle = solo.getString(R.string.upload_project_dialog_title);
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, saveToken).commit();
		super.tearDown();
	}

	private void setServerURLToTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	@FlakyTest(tolerance = 4)
	public void testUploadProjectSuccessAndTokenReplacementAfterUpload() throws Throwable {
		setServerURLToTestUrl();
		createTestProject(testProject);
		addABrickToProject();

		//intent to the main activity is sent since changing activity orientation is not working
		//after executing line "UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);" 
		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		getActivity().startActivity(intent);

		UiTestUtils.createValidUser(getActivity());
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String originalToken = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		uploadProject(newTestProject, newTestDescription);
		String newToken = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);

		assertFalse("Original token not available", originalToken.equals(Constants.NO_TOKEN));
		assertFalse("New token not available", newToken.equals(Constants.NO_TOKEN));
		assertFalse("Original token should be replaced by new token after upload", originalToken.equals(newToken));

		UiTestUtils.clearAllUtilTestProjects();

		//TODO: refactor test method downloadProject()
		//downloadProject();
	}

	@FlakyTest(tolerance = 4)
	public void testUploadProjectOldCatrobatLanguageVersion() throws Throwable {
		setServerURLToTestUrl();

		createTestProject(testProject);
		solo.waitForFragmentById(R.id.fragment_sprites_list);
		solo.sleep(1000);
		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		UiTestUtils.createValidUser(getActivity());

		// change catrobatLanguage to a version that is not supported by web
		// should lead to an errormessage after upload
		Project testProject = ProjectManager.INSTANCE.getCurrentProject();
		testProject.setCatrobatLanguageVersion(0.3f);
		StorageHandler.getInstance().saveProject(testProject);

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadDialogTitle);

		// enter a new title
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, newTestProject);
		solo.goBack();

		// enter a description
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, newTestDescription);

		solo.clickOnButton(solo.getString(R.string.upload_button));

		boolean uploadErrorOccurred = solo.waitForText(solo.getString(R.string.error_project_upload));

		int statusCode = 0;
		int statusCodeWrongLanguageVersion = 518;
		statusCode = (Integer) Reflection.getPrivateField(ServerCalls.getInstance(), "uploadStatusCode");
		Log.v("statusCode=", "" + statusCode);

		assertTrue("Upload did work, but error toastmessage should have been displayed", uploadErrorOccurred);
		assertEquals("Wrong status code from Web", statusCodeWrongLanguageVersion, statusCode);
		UiTestUtils.clearAllUtilTestProjects();
	}

	@FlakyTest(tolerance = 4)
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
		assertTrue("Project description on server was not correctly renamed",
				serverProjectDescription.equalsIgnoreCase(projectDescriptionSetWhenUploading));
	}

	@FlakyTest(tolerance = 4)
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

	@FlakyTest(tolerance = 4)
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

		downloadProjectAndReplace(testProject);
		Project downloadedProject = StorageHandler.getInstance().loadProject(testProject);

		String serverProjectName = downloadedProject.getName();
		assertTrue("Project name on server was changed", serverProjectName.equalsIgnoreCase(testProject));
	}

	@FlakyTest(tolerance = 4)
	public void testDownload() throws Throwable {
		setServerURLToTestUrl();

		String projectName = UiTestUtils.DEFAULT_TEST_PROJECT_NAME;
		UiTestUtils.createTestProject();

		//Adds a sufficient number of media files so that the project is big enough (5 files ~0.4MB) for download-testing
		int numberMediaFiles = 5;
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
		StorageHandler.getInstance().saveProject(ProjectManager.INSTANCE.getCurrentProject());
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

	@FlakyTest(tolerance = 4)
	public void testUploadStandardProject() throws Throwable {
		if (!createAndSaveStandardProject() || this.standardProject == null) {
			fail("Standard project not created");
		}

		setServerURLToTestUrl();
		UiTestUtils.createValidUser(getActivity());

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));

		String uploadButtonText = solo.getString(R.string.upload_button);
		assertTrue("Upload button not found within 5 secs!", solo.waitForText(uploadButtonText, 0, 5000));

		solo.goBack();
		solo.sleep(500);
		solo.clickOnButton(uploadButtonText);

		assertTrue("When uploading a project with the standard project name,  the error message should be shown",
				solo.searchText(solo.getString(R.string.error_upload_project_with_default_name)));

		solo.clickOnButton(solo.getString(R.string.close));

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadButtonText);
		solo.goBack();
		solo.sleep(500);

		while (solo.scrollUp()) {

		}
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.clickOnButton(uploadButtonText);
		solo.waitForDialogToClose(10000);

		assertTrue("Upload of unmodified standard project should not be possible, but succeeded",
				solo.searchText(solo.getString(R.string.error_upload_default_project)));

	}

	@FlakyTest(tolerance = 4)
	public void testUploadModifiedStandardProject() throws Throwable {
		if (!createAndSaveStandardProject() || this.standardProject == null) {
			fail("Standard project not created");
		}

		setServerURLToTestUrl();
		UiTestUtils.createValidUser(getActivity());

		solo.waitForText(solo.getString(R.string.main_menu_continue));
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		solo.waitForText(solo.getString(R.string.default_project_sprites_mole_name) + " 1");
		solo.clickOnText(solo.getString(R.string.default_project_sprites_mole_name) + " 1");

		solo.waitForText(solo.getString(R.string.looks));
		solo.clickOnButton(solo.getString(R.string.looks));

		String deleteLookText = solo.getString(R.string.delete);
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_mole_whacked));
		solo.waitForText(deleteLookText);
		solo.clickOnText(deleteLookText);
		solo.clickOnButton(solo.getString(R.string.yes));

		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		String uploadButtonText = solo.getString(R.string.upload_button);
		solo.waitForText(uploadButtonText);
		solo.goBack();
		solo.waitForText(uploadButtonText);
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.clickOnButton(uploadButtonText);

		assertTrue("Upload of the modified standard project should be possible, but did not succeed",
				solo.waitForText(solo.getString(R.string.success_project_upload), 0, 10000));

	}

	private boolean createAndSaveStandardProject() {
		try {
			standardProject = StandardProjectHandler.createAndSaveStandardProject(
					solo.getString(R.string.default_project_name), getInstrumentation().getTargetContext());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		ProjectManager.INSTANCE.setProject(standardProject);
		StorageHandler.getInstance().saveProject(standardProject);
		return true;
	}

	private void createTestProject(String projectToCreate) {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectToCreate);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		assertFalse("testProject was not deleted!", directory.exists());

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.enterText(0, projectToCreate);
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.enterText(0, "new sprite");
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(2000);

		File file = new File(Constants.DEFAULT_ROOT + "/" + projectToCreate + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(projectToCreate + " was not created!", file.exists());
	}

	private void addABrickToProject() {
		solo.clickInList(0);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		UiTestUtils.goToHomeActivity(getActivity());
	}

	private void uploadProject(String uploadProjectName, String uploadProjectDescription) {
		// change project to a non default state
		Sprite firstSprite = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0);
		Script firstScript = firstSprite.getScript(0);
		firstScript.addBrick(new WaitBrick(firstSprite, 1000));

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadDialogTitle);

		// enter a new title
		EditText projectUploadName = (EditText) solo.getView(R.id.project_upload_name);
		solo.clearEditText(projectUploadName);
		solo.enterText(projectUploadName, uploadProjectName);

		// enter a description
		EditText projectUploadDescription = (EditText) solo.getView(R.id.project_description_upload);
		solo.clearEditText(projectUploadDescription);
		solo.enterText(projectUploadDescription, uploadProjectDescription);

		solo.clickOnButton(solo.getString(R.string.upload_button));
		solo.sleep(500);

		boolean success = solo.waitForText(solo.getString(R.string.success_project_upload), 1, 50000);
		assertTrue("Upload failed. Internet connection?", success);
		String resultString = (String) Reflection.getPrivateField(ServerCalls.getInstance(), "resultString");

		try {
			JSONObject jsonObject;
			jsonObject = new JSONObject(resultString);
			serverProjectId = jsonObject.optInt("projectId");
			Log.v("serverID=", "" + serverProjectId);

		} catch (JSONException e) {
			fail("JSON exception orrured");
		}
	}

	private void downloadProjectAndReplace(String projectName) {
		String downloadUrl = TEST_FILE_DOWNLOAD_URL + serverProjectId + Constants.CATROBAT_EXTENTION;
		downloadUrl += "?fname=" + projectName;

		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(downloadUrl));
		solo.goBack();
		launchActivityWithIntent("org.catrobat.catroid", MainMenuActivity.class, intent);
		solo.sleep(500);
		assertTrue("OverwriteRenameDialog not shown.", solo.searchText(solo.getString(R.string.overwrite_text)));
		solo.clickOnText(solo.getString(R.string.overwrite_replace));
		solo.clickOnButton(solo.getString(R.string.ok));

		boolean waitResult = solo.waitForActivity("MainMenuActivity", 10000);
		assertTrue("Download takes too long.", waitResult);
		assertTrue("Download not successful.", solo.searchText(solo.getString(R.string.success_project_download)));
		assertEquals("Testproject not loaded.", projectName, ProjectManager.INSTANCE.getCurrentProject().getName());

		String projectPath = Constants.DEFAULT_ROOT + "/" + projectName;
		File downloadedDirectory = new File(projectPath);
		File downloadedProjectFile = new File(projectPath + "/" + Constants.PROJECTCODE_NAME);
		assertTrue("Original Directory does not exist.", downloadedDirectory.exists());
		assertTrue("Original Project File does not exist.", downloadedProjectFile.exists());
	}

	@SuppressWarnings("unused")
	private void downloadProject() {
		String downloadUrl = TEST_FILE_DOWNLOAD_URL + serverProjectId + Constants.CATROBAT_EXTENTION;
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
