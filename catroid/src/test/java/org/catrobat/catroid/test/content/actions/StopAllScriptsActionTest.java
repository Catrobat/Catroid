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

package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.eventids.BroadcastEventId;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class StopAllScriptsActionTest {
	private Sprite sprite;
	private Script startScript1;
	private Script startScript2;

	@Before
	public void setUp() {
		PowerMockito.mockStatic(GdxNativesLoader.class);
		sprite = new Sprite("testSprite");
		createProjectWithSprite(sprite);
		startScript1 = new StartScript();
		startScript2 = new StartScript();
		sprite.addScript(startScript1);
		sprite.addScript(startScript2);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
	}

	private Project createProjectWithSprite(Sprite sprite) {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);
		project.getDefaultScene().addSprite(sprite);

		Array<Actor> actors = new Array<>();
		actors.add(sprite.look);
		StageActivity.stageListener = Mockito.mock(StageListener.class);
		Mockito.when(StageActivity.stageListener.getStage()).thenReturn(Mockito.mock(Stage.class));
		Mockito.when(StageActivity.stageListener.getStage().getActors()).thenReturn(actors);

		List<Sprite> sprites = new ArrayList<>();
		sprites.add(sprite);
		Mockito.when(StageActivity.stageListener.getSpritesFromStage()).thenReturn(sprites);
		return project;
	}

	@Test
	public void testStopAllScriptsBasic() {
		final int position = 15;
		startScript1.addBrick(new StopScriptBrick(BrickValues.STOP_ALL_SCRIPTS));
		startScript1.addBrick(new ChangeXByNBrick(position));
		startScript2.addBrick(new WaitBrick(50));
		startScript2.addBrick(new ChangeXByNBrick(position * 2));
		sprite.initializeEventThreads(EventId.START);

		executeAllActions();

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testStopAllScriptsInEvent() {
		final int position = 15;
		final String broadcastMessage = "message1";
		startScript1.addBrick(new BroadcastBrick(broadcastMessage));
		startScript1.addBrick(new WaitBrick(50));
		startScript1.addBrick(new ChangeXByNBrick(100));

		BroadcastScript broadcastScript = new BroadcastScript(broadcastMessage);
		broadcastScript.addBrick(new StopScriptBrick(BrickValues.STOP_ALL_SCRIPTS));
		broadcastScript.addBrick(new ChangeXByNBrick(position));
		sprite.addScript(broadcastScript);
		sprite.initializeEventThreads(EventId.START);

		executeAllActions();

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testStopAllScriptsAfterEventAndWait() {
		final int position = 15;
		final String broadcastMessage = "message1";
		startScript1.addBrick(new BroadcastWaitBrick(broadcastMessage));
		startScript1.addBrick(new ChangeXByNBrick(100));

		BroadcastScript broadcastScript = new BroadcastScript(broadcastMessage);
		broadcastScript.addBrick(new ChangeXByNBrick(position));
		broadcastScript.addBrick(new StopScriptBrick(BrickValues.STOP_ALL_SCRIPTS));
		broadcastScript.addBrick(new ChangeXByNBrick(position));
		sprite.addScript(broadcastScript);
		sprite.initializeEventThreads(EventId.START);

		executeAllActions();

		assertEquals((float) position, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testRestartEventAfterStopAllScripts() {
		final int position = 15;
		final String broadcastMessage = "message1";
		BroadcastScript broadcastScript = new BroadcastScript(broadcastMessage);
		broadcastScript.addBrick(new WaitBrick(50));
		broadcastScript.addBrick(new ChangeXByNBrick(position));

		startScript1.addBrick(new BroadcastBrick(broadcastMessage));
		startScript1.addBrick(new StopScriptBrick(BrickValues.STOP_ALL_SCRIPTS));
		startScript1.addBrick(new ChangeXByNBrick(100));

		sprite.addScript(broadcastScript);
		sprite.initializeEventThreads(EventId.START);

		executeAllActions();
		sprite.look.fire(new EventWrapper(new BroadcastEventId(broadcastMessage), false));
		executeAllActions();

		assertEquals((float) position, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	private void executeAllActions() {
		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}
	}
}
