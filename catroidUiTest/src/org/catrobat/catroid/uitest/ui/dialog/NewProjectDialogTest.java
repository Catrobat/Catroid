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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class NewProjectDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private String testingproject = UiTestUtils.PROJECTNAME1;

	public NewProjectDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
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
}
