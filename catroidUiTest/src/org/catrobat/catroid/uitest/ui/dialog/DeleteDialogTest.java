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

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.ListAdapter;

import com.jayway.android.robotium.solo.Solo;

public class DeleteDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private final int RESOURCE_IMAGE = org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = org.catrobat.catroid.uitest.R.drawable.catroid_banzai;
	private final int RESOURCE_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = org.catrobat.catroid.uitest.R.raw.testsoundui;
	private Solo solo;

	private String lookName = "lookNametest";
	private File imageFile;
	private File imageFile2;
	private ArrayList<LookData> lookDataList;

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
		lookDataList = ProjectManager.INSTANCE.getCurrentSprite().getLookDataList();
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

	public void testDeleteLooks() throws Exception {
		addLooksToProject();
		String buttonOkText = solo.getString(R.string.ok);
		String buttonCancelText = solo.getString(R.string.cancel_button);
		String deleteLookText = solo.getString(R.string.delete);
		String scriptsSpinnerText = solo.getString(R.string.scripts);
		String looksSpinnerText = solo.getString(R.string.category_looks);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.changeToFragmentViaActionbar(solo, scriptsSpinnerText, looksSpinnerText);
		UiTestUtils.waitForFragment(solo, R.id.fragment_look_relative_layout);

		clickOnContextMenuItem(lookName, deleteLookText);

		assertTrue("No ok button found", solo.searchButton(buttonOkText));
		assertTrue("No cancel button found", solo.searchButton(buttonCancelText));

		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		LookFragment fragment = (LookFragment) activity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
		ListAdapter adapter = fragment.getListAdapter();

		int oldCount = adapter.getCount();
		solo.clickOnButton(buttonCancelText);
		int newCount = adapter.getCount();
		assertEquals("The look number not ok after canceling the deletion", newCount, oldCount);

		clickOnContextMenuItem(lookName, deleteLookText);
		solo.clickOnButton(buttonOkText);

		solo.sleep(500);
		newCount = adapter.getCount();
		assertEquals("The look was not deleted", oldCount - 1, newCount);
		assertEquals("The look was not deleted from lookDataList", newCount, lookDataList.size());
	}

	public void testDeleteSounds() throws Exception {
		addSoundsToProject();
		String buttonOkText = solo.getString(R.string.ok);
		String buttonCancelText = solo.getString(R.string.cancel_button);
		String deleteSoundText = solo.getString(R.string.delete);
		String scriptsSpinnerText = solo.getString(R.string.scripts);
		String soundsSpinnerText = solo.getString(R.string.sounds);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.changeToFragmentViaActionbar(solo, scriptsSpinnerText, soundsSpinnerText);
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound_relative_layout);
		solo.clickLongOnText(soundName);
		solo.clickOnText(deleteSoundText);

		assertTrue("No ok button found", solo.searchButton(buttonOkText));
		assertTrue("No cancel button found", solo.searchButton(buttonCancelText));

		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		SoundFragment fragment = (SoundFragment) activity.getFragment(ScriptActivity.FRAGMENT_SOUNDS);
		ListAdapter adapter = fragment.getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(buttonCancelText);
		int newCount = adapter.getCount();
		assertEquals("The look number not ok after canceling the deletion", newCount, oldCount);

		solo.clickLongOnText(soundName);
		solo.clickOnText(deleteSoundText);
		solo.clickOnButton(buttonOkText);

		solo.sleep(500);
		newCount = adapter.getCount();
		assertEquals("The sound was not deleted", oldCount - 1, newCount);
		assertEquals("The sound was not deleted from lookDataList", newCount, soundInfoList.size());
	}

	@SuppressWarnings("deprecation")
	private void addLooksToProject() throws Exception {
		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);

		lookDataList = ProjectManager.INSTANCE.getCurrentSprite().getLookDataList();
		LookData lookData = new LookData();
		lookData.setLookFilename(imageFile.getName());
		lookData.setLookName(lookName);
		lookDataList.add(lookData);
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(lookData.getChecksum(),
				lookData.getAbsolutePath());
		lookData = new LookData();
		lookData.setLookFilename(imageFile2.getName());
		lookData.setLookName("lookNameTest2");
		lookDataList.add(lookData);
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(lookData.getChecksum(),
				lookData.getAbsolutePath());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		ProjectManager.INSTANCE.getCurrentProject().getXmlHeader().virtualScreenWidth = display.getWidth();
		ProjectManager.INSTANCE.getCurrentProject().getXmlHeader().virtualScreenHeight = display.getHeight();
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

	private void clickOnContextMenuItem(String lookName, String menuItemName) {
		solo.clickLongOnText(lookName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}
}
