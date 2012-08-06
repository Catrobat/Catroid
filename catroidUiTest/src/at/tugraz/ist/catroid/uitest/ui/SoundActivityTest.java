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
package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SoundActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SoundActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private final int RESOURCE_SOUND = at.tugraz.ist.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = at.tugraz.ist.catroid.uitest.R.raw.testsoundui;

	private Solo solo;
	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private ArrayList<SoundInfo> soundInfoList;

	public SoundActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
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
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testDeleteSound() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		ListAdapter adapter = ((SoundActivity) solo.getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(200);
		adapter = ((SoundActivity) solo.getCurrentActivity()).getListAdapter();
		int newCount = adapter.getCount();
		assertEquals("the old count was not rigth", 2, oldCount);
		assertEquals("the new count is not rigth - one costume should be deleted", 1, newCount);
		assertEquals("the count of the costumeDataList is not right", 1, soundInfoList.size());
	}

	public void testRenameSound() {
		String newName = "newSoundName";
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.sound_name));
		assertTrue("wrong title of dialog", solo.searchText(soundName));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(200);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		assertEquals("sound is not renamed in SoundList", newName, soundInfoList.get(0).getTitle());
		if (!solo.searchText(newName)) {
			fail("sound not renamed in actual view");
		}
	}

	public void testRenameSoundMixedCase() {
		String newNameMixedCase = "TeStSoUNd1";
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(500);
		solo.clickOnView(solo.getView(R.id.sound_name));
		solo.sleep(300);
		solo.clearEditText(0);
		solo.enterText(0, newNameMixedCase);
		solo.sleep(300);
		solo.sendKey(Solo.ENTER);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		solo.sleep(500);
		assertEquals("sound is not renamed to mixed case", newNameMixedCase, soundInfoList.get(0).getTitle());
	}

	public void testPlayAndStopStound() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);
		solo.clickOnButton(getActivity().getString(R.string.sound_play));
		solo.sleep(100);
		assertTrue("Mediaplayer is not playing", soundInfo.isPlaying);
		solo.clickOnButton(getActivity().getString(R.string.sound_pause));
		solo.sleep(100);
		assertFalse("Mediaplayer is playing after touching stop button", soundInfo.isPlaying);
	}

	public void testToStageButton() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("not in stage", StageActivity.class);

		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		assertEquals("soundlist in sprite doesn't hold the right number of soundinfos", 2, soundInfoList.size());
		// needed to fix NullPointerException in next Testcase
		solo.finishInactiveActivities();
	}

	public void testMainMenuButton() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
		// needed to fix NullPointerException in next Testcase
		solo.finishInactiveActivities();
	}

	public void testDialogsOnChangeOrientation() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		String newName = "newTestName";
		String buttonOKText = solo.getCurrentActivity().getString(R.string.ok);
		solo.clickOnView(solo.getView(R.id.sound_name));
		assertTrue("Dialog is not visible", solo.searchText(buttonOKText));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("Dialog is not visible", solo.searchText(buttonOKText));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.goBack();
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		solo.sleep(100);
		assertTrue("Sounds wasnt renamed", solo.searchText(newName));
	}

	public void testSoundNames() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		renameSound(soundName, "sound");
		renameSound(soundName2, "sound");
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		assertEquals("sound not renamed correctly", "sound1", soundInfoList.get(1).getTitle());
	}

	private void renameSound(String currentSoundTitle, String newSoundTitle) {
		solo.clickOnText(currentSoundTitle);
		EditText editTextSoundTitle = (EditText) solo.getView(R.id.dialog_rename_sound_editText);
		solo.clearEditText(editTextSoundTitle);
		solo.enterText(editTextSoundTitle, newSoundTitle);
		String buttonOKText = solo.getCurrentActivity().getString(R.string.ok);
		solo.clickOnButton(buttonOKText);
	}
}
