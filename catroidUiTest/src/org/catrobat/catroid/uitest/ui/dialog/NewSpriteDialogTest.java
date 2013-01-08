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
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class NewSpriteDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private String testingproject = UiTestUtils.PROJECTNAME1;
	private String testingsprite = "testingsprite";

	public NewSpriteDialogTest() {
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

	public void testNewSpriteDialog() throws NameNotFoundException, IOException {
		createTestProject(testingproject);
		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fr_projects_list);
		assertTrue("Cannot click on project.", UiTestUtils.clickOnTextInList(solo, testingproject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fr_sprites_list);

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		solo.waitForView(EditText.class);
		int spriteEditTextId = solo.getCurrentEditTexts().size() - 1;
		UiTestUtils.enterText(solo, spriteEditTextId, testingsprite);
		solo.sendKey(Solo.ENTER);
		solo.sleep(300);
		solo.clickOnText(testingsprite);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.assertCurrentActivity("Current Activity is not ScriptActivity", ScriptTabActivity.class);
	}

	public void testAddSpriteDialogNoName() {
		createTestProject(testingproject);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fr_projects_list);
		UiTestUtils.clickOnTextInList(solo, testingproject);
		solo.sleep(500);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fr_sprites_list);
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		solo.waitForView(EditText.class);
		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, " ");
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		String errorMessageInvalidInput = solo.getString(R.string.spritename_invalid);
		assertTrue("No or wrong error message shown", solo.searchText(errorMessageInvalidInput));
		solo.clickOnButton(solo.getString(R.string.close));
	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();
		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		project.addSprite(firstSprite);
		storageHandler.saveProject(project);
	}
}
