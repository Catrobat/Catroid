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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
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
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.waitForText(solo.getString(R.string.new_project_dialog_title));
		int nameEditTextId = solo.getCurrentEditTexts().size() - 1;
		UiTestUtils.enterText(solo, nameEditTextId, testingproject);
		solo.sendKey(Solo.ENTER);
		solo.sleep(300);
		assertTrue("New Project is not testingproject!", ProjectManager.getInstance().getCurrentProject().getName()
				.equals(UiTestUtils.PROJECTNAME1));
	}

	public void testPositiveButtonDisabledOnCreate() {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.sleep(1000);

		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		NewProjectDialog dialogFragment = (NewProjectDialog) fragmentManager.findFragmentByTag("dialog_new_project");
		AlertDialog dialog = (AlertDialog) dialogFragment.getDialog();

		Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		assertFalse("New project ok button is enabled!", okButton.isEnabled());
	}

	public void testPositiveButtonChangesState() {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.sleep(1000);

		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		NewProjectDialog dialogFragment = (NewProjectDialog) fragmentManager.findFragmentByTag("dialog_new_project");
		AlertDialog dialog = (AlertDialog) dialogFragment.getDialog();

		Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		EditText editText = (EditText) solo.getView(R.id.dialog_text_EditText);

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
}
