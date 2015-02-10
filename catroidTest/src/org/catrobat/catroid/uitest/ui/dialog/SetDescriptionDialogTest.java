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
import android.os.Build;
import android.widget.EditText;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class SetDescriptionDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String testProject = UiTestUtils.PROJECTNAME1;

	public SetDescriptionDialogTest() {
		super(MainMenuActivity.class);
	}

	// Not testable with Android 2.3, because solo is not able to enter new lines
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void testMultiLineProjectDescription() {
		StorageHandler storageHandler = StorageHandler.getInstance();
		Project uploadProject = new Project(getActivity(), testProject);
		storageHandler.saveProject(uploadProject);

		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		solo.clickLongOnText(testProject);
		solo.clickOnText(solo.getString(R.string.set_description));
		EditText description = (EditText) solo.getView(R.id.dialog_text_EditMultiLineText);
		solo.sleep(2000);
		int descriptionInputType = description.getInputType();
		int typeToCheck = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE | android.text.InputType.TYPE_CLASS_TEXT
				| android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
		assertEquals("Description field is not multiline!", descriptionInputType, typeToCheck);

		int projectDescriptionNumberOfLines = description.getLineCount();
		assertEquals("Project description field should only have one line without some input", 1,
				projectDescriptionNumberOfLines);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			projectDescriptionNumberOfLines = description.getMaxLines();
			assertEquals("Project description field is not multiline", 3, projectDescriptionNumberOfLines);
		}
	}
}
