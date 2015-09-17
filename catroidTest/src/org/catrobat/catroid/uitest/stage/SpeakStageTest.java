/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SpeakStageTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private SoundManagerMock soundManagerMock;

	private final String testText = "Test test.";
	private final File speechFileTestText = new File(Constants.TEXT_TO_SPEECH_TMP_PATH, Utils.md5Checksum(testText)
			+ Constants.SOUND_STANDARD_EXTENSION);

	private final String anotherLongerText = "This text is slightly longer than the Test test.";
	private final File speechFileAnotherLongerText = new File(Constants.TEXT_TO_SPEECH_TMP_PATH, Utils.md5Checksum(anotherLongerText)
			+ Constants.SOUND_STANDARD_EXTENSION);

	public SpeakStageTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		soundManagerMock = new SoundManagerMock();
		Reflection.setPrivateField(SoundManager.class, "INSTANCE", soundManagerMock);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		deleteSpeechFiles();
	}

	private void createSingleTestProject() {
		Sprite spriteNormal = new Sprite("testSingleSpeech");

		Script startScriptNormal = new StartScript();
		startScriptNormal.addBrick(new SpeakBrick(testText));
		startScriptNormal.addBrick(new WaitBrick(1000));

		spriteNormal.addScript(startScriptNormal);

		ArrayList<Sprite> spriteListNormal = new ArrayList<Sprite>();
		spriteListNormal.add(spriteNormal);

		UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteListNormal, getActivity().getApplicationContext());
		prepareStageForTesting();
	}

	private void createMultipleSpeechTestProject() {
		Sprite spriteMultiple = new Sprite("testMultipleSpeech");

		Script startScriptMultiple = new StartScript();
		startScriptMultiple.addBrick(new SpeakBrick(anotherLongerText));
		startScriptMultiple.addBrick(new SpeakBrick(testText));
		startScriptMultiple.addBrick(new WaitBrick(1000));

		spriteMultiple.addScript(startScriptMultiple);

		ArrayList<Sprite> spriteListNormal = new ArrayList<Sprite>();
		spriteListNormal.add(spriteMultiple);

		UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteListNormal, getActivity().getApplicationContext());
		prepareStageForTesting();
	}

	private void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		pathToSpeechFiles.mkdirs();
		File[] files = pathToSpeechFiles.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	private void prepareStageForTesting() {
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	}

	@Device
	public void testSingleSpeech() {
		createSingleTestProject();
		solo.waitForActivity(StageActivity.class.getSimpleName());
		int currentTry = 0;
		boolean found = false;
		while (currentTry != 60) {
			currentTry++;
			if (speechFileTestText.exists()) {
				found = true;
				break;
			}
			solo.sleep(1000);
		}

		assertTrue("some of the required speechfiles do not exist.", found);

		solo.sleep(5000);

		assertTrue("speechFileTestText was not played.",
				soundManagerMock.playedSoundFiles.contains(speechFileTestText.getAbsolutePath()));
		assertEquals("Wrong amount of soundfiles played", 1, soundManagerMock.playedSoundFiles.size());
	}

	@Device
	public void testMultipleSimultaneousSpeech() {
		createMultipleSpeechTestProject();
		solo.waitForActivity(StageActivity.class.getSimpleName());
		int currentTry = 0;
		boolean found = false;
		while (currentTry != 120) {
			currentTry++;
			if (speechFileTestText.exists() && speechFileAnotherLongerText.exists()) {
				found = true;
				break;
			}
			solo.sleep(1000);
		}

		assertTrue("some of the required speechfiles do not exist.", found);

		currentTry = 0;
		found = false;
		while (currentTry != 60) {
			currentTry++;
			if (soundManagerMock.playedSoundFiles.contains(speechFileTestText.getAbsolutePath())) {
				found = true;
				break;
			}
			solo.sleep(1000);
		}

		assertTrue("speechFileTestText was not played.",
				soundManagerMock.playedSoundFiles.contains(speechFileTestText.getAbsolutePath()));

		currentTry = 0;
		found = false;
		while (currentTry != 60) {
			currentTry++;
			if (soundManagerMock.playedSoundFiles.contains(speechFileAnotherLongerText.getAbsolutePath())) {
				found = true;
				break;
			}
			solo.sleep(1000);
		}

		assertTrue("speechFileAnotherLongerText was not played.",
				soundManagerMock.playedSoundFiles.contains(speechFileAnotherLongerText.getAbsolutePath()));

		assertEquals("Wrong amount of soundfiles played", 2, soundManagerMock.playedSoundFiles.size());
	}

	@Device
	public void testDeleteSpeechFiles() {
		createMultipleSpeechTestProject();
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		int currentTry = 0;
		boolean found = false;
		while (currentTry != 120) {
			currentTry++;
			if (speechFileTestText.exists()) {
				found = true;
				break;
			}
			solo.sleep(1000);
		}

		assertTrue("some of the required speechfiles do not exist.", found);

		UiTestUtils.goToHomeActivity(getActivity());
		solo.waitForActivity(MainMenuActivity.class);

		File file = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);

		currentTry = 0;
		while (currentTry != 60) {
			currentTry++;
			if (file.listFiles().length == 0) {
				break;
			}
			solo.sleep(1000);
		}

		assertEquals("TextToSpeech folder is not empty", 0, file.listFiles().length);
	}

	private class SoundManagerMock extends SoundManager {

		private final Set<String> playedSoundFiles = new HashSet<String>();

		@Override
		public synchronized void playSoundFile(String pathToSoundfile) {
			playedSoundFiles.add(pathToSoundfile);
		}
	}
}
