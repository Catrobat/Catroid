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
package org.catrobat.catroid.uitest.content.brick;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SetLookBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private final int RESOURCE_LOOK = org.catrobat.catroid.uitest.R.raw.icon;
	private final int RESOURCE_LOOK2 = org.catrobat.catroid.uitest.R.raw.icon2;

	private Solo solo;
	private String lookName = "testLook1";
	private String lookName2 = "testLook2";
	private File lookFile;
	private File lookFile2;
	private ArrayList<LookData> lookDataList;

	public SetLookBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		if (lookFile.exists()) {
			lookFile.delete();
		}
		if (lookFile2.exists()) {
			lookFile2.delete();
		}
		super.tearDown();
		solo = null;
	}

	public void testSelectLookAndPlay() {
		solo.clickOnText(solo.getString(R.string.broadcast_nothing_selected));
		solo.clickOnText(lookName);
		assertTrue(lookName + " is not selected in Spinner", solo.searchText(lookName));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
		Look look = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).look;
		assertEquals("look not set", look.getImagePath(), lookDataList.get(0).getAbsolutePath());
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.clickOnText(lookName);
		solo.clickOnText(lookName2);
		assertTrue(lookName2 + " is not selected in Spinner", solo.searchText(lookName2));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
		look = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).look;
		assertEquals("look not set", look.getImagePath(), lookDataList.get(1).getAbsolutePath());
	}

	public void testSpinnerUpdatesDelete() {
		String spinnerNothingText = solo.getString(R.string.broadcast_nothing_selected);

		solo.clickOnText(spinnerNothingText);

		assertTrue(lookName + " is not in Spinner", solo.searchText(lookName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));

		solo.goBack();
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));

		clickOnContextMenuItem(lookName, solo.getString(R.string.delete));
		solo.clickOnButton(solo.getString(R.string.ok));

		clickOnSpinnerItem(solo.getString(R.string.category_looks), solo.getString(R.string.scripts));

		solo.clickOnText(spinnerNothingText);

		assertFalse(lookName + " is still in Spinner", solo.searchText(lookName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));
	}

	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";
		String spinnerNothingText = solo.getString(R.string.broadcast_nothing_selected);

		solo.clickOnText(spinnerNothingText);

		assertTrue(lookName + " is not in Spinner", solo.searchText(lookName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));

		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.backgrounds));

		clickOnContextMenuItem(lookName, solo.getString(R.string.rename));

		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.clickOnButton(solo.getString(R.string.ok));

		clickOnSpinnerItem(solo.getString(R.string.category_looks), solo.getString(R.string.scripts));

		solo.clickOnText(spinnerNothingText);

		assertTrue(newName + " is not in Spinner", solo.searchText(newName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));
	}

	public void testAdapterUpdateInScriptActivity() {
		String look1ImagePath = lookDataList.get(0).getAbsolutePath();
		String look2ImagePath = lookDataList.get(1).getAbsolutePath();
		solo.clickOnText(solo.getString(R.string.broadcast_nothing_selected));
		solo.clickOnText(lookName);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		String lookPath = ProjectManager.getInstance().getCurrentSprite().getLookDataList().get(0).getAbsolutePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", look1ImagePath, lookPath);
		solo.goBack();
		solo.goBack();

		for (int i = 0; i < 5; ++i) {
			selectLook(lookName2, lookName, look2ImagePath);
			selectLook(lookName, lookName2, look1ImagePath);
		}
	}

	public void selectLook(String newLook, String oldName, String lookImagePath) {
		solo.clickOnText(oldName);
		solo.clickOnText(newLook);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(5000);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		String lookPath = ProjectManager.getInstance().getCurrentSprite().look.getImagePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", lookImagePath, lookPath);
		solo.goBack();
		solo.goBack();
	}

	private void clickOnSpinnerItem(String selectedSpinnerItem, String itemName) {
		solo.clickOnText(selectedSpinnerItem);
		solo.clickOnText(itemName);
	}

	private void createProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		SetLookBrick setLookBrick = new SetLookBrick(firstSprite);
		testScript.addBrick(setLookBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		lookFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image.png", RESOURCE_LOOK,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		LookData lookData = new LookData();
		lookData.setLookFilename(lookFile.getName());
		lookData.setLookName(lookName);

		lookFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image2.png", RESOURCE_LOOK2,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		LookData lookData2 = new LookData();
		lookData2.setLookFilename(lookFile2.getName());
		lookData2.setLookName(lookName2);

		lookDataList.add(lookData);
		lookDataList.add(lookData2);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(lookData2.getChecksum(), lookData2.getAbsolutePath());
	}

	private void clickOnContextMenuItem(String lookName, String menuItemName) {
		solo.clickLongOnText(lookName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}
}
