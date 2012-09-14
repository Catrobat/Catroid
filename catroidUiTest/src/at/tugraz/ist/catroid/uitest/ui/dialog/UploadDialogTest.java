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
package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.File;

import junit.framework.AssertionFailedError;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.MyProjectsActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.web.ServerCalls;

import com.jayway.android.robotium.solo.Solo;

public class UploadDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private String testProject = UiTestUtils.PROJECTNAME1;
	private String newTestProject = UiTestUtils.PROJECTNAME2;

	private Solo solo;
	private String saveToken;

	public UploadDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Constants.TOKEN, "0");
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
		UiTestUtils.createValidUser(getActivity());
		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.waitForDialogToClose(5000);

		View renameView = solo.getText(getActivity().getString(R.string.project_rename));
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

		solo.clickOnButton(getActivity().getString(R.string.cancel_button));
	}

	public void testOrientationChange() throws Throwable {
		setServerURLToTestURL();
		createTestProject();
		solo.sleep(200);
		String testText1 = "testText1";
		String testText2 = "testText2";
		UiTestUtils.createValidUser(getActivity());
		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(200);
		solo.clearEditText(0);
		solo.enterText(0, testText1);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testText1));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.enterText(1, testText2);

		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testText2));
	}

	public void testUploadingProjectDescriptionDefaultValue() throws Throwable {
		String testDescription = "Test description";
		String actionSetDescriptionText = solo.getString(R.string.set_description);
		String setDescriptionDialogTitle = solo.getString(R.string.description);
		Project uploadProject = new Project(getActivity(), testProject);
		ProjectManager.INSTANCE.setProject(uploadProject);
		ProjectManager.INSTANCE.saveProject();
		setServerURLToTestURL();
		UiTestUtils.createValidUser(getActivity());

		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.my_projects));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fr_projects_list);
		UiTestUtils.longClickOnTextInList(solo, uploadProject.getName());
		assertTrue("context menu not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clickOnText(actionSetDescriptionText);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		solo.clearEditText(0);
		solo.enterText(0, testDescription);
		solo.sleep(200);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(setDescriptionDialogTitle, 0, 5000));
		solo.sleep(300);

		String buttonPositiveText = solo.getString(R.string.ok);
		// if keyboard is there, hide it and click ok
		try {
			solo.clickOnText(buttonPositiveText);
		} catch (AssertionFailedError e) {
			solo.goBack();
			solo.clickOnText(buttonPositiveText);
		}
		solo.waitForDialogToClose(500);
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnText(solo.getString(R.string.upload_project));
		assertTrue("upload project dialog not shown",
				solo.waitForText(solo.getString(R.string.upload_project_dialog_title), 0, 5000));
		EditText uploadDescriptionView = (EditText) solo.getView(R.id.project_description_upload);
		String uploadDescription = uploadDescriptionView.getText().toString();
		solo.sleep(500);
		assertEquals("Project description was not set or is wrong", testDescription, uploadDescription);
	}

	public void testProjectDescriptionUploadProject() throws Throwable {
		Project uploadProject = new Project(getActivity(), testProject);
		ProjectManager.INSTANCE.setProject(uploadProject);
		ProjectManager.INSTANCE.saveProject();

		solo.sleep(300);
		setServerURLToTestURL();
		UiTestUtils.createValidUser(getActivity());
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.upload_project));

		assertTrue("upload project dialog not shown",
				solo.waitForText(solo.getString(R.string.upload_project_dialog_title), 0, 5000));
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

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.enterText(0, testProject);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(2000);

		File file = new File(Constants.DEFAULT_ROOT + "/" + testProject + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(testProject + " was not created!", file.exists());
		UiTestUtils.goToHomeActivity(getActivity());
	}
}
