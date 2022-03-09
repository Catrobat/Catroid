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

import android.content.Context;

import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.koin.core.module.Module;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class StopThisScriptActionTest {
	private Sprite sprite;
	private Script startScript;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() {
		sprite = new Sprite("testSprite");
		createProjectWithSprite(sprite);
		startScript = new StartScript();
		sprite.addScript(startScript);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
		PowerMockito.mockStatic(GdxNativesLoader.class);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	private Project createProjectWithSprite(Sprite sprite) {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "testProject");
		projectManager.getValue().setCurrentProject(project);
		project.getDefaultScene().addSprite(sprite);
		return project;
	}

	@Test
	public void testStopThisScriptBasic() {
		final int invalidPosition = 15;
		startScript.addBrick(new StopScriptBrick(BrickValues.STOP_THIS_SCRIPT));
		startScript.addBrick(new SetXBrick(new Formula(invalidPosition)));
		sprite.initializeEventThreads(EventId.START);

		executeAllActions();

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testStopThisScriptAfterEvent() {
		final int position = 15;
		final String broadcastMessage = "message1";
		BroadcastScript broadcastScript = new BroadcastScript(broadcastMessage);
		broadcastScript.addBrick(new StopScriptBrick(BrickValues.STOP_THIS_SCRIPT));
		broadcastScript.addBrick(new ChangeXByNBrick(100));
		sprite.addScript(broadcastScript);
		startScript.addBrick(new BroadcastBrick(broadcastMessage));
		startScript.addBrick(new ChangeXByNBrick(position));
		sprite.initializeEventThreads(EventId.START);

		executeAllActions();

		assertEquals((float) position, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testStopThisScriptAfterEventAndWait() {
		final int position = 15;
		final String broadcastMessage = "message1";
		BroadcastScript broadcastScript = new BroadcastScript(broadcastMessage);
		broadcastScript.addBrick(new StopScriptBrick(BrickValues.STOP_THIS_SCRIPT));
		broadcastScript.addBrick(new ChangeXByNBrick(100));
		sprite.addScript(broadcastScript);
		startScript.addBrick(new BroadcastWaitBrick(broadcastMessage));
		startScript.addBrick(new ChangeXByNBrick(position));
		sprite.initializeEventThreads(EventId.START);

		executeAllActions();

		assertEquals((float) position, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	private void executeAllActions() {
		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}
	}
}
