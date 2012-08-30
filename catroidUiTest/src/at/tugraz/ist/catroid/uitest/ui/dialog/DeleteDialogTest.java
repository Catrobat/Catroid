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
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.fragment.CostumeFragment;
import at.tugraz.ist.catroid.ui.fragment.SoundFragment;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class DeleteDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private final int RESOURCE_IMAGE = at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;
	private final int RESOURCE_SOUND = at.tugraz.ist.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = at.tugraz.ist.catroid.uitest.R.raw.testsoundui;
	private Solo solo;

	private String costumeName = "costumeNametest";
	private File imageFile;
	private File imageFile2;
	private ArrayList<CostumeData> costumeDataList;

	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private ArrayList<SoundInfo> soundInfoList;

	public DeleteDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
		soundInfoList = ProjectManager.INSTANCE.getCurrentSprite().getSoundList();
		costumeDataList = ProjectManager.INSTANCE.getCurrentSprite().getCostumeDataList();
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		ProjectManager.getInstance().deleteCurrentProject();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testDeleteCostumes() throws Exception {
		addCostumesToProject();
		String buttonOkText = solo.getString(R.string.ok);
		String buttonCancelText = solo.getString(R.string.cancel_button);
		String deleteCostumeText = solo.getString(R.string.sound_delete);
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);

		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(200);
		solo.clickOnButton(deleteCostumeText);

		assertTrue("No ok button found", solo.searchButton(buttonOkText));
		assertTrue("No cancel button found", solo.searchButton(buttonCancelText));

		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		CostumeFragment fragment = (CostumeFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_COSTUMES);
		ListAdapter adapter = fragment.getListAdapter();

		int oldCount = adapter.getCount();
		solo.clickOnButton(buttonCancelText);
		int newCount = adapter.getCount();
		assertEquals("The costume number not ok after canceling the deletion", newCount, oldCount);

		solo.clickOnButton(deleteCostumeText);
		solo.clickOnButton(buttonOkText);

		solo.sleep(500);
		newCount = adapter.getCount();
		assertEquals("The costume was not deleted", oldCount - 1, newCount);
		assertEquals("The costume was not deleted from costumeDataList", newCount, costumeDataList.size());
	}

	public void testDeleteSounds() throws Exception {
		addSoundsToProject();
		String buttonOkText = solo.getString(R.string.ok);
		String buttonCancelText = solo.getString(R.string.cancel_button);
		String deleteSoundText = solo.getString(R.string.sound_delete);
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);

		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(200);
		solo.clickOnButton(deleteSoundText);

		assertTrue("No ok button found", solo.searchButton(buttonOkText));
		assertTrue("No cancel button found", solo.searchButton(buttonCancelText));

		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		SoundFragment fragment = (SoundFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SOUNDS);
		ListAdapter adapter = fragment.getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(buttonCancelText);
		int newCount = adapter.getCount();
		assertEquals("The costume number not ok after canceling the deletion", newCount, oldCount);

		solo.clickOnButton(deleteSoundText);
		solo.clickOnButton(buttonOkText);

		solo.sleep(500);
		newCount = adapter.getCount();
		assertEquals("The sound was not deleted", oldCount - 1, newCount);
		assertEquals("The sound was not deleted from costumeDataList", newCount, soundInfoList.size());
	}

	@SuppressWarnings("deprecation")
	private void addCostumesToProject() throws Exception {
		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);

		costumeDataList = ProjectManager.INSTANCE.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(costumeData.getChecksum(),
				costumeData.getAbsolutePath());
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile2.getName());
		costumeData.setCostumeName("costumeNameTest2");
		costumeDataList.add(costumeData);
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(costumeData.getChecksum(),
				costumeData.getAbsolutePath());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		ProjectManager.INSTANCE.getCurrentProject().virtualScreenWidth = display.getWidth();
		ProjectManager.INSTANCE.getCurrentProject().virtualScreenHeight = display.getHeight();
	}

	private void addSoundsToProject() throws Exception {
		soundInfoList = ProjectManager.INSTANCE.getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getActivity(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "testsoundui.mp3",
				RESOURCE_SOUND2, getActivity(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile2.getName());
		soundInfo2.setTitle(soundName2);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(),
				soundInfo.getAbsolutePath());
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(soundInfo2.getChecksum(),
				soundInfo2.getAbsolutePath());
	}
}
