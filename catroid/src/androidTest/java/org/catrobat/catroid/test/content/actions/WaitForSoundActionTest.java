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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SoundFilePathWithSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.WaitForSoundAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class WaitForSoundActionTest {

	private static ProjectManager projectManager;
	private static Project project;
	private WaitForSoundAction action;
	private static final float SOUND_DURATION = 2.0f;
	private static final String PATH_TO_SOUND_FILE = "soundFilePath";
	private Set<SoundFilePathWithSprite> pathSet;

	@BeforeClass
	public static void setUpProjectManager() {
		project = new Project(ApplicationProvider.getApplicationContext(), "projectName");
		projectManager = ProjectManager.getInstance();
	}

	@Before
	public void setUp() {
		createProject(this.getClass().getSimpleName());
	}

	@Test
	public void testWaitDurationSameAsSoundDuration() {
		createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE);
		action.act(0.1f);
		assertEquals(SOUND_DURATION, action.getDuration());
	}

	@Test
	public void testStopWaitWhenSameSoundStartsPlaying() {
		createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE);
		action.act(0.1f);
		assertEquals(action.getTime(), action.getDuration());
	}

	@Test
	public void testWaitWhenDifferentSoundsStartsPlaying() {
		createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE + "test");
		action.act(0.1f);
		assertNotEquals(action.getTime(), action.getDuration(), 0.0);
	}

	@Test
	public void testWaitWhenOtherSpriteStoppedSameSound() {
		createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE);
		pathSet.clear();
		pathSet.add(new SoundFilePathWithSprite(PATH_TO_SOUND_FILE, mock(Sprite.class)));
		action.act(0.1f);
		assertNotEquals(action.getTime(), action.getDuration(), 0.0);
	}

	private void createProject(String projectName) {
		project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(project.getDefaultScene().getBackgroundSprite());
	}

	private void createActionWithStoppedSoundFilePath(String soundPath) {
		SoundManager soundManager = Mockito.mock(SoundManager.class);
		pathSet = new HashSet<>();
		pathSet.add(new SoundFilePathWithSprite(soundPath, project.getDefaultScene().getBackgroundSprite()));
		when(soundManager.getRecentlyStoppedSoundfilePaths()).thenReturn(pathSet);
		when(soundManager.getDurationOfSoundFile(anyString())).thenReturn(SOUND_DURATION * 1000);
		action = (WaitForSoundAction) (new ActionFactory()).createWaitForSoundAction(
				project.getDefaultScene().getBackgroundSprite(), new SequenceAction(),
				new Formula(SOUND_DURATION),
				PATH_TO_SOUND_FILE);
		action.setSoundManager(soundManager);
	}
}
