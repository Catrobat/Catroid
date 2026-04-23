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

package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.MediaPlayerWithSoundDetails;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.PlaySoundAtAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PlaySoundAtActionTest {

	private static SoundManager soundManager = SoundManager.getInstance();
	private static Project project;
	private static File soundFile;
	private PlaySoundAtAction action;

	@Before
	public void setUp() throws IOException {
		TestUtils.deleteProjects();
		soundManager.clear();
		project = new Project(ApplicationProvider.getApplicationContext(), "projectName");
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		soundFile = TestUtils.createSoundFile(project, R.raw.testsoundui, "soundTest.mp3");
	}

	@After
	public void cleanup() throws IOException {
		TestUtils.deleteProjects();
		soundManager.clear();
	}

	@Test
	public void testPlaySound() {
		soundManager.clear();
		float offset = 0.0f;

		Sprite testSprite = new Sprite("testSprite");
		SoundInfo soundinfo = createSoundInfo(soundFile);

		ActionFactory factory = testSprite.getActionFactory();
		testSprite.getSoundList().add(soundinfo);

		action = (PlaySoundAtAction) factory.createPlaySoundAtAction(testSprite,
				new SequenceAction(),
				new Formula(offset), soundinfo);

		action.act(1.0f);

		List<MediaPlayerWithSoundDetails> mediaPlayers = soundManager.getMediaPlayers();
		assertEquals(1, mediaPlayers.size());
		assertTrue(mediaPlayers.get(0).isPlaying());
	}

	@Test
	public void testPlaySoundNoOffset() {
		soundManager.clear();
		float offset = 0.0f;
		float soundDuration = soundManager.getDurationOfSoundFile(soundFile.getAbsolutePath());

		Sprite testSprite = new Sprite("testSprite");
		SoundInfo soundinfo = createSoundInfo(soundFile);

		ActionFactory factory = testSprite.getActionFactory();
		testSprite.getSoundList().add(soundinfo);

		action = (PlaySoundAtAction) factory.createPlaySoundAtAction(testSprite,
				new SequenceAction(),
				new Formula(offset), soundinfo);

		float playedDuration = action.runWithMockedSoundManager(soundManager);
		action.act(1.0f);

		assertEquals(playedDuration, soundDuration);
	}

	@Test
	public void testPlaySoundAtOffset() {
		soundManager.clear();
		float offset = 1.5f;
		float soundDuration =
				soundManager.getDurationOfSoundFile(soundFile.getAbsolutePath()) - (offset * 1000.0f);

		Sprite testSprite = new Sprite("testSprite");
		SoundInfo soundinfo = createSoundInfo(soundFile);

		ActionFactory factory = testSprite.getActionFactory();
		testSprite.getSoundList().add(soundinfo);

		action = (PlaySoundAtAction) factory.createPlaySoundAtAction(testSprite,
				new SequenceAction(),
				new Formula(offset), soundinfo);

		float playedDuration = action.runWithMockedSoundManager(soundManager);
		action.act(1.0f);

		assertEquals(playedDuration, soundDuration);
	}

	@Test
	public void testPlaySoundWrongParameter() {
		soundManager.clear();

		Sprite testSprite = new Sprite("testSprite");
		SoundInfo soundinfo = createSoundInfo(soundFile);

		ActionFactory factory = testSprite.getActionFactory();
		testSprite.getSoundList().add(soundinfo);

		action = (PlaySoundAtAction) factory.createPlaySoundAtAction(testSprite,
				new SequenceAction(),
				new Formula("WrongParameter"), soundinfo);

		action.act(1.0f);

		List<MediaPlayerWithSoundDetails> mediaPlayers = soundManager.getMediaPlayers();
		assertEquals(0, mediaPlayers.size());
	}

	private SoundInfo createSoundInfo(File soundFile) {
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		return soundInfo;
	}
}
