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
package org.catrobat.catroid.test.content.actions;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.PlaySoundAction;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlaySoundActionTest extends InstrumentationTestCase {
	private final SoundManager soundManager = SoundManager.getInstance();
	private final int soundFileId = R.raw.testsound;
	private final String projectName = TestUtils.DEFAULT_TEST_PROJECT_NAME;
	private File soundFile;

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		soundManager.clear();
		this.createTestProject();
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.deleteTestProjects();
		soundManager.clear();
		super.tearDown();
	}

	public void testPlaySound() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		SoundInfo soundInfo = createSoundInfo(soundFile);
		testSprite.getSoundList().add(soundInfo);

		PlaySoundAction action = ExtendedActions.playSound(testSprite, soundInfo);
		action.act(1.0f);

		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		assertEquals("Wrong media player count", 1, mediaPlayers.size());
		assertTrue("MediaPlayer is not playing", mediaPlayers.get(0).isPlaying());
	}

	public void testPlaySimultaneousSounds() {
		Sprite testSprite = new Sprite("testSprite");
		SoundInfo soundInfo = createSoundInfo(soundFile);
		testSprite.getSoundList().add(soundInfo);

		PlaySoundAction playSoundAction1 = ExtendedActions.playSound(testSprite, soundInfo);
		PlaySoundAction playSoundAction2 = ExtendedActions.playSound(testSprite, soundInfo);

		playSoundAction1.act(1.0f);
		playSoundAction2.act(1.0f);

		List<MediaPlayer> mediaPlayers = getMediaPlayers();
		assertEquals("Wrong media player count", 2, mediaPlayers.size());
		assertTrue("First MediaPlayer is not playing", mediaPlayers.get(0).isPlaying());
		assertTrue("Second MediaPlayer is not playing", mediaPlayers.get(1).isPlaying());
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		soundFile = TestUtils.saveFileToProject(projectName, "soundTest.mp3", soundFileId, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
	}

	private SoundInfo createSoundInfo(File soundFile) {
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		return soundInfo;
	}

	@SuppressWarnings("unchecked")
	private List<MediaPlayer> getMediaPlayers() {
		return (List<MediaPlayer>) Reflection.getPrivateField(soundManager, "mediaPlayers");
	}
}
