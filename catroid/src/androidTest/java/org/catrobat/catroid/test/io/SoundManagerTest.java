/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

public class SoundManagerTest extends InstrumentationTestCase {

	private final SoundManager soundManager = SoundManager.getInstance();

	private Project project;
	private File soundFile;

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteProjects();
		createProject();
		soundManager.clear();

		soundFile = ResourceImporter
				.createSoundFileFromResourcesInDirectory(getInstrumentation().getContext().getResources(),
						R.raw.testsound, new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
						"testsound.m4a");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		soundManager.clear();
		TestUtils.deleteProjects();
		super.tearDown();
	}

	public void testPlaySound() {
		soundManager.playSoundFile(soundFile.getAbsolutePath());

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue(mediaPlayer.isPlaying());
		assertEquals(144, mediaPlayer.getDuration());
	}

	public void testClear() {
		soundManager.playSoundFile(soundFile.getAbsolutePath());

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue(mediaPlayer.isPlaying());

		soundManager.clear();
		assertTrue(getMediaPlayers().isEmpty());
		try {
			mediaPlayer.isPlaying();
			fail("The media player hasn't been released");
		} catch (IllegalStateException expected) {
		}
	}

	public void testPauseAndResume() {
		soundManager.playSoundFile(soundFile.getAbsolutePath());

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue(mediaPlayer.isPlaying());

		soundManager.pause();
		assertFalse(mediaPlayer.isPlaying());

		soundManager.resume();
		assertTrue(mediaPlayer.isPlaying());
	}

	public void testPauseAndResumeMultipleSounds() {
		final int playSoundFilesCount = 3;
		List<MediaPlayer> mediaPlayers = getMediaPlayers();

		for (int index = 0; index < playSoundFilesCount; index++) {
			soundManager.playSoundFile(soundFile.getAbsolutePath());
		}

		for (int index = 0; index < playSoundFilesCount; index++) {
			assertTrue(mediaPlayers.get(index).isPlaying());
		}

		soundManager.pause();

		for (int index = 0; index < playSoundFilesCount; index++) {
			assertFalse(mediaPlayers.get(index).isPlaying());
		}

		soundManager.resume();

		for (int index = 0; index < playSoundFilesCount; index++) {
			assertTrue(mediaPlayers.get(index).isPlaying());
		}
	}

	public void testMediaPlayerLimit() {
		assertEquals(7, SoundManager.MAX_MEDIA_PLAYERS);

		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		for (int index = 0; index < SoundManager.MAX_MEDIA_PLAYERS + 3; index++) {
			soundManager.playSoundFile(soundFile.getAbsolutePath());
		}

		assertEquals(SoundManager.MAX_MEDIA_PLAYERS, mediaPlayers.size());
	}

	public void testIfAllMediaPlayersInTheListAreUnique() {
		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		for (int index = 0; index < SoundManager.MAX_MEDIA_PLAYERS; index++) {
			SoundManager.getInstance().playSoundFile(soundFile.getAbsolutePath());
		}

		for (MediaPlayer mediaPlayer : mediaPlayers) {
			assertEquals(1, Collections.frequency(mediaPlayers, mediaPlayer));
		}
	}

	public void testInitialVolume() {
		SoundManager soundManager = new SoundManager() {
		};
		assertEquals(70.0f, soundManager.getVolume());
	}

	public void testSetVolume() {
		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		MediaPlayerMock mediaPlayerMock = new MediaPlayerMock();
		mediaPlayers.add(mediaPlayerMock);

		float newVolume = 80.9f;
		soundManager.setVolume(newVolume);

		assertEquals(newVolume, soundManager.getVolume());
		assertEquals(newVolume / 100f, mediaPlayerMock.leftVolume);
		assertEquals(newVolume / 100f, mediaPlayerMock.rightVolume);
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

	private void createProject() {
		project = new Project(getInstrumentation().getTargetContext(), "testProject");

		Sprite sprite = new Sprite("TestSprite");

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}
