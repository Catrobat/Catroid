/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.Costume;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SetCostumeBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private final int RESOURCE_COSTUME = org.catrobat.catroid.uitest.R.raw.icon;
	private final int RESOURCE_COSTUME2 = org.catrobat.catroid.uitest.R.raw.icon2;

	private Solo solo;
	private String costumeName = "testCostume1";
	private String costumeName2 = "testCostume2";
	private File costumeFile;
	private File costumeFile2;
	private ArrayList<CostumeData> costumeDataList;

	public SetCostumeBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();

		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		testScript.addBrick(setCostumeBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();

		costumeFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image.png",
				RESOURCE_COSTUME, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(costumeFile.getName());
		costumeData.setCostumeName(costumeName);

		costumeFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image2.png",
				RESOURCE_COSTUME2, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		CostumeData costumeData2 = new CostumeData();
		costumeData2.setCostumeFilename(costumeFile2.getName());
		costumeData2.setCostumeName(costumeName2);

		costumeDataList.add(costumeData);
		costumeDataList.add(costumeData2);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(costumeData2.getChecksum(), costumeData2.getAbsolutePath());

		solo = new Solo(getInstrumentation(), getActivity());

		Intent intent = new Intent(getActivity(), ScriptActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		getActivity().startActivity(intent);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		if (costumeFile.exists()) {
			costumeFile.delete();
		}
		if (costumeFile2.exists()) {
			costumeFile2.delete();
		}
		super.tearDown();
		solo = null;
	}

	public void testSelectCostumeAndPlay() {
		solo.clickOnText(solo.getString(R.string.broadcast_nothing_selected));
		solo.clickOnText(costumeName);
		assertTrue(costumeName + " is not selected in Spinner", solo.searchText(costumeName));

		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.sleep(7000);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).costume;
		assertEquals("costume not set", costume.getImagePath(), costumeDataList.get(0).getAbsolutePath());
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.clickOnText(costumeName);
		solo.clickOnText(costumeName2);
		assertTrue(costumeName2 + " is not selected in Spinner", solo.searchText(costumeName2));
		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).costume;
		assertEquals("costume not set", costume.getImagePath(), costumeDataList.get(1).getAbsolutePath());
	}

	public void testSpinnerUpdates() {
		String spinnerNothingText = solo.getString(R.string.broadcast_nothing_selected);
		solo.clickOnText(spinnerNothingText);
		assertTrue(costumeName + " is not in Spinner", solo.searchText(costumeName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.clickOnButton(solo.getString(R.string.delete_lowercase));
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.clickOnText(spinnerNothingText);
		assertFalse(costumeName + " is still in Spinner", solo.searchText(costumeName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
	}

	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";
		String spinnerNothingText = solo.getString(R.string.broadcast_nothing_selected);

		solo.clickOnText(spinnerNothingText);
		assertTrue(costumeName + " is not in Spinner", solo.searchText(costumeName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.clickOnView(solo.getView(R.id.costume_name));
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(500);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.clickOnText(spinnerNothingText);
		assertTrue(newName + " is not in Spinner", solo.searchText(newName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
	}

	public void testAdapterUpdateInScriptActivity() {
		String costume1ImagePath = costumeDataList.get(0).getAbsolutePath();
		String costume2ImagePath = costumeDataList.get(1).getAbsolutePath();
		solo.clickOnText(solo.getString(R.string.broadcast_nothing_selected));
		solo.clickOnText(costumeName);

		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		String costumePath = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList().get(0)
				.getAbsolutePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", costume1ImagePath,
				costumePath);
		solo.goBack();
		solo.sleep(500);
		solo.goBack();
		for (int i = 0; i < 5; i++) {
			selectCostume(costumeName2, costumeName, costume2ImagePath);
			selectCostume(costumeName, costumeName2, costume1ImagePath);
		}
	}

	public void selectCostume(String newCostume, String oldName, String costumeImagePath) {
		solo.clickOnText(oldName);
		solo.clickOnText(newCostume);
		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.sleep(5000);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		String costumePath = ProjectManager.getInstance().getCurrentSprite().costume.getImagePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", costumeImagePath,
				costumePath);
		solo.goBack();
		solo.goBack();
	}
}
