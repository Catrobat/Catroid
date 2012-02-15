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
package at.tugraz.ist.catroid.test.io;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class SoundManagerTest extends InstrumentationTestCase {
	private static final int LONG_TEST_SOUND = R.raw.longtestsound;
	private static final int TEST_SOUND = R.raw.testsound;
	private File soundFile;
	private File longSoundFile;

	@Override
	protected void setUp() throws Exception {
		soundFile = TestUtils.createTestMediaFile(Consts.DEFAULT_ROOT + "testSound.mp3", TEST_SOUND,
				getInstrumentation().getContext());

		longSoundFile = TestUtils.createTestMediaFile(Consts.DEFAULT_ROOT + "longTestSound.mp3", LONG_TEST_SOUND,
				getInstrumentation().getContext());
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		if (longSoundFile != null && longSoundFile.exists()) {
			longSoundFile.delete();
		}
		SoundManager.getInstance().clear();
	}

	public void testGetInstance() {
		SoundManager soundManager = SoundManager.getInstance();
		assertNotNull("SoundManager could not be initialized", soundManager);
	}

	public void testGetMediaPlayer() {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		assertNotNull("SoundManager failed to return a MediaPlayer", mediaPlayer);
		assertFalse("SoundManager provided a MediaPlayer that was already playing", mediaPlayer.isPlaying());
	}

	public void testClear() {
		SoundManager soundManager = SoundManager.getInstance();
		soundManager.clear();
		assertEquals("SoundManager was destroyed when calling clear", soundManager, SoundManager.getInstance());

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		assertNotNull("SoundManager failed to return a MediaPlayer after clear", mediaPlayer);
		assertFalse("SoundManager provided a MediaPlayer that was already playing", mediaPlayer.isPlaying());
	}

	public void testPauseAndResume() throws IllegalStateException, IOException, InterruptedException {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		mediaPlayer.setDataSource(soundFilePath);
		mediaPlayer.prepare();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		mediaPlayer.start();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		SoundManager.getInstance().pause();
		assertFalse("MediaPlayer is still playing after SoundManager was paused", mediaPlayer.isPlaying());

		SoundManager.getInstance().resume();
		assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());

		final int duration = mediaPlayer.getDuration() + 100;
		Thread.sleep(duration);

		assertFalse("MediaPlayer is not done playing after pause and resume", mediaPlayer.isPlaying());
	}

	/**
	 * TODO: Test was meaningless before (because it only really tested one MediaPlayer) and fails now.
	 * There seems to be a bigger problem with the SoundManager itself. See ticket "Improve MediaPlayer recycling" for
	 * details.
	 */
	//	public void testPauseAndResumeMultiplePlayers() throws IllegalArgumentException, IllegalStateException, IOException {
	//		final String soundFilePath = soundFile.getAbsolutePath();
	//		assertNotNull("Could not open test sound file", soundFilePath);
	//		assertTrue("Could not open test sound file", soundFilePath.length() > 0);
	//
	//		ArrayList<MediaPlayer> mediaPlayers = new ArrayList<MediaPlayer>();
	//		for (int i = 0; i < SoundManager.MAX_MEDIA_PLAYERS; i++) {
	//			MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
	//			mediaPlayers.add(mediaPlayer);
	//			mediaPlayer.setDataSource(soundFilePath);
	//			mediaPlayer.prepare();
	//			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	//				public void onCompletion(MediaPlayer mp) {
	//					mp.release();
	//				}
	//			});
	//			mediaPlayer.start();
	//			mediaPlayer.pause();
	//		}
	//
	//		for (int i = 0; i < mediaPlayers.size(); i++) {
	//			for (int j = i + 1; j < mediaPlayers.size(); j++) {
	//				assertNotSame("SoundManager returned the same MediaPlayer twice. i = " + i + ", j = " + j,
	//						mediaPlayers.get(i), mediaPlayers.get(j));
	//			}
	//		}
	//
	//		for (MediaPlayer mediaPlayer : mediaPlayers) {
	//			mediaPlayer.start();
	//			assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
	//		}
	//
	//		SoundManager.getInstance().pause();
	//		for (MediaPlayer mediaPlayer : mediaPlayers) {
	//			assertFalse("MediaPlayer is still playing after SoundManager was paused", mediaPlayer.isPlaying());
	//		}
	//
	//		SoundManager.getInstance().resume();
	//		for (MediaPlayer mediaPlayer : mediaPlayers) {
	//			assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());
	//		}
	//	}

	public void testMediaPlayerLimit() throws IllegalArgumentException, IllegalStateException, IOException {
		assertNotNull("Test sound file was not copied properly", longSoundFile);
		final String soundFilePath = longSoundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		for (int i = 0; i < SoundManager.MAX_MEDIA_PLAYERS; i++) {
			MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
			mediaPlayer.setDataSource(soundFilePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
			mediaPlayer.start();
			assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
		}

		assertNull("Too many MediaPlayers created by SoundManager", SoundManager.getInstance().getMediaPlayer());
	}

	public void testSetVolume() {
		float vol = 80.9f;
		SoundManager.getInstance().setVolume(vol);
		assertEquals("Volume loudness doesn't change", vol, SoundManager.getInstance().getVolume());
	}
}
