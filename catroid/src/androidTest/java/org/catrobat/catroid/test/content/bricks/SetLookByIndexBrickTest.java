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

package org.catrobat.catroid.test.content.bricks;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick;
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(AndroidJUnit4.class)
public class SetLookByIndexBrickTest {

	private Scene currentlyPlayingScene;
	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "Project");
		currentlyPlayingScene = new Scene("Currently playing scene", project);
		sprite = new Sprite("Sprite");

		currentlyPlayingScene.addSprite(sprite);
		project.addScene(currentlyPlayingScene);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(new Scene());
		ProjectManager.getInstance().setCurrentlyPlayingScene(currentlyPlayingScene);
	}

	@Test
	public void testSetLookByIndexBrickCreatesActionWithCorrectSprite() {
		ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
		sprite.setActionFactory(actionFactory);
		SetLookByIndexBrick brick = new SetLookByIndexBrick(1);

		brick.addActionToSequence(sprite, new ScriptSequenceAction(Mockito.mock(Script.class)));

		Mockito.verify(actionFactory).createSetLookByIndexAction(eq(sprite), any(Formula.class), anyInt());
	}

	@Test
	public void testSetBackgroundByIndexBrickCreatesActionWithCorrectSprite() {
		ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
		sprite.setActionFactory(actionFactory);
		SetBackgroundByIndexBrick brick = new SetBackgroundByIndexBrick(1);

		brick.addActionToSequence(sprite, new ScriptSequenceAction(Mockito.mock(Script.class)));

		Mockito.verify(actionFactory).createSetBackgroundLookByIndexAction(eq(sprite), any(Formula.class), anyInt());
	}
}
