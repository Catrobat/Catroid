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

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.ListAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SoundActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class DeleteDialogTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;

	private String costumeName = "costumeNametest";
	private File imageFile;
	private File imageFile2;
	private ArrayList<CostumeData> costumeDataList;
	private final int RESOURCE_IMAGE = at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;

	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private ArrayList<SoundInfo> soundInfoList;
	private final int RESOURCE_SOUND = at.tugraz.ist.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = at.tugraz.ist.catroid.uitest.R.raw.testsoundui;

	private ProjectManager projectManager = ProjectManager.getInstance();

	public DeleteDialogTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	private void createTestProject() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile2.getName());
		costumeData.setCostumeName("costumeNameTest2");
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();

		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "testsoundui.mp3",
				RESOURCE_SOUND2, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile2.getName());
		soundInfo2.setTitle(soundName2);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(soundInfo.getChecksum(),
				soundInfo.getAbsolutePath());
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(soundInfo2.getChecksum(),
				soundInfo2.getAbsolutePath());

	}

	public void testDeleteCostumes() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));

		assertTrue("No ok button found", solo.searchButton(getActivity().getString(R.string.ok)));
		assertTrue("No cancel button found", solo.searchButton(getActivity().getString(R.string.cancel_button)));

		ListAdapter adapter = ((CostumeActivity) solo.getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.cancel_button));
		int newCount = adapter.getCount();
		assertEquals("The costume number not ok after canceling the deletion", newCount, oldCount);

		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		solo.clickOnButton(getActivity().getString(R.string.ok));

		solo.sleep(1000);

		newCount = adapter.getCount();
		assertEquals("The costume was not deleted", oldCount - 1, newCount);
		assertEquals("The costume was not deleted from costumeDataList", newCount, costumeDataList.size());

	}

	public void testDeleteSounds() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));

		assertTrue("No ok button found", solo.searchButton(getActivity().getString(R.string.ok)));
		assertTrue("No cancel button found", solo.searchButton(getActivity().getString(R.string.cancel_button)));

		ListAdapter adapter = ((SoundActivity) solo.getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.cancel_button));
		int newCount = adapter.getCount();
		assertEquals("The costume number not ok after canceling the deletion", newCount, oldCount);

		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		solo.clickOnButton(getActivity().getString(R.string.ok));

		solo.sleep(1000);

		newCount = adapter.getCount();
		assertEquals("The sound was not deleted", oldCount - 1, newCount);
		assertEquals("The sound was not deleted from costumeDataList", newCount, soundInfoList.size());

	}
}
