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
package org.catrobat.catroid.uitest.ui.dialog;

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.web.ServerCalls;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class UploadDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private String testProject = UiTestUtils.PROJECTNAME1;
	private String newTestProject = UiTestUtils.PROJECTNAME2;

	private Solo solo;
	private String saveToken;
	private String uploadDialogTitle;

	public UploadDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		uploadDialogTitle = solo.getString(R.string.upload_project_dialog_title);
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, saveToken).commit();
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		ProjectManager.getInstance().deleteCurrentProject();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	private void setServerURLToTestURL() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	public void testUploadDialog() throws Throwable {
		setServerURLToTestURL();
		createTestProject();
		solo.sleep(200);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName(), 3000);
		UiTestUtils.createValidUser(getActivity());
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadDialogTitle);

		// robotium updated getText with RegularExpressions
		// need to escape brackets for test to work
		String projectRenameString = solo.getString(R.string.project_rename);
		projectRenameString.replaceAll("\\(", "");
		projectRenameString.replaceAll("\\)", "");
		View renameView = solo.getText("\\(" + projectRenameString + "\\)");
		assertNotNull("View for rename project could not be found", renameView);
		assertEquals("rename View is visible.", renameView.getVisibility(), View.GONE);

		// clear the title
		solo.clearEditText(0);
		assertEquals("rename View is hidden.", renameView.getVisibility(), View.VISIBLE);

		// enter the same title
		solo.clickOnEditText(0);
		solo.enterText(0, testProject);
		assertEquals("rename View is visible.", renameView.getVisibility(), View.GONE);

		// enter a new title
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, newTestProject);
		assertEquals("rename View is hidden.", renameView.getVisibility(), View.VISIBLE);
		solo.goBack();

		solo.clickOnButton(solo.getString(R.string.cancel_button));
	}

	public void testUploadingProjectDescriptionDefaultValue() throws Throwable {
		String testDescription = "Test description";
		String actionSetDescriptionText = solo.getString(R.string.set_description);
		String setDescriptionDialogTitle = solo.getString(R.string.description);
		Project uploadProject = new Project(getActivity(), testProject);
		ProjectManager.INSTANCE.setProject(uploadProject);
		StorageHandler.getInstance().saveProject(uploadProject);
		setServerURLToTestURL();
		UiTestUtils.createValidUser(getActivity());

		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.longClickOnTextInList(solo, uploadProject.getName());
		assertTrue("context menu not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clickOnText(actionSetDescriptionText);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		solo.clearEditText(0);
		solo.enterText(0, testDescription);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		solo.sleep(300);

		// workaround - Ok button not clickable
		solo.sendKey(Solo.ENTER);
		solo.sendKey(Solo.ENTER);

		solo.waitForDialogToClose(500);
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		boolean uploadDialogShown = solo.waitForText(uploadDialogTitle);

		assertTrue("upload project dialog not shown", uploadDialogShown);
		EditText uploadDescriptionView = (EditText) solo.getView(R.id.project_description_upload);
		String uploadDescription = uploadDescriptionView.getText().toString();
		solo.sleep(500);
		assertEquals("Project description was not set or is wrong", testDescription, uploadDescription);
	}

	public void testProjectDescriptionUploadProject() throws Throwable {
		Project uploadProject = new Project(getActivity(), testProject);
		ProjectManager.INSTANCE.setProject(uploadProject);
		StorageHandler.getInstance().saveProject(uploadProject);

		solo.sleep(300);
		setServerURLToTestURL();
		UiTestUtils.createValidUser(getActivity());
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		boolean uploadDialogShown = solo.waitForText(uploadDialogTitle);

		assertTrue("upload project dialog not shown", uploadDialogShown);
		EditText editTextUploadName = solo.getEditText(0);
		EditText editTextUploadDescription = solo.getEditText(1);
		int projectUploadNameInputType = editTextUploadName.getInputType();
		int projectUploadDescriptionInputType = editTextUploadDescription.getInputType();
		int newProjectInputTypeReference = android.text.InputType.TYPE_CLASS_TEXT
				| android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
		int newProjectDescriptionInputTypeReference = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
				| android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
		solo.sleep(200);
		assertEquals("Project name field is not a text field", newProjectInputTypeReference, projectUploadNameInputType);
		assertEquals("Project description field is not multiline", newProjectDescriptionInputTypeReference,
				projectUploadDescriptionInputType);

		int projectUploadNameNumberOfLines = (editTextUploadName.getHeight()
				- editTextUploadName.getCompoundPaddingTop() - editTextUploadName.getCompoundPaddingBottom())
				/ editTextUploadName.getLineHeight();
		int projectUploadDescriptionNumberOfLines = (editTextUploadDescription.getHeight()
				- editTextUploadDescription.getCompoundPaddingTop() - editTextUploadDescription
					.getCompoundPaddingBottom()) / editTextUploadDescription.getLineHeight();
		assertEquals("Project name field is not a text field", 1, projectUploadNameNumberOfLines);
		assertEquals("Project description field is not multiline", 2, projectUploadDescriptionNumberOfLines);
	}

	private void createTestProject() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		assertFalse("testProject was not deleted!", directory.exists());

		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.enterText(0, testProject);
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(2000);

		File file = new File(Constants.DEFAULT_ROOT + "/" + testProject + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(testProject + " was not created!", file.exists());
		UiTestUtils.goToHomeActivity(getActivity());
	}
}
