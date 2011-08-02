/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
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
		Script testScript = new StartScript("testscript", firstSprite);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		testScript.addBrick(setCostumeBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();

		costumeFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image.png",
				RESOURCE_COSTUME, getInstrumentation().getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(costumeFile.getName());
		costumeData.setCostumeName(costumeName);

		costumeFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image2.png",
				RESOURCE_COSTUME2, getInstrumentation().getContext(), UiTestUtils.TYPE_IMAGE_FILE);
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
		super.tearDown();
	}

	public void testSelectCostumeAndPlay() {
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.sleep(100);
		solo.clickOnText(costumeName);
		solo.sleep(100);
		assertTrue(costumeName + " is not selected in Spinner", solo.searchText(costumeName));
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(7000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getCostume();
		assertEquals("costume not set", costume.getImagePath(), costumeDataList.get(0).getAbsolutePath());
		solo.goBack();
		solo.sleep(200);

		//changing le costume
		solo.clickOnText(costumeName);
		solo.sleep(100);
		solo.clickOnText(costumeName2);
		solo.sleep(100);
		assertTrue(costumeName2 + " is not selected in Spinner", solo.searchText(costumeName2));
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(7000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getCostume();
		assertEquals("costume not set", costume.getImagePath(), costumeDataList.get(1).getAbsolutePath());
	}

	public void testSpinnerUpdates() {
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.sleep(100);
		assertTrue(costumeName + " is not in Spinner", solo.searchText(costumeName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(300);
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		solo.sleep(300);
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
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(300);
		solo.clickOnButton(getActivity().getString(R.string.edit_costume));
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.rename_costume_dialog));
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(300);
		solo.clickOnText(getActivity().getString(R.string.scripts));
		solo.sleep(300);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		assertTrue(newName + " is not in Spinner", solo.searchText(newName));
		assertTrue(costumeName2 + " is not in Spinner", solo.searchText(costumeName2));
	}
}
