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
import org.catrobat.catroid.content.actions.StopAllSoundsAction;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;

public class StopAllSoundsActionTest extends InstrumentationTestCase {
	private static final int SOUND_FILE_ID = R.raw.longtestsound;
	private File soundFile;
	private String projectName = "projectName";

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

	public void testStopOneSound() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		Sprite testSprite = new Sprite("1");
		SoundInfo soundInfo = getSoundInfo();
		testSprite.getSoundList().add(soundInfo);
		PlaySoundAction playSoundAction = ExtendedActions.playSound(testSprite, soundInfo);
		StopAllSoundsAction stopAllSoundsAction = ExtendedActions.stopAllSounds();
		playSoundAction.act(1.0f);
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
		stopAllSoundsAction.act(1.0f);
		assertFalse("MediaPlayer is still playing", mediaPlayer.isPlaying());

	}

	public void testStopSimultaneousPlayingSounds() throws InterruptedException {
		final MediaPlayer mediaPlayer1 = SoundManager.getInstance().getMediaPlayer();
		final MediaPlayer mediaPlayer2 = SoundManager.getInstance().getMediaPlayer();

		class ThreadSound1 extends Thread {
			Sprite testSprite = new Sprite("8");

			@Override
			public void run() {
				SoundInfo soundInfo = getSoundInfo();
				PlaySoundAction action = ExtendedActions.playSound(testSprite, soundInfo);
				testSprite.getSoundList().add(soundInfo);
				action.act(1.0f);
			}
		}

		class ThreadSound2 extends Thread {
			Sprite testSprite = new Sprite("9");;

			@Override
			public void run() {
				SoundInfo soundInfo = getSoundInfo();
				PlaySoundAction action = ExtendedActions.playSound(testSprite, soundInfo);
				testSprite.getSoundList().add(soundInfo);
				action.act(1.0f);
			}
		}

		StopAllSoundsAction action = ExtendedActions.stopAllSounds();
		ThreadSound1 threadSound1 = new ThreadSound1();
		ThreadSound2 threadSound2 = new ThreadSound2();
		threadSound1.start();
		threadSound2.start();
		Thread.sleep(200);
		assertTrue("mediaPlayer1 is not playing", mediaPlayer1.isPlaying());
		assertTrue("mediaPlayer2 is not playing", mediaPlayer2.isPlaying());
		action.act(1.0f);
		assertFalse("mediaPlayer1 is not stopped", mediaPlayer1.isPlaying());
		assertFalse("mediaPlayer2 is not stopped", mediaPlayer2.isPlaying());
		Thread.sleep(1000);
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.INSTANCE.setProject(project);

		setUpSoundFile();
	}

	private void setUpSoundFile() throws IOException {
		soundFile = TestUtils.saveFileToProject(projectName, "longtestsound", SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
	}

	private SoundInfo getSoundInfo() {
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle("testsSoundFile");
		return soundInfo;
	}
}
