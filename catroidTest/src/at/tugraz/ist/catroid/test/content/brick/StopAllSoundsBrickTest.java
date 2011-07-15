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
import at.tugraz.ist.catroid.content.bricks.StopAllSoundsBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class StopAllSoundsBrickTest extends InstrumentationTestCase {
	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;
	private String projectName = "projectName";

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

	public void testStopOneSound() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		PlaySoundBrick testBrick = new PlaySoundBrick(new Sprite("1"));
		StopAllSoundsBrick testBrick1 = new StopAllSoundsBrick(new Sprite("1"));
		testBrick.setPathToSoundfile(soundFile.getName());
		testBrick.execute();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
		testBrick1.execute();
		assertFalse("MediaPlayer is still playing", mediaPlayer.isPlaying());

	}

	public void testStopSimultaneousPlayingSounds() throws InterruptedException {
		final MediaPlayer mediaPlayer1 = SoundManager.getInstance().getMediaPlayer();
		final MediaPlayer mediaPlayer2 = SoundManager.getInstance().getMediaPlayer();
		class ThreadSound1 extends Thread {
			PlaySoundBrick testBrick1 = new PlaySoundBrick(new Sprite("8"));

			@Override
			public void run() {
				testBrick1.setPathToSoundfile(soundFile.getName());
				testBrick1.execute();
			}
		}

		class ThreadSound2 extends Thread {
			PlaySoundBrick testBrick1 = new PlaySoundBrick(new Sprite("9"));

			@Override
			public void run() {
				testBrick1.setPathToSoundfile(soundFile.getName());
				testBrick1.execute();
			}
		}

		StopAllSoundsBrick testBrick1 = new StopAllSoundsBrick(new Sprite("10"));
		ThreadSound1 th1 = new ThreadSound1();
		ThreadSound2 th2 = new ThreadSound2();
		th1.start();
		th2.start();
		Thread.sleep(100);
		assertTrue("mediaPlayer1 is not playing", mediaPlayer1.isPlaying());
		assertTrue("mediaPlayer2 is not playing", mediaPlayer2.isPlaying());
		testBrick1.execute();
		assertFalse("mediaPlayer1 is not stopped", mediaPlayer1.isPlaying());
		assertFalse("mediaPlayer2 is not stopped", mediaPlayer2.isPlaying());
		Thread.sleep(1000);
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		setUpSoundFile();
	}

	private void setUpSoundFile() throws IOException {
		soundFile = TestUtils.saveFileToProject(projectName, "longtestsound", SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
	}
}
