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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SetCostumeBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private String costumeName = "testCostume1";
	private String costumeName2 = "testCostume2";
	private File costumeFile;
	private File costumeFile2;
	private ArrayList<CostumeData> costumeDataList;
	private final int RESOURCE_COSTUME = at.tugraz.ist.catroid.uitest.R.raw.icon;
	private final int RESOURCE_COSTUME2 = at.tugraz.ist.catroid.uitest.R.raw.icon2;

	public SetCostumeBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
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
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(costumeData.getChecksum(),
				costumeData.getAbsolutePath());
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(costumeData2.getChecksum(),
				costumeData2.getAbsolutePath());

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		if (costumeFile.exists()) {
			costumeFile.delete();
		}
		if (costumeFile2.exists()) {
			costumeFile2.delete();
		}
		super.tearDown();
	}

	public void testSelectCostumeAndPlay() {
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.sleep(100);
		solo.clickOnText(costumeName);
		solo.sleep(100);
		assertTrue(costumeName + " is not selected in Spinner", solo.searchText(costumeName));
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(7000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).costume;
		assertEquals("costume not set", costume.getImagePath(), costumeDataList.get(0).getAbsolutePath());
		solo.goBack();
		solo.goBack();
		solo.sleep(200);

		//changing le costume
		solo.clickOnText(costumeName);
		solo.sleep(100);
		solo.clickOnText(costumeName2);
		solo.sleep(100);
		assertTrue(costumeName2 + " is not selected in Spinner", solo.searchText(costumeName2));
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(7000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).costume;
		assertEquals("costume not set", costume.getImagePath(), costumeDataList.get(1).getAbsolutePath());
	}

	public void testSpinnerUpdates() {
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.sleep(100);
		assertTrue(costumeName + " is not in Spinner", solo.searchText(costumeName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(300);
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		solo.sleep(300);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(1000);
		solo.clickOnText(getActivity().getString(R.string.scripts));
		solo.sleep(300);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		assertFalse(costumeName + " is still in Spinner", solo.searchText(costumeName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
	}

	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.sleep(100);
		assertTrue(costumeName + " is not in Spinner", solo.searchText(costumeName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(300);
		solo.clickOnView(solo.getView(R.id.costume_name));
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(300);
		solo.clickOnText(getActivity().getString(R.string.scripts));
		solo.sleep(300);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		assertTrue(newName + " is not in Spinner", solo.searchText(newName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
	}

	public void testAdapterUpdateInScriptActivity() {
		String costume1ImagePath = costumeDataList.get(0).getAbsolutePath();
		String costume2ImagePath = costumeDataList.get(1).getAbsolutePath();
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.sleep(100);
		solo.clickOnText(costumeName);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(5000);
		String costumePath = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList().get(0)
				.getAbsolutePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", costume1ImagePath,
				costumePath);
		solo.goBack();
		solo.goBack();
		solo.sleep(300);
		for (int i = 0; i < 5; i++) {
			selectCostume(costumeName2, costumeName, costume2ImagePath);
			selectCostume(costumeName, costumeName2, costume1ImagePath);
		}

	}

	public void selectCostume(String newCostume, String oldName, String costumeImagePath) {
		solo.sleep(100);
		solo.clickOnText(oldName);
		solo.sleep(100);
		solo.clickOnText(newCostume);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(5000);
		String costumePath = ProjectManager.getInstance().getCurrentSprite().costume.getImagePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", costumeImagePath,
				costumePath);
		solo.goBack();
		solo.goBack();
		solo.sleep(300);
	}
}
