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
package at.tugraz.ist.catroid.test.content.brick;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class PlaySoundBrickTest extends InstrumentationTestCase {
	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;
	private String projectName = "projectName";
	private SoundInfo tempSoundInfo;

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
		TestUtils.clearProject(projectName);
		SoundManager.getInstance().clear();
	}

	public void testPlaySound() throws InterruptedException {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		Sprite testSprite = new Sprite("1");
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(testSprite);
		tempSoundInfo = getSoundInfo();
		playSoundBrick.setSoundInfo(tempSoundInfo);
		testSprite.getSoundList().add(tempSoundInfo);
		playSoundBrick.execute();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
	}

	public void testIllegalArgument() {
		Sprite testSprite = new Sprite("2");
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(testSprite);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName("illegalFileName");
		playSoundBrick.setSoundInfo(soundInfo);
		testSprite.getSoundList().add(soundInfo);
		try {
			playSoundBrick.execute();
			fail("Execution of PlaySoundBrick with illegal file path did not cause an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testPlaySimultaneousSounds() throws InterruptedException {
		Thread soundThread01 = new Thread(new Runnable() {
			PlaySoundBrick playSoundBrick = new PlaySoundBrick(new Sprite("4"));

			public void run() {
				playSoundBrick.setSoundInfo(getSoundInfo());
				playSoundBrick.execute();
			}
		});

		Thread soundThread02 = new Thread(new Runnable() {
			PlaySoundBrick playSoundBrick2 = new PlaySoundBrick(new Sprite("5"));

			public void run() {
				playSoundBrick2.setSoundInfo(getSoundInfo());
				playSoundBrick2.execute();
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
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(testSprite);
		tempSoundInfo = getSoundInfo();
		playSoundBrick.setSoundInfo(tempSoundInfo);
		testSprite.getSoundList().add(tempSoundInfo);
		playSoundBrick.execute();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		mediaPlayer.pause();
		assertFalse("MediaPlayer is still playing after pause", mediaPlayer.isPlaying());

		mediaPlayer.start();
		assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

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
