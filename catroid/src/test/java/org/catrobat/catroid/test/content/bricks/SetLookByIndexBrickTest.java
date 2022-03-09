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

package org.catrobat.catroid.test.content.bricks;

import android.content.Context;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

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
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static org.koin.java.KoinJavaComponent.inject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(JUnit4.class)
public class SetLookByIndexBrickTest {

	private Sprite sprite;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "Project");

		Scene currentlyPlayingScene = new Scene("Currently playing scene", project);
		sprite = new Sprite("Sprite");

		currentlyPlayingScene.addSprite(sprite);
		project.addScene(currentlyPlayingScene);
		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentlyEditedScene(new Scene());
		projectManager.getValue().setCurrentlyPlayingScene(currentlyPlayingScene);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testSetLookByIndexBrickCreatesActionWithCorrectSprite() {
		ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
		sprite.setActionFactory(actionFactory);
		SetLookByIndexBrick brick = new SetLookByIndexBrick(1);

		brick.addActionToSequence(sprite, new ScriptSequenceAction(Mockito.mock(Script.class)));

		Mockito.verify(actionFactory).createSetLookByIndexAction(eq(sprite),
				any(SequenceAction.class), any(Formula.class));
	}

	@Test
	public void testSetBackgroundByIndexBrickCreatesActionWithCorrectSprite() {
		ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
		sprite.setActionFactory(actionFactory);
		SetBackgroundByIndexBrick brick = new SetBackgroundByIndexBrick(1);

		brick.addActionToSequence(sprite, new ScriptSequenceAction(Mockito.mock(Script.class)));

		Mockito.verify(actionFactory).createSetBackgroundByIndexAction(eq(sprite),
				any(SequenceAction.class), any(Formula.class), anyBoolean());
	}
}
