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
package at.tugraz.ist.catroid.test.content.brick;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.util.Utils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class PlaySoundBrickTest extends InstrumentationTestCase {
	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;
	private final int timeoutMarginInMilliseconds = 200; // acceptable time margin for PlaySoundBrick to finish playing sound
	private String projectName = "projectiName";

	@Override
	protected void setUp() throws Exception {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		UtilFile.deleteDirectory(directory);
		this.createTestProject();
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		Utils.clearProject(projectName);
		SoundManager.getInstance().clear();
	}

	public void testPlaySound() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		PlaySoundBrick testBrick = new PlaySoundBrick(new Sprite("1"));
		testBrick.setPathToSoundfile(soundFile.getName());
		testBrick.execute();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		final int duration = mediaPlayer.getDuration() + timeoutMarginInMilliseconds;
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse("MediaPlayer is not done playing", mediaPlayer.isPlaying());
	}

	public void testIllegalArgument() {
		final String illegalPath = "file/that/does/not/exist";
		PlaySoundBrick testBrick = new PlaySoundBrick(new Sprite("2"));
		testBrick.setPathToSoundfile(illegalPath);
		try {
			testBrick.execute();
			fail("Execution of PlaySoundBrick with illegal file path did not cause an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testPlayMultipleSounds() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		for (int i = 0; i < SoundManager.MAX_MEDIA_PLAYERS; i++) {
			MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
			PlaySoundBrick testBrick = new PlaySoundBrick(new Sprite("3"));
			testBrick.setPathToSoundfile(soundFile.getName());
			testBrick.execute();
			assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
		}
	}

	public void testPlaySimultaneousSounds() throws InterruptedException {
		Thread soundThread01 = new Thread(new Runnable() {
			//final String soundFilePath = soundFile.getAbsolutePath();
			PlaySoundBrick testBrick1 = new PlaySoundBrick(new Sprite("4"));

			public void run() {
				testBrick1.setPathToSoundfile(soundFile.getName());
				testBrick1.execute();
			}
		});

		Thread soundThread02 = new Thread(new Runnable() {
			//final String soundFilePath = soundFile.getAbsolutePath();
			PlaySoundBrick testBrick2 = new PlaySoundBrick(new Sprite("5"));

			public void run() {
				testBrick2.setPathToSoundfile(soundFile.getName());
				testBrick2.execute();
			}
		});

		soundThread01.start();
		soundThread02.start();
		Thread.sleep(1000);
		//Test fails if MediaPlayer throws IllegalArgumentException
	}

	public void testPauseAndResume() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		PlaySoundBrick testBrick = new PlaySoundBrick(new Sprite("4"));
		testBrick.setPathToSoundfile(soundFile.getName());
		testBrick.execute();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		mediaPlayer.pause();
		assertFalse("MediaPlayer is still playing after pause", mediaPlayer.isPlaying());

		mediaPlayer.start();
		assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());

		final int duration = mediaPlayer.getDuration() + timeoutMarginInMilliseconds;
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse("MediaPlayer is not done playing after pause and resume", mediaPlayer.isPlaying());
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		setUpSoundFile();
	}

	private void setUpSoundFile() throws IOException {

		soundFile = Utils.saveFileToProject(projectName, "soundTest.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), Utils.TYPE_SOUND_FILE);

	}
}
