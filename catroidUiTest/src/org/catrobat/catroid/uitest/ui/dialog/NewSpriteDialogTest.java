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

import java.io.IOException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class NewSpriteDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String testingproject = UiTestUtils.PROJECTNAME1;
	private String testingsprite = "testingsprite";

	public NewSpriteDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void tearDown() throws Exception {
		// normally super.teardown should be called last
		// but tests crashed with Nullpointer
		super.tearDown();
		ProjectManager.INSTANCE.deleteCurrentProject();
	}

	public void testNewSpriteDialog() throws NameNotFoundException, IOException {
		createTestProject(testingproject);
		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("Cannot click on project.", UiTestUtils.clickOnTextInList(solo, testingproject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		String spriteName = "spriteError";
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForView(EditText.class);
		enterTextAndCloseDialog(spriteName);
		assertTrue("Sprite not successfully added", ProjectManager.INSTANCE.spriteExists(spriteName));

		//Add sprite which already exists
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		enterTextAndCloseDialog(spriteName);
		String errorMessageText = solo.getString(R.string.spritename_already_exists);
		String buttonCloseText = solo.getString(R.string.close);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(errorMessageText));
		solo.clickOnButton(buttonCloseText);
		solo.sleep(200);

		//Check if button is deactivated when adding a sprite without a name
		UiTestUtils.enterText(solo, 0, "");
		solo.sleep(200);
		String okButtonText = solo.getString(R.string.ok);
		boolean okButtonEnabled = solo.getButton(okButtonText).isEnabled();
		assertFalse("'" + okButtonText + "' button not deactivated", okButtonEnabled);

		int spriteEditTextId = solo.getCurrentViews(EditText.class).size() - 1;
		UiTestUtils.enterText(solo, spriteEditTextId, " ");
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		String errorMessageInvalidInput = solo.getString(R.string.spritename_invalid);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageInvalidInput));
		solo.clickOnButton(solo.getString(R.string.close));
		solo.sleep(200);

		//Test to add sprite without name ("") with ENTER key
		solo.clickOnEditText(0);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(solo.getString(R.string.spritename_invalid)));
		solo.clickOnButton(buttonCloseText);
		solo.sleep(200);
		assertTrue("Not in NewSpriteDialog", solo.searchText(solo.getString(R.string.new_sprite_dialog_title)));

		UiTestUtils.enterText(solo, spriteEditTextId, testingsprite);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(300);
		solo.clickOnText(testingsprite);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.assertCurrentActivity("Current Activity is not ScriptActivity", ScriptActivity.class);
	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();
		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		project.addSprite(firstSprite);
		storageHandler.saveProject(project);
	}

	private void enterTextAndCloseDialog(String text) {
		solo.clearEditText(0);
		solo.enterText(0, text);
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(200);
	}
}
