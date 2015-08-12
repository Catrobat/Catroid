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
package org.catrobat.catroid.test.io;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

public class SoundManagerTest extends InstrumentationTestCase {
	private final SoundManager soundManager = SoundManager.getInstance();
	private final int soundFileId = R.raw.testsound;
	private final int soundFileDuration = 144;

	private File soundFile;

	@Override
	protected void setUp() throws Exception {
		soundManager.clear();
		soundFile = TestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/testSound.mp3", soundFileId,
				getInstrumentation().getContext());
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		soundManager.clear();
		super.tearDown();
	}

	public void testPlaySound() {
		soundManager.playSoundFile(soundFile.getAbsolutePath());

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue("Media player isn't playing", mediaPlayer.isPlaying());
		assertEquals("Wrong sound file is playing", soundFileDuration, mediaPlayer.getDuration());
	}

	public void testClear() {
		soundManager.playSoundFile(soundFile.getAbsolutePath());

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue("Media player isn't playing", mediaPlayer.isPlaying());

		soundManager.clear();
		assertTrue("SoundManager still holds media player references", getMediaPlayers().isEmpty());
		try {
			mediaPlayer.isPlaying();
			fail("The media player hasn't been released");
		} catch (IllegalStateException expected) {
		}
	}

	public void testPauseAndResume() {
		soundManager.playSoundFile(soundFile.getAbsolutePath());

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue("Media player isn't playing", mediaPlayer.isPlaying());

		soundManager.pause();
		assertFalse("Media player is still playing", mediaPlayer.isPlaying());

		soundManager.resume();
		assertTrue("Media player isn't playing", mediaPlayer.isPlaying());
	}

	public void testPauseAndResumeMultipleSounds() {
		final int playSoundFilesCount = 3;
		List<MediaPlayer> mediaPlayers = getMediaPlayers();

		for (int index = 0; index < playSoundFilesCount; index++) {
			soundManager.playSoundFile(soundFile.getAbsolutePath());
		}

		for (int index = 0; index < playSoundFilesCount; index++) {
			assertTrue("Media player isn't playing", mediaPlayers.get(index).isPlaying());
		}

		soundManager.pause();

		for (int index = 0; index < playSoundFilesCount; index++) {
			assertFalse("Media player is still playing", mediaPlayers.get(index).isPlaying());
		}

		soundManager.resume();

		for (int index = 0; index < playSoundFilesCount; index++) {
			assertTrue("Media player isn't playing", mediaPlayers.get(index).isPlaying());
		}
	}

	public void testMediaPlayerLimit() {
		assertEquals("Wrong maximum count of sound players", 7, SoundManager.MAX_MEDIA_PLAYERS);

		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		for (int index = 0; index < SoundManager.MAX_MEDIA_PLAYERS + 3; index++) {
			soundManager.playSoundFile(soundFile.getAbsolutePath());
		}

		assertEquals("Maximum count of media players is exceeded", SoundManager.MAX_MEDIA_PLAYERS, mediaPlayers.size());
	}

	public void testIfAllMediaPlayersInTheListAreUnique() {
		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		for (int index = 0; index < SoundManager.MAX_MEDIA_PLAYERS; index++) {
			SoundManager.getInstance().playSoundFile(soundFile.getAbsolutePath());
		}

		for (MediaPlayer mediaPlayer : mediaPlayers) {
			assertEquals("MediaPlayerList contains one media players twice.", 1,
					Collections.frequency(mediaPlayers, mediaPlayer));
		}
	}

	/*
	 * TODO: Since the SoundManager shouldn't be a Singleton, this is just a temporary solution.
	 */
	public void testInitialVolumeValue() {
		Constructor<SoundManager> privateSoundManagerConstructor = null;
		try {
			privateSoundManagerConstructor = SoundManager.class.getDeclaredConstructor((Class<?>[]) null);
			privateSoundManagerConstructor.setAccessible(true);
			SoundManager soundManager = privateSoundManagerConstructor.newInstance();

			assertEquals("Wrong initial sound volume value", 70.0f, soundManager.getVolume());
		} catch (Exception exception) {
			fail("Couldn't instantiate sound manager");
		} finally {
			if (privateSoundManagerConstructor != null) {
				privateSoundManagerConstructor.setAccessible(false);
			}
		}
	}

	public void testSetVolume() {
		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		MediaPlayerMock mediaPlayerMock = new MediaPlayerMock();
		mediaPlayers.add(mediaPlayerMock);

		float newVolume = 80.9f;
		soundManager.setVolume(newVolume);

		assertEquals("Volume hasn't changed", newVolume, soundManager.getVolume());
		assertEquals("Wrong volume value", newVolume / 100f, mediaPlayerMock.leftVolume);
		assertEquals("Wrong volume value", newVolume / 100f, mediaPlayerMock.rightVolume);
	}

	@SuppressWarnings("unchecked")
	private List<MediaPlayer> getMediaPlayers() {
		return (List<MediaPlayer>) Reflection.getPrivateField(soundManager, "mediaPlayers");
	}

	private class MediaPlayerMock extends MediaPlayer {
		private float leftVolume;
		private float rightVolume;

		@Override
		public void setVolume(float leftVolume, float rightVolume) {
			this.leftVolume = leftVolume;
			this.rightVolume = rightVolume;
		}
	}
}
