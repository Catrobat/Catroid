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
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.MyProjectsActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SetDescriptionDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private String testProject = UiTestUtils.PROJECTNAME1;

	public SetDescriptionDialogTest() {
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

	public void testMultiLineProjectDescription() {
		StorageHandler storageHandler = StorageHandler.getInstance();
		Project uploadProject = new Project(getActivity(), testProject);
		storageHandler.saveProject(uploadProject);

		solo.sleep(300);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.clickLongOnText(testProject);
		solo.clickInList(2);
		EditText description = (EditText) solo.getView(R.id.dialog_text_EditMultiLineText);
		solo.sleep(2000);
		int descriptionInputType = description.getInputType();
		int typeToCheck = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE | android.text.InputType.TYPE_CLASS_TEXT
				| android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
		assertEquals("Description field is not multiline!", descriptionInputType, typeToCheck);

		int projectDescriptionNumberOfLines = (description.getHeight() - description.getCompoundPaddingTop() - description
				.getCompoundPaddingBottom()) / description.getLineHeight();
		assertEquals("Project description field is not multiline", 3, projectDescriptionNumberOfLines);
	}
}
