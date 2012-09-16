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

import android.media.MediaPlayer;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class PlaySoundBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private final int RESOURCE_SOUND = at.tugraz.ist.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = at.tugraz.ist.catroid.uitest.R.raw.testsoundui;

	private Solo solo;
	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private ArrayList<SoundInfo> soundInfoList;

	public PlaySoundBrickTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();

		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		PlaySoundBrick playSoundBrick = new PlaySoundBrick(firstSprite);
		testScript.addBrick(playSoundBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		soundInfoList = projectManager.getCurrentSprite().getSoundList();

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
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		if (soundFile.exists()) {
			soundFile.delete();
		}
		if (soundFile2.exists()) {
			soundFile2.delete();
		}
		super.tearDown();
	}

	public void testSelectandPlaySoundFile() {
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.sleep(1000);
		assertTrue(soundName + " is not in Spinner", solo.searchText(soundName));
		assertTrue(soundName2 + " is not in Spinner", solo.searchText(soundName2));
		solo.clickOnText(soundName);
		assertTrue(soundName + " is not selected in Spinner", solo.searchText(soundName));
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertTrue("mediaPlayer is not playing", mediaPlayer.isPlaying());
		assertEquals("wrong file playing", 7592, mediaPlayer.getDuration());
		solo.goBack();
		solo.goBack();

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.clickOnText(soundName);
		solo.clickOnText(soundName2);
		assertTrue(soundName2 + " is not selected in Spinner", solo.searchText(soundName2));
		mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertTrue("mediaPlayer is not playing", mediaPlayer.isPlaying());
		assertEquals("wrong file playing", 4875, mediaPlayer.getDuration());
	}

	public void testSpinnerUpdatesDelete() {
		String spinnerNothingText = solo.getString(R.string.broadcast_nothing_selected);
		String buttonDeleteText = solo.getString(R.string.sound_delete);
		solo.clickOnText(spinnerNothingText);
		assertTrue(soundName + " is not in Spinner", solo.searchText(soundName));
		assertTrue(soundName2 + " is not in Spinner", solo.searchText(soundName2));
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForText(buttonDeleteText);
		solo.clickOnButton(buttonDeleteText);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.clickOnText(getActivity().getString(R.string.scripts));
		solo.clickOnText(spinnerNothingText);
		assertFalse(soundName + " is still in Spinner", solo.searchText(soundName));
		assertTrue(soundName2 + " is not in Spinner", solo.searchText(soundName2));
	}

	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";
		String spinnerNothingText = solo.getString(R.string.broadcast_nothing_selected);

		solo.clickOnText(spinnerNothingText);
		assertTrue(soundName + " is not in Spinner", solo.searchText(soundName));
		assertTrue(soundName2 + " is not in Spinner", solo.searchText(soundName2));
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.clickOnView(solo.getView(R.id.sound_name));
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		solo.goBack();
		solo.sendKey(Solo.ENTER);
		solo.waitForDialogToClose(500);
		solo.sleep(500);
		solo.clickOnText(getActivity().getString(R.string.scripts));
		solo.clickOnText(spinnerNothingText);
		assertTrue(newName + " is not in Spinner", solo.searchText(newName));
		assertTrue(soundName2 + " is not in Spinner", solo.searchText(soundName2));
	}
}
