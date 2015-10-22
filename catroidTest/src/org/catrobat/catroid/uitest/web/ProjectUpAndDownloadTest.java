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
package org.catrobat.catroid.uitest.web;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.web.ServerCalls;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectUpAndDownloadTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final String TEST_FILE_DOWNLOAD_URL = ServerCalls.BASE_URL_TEST_HTTP + "catroid/download/";
	private static final int LONG_TEST_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private final String testProject = UiTestUtils.PROJECTNAME1;
	private final String newTestProject = UiTestUtils.PROJECTNAME2;
	private final String testDescription = UiTestUtils.PROJECTDESCRIPTION1;
	private final String newTestDescription = UiTestUtils.PROJECTDESCRIPTION2;
	private final String offensiveLanguageDescription = UiTestUtils.PROJECTNAMEOFFENSIVELANGUAGE;
	private String saveToken;
	private String uploadDialogTitle;
	private int serverProjectId;

	private float currentLanguageVersion;

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
		currentLanguageVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, saveToken).commit();
		UiTestUtils.cancelAllNotifications(getActivity());
		Reflection.setPrivateField(Constants.class, "CURRENT_CATROBAT_LANGUAGE_VERSION", currentLanguageVersion);
		super.tearDown();
	}

	private void setServerURLToTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	public void testUploadProjectSuccessAndTokenReplacementAfterUpload() throws Throwable {
		setServerURLToTestUrl();
		UiTestUtils.createTestProject(testProject);

		UiTestUtils.createValidUser(getActivity());
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String originalToken = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		uploadProjectFromMainMenu(newTestProject, newTestDescription);
		String newToken = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);

		assertFalse("Original token not available", originalToken.equals(Constants.NO_TOKEN));
		assertFalse("New token not available", newToken.equals(Constants.NO_TOKEN));
		assertFalse("Original token should be replaced by new token after upload", originalToken.equals(newToken));

		UiTestUtils.clearAllUtilTestProjects();

		downloadProject(newTestProject, testProject);
	}

	public void testUploadProjectOldCatrobatLanguageVersion() throws Throwable {
		setServerURLToTestUrl();

		UiTestUtils.createTestProject(testProject);
		solo.waitForFragmentById(R.id.fragment_sprites_list);
		solo.sleep(1000);
		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		UiTestUtils.createValidUser(getActivity());

		// change catrobatLanguage to a version that is not supported by web
		// should lead to an errormessage after upload
		Project testProject = ProjectManager.getInstance().getCurrentProject();
		Reflection.setPrivateField(Constants.class, "CURRENT_CATROBAT_LANGUAGE_VERSION", 0.3f);

		StorageHandler.getInstance().saveProject(testProject);

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadDialogTitle);

		// enter a new title
		solo.clearEditText(0);
		solo.enterText(0, newTestProject);

		// enter a description
		solo.clearEditText(1);
		solo.enterText(1, newTestDescription);

		solo.clickOnButton(solo.getString(R.string.upload_button));

		boolean uploadErrorOccurred = solo.waitForText(solo.getString(R.string.error_project_upload));

		assertTrue("Upload did work, but error toastmessage should have been displayed", uploadErrorOccurred);
		UiTestUtils.clearAllUtilTestProjects();
	}

	public void testUploadProjectOffensiveLanguageUsed() throws Throwable {
		setServerURLToTestUrl();

		UiTestUtils.createTestProject(testProject);
		solo.waitForFragmentById(R.id.fragment_sprites_list);
		solo.sleep(1000);
		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		UiTestUtils.createValidUser(getActivity());
		Project testProject = ProjectManager.getInstance().getCurrentProject();
		StorageHandler.getInstance().saveProject(testProject);

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadDialogTitle);

		// enter a new title
		solo.clearEditText(0);
		solo.enterText(0, offensiveLanguageDescription);

		// enter a description
		solo.clearEditText(1);
		solo.enterText(1, newTestDescription);

		solo.clickOnButton(solo.getString(R.string.upload_button));

		boolean uploadErrorOccurred = solo.waitForText(solo.getString(R.string.error_project_upload));

		assertTrue("Upload did work, but error toastmessage should have been displayed", uploadErrorOccurred);
		UiTestUtils.clearAllUtilTestProjects();
	}

	public void testRenameProjectNameAndDescriptionWhenUploading() throws Throwable {
		setServerURLToTestUrl();

		String originalProjectName = testProject;
		String originalProjectDescription = testDescription;
		UiTestUtils.createTestProject(originalProjectName);
		ProjectManager.getInstance().getCurrentProject().setDescription(originalProjectDescription);

		UiTestUtils.createValidUser(getActivity());

		//Project name and description are renamed to testproject2 and testdescription2 in uploadProject()
		String projectNameSetWhenUploading = newTestProject;
		String projectDescriptionSetWhenUploading = newTestDescription;
		uploadProjectFromMainMenu(newTestProject, newTestDescription);
		solo.sleep(5000);

		Project uploadProject = StorageHandler.getInstance().loadProject(newTestProject);

		String deserializedProjectName = uploadProject.getName();
		String deserializedProjectDescription = uploadProject.getDescription();
		assertTrue("Deserialized project name was not renamed correctly",
				deserializedProjectName.equalsIgnoreCase(projectNameSetWhenUploading));
		assertTrue("Deserialized project description was not renamed correctly",
				deserializedProjectDescription.equalsIgnoreCase(projectDescriptionSetWhenUploading));

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

	public void testRenameProjectDescriptionWhenUploading() throws Throwable {
		setServerURLToTestUrl();

		UiTestUtils.createTestProject(testProject);
		ProjectManager.getInstance().getCurrentProject().setDescription(testDescription);

		UiTestUtils.createValidUser(getActivity());

		//Project description is changed to testdescription2 in uploadProject()
		uploadProjectFromMainMenu(testProject, newTestDescription);
		solo.sleep(5000);

		checkProjectNameAndDescriptionBeforAndAfterDownload(testProject, newTestDescription);
	}

	public void testUploadingFromProgrammList() throws Throwable {
		setServerURLToTestUrl();

		UiTestUtils.createTestProject(testProject);
		ProjectManager.getInstance().getCurrentProject().setDescription(testDescription);

		UiTestUtils.createValidUser(getActivity());

		uploadProjectFromProgrammList(testProject, newTestDescription);
		solo.sleep(5000);

		checkProjectNameAndDescriptionBeforAndAfterDownload(testProject, newTestDescription);
	}

	public void testUploadFromProgramm() throws Throwable {
		setServerURLToTestUrl();

		UiTestUtils.createTestProject(testProject);
		ProjectManager.getInstance().getCurrentProject().setDescription(testDescription);

		UiTestUtils.createValidUser(getActivity());

		uploadProjectFromProgramm(testProject, newTestDescription);
		solo.sleep(5000);

		checkProjectNameAndDescriptionBeforAndAfterDownload(testProject, newTestDescription);
	}

	public void testUpAndDownloadJapaneseUnicodeProject() throws Throwable {
		setServerURLToTestUrl();

		String testProject = UiTestUtils.JAPANESE_PROJECT_NAME;
		UiTestUtils.createTestProject(testProject);

		UiTestUtils.createValidUser(getActivity());

		uploadProjectFromMainMenu(testProject, "");

		try {
			ProjectManager.getInstance().loadProject(testProject, getActivity());
			assertTrue("Load project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Project is not loaded successfully");
		}

		Project uploadProject = StorageHandler.getInstance().loadProject(testProject);
		assertEquals("Deserialized project name was changed", testProject, uploadProject.getName());

		downloadProjectAndReplace(testProject);
		Project downloadedProject = StorageHandler.getInstance().loadProject(testProject);

		String serverProjectName = downloadedProject.getName();
		assertTrue("Project name on server was changed", serverProjectName.equalsIgnoreCase(testProject));
	}

	public void testDownload() throws Throwable {
		setServerURLToTestUrl();

		String projectName = UiTestUtils.DEFAULT_TEST_PROJECT_NAME;
		UiTestUtils.createTestProject();

		//Adds a sufficient number of media files so that the project is big enough (5 files ~0.4MB) for download-testing
		int numberMediaFiles = 5;
		String soundName = "testSound";

		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		for (int number = 0; number < numberMediaFiles; number++) {
			File soundFile = UiTestUtils.saveFileToProject(projectName,
					"longsound" + Integer.toString(number) + ".mp3", LONG_TEST_SOUND,
					getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setSoundFileName(soundFile.getName());
			soundInfo.setTitle(soundName + Integer.toString(number));
			soundInfoList.add(soundInfo);
			ProjectManager.getInstance().getFileChecksumContainer()
					.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		}
		StorageHandler.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
		Project newProject = StorageHandler.getInstance().loadProject(projectName);
		ProjectManager.getInstance().setProject(newProject);

		UiTestUtils.createValidUser(getActivity());
		uploadProjectFromMainMenu(projectName, "");

		Project uploadProject = StorageHandler.getInstance().loadProject(projectName);
		String deserializedProjectName = uploadProject.getName();
		assertTrue("Project was successfully uploaded", deserializedProjectName.equalsIgnoreCase(projectName));
		UiTestUtils.clearAllUtilTestProjects();

		downloadProjectAndReplace(projectName);
		Project downloadedProject = StorageHandler.getInstance().loadProject(projectName);
		String serverProjectName = downloadedProject.getName();
		assertTrue("Project was successfully downloaded", serverProjectName.equalsIgnoreCase(projectName));
	}

	public void testUploadStandardProject() throws Throwable {
		if (UiTestUtils.deleteOldAndCreateAndSaveCleanStandardProject(getActivity(), getInstrumentation()) == null) {
			fail("StandardProject Not created!");
		}

		setServerURLToTestUrl();
		UiTestUtils.createValidUser(getActivity());

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));

		String uploadButtonText = solo.getString(R.string.upload_button);
		assertTrue("Upload button not found within 5 secs!", solo.waitForText(uploadButtonText, 0, 5000));

		solo.sleep(500);
		solo.clickOnButton(uploadButtonText);

		assertTrue("When uploading a project with the standard project name,  the error message should be shown",
				solo.searchText(solo.getString(R.string.error_upload_project_with_default_name)));

		solo.clickOnButton(solo.getString(R.string.close));

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadButtonText);
		solo.sleep(500);

		solo.scrollToTop();

		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.clickOnButton(uploadButtonText);
		solo.waitForDialogToClose(10000);

		assertTrue("Upload of unmodified standard project should not be possible, but succeeded",
				solo.searchText(solo.getString(R.string.error_upload_default_project)));
	}

	@Device
	public void testUploadModifiedStandardProject() throws Throwable {
		if (UiTestUtils.deleteOldAndCreateAndSaveCleanStandardProject(getActivity(), getInstrumentation()) == null) {
			fail("StandardProject Not created!");
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

		UiTestUtils.clickOnHomeActionBarButton(solo);

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		String uploadButtonText = solo.getString(R.string.upload_button);
		solo.waitForText(uploadButtonText);
		solo.hideSoftKeyboard();
		solo.waitForText(uploadButtonText);
		solo.clearEditText(0);
		solo.enterText(0, testProject);
		solo.clickOnButton(uploadButtonText);

		assertTrue("Upload of the modified standard project should be possible, but did not succeed",
				solo.waitForText(solo.getString(R.string.notification_upload_finished), 0, 15000));
	}

	public void testDownloadProjectAfterModification() throws Throwable {
		setServerURLToTestUrl();

		String projectName = UiTestUtils.DEFAULT_TEST_PROJECT_NAME;
		UiTestUtils.createTestProject();

		int numberOfMediaFilesToExtentDownloadTime = 5;
		String soundName = "testSound";

		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		for (int number = 0; number < numberOfMediaFilesToExtentDownloadTime; number++) {
			File soundFile = UiTestUtils.saveFileToProject(projectName,
					"longsound" + Integer.toString(number) + ".mp3", LONG_TEST_SOUND,
					getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setSoundFileName(soundFile.getName());
			soundInfo.setTitle(soundName + Integer.toString(number));
			soundInfoList.add(soundInfo);
			ProjectManager.getInstance().getFileChecksumContainer()
					.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		}
		StorageHandler.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
		Project newProject = StorageHandler.getInstance().loadProject(projectName);
		ProjectManager.getInstance().setProject(newProject);

		UiTestUtils.createValidUser(getActivity());
		uploadProjectFromMainMenu(projectName, "");

		Project uploadProject = StorageHandler.getInstance().loadProject(projectName);
		String deserializedProjectName = uploadProject.getName();
		assertTrue("Project was successfully uploaded", deserializedProjectName.equalsIgnoreCase(projectName));

		List<Sprite> spriteList = uploadProject.getSpriteList();

		UiTestUtils.clearAllUtilTestProjects();

		solo.sleep(500);

		soundInfoList.remove(0);

		int numberOfSounds = soundInfoList.size();
		assertEquals("Number of sounds has not changed after deletion", numberOfMediaFilesToExtentDownloadTime - 1,
				numberOfSounds);

		downloadProjectAndReplace(projectName);
		Project downloadedProject = StorageHandler.getInstance().loadProject(projectName);
		String serverProjectName = downloadedProject.getName();
		assertTrue("Project was successfully downloaded", serverProjectName.equalsIgnoreCase(projectName));

		List<Sprite> downloadedProjectSpriteList = downloadedProject.getSpriteList();

		assertEquals("Program wasn't replaced", spriteList.size(), downloadedProjectSpriteList.size());
	}

	private void uploadProjectFromMainMenu(String uploadProjectName, String uploadProjectDescription) {
		preUploadProject();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));

		uploadProject(uploadProjectName, uploadProjectDescription);
	}

	private void uploadProjectFromProgrammList(String uploadProjectName, String uploadProjectDescription) {
		preUploadProject();

		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());

		String upload = solo.getString(R.string.upload_button);
		solo.clickLongOnText(testProject);
		solo.waitForText(upload);
		solo.clickOnText(upload);

		uploadProject(uploadProjectName, uploadProjectDescription);
		solo.goBack();
	}

	private void uploadProjectFromProgramm(String uploadProjectName, String uploadProjectDescription) {
		preUploadProject();

		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(testProject);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnActionBar(solo, R.id.upload);

		uploadProject(uploadProjectName, uploadProjectDescription);
		solo.goBack();
	}

	private void preUploadProject() {
		// change project to a non default state
		Sprite firstSprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0);
		Script firstScript = firstSprite.getScript(0);
		firstScript.addBrick(new WaitBrick(1000));
	}

	private void checkProjectNameAndDescriptionBeforAndAfterDownload(String projectName, String description) {

		Project uploadProject = StorageHandler.getInstance().loadProject(projectName);

		assertEquals("Deserialized project name was changed", projectName, uploadProject.getName());
		assertEquals("Deserialized project description was not renamed correctly", description,
				uploadProject.getDescription());

		//Download replaces project. Name and description should be projectName and description
		downloadProjectAndReplace(projectName);
		Project downloadedProject = StorageHandler.getInstance().loadProject(projectName);

		assertEquals("Project name on server was changed", projectName, downloadedProject.getName());
		assertEquals("Project name on server was not correctly renamed", description,
				downloadedProject.getDescription());
	}

	private void uploadProject(String uploadProjectName, String uploadProjectDescription) {

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

		boolean success = solo.waitForText(solo.getString(R.string.notification_upload_finished), 1, 50000);
		assertTrue("Upload failed. Internet connection?", success);
		String resultString = (String) Reflection.getPrivateField(ServerCalls.getInstance(), "resultString");

		try {
			Log.v("resultString=", "" + resultString);
			JSONObject jsonObject;
			jsonObject = new JSONObject(resultString);
			serverProjectId = jsonObject.optInt("projectId");
			Log.v("serverID=", "" + serverProjectId);
		} catch (JSONException e) {
			fail("JSON exception occurred");
		}
	}

	private void downloadProjectAndReplace(String projectName) {
		downloadProject(projectName, null);
	}

	private void downloadProject(String projectName, String newProjectName) {
		String downloadUrl = TEST_FILE_DOWNLOAD_URL + serverProjectId + Constants.CATROBAT_EXTENSION;
		downloadUrl += "?fname=" + projectName;

		solo.goBack();

		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(downloadUrl));
		launchActivityWithIntent(getInstrumentation().getTargetContext().getPackageName(), MainMenuActivity.class,
				intent);
		solo.sleep(500);
		assertTrue("OverwriteRenameDialog not shown.", solo.searchText(solo.getString(R.string.overwrite_text)));

		if (newProjectName == null) {
			solo.clickOnText(solo.getString(R.string.overwrite_replace));
		} else {
			solo.clickOnText(solo.getString(R.string.overwrite_rename));
			assertTrue("No text field to enter new name.", solo.searchEditText(projectName));

			solo.clickOnButton(solo.getString(R.string.ok));
			solo.sleep(500);
			assertTrue("projectName not visible.", solo.searchText(projectName));

			solo.sleep(500);
			solo.clearEditText(0);
			solo.enterText(0, newProjectName);
		}
		solo.clickOnButton(solo.getString(R.string.ok));

		boolean waitResult = solo.waitForActivity("MainMenuActivity", 10000);
		assertTrue("Download takes too long.", waitResult);
		assertTrue("Download not successful.", solo.searchText(solo.getString(R.string.notification_download_finished)));

		Project loadProject = StorageHandler.getInstance().loadProject(projectName);
		ProjectManager.getInstance().setProject(loadProject);

		assertEquals("Testproject not loaded.", projectName, ProjectManager.getInstance().getCurrentProject().getName());

		String projectPath = Constants.DEFAULT_ROOT + "/" + projectName;
		File downloadedDirectory = new File(projectPath);
		File downloadedProjectFile = new File(projectPath + "/" + Constants.PROJECTCODE_NAME);
		assertTrue("Original Directory does not exist.", downloadedDirectory.exists());
		assertTrue("Original Project File does not exist.", downloadedProjectFile.exists());
	}
}
