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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class NewProjectDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private String testingproject = UiTestUtils.PROJECTNAME1;
    private SharedPreferences preferences;

	public NewProjectDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().remove(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT).commit();
	}

	@Override
	protected void tearDown() throws Exception {
        preferences.edit().remove(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT).commit();
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		ProjectManager.getInstance().deleteCurrentProject();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testNewProjectDialog() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.main_menu_new));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProject = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProject, testingproject);
		solo.goBack();
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertTrue("New Project is not testingproject!", ProjectManager.getInstance().getCurrentProject().getName()
				.equals(UiTestUtils.PROJECTNAME1));
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

		assertTrue("EditText was not empty", editText.getText().length() == 0);

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

		int projectNameNumberOfLines = (newProjectName.getHeight() - newProjectName.getCompoundPaddingTop() - newProjectName
				.getCompoundPaddingBottom()) / newProjectName.getLineHeight();

		assertEquals("Project name field is not a text field", 1, projectNameNumberOfLines);

	}

    public void testCreateEmptyProject() {
        solo.clickOnButton(solo.getString(R.string.main_menu_new));
        UiTestUtils.waitForText(solo, solo.getString(R.string.new_project_dialog_title));
        solo.goBack(); // get rid of the keyboard since it is disturbing checkbox-click-event
        solo.clickOnCheckBox(0);
        solo.enterText(0, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
        solo.clickOnButton(solo.getString(R.string.ok));

        UiTestUtils.waitForText(solo, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
        Project project = ProjectManager.getInstance().getCurrentProject();

        assertNotNull("Empty project shouldn't be null", project);
        assertEquals("Just background object should exist", 1, project.getSpriteList().size());
        assertEquals("Just background object should exist", solo.getString(R.string.background), project.getSpriteList().get(0).getName());

        assertTrue("Checkbox state should be saved", preferences.getBoolean(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT, false));

        solo.goBack();
        solo.clickOnButton(solo.getString(R.string.main_menu_new));
        UiTestUtils.waitForText(solo, solo.getString(R.string.new_project_dialog_title));

        CheckBox emptyProjectCheckBox = (CheckBox) solo.getView(R.id.project_empty_checkbox);
        assertTrue("Checkbox should be checked", emptyProjectCheckBox.isChecked());

        solo.goBack(); // get rid of the keyboard since it is disturbing checkbox-click-event
        solo.clickOnCheckBox(0);
        solo.clickOnButton(solo.getString(R.string.cancel_button));
        assertTrue("Checkbox state should not be saved when canceling dialog",
                preferences.getBoolean(NewProjectDialog.SHARED_PREFERENCES_EMPTY_PROJECT, false));
    }
}
