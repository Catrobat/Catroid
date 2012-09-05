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

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

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
	}

	public void testNewProjectDialog() {
		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnButton(solo.getString(R.string.new_project));
		assertTrue("dialog not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.new_project_dialog_title), 0, 5000));
		EditText newProject = (EditText) solo.getView(R.id.project_name_edittext);
		solo.enterText(newProject, testingproject);
		solo.clickOnButton(buttonOkText);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertTrue("New Project is not testingproject!", ProjectManager.getInstance().getCurrentProject().getName()
				.equals(UiTestUtils.PROJECTNAME1));
	}

	public void testPositiveButtonDisabledOnCreate() {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.sleep(500);

		Button okButton = (Button) solo.getView(R.id.new_project_ok_button);
		assertFalse("New project ok button is enabled!", okButton.isEnabled());
	}

	public void testPositiveButtonChangesState() {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.sleep(1000);

		Button okButton = (Button) solo.getView(R.id.new_project_ok_button);
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

	public void testProjectDescriptionNewProject() {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.sleep(2000);

		EditText newProjectName = (EditText) solo.getView(R.id.project_name_edittext);
		EditText newProjectDescription = (EditText) solo.getView(R.id.project_description_edittext);
		int newProjectInputType = newProjectName.getInputType();
		int newProjectDescriptionInputType = newProjectDescription.getInputType();
		int newProjectInputTypeReference = android.text.InputType.TYPE_CLASS_TEXT
				| android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
		int newProjectDescriptionInputTypeReference = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
				| android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
		solo.sleep(2000);
		assertEquals("New project name field is not a text field", newProjectInputTypeReference, newProjectInputType);
		assertEquals("Project description field is not multiline", newProjectDescriptionInputTypeReference,
				newProjectDescriptionInputType);

		int projectNameNumberOfLines = (newProjectName.getHeight() - newProjectName.getCompoundPaddingTop() - newProjectName
				.getCompoundPaddingBottom()) / newProjectName.getLineHeight();
		int projectDescriptionNumberOfLines = (newProjectDescription.getHeight()
				- newProjectDescription.getCompoundPaddingTop() - newProjectDescription.getCompoundPaddingBottom())
				/ newProjectDescription.getLineHeight();
		assertEquals("Project name field is not a text field", 1, projectNameNumberOfLines);
		assertEquals("Project description field is not multiline", 2, projectDescriptionNumberOfLines);
	}
}
