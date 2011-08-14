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

package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class CostumeActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private String costumeName = "costumeNametest";
	private File imageFile;
	private ArrayList<CostumeData> costumeDataList;
	private final int RESOURCE_IMAGE = R.drawable.catroid_sunglasses;

	public CostumeActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.TYPE_IMAGE_FILE);
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(costumeData.getChecksum(),
				costumeData.getAbsolutePath());

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

	public void testCopyCostume() {
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.copy_costume));
		if (solo.searchText(costumeDataList.get(0).getCostumeName() + "_"
				+ getActivity().getString(R.string.copy_costume_addition))) {
			assertEquals("the copy of the costume wasn't added to the costumeDataList in the sprite", 2,
					costumeDataList.size());
		} else {
			fail("copy costume didn't work");
		}
	}

	public void testDeleteCostume() {
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(700);
		ListAdapter adapter = ((CostumeActivity) solo.getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		int newCount = adapter.getCount();
		assertEquals("the old count was not rigth", 1, oldCount);
		assertEquals("the new count is not rigth - all costumes should be deleted", 0, newCount);
		assertEquals("the count of the costumeDataList is not right", 0, costumeDataList.size());
	}

	public void testRenameCostume() {
		String newName = "newName";
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.sound_rename));
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.sleep(300);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(500);
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		assertEquals("costume is not renamed in CostumeList", newName, costumeDataList.get(0).getCostumeName());
		if (!solo.searchText(newName)) {
			fail("costume not renamed in actual view");
		}
	}

	public void testToStageButton() {
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(500);
		//fu!?
		solo.clickOnImageButton(2); //sorry UiTestUtils.clickOnImageButton just won't work after switching tabs

		solo.sleep(5000);
		solo.assertCurrentActivity("not in stage", StageActivity.class);
		solo.goBack();
		solo.sleep(3000);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		assertEquals("costumeDataList in sprite doesn't hold the right number of costumeData", 1,
				costumeDataList.size());
	}

	public void testMainMenuButton() {
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(500);
		solo.clickOnImageButton(0);
		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
	}
}
