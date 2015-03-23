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
package org.catrobat.catroid.uitest.ui.dialog;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.web.ServerCalls;

import java.io.File;

public class UploadDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private String testProject = UiTestUtils.PROJECTNAME1;

	private String saveToken;
	private String uploadDialogTitle;
	private Project uploadProject;

	public UploadDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		uploadDialogTitle = solo.getString(R.string.upload_project_dialog_title);
		createTestProject();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName(), 3000);
		solo.sleep(300);
		try {
			setServerURLToTestURL();
		} catch (Throwable e) {
			throw new Exception();
		}
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, saveToken).commit();
		uploadProject = null;
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
		UiTestUtils.createValidUser(getActivity());
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadDialogTitle);

		View renameView = solo.getText(solo
				.getString(R.string.project_rename));
		assertNotNull("View for rename project could not be found", renameView);
		assertEquals("rename View is visible.", renameView.getVisibility(), View.GONE);

		// clear the title
		solo.clearEditText(0);
		assertEquals("rename View is hidden.", renameView.getVisibility(), View.VISIBLE);

		// enter the same title
		solo.enterText(0, testProject);
		assertEquals("rename View is visible.", renameView.getVisibility(), View.GONE);

		// enter a new title
		solo.clearEditText(0);
		solo.enterText(0, UiTestUtils.PROJECTNAME2);
		assertEquals("rename View is hidden.", renameView.getVisibility(), View.VISIBLE);

		solo.clickOnButton(solo.getString(R.string.cancel_button));
	}

	public void testUploadingProjectDescriptionDefaultValue() throws Throwable {
		UiTestUtils.createValidUser(getActivity());
		String testDescription = "Test description";
		String actionSetDescriptionText = solo.getString(R.string.set_description);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		solo.clickLongOnText(uploadProject.getName());
		assertTrue("context menu not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clickOnText(actionSetDescriptionText);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.clearEditText(0);
		solo.enterText(0, testDescription);
		assertTrue("dialog not loaded in 5 seconds", solo.waitForText(actionSetDescriptionText, 0, 5000));
		solo.sleep(300);

		solo.clickOnText(solo.getString(R.string.ok));

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

	// Not testable with Android 2.3, because solo is not able to enter new lines
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void testProjectDescriptionUploadProject() throws Throwable {
		UiTestUtils.createValidUser(getActivity());
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

		int projectUploadNameNumberOfLines = editTextUploadName.getLineCount();
		assertEquals("Project name field is not a text field", 1, projectUploadNameNumberOfLines);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			int projectUploadDescriptionNumberOfLines = editTextUploadDescription.getMaxLines();
			assertEquals("Project description field is not multiline", 2, projectUploadDescriptionNumberOfLines);
		}
	}

	private void createTestProject() {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + testProject);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		assertFalse("testProject was not deleted!", directory.exists());

		uploadProject = new Project(getActivity(), testProject);
		ProjectManager.getInstance().setProject(uploadProject);
		StorageHandler.getInstance().saveProject(uploadProject);

		File file = new File(Constants.DEFAULT_ROOT + "/" + testProject + "/" + Constants.PROJECTCODE_NAME);
		assertTrue(testProject + " was not created!", file.exists());
	}
}
