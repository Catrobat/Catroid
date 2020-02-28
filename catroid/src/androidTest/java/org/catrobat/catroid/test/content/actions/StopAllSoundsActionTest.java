/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.test.content.actions;

import android.media.MediaPlayer;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

@RunWith(AndroidJUnit4.class)
public class StopAllSoundsActionTest {
	private final SoundManager soundManager = SoundManager.getInstance();
	private File soundFile;

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects();
		soundManager.clear();
		this.createTestProject();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects();
		soundManager.clear();
	}

	@Test
	public void testStopOneSound() throws Exception {
		Sprite testSprite = new SingleSprite("testSprite");
		SoundInfo soundInfo = createSoundInfo(soundFile);
		testSprite.getSoundList().add(soundInfo);

		List<MediaPlayer> mediaPlayers = soundManager.getMediaPlayers();

		ActionFactory factory = testSprite.getActionFactory();
		Action playSoundAction = factory.createPlaySoundAction(testSprite, soundInfo);
		Action stopAllSoundsAction = factory.createStopAllSoundsAction();

		playSoundAction.act(1.0f);

		assertEquals(1, mediaPlayers.size());
		assertTrue(mediaPlayers.get(0).isPlaying());

		stopAllSoundsAction.act(1.0f);

		assertFalse(mediaPlayers.get(0).isPlaying());
	}

	@Test
	public void testStopSimultaneousPlayingSounds() throws Exception {
		Sprite testSprite = new SingleSprite("testSprite");
		SoundInfo soundInfo = createSoundInfo(soundFile);
		testSprite.getSoundList().add(soundInfo);

		ActionFactory factory = testSprite.getActionFactory();
		Action playSoundAction1 = factory.createPlaySoundAction(testSprite, soundInfo);
		Action playSoundAction2 = factory.createPlaySoundAction(testSprite, soundInfo);

		playSoundAction1.act(1.0f);
		playSoundAction2.act(1.0f);

		List<MediaPlayer> mediaPlayers = soundManager.getMediaPlayers();
		assertEquals(2, mediaPlayers.size());
		assertTrue(mediaPlayers.get(0).isPlaying());
		assertTrue(mediaPlayers.get(1).isPlaying());

		Action stopAllSoundsAction = factory.createStopAllSoundsAction();
		stopAllSoundsAction.act(1.0f);

		assertFalse(mediaPlayers.get(0).isPlaying());
		assertFalse(mediaPlayers.get(1).isPlaying());
	}

	private void createTestProject() throws IOException {
		final String projectName = TestUtils.DEFAULT_TEST_PROJECT_NAME;

		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		int soundFileId = R.raw.testsound;
		soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				soundFileId,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"soundTest.mp3");
	}

	private SoundInfo createSoundInfo(File soundFile) {
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		return soundInfo;
	}
}
