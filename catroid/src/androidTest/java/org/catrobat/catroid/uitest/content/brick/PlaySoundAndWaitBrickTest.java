/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uitest.content.brick;

import android.media.MediaPlayer;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.List;

public class PlaySoundAndWaitBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private File soundFile;
	private File soundFile2;
	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private List<SoundInfo> soundInfoList;

	public PlaySoundAndWaitBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		if (soundFile2 != null && soundFile2.exists()) {
			soundFile2.delete();
		}
		super.tearDown();
	}

	public void testSelectAndPlaySoundFile() {
		solo.clickOnText(soundName);
		solo.sleep(1000);
		assertTrue(soundName + " is not in Spinner", solo.searchText(soundName));
		assertTrue(soundName2 + " is not in Spinner", solo.searchText(soundName2));
		solo.clickOnText(soundName);
		assertTrue(soundName + " is not selected in Spinner", solo.searchText(soundName));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue("mediaPlayer is not playing", mediaPlayer.isPlaying());
		assertEquals("wrong file playing", 7592, mediaPlayer.getDuration());
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnText(soundName);
		solo.clickOnText(soundName2);
		assertTrue(soundName2 + " is not selected in Spinner", solo.searchText(soundName2));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		mediaPlayer = getMediaPlayers().get(0);
		assertTrue("mediaPlayer is not playing", mediaPlayer.isPlaying());
		assertEquals("wrong file playing", 4875, mediaPlayer.getDuration());
	}

	public void testDismissNewSoundDialog() {
		String newText = solo.getString(R.string.new_broadcast_message);

		solo.clickOnText(soundName);
		solo.clickOnText(newText);
		solo.waitForDialogToOpen(10000);
		solo.goBack();
		solo.waitForDialogToClose(10000);
		solo.sleep(500);
		assertEquals("Not in ScriptActivity", "ui.ScriptActivity", solo.getCurrentActivity().getLocalClassName());
		assertTrue("Spinner not updated", solo.searchText(soundName));
	}

	public void testAddNewSound() {
		String newText = solo.getString(R.string.new_broadcast_message);
		String recordedFilename = solo.getString(R.string.soundrecorder_recorded_filename);

		solo.clickOnText(soundName);
		solo.clickOnText(newText);

		String soundRecorderText = solo.getString(R.string.add_sound_from_recorder);
		solo.waitForText(soundRecorderText);
		assertTrue("Catroid Sound Recorder is not present", solo.searchText(soundRecorderText));
		solo.clickOnText(soundRecorderText);

		solo.waitForActivity(SoundRecorderActivity.class.getSimpleName());
		solo.clickOnImageButton(0);
		solo.sleep(500);
		solo.clickOnImageButton(0);

		solo.waitForText(recordedFilename);
		solo.waitForFragmentByTag(SoundFragment.TAG);
		solo.sleep(1000);
		assertTrue("New sound file is not selected", solo.waitForText(recordedFilename));

		solo.goBack();
		String programMenuActivityClass = ProgramMenuActivity.class.getSimpleName();
		assertTrue("Should be in " + programMenuActivityClass, solo.getCurrentActivity().getClass().getSimpleName()
				.equals(programMenuActivityClass));
	}

	private void createProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("sprite");
		Script testScript = new StartScript();

		PlaySoundAndWaitBrick playSoundAndWaitBrick = new PlaySoundAndWaitBrick();
		testScript.addBrick(playSoundAndWaitBrick);

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		soundInfoList = projectManager.getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
				"longsound.mp3", org.catrobat.catroid.test.R.raw.longsound, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
				"testsoundui.mp3", org.catrobat.catroid.test.R.raw.testsoundui, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile2.getName());
		soundInfo2.setTitle(soundName2);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);

		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());
	}

	@SuppressWarnings("unchecked")
	private List<MediaPlayer> getMediaPlayers() {
		return (List<MediaPlayer>) Reflection.getPrivateField(SoundManager.getInstance(), "mediaPlayers");
	}
}
