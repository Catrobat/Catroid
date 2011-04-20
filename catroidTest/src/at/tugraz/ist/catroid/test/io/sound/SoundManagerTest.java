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
package at.tugraz.ist.catroid.test.io.sound;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.test.R;

public class SoundManagerTest extends InstrumentationTestCase {
	private File soundFile;
	private File longSoundFile;
	
	@Override
	protected void setUp() throws Exception {
		// Note: Files need to be copied as MediaPlayer has no access to resources
		BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources().openRawResource(R.raw.testsound));
		soundFile = File.createTempFile("testSound", ".mp3");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(soundFile), 1024);

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
		
		inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources().openRawResource(R.raw.longtestsound));
		longSoundFile = File.createTempFile("longTestSound", ".mp3");
		outputStream = new BufferedOutputStream(new FileOutputStream(longSoundFile), 1024);

		length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists())
			soundFile.delete();
		if (longSoundFile != null && longSoundFile.exists())
			longSoundFile.delete();
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

	public void testGetMultipleMediaPlayers() {
		final int mediaPlayerCount = 10;
		for (int i = 0; i < mediaPlayerCount; i++) {
			MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
			assertNotNull("SoundManager failed to return a MediaPlayer", mediaPlayer);
			assertFalse("SoundManager provided a MediaPlayer that was already playing", mediaPlayer.isPlaying());
		}
	}

	public void testClear() {
		SoundManager soundManager = SoundManager.getInstance();
		soundManager.clear();
		assertEquals("SoundManager was destroyed when calling clear", soundManager, SoundManager.getInstance());

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		assertNotNull("SoundManager failed to return a MediaPlayer after clear", mediaPlayer);
		assertFalse("SoundManager provided a MediaPlayer that was already playing", mediaPlayer.isPlaying());
	}

	public void testPauseAndResume() throws IllegalStateException, IOException {
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
		
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertFalse("MediaPlayer is not done playing after pause and resume", mediaPlayer.isPlaying());
	}

	public void testPauseAndResumeMultiplePlayers() throws IllegalArgumentException, IllegalStateException, IOException {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		final int mediaPlayerCount = 10;
		ArrayList<MediaPlayer> mediaPlayers = new ArrayList<MediaPlayer>();
		for (int i = 0; i < mediaPlayerCount; i++) {
			MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
			mediaPlayers.add(mediaPlayer);
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

		SoundManager.getInstance().pause();
		for (MediaPlayer mediaPlayer : mediaPlayers)
			assertFalse("MediaPlayer is still playing after SoundManager was paused", mediaPlayer.isPlaying());

		SoundManager.getInstance().resume();
		for (MediaPlayer mediaPlayer : mediaPlayers)
			assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());
	}
	
	public void testMediaPlayerLimit() throws IllegalArgumentException, IllegalStateException, IOException {
		assertNotNull("Test sound file was not copied properly", longSoundFile);
		final String soundFilePath = longSoundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);
		
		final int mediaPlayerCount = SoundManager.MAX_MEDIA_PLAYERS;
		for (int i = 0; i < mediaPlayerCount; i++) {
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
}
