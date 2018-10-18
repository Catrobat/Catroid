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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StopThisScriptActionTest {
	private Sprite sprite;
	private Script startScript;

	@Before
	public void setUp() {
		sprite = new SingleSprite("testSprite");
		createProjectWithSprite(sprite);
		startScript = new StartScript();
		sprite.addScript(startScript);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
	}

	private Project createProjectWithSprite(Sprite sprite) {
		Project project = new Project(InstrumentationRegistry.getInstrumentation().getTargetContext(), "testProject");
		ProjectManager.getInstance().setProject(project);
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
