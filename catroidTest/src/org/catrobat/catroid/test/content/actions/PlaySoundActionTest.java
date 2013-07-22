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
package org.catrobat.catroid.test.content.actions;

import java.io.File;
import java.io.IOException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.PlaySoundAction;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;

public class PlaySoundActionTest extends InstrumentationTestCase {
	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;
	private String projectName = "projectName";
	private SoundInfo tempSoundInfo;

	@Override
	protected void setUp() throws Exception {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectName);
		UtilFile.deleteDirectory(directory);
		this.createTestProject();
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		TestUtils.clearProject(projectName);
		SoundManager.getInstance().clear();
		super.tearDown();
	}

	public void testPlaySound() throws InterruptedException {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		Sprite testSprite = new Sprite("1");
		tempSoundInfo = getSoundInfo();
		testSprite.getSoundList().add(tempSoundInfo);
		PlaySoundAction action = ExtendedActions.playSound(testSprite, tempSoundInfo);
		action.act(1.0f);
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
	}

	public void testIllegalArgument() {
		Sprite testSprite = new Sprite("2");
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName("illegalFileName");
		testSprite.getSoundList().add(soundInfo);
		PlaySoundAction action = ExtendedActions.playSound(testSprite, soundInfo);
		try {
			action.act(1.0f);
			fail("Execution of PlaySoundBrick with illegal file path did not cause an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testPlaySimultaneousSounds() throws InterruptedException {
		Thread soundThread01 = new Thread(new Runnable() {
			PlaySoundAction action = ExtendedActions.playSound(new Sprite("4"), getSoundInfo());

			public void run() {
				action.act(1.0f);
			}
		});

		Thread soundThread02 = new Thread(new Runnable() {
			PlaySoundAction action = ExtendedActions.playSound(new Sprite("5"), getSoundInfo());

			public void run() {
				action.act(1.0f);
			}
		});

		soundThread01.start();
		soundThread02.start();
		Thread.sleep(500);
		//Test fails if MediaPlayer throws IllegalArgumentException
	}

	public void testPauseAndResume() throws InterruptedException {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		Sprite testSprite = new Sprite("4");
		tempSoundInfo = getSoundInfo();
		testSprite.getSoundList().add(tempSoundInfo);
		PlaySoundAction action = ExtendedActions.playSound(testSprite, tempSoundInfo);
		action.act(1.0f);
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		mediaPlayer.pause();
		assertFalse("MediaPlayer is still playing after pause", mediaPlayer.isPlaying());

		mediaPlayer.start();
		assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.INSTANCE.setProject(project);

		setUpSoundFile();
	}

	private void setUpSoundFile() throws IOException {
		soundFile = TestUtils.saveFileToProject(projectName, "soundTest.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
	}

	private SoundInfo getSoundInfo() {
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle("testsSoundFile");
		return soundInfo;
	}
}
