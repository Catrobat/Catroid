/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.MediaPlayerWithSoundDetails;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.File;
import java.util.Collections;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.koin.java.KoinJavaComponent.inject;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class SoundManagerTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private final SoundManager soundManager = SoundManager.getInstance();
	private static final int NUMBER_OF_SOUNDFILES = 3;
	private Project project;
	private final File[] soundFiles = new File[NUMBER_OF_SOUNDFILES];

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects();
		createProject();
		soundManager.clear();
		soundFiles[0] = TestUtils.createSoundFile(project, R.raw.testsound, "testsound.m4a");
		soundFiles[1] = TestUtils.createSoundFile(project, R.raw.testsoundui, "testsoundui.mp3");
		soundFiles[2] = TestUtils.createSoundFile(project, R.raw.longsound, "longsound.mp3");
	}

	@After
	public void tearDown() throws Exception {
		soundManager.clear();
		TestUtils.deleteProjects();
	}

	@Test
	public void testPlaySound() {
		soundManager.playSoundFile(soundFiles[0].getAbsolutePath(),
				project.getDefaultScene().getBackgroundSprite());

		MediaPlayer mediaPlayer = soundManager.getMediaPlayers().get(0);
		assertTrue(mediaPlayer.isPlaying());
		assertEquals(144, mediaPlayer.getDuration());
	}

	@Test
	public void testClear() {
		soundManager.playSoundFile(soundFiles[1].getAbsolutePath(),
				project.getDefaultScene().getBackgroundSprite());

		MediaPlayer mediaPlayer = soundManager.getMediaPlayers().get(0);
		assertTrue(mediaPlayer.isPlaying());

		soundManager.clear();
		assertTrue(soundManager.getMediaPlayers().isEmpty());

		exception.expect(IllegalStateException.class);
		mediaPlayer.isPlaying();
	}

	@Test
	public void testPauseAndResume() {
		soundManager.playSoundFile(soundFiles[0].getAbsolutePath(),
				project.getDefaultScene().getBackgroundSprite());

		MediaPlayer mediaPlayer = soundManager.getMediaPlayers().get(0);
		assertTrue(mediaPlayer.isPlaying());

		soundManager.pause();
		assertFalse(mediaPlayer.isPlaying());

		soundManager.resume();
		assertTrue(mediaPlayer.isPlaying());
	}

	@Test
	public void testPauseAndResumeMultipleSounds() {
		List<MediaPlayerWithSoundDetails> mediaPlayers = soundManager.getMediaPlayers();

		for (int index = 0; index < NUMBER_OF_SOUNDFILES; index++) {
			soundManager.playSoundFile(soundFiles[index].getAbsolutePath(),
					Mockito.mock(Sprite.class));
		}

		for (int index = 0; index < NUMBER_OF_SOUNDFILES; index++) {
			assertTrue(mediaPlayers.get(index).isPlaying());
		}

		soundManager.pause();

		for (int index = 0; index < NUMBER_OF_SOUNDFILES; index++) {
			assertFalse(mediaPlayers.get(index).isPlaying());
		}

		soundManager.resume();

		for (int index = 0; index < NUMBER_OF_SOUNDFILES; index++) {
			assertTrue(mediaPlayers.get(index).isPlaying());
		}
	}

	@Test
	public void testStopOfSoundWhenSameSoundIsStarted() {
		List<MediaPlayerWithSoundDetails> mediaPlayers = soundManager.getMediaPlayers();

		for (int index = 0; index < NUMBER_OF_SOUNDFILES; index++) {
			soundManager.playSoundFile(soundFiles[0].getAbsolutePath(),
					project.getDefaultScene().getBackgroundSprite());
		}
		assertEquals(1, mediaPlayers.size());
		assertTrue(mediaPlayers.get(0).isPlaying());
	}

	@Test
	public void testPlaySameSoundDifferentSprite() {
		List<MediaPlayerWithSoundDetails> mediaPlayers = soundManager.getMediaPlayers();
		soundManager.playSoundFile(soundFiles[2].getAbsolutePath(),
				project.getDefaultScene().getBackgroundSprite());
		soundManager.playSoundFile(soundFiles[2].getAbsolutePath(),
				project.getDefaultScene().getSpriteList().get(1));
		assertTrue(mediaPlayers.get(0).isPlaying());
		assertTrue(mediaPlayers.get(1).isPlaying());
	}

	@Test
	public void testPlaySameSoundFirstStopped() {
		soundManager.playSoundFile(soundFiles[0].getAbsolutePath(),
				project.getDefaultScene().getBackgroundSprite());
		soundManager.getMediaPlayers().get(0).stop();
		assertFalse(soundManager.getMediaPlayers().get(0).isPlaying());
		soundManager.playSoundFile(soundFiles[0].getAbsolutePath(),
				project.getDefaultScene().getBackgroundSprite());
		assertTrue(soundManager.getMediaPlayers().get(0).isPlaying());
	}

	@Test
	public void testMediaPlayerLimit() {
		assertEquals(7, SoundManager.MAX_MEDIA_PLAYERS);

		for (int index = 0; index < SoundManager.MAX_MEDIA_PLAYERS + 3; index++) {
			soundManager.playSoundFile(soundFiles[0].getAbsolutePath(), Mockito.mock(Sprite.class));
		}

		assertEquals(SoundManager.MAX_MEDIA_PLAYERS, soundManager.getMediaPlayers().size());
	}

	@Test
	public void testIfAllMediaPlayersInTheListAreUnique() {
		for (int index = 0; index < SoundManager.MAX_MEDIA_PLAYERS; index++) {
			SoundManager.getInstance().playSoundFile(soundFiles[0].getAbsolutePath(),
					Mockito.mock(Sprite.class));
		}
		List<MediaPlayerWithSoundDetails> mediaPlayers = soundManager.getMediaPlayers();
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			assertEquals(1, Collections.frequency(mediaPlayers, mediaPlayer));
		}
	}

	@Test
	public void testInitialVolume() {
		SoundManager soundManager = new SoundManager();
		assertEquals(70.0f, soundManager.getVolume());
	}

	@Test
	public void testSetVolume() {
		MediaPlayerWithSoundDetails mediaPlayerMock = Mockito.mock(
				MediaPlayerWithSoundDetails.class);
		soundManager.getMediaPlayers().add(mediaPlayerMock);

		float newVolume = 80.9f;
		soundManager.setVolume(newVolume);
		assertEquals(newVolume, soundManager.getVolume());

		verify(mediaPlayerMock).setVolume(newVolume / 100f, newVolume / 100f);
	}

	private void createProject() {
		project = new Project(ApplicationProvider.getApplicationContext(), "testProject");

		Sprite sprite = new Sprite("TestSprite");

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
	}
}
