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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public class NewProjectDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String testingproject = UiTestUtils.PROJECTNAME1;
	private String testingProjectJustSpecialChars = UiTestUtils.JUST_SPECIAL_CHAR_PROJECT_NAME;
	private String testingProjectJustSpecialChars2 = UiTestUtils.JUST_SPECIAL_CHAR_PROJECT_NAME2;
	private String testingProjectWithNormalAndSpecialChars = UiTestUtils.NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME2;
	private String testingProjectJustOneDot = UiTestUtils.JUST_ONE_DOT_PROJECT_NAME;
	private String testingProjectJustTwoDots = UiTestUtils.JUST_TWO_DOTS_PROJECT_NAME;
	private SharedPreferences preferences;

	public NewProjectDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.edit().remove(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT).commit();
	}

	@Override
	protected void tearDown() throws Exception {
		preferences.edit().remove(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT).commit();
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(testingProjectJustSpecialChars)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(testingProjectJustSpecialChars2)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(testingProjectWithNormalAndSpecialChars)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(testingProjectJustOneDot)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(testingProjectJustTwoDots)));
		super.tearDown();
	}

	public void testNewProjectDialog() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProject = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProject, testingproject);
		solo.clickOnButton(buttonOkText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("New Project is not testingproject!", UiTestUtils.PROJECTNAME1, ProjectManager.getInstance().getCurrentProject().getName());
	}

	public void testNewProjectJustSpecialChars() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProjectOne = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProjectOne, testingProjectJustSpecialChars);
		solo.clickOnButton(buttonOkText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("New Project is not testingProjectJustSpecialChars!", UiTestUtils.JUST_SPECIAL_CHAR_PROJECT_NAME, ProjectManager.getInstance().getCurrentProject().getName());
	}

	public void testNewProjectJustSpecialCharsTwo() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProjectTwo = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProjectTwo, testingProjectJustSpecialChars2);
		solo.clickOnButton(buttonOkText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("New Project is not testingProjectJustSpecialChars2!", UiTestUtils.JUST_SPECIAL_CHAR_PROJECT_NAME2, ProjectManager.getInstance().getCurrentProject().getName());
	}

	public void testNewProjectWithNormalAndSpecialCharacters() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProjectThree = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProjectThree, testingProjectWithNormalAndSpecialChars);
		solo.clickOnButton(buttonOkText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("New Project is not testingProjectWithNormalAndSpecialChars!", UiTestUtils.NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME2, ProjectManager.getInstance().getCurrentProject().getName());
	}

	public void testNewProjectJustDot() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProjectOne = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProjectOne, testingProjectJustOneDot);
		solo.clickOnButton(buttonOkText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("New Project is not testingProjectJustOneDot!", UiTestUtils.JUST_ONE_DOT_PROJECT_NAME, ProjectManager.getInstance().getCurrentProject().getName());
	}

	public void testNewProjectJustDots() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProjectTwo = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProjectTwo, testingProjectJustTwoDots);
		solo.clickOnButton(buttonOkText);
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("New Project is not testingProjectJustTwoDots!", UiTestUtils.JUST_TWO_DOTS_PROJECT_NAME, ProjectManager.getInstance().getCurrentProject().getName());
	}

	public void testPositiveButtonDisabledOnCreate() {
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.sleep(500);

		Button okButton = solo.getButton(getActivity().getString(R.string.ok));
		assertFalse("New project ok button is enabled!", okButton.isEnabled());
	}

	public void testPositiveButtonChangesState() {
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.sleep(1000);

		Button okButton = solo.getButton(getActivity().getString(R.string.ok));
		EditText editText = (EditText) solo.getView(R.id.project_name_edittext);

		assertTrue("EditText was not empty", editText.length() == 0);

		final String projectName = "MyTestProject";
		UiTestUtils.enterText(solo, 0, projectName);

		assertEquals("Wrong projectname in EditText - should be MyTestProject", projectName, editText.getText()
				.toString());
		assertTrue("New project ok button not enabled!", okButton.isEnabled());

		UiTestUtils.enterText(solo, 0, "");

		assertEquals("EditText was not empty", "", editText.getText().toString());
		assertFalse("New project ok button not disabled!", okButton.isEnabled());
	}

	public void testNewProjectDialogHeight() {
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		solo.sleep(2000);

		EditText newProjectName = (EditText) solo.getView(R.id.project_name_edittext);

		int newProjectInputType = newProjectName.getInputType();

		int newProjectInputTypeReference = android.text.InputType.TYPE_CLASS_TEXT
				| android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;

		solo.sleep(2000);
		assertEquals("New project name field is not a text field", newProjectInputTypeReference, newProjectInputType);

		int projectNameNumberOfLines = newProjectName.getLineCount();

		assertEquals("Project name field is not a text field", 1, projectNameNumberOfLines);
	}

	public void testCreateEmptyProject() {
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		UiTestUtils.waitForText(solo, solo.getString(R.string.new_project_dialog_title));
		solo.clickOnCheckBox(0);
		solo.enterText(0, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_orientation_title), 0, 5000));
		solo.clickOnButton(solo.getString(R.string.ok));

		UiTestUtils.waitForText(solo, solo.getString(R.string.background));
		Project project = ProjectManager.getInstance().getCurrentProject();

		assertNotNull("Empty project shouldn't be null", project);
		assertEquals("Just background object should exist", 1, project.getSpriteList().size());
		assertEquals("Just background object should exist", solo.getString(R.string.background), project
				.getSpriteList().get(0).getName());

		assertTrue("Checkbox state should be saved",
				preferences.getBoolean(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT, false));

		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		UiTestUtils.waitForText(solo, solo.getString(R.string.new_project_dialog_title));

		CheckBox emptyProjectCheckBox = (CheckBox) solo.getView(R.id.project_empty_checkbox);
		assertTrue("Checkbox should be checked", emptyProjectCheckBox.isChecked());

		solo.clickOnCheckBox(0);
		solo.clickOnButton(solo.getString(R.string.cancel_button));
		assertTrue("Checkbox state should not be saved when canceling dialog",
				preferences.getBoolean(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT, false));
	}
}
