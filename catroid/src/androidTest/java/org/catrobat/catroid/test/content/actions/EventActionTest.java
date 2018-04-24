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
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class EventActionTest {
	static final String MESSAGE1 = "message1";
	static final String MESSAGE2 = "message2";
	private Sprite sprite;
	private Script startScript;
	private Script broadcastScript1;

	@Before
	public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		sprite = new SingleSprite("testSprite");
		createProjectWithSprite(sprite);
		startScript = new StartScript();
		broadcastScript1 = new BroadcastScript(MESSAGE1);
		sprite.addScript(startScript);
		sprite.addScript(broadcastScript1);
	}

	private Project createProjectWithSprite(Sprite sprite) {
		Project project = new Project(InstrumentationRegistry.getInstrumentation().getTargetContext(), "testProject");
		ProjectManager.getInstance().setProject(project);
		project.getDefaultScene().addSprite(sprite);
		return project;
	}

	@Test
	public void testBroadcast() {
		int testPosition = 100;
		startScript.addBrick(new BroadcastBrick(MESSAGE1));
		broadcastScript1.addBrick(new SetXBrick(testPosition));

		sprite.createAndAddActions(Sprite.INCLUDE_START_ACTIONS);

		executeAllActions();

		assertEquals("Simple broadcast failed", testPosition, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testBroadcastWait() {
		int testPosition = 100;
		int setTestPosition = 20;

		startScript.addBrick(new BroadcastWaitBrick(MESSAGE1));
		startScript.addBrick(new SetXBrick(testPosition));
		broadcastScript1.addBrick(new WaitBrick(500));
		broadcastScript1.addBrick(new SetXBrick(setTestPosition));

		sprite.createAndAddActions(Sprite.INCLUDE_START_ACTIONS);

		executeAllActions();

		assertEquals("Broadcast and wait failed", testPosition, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testWhenScriptRestartingItself() {
		final int xMovement = 1;
		startScript.addBrick(new BroadcastBrick(MESSAGE1));

		broadcastScript1.addBrick(new ChangeXByNBrick(xMovement));
		broadcastScript1.addBrick(new BroadcastBrick(MESSAGE1));
		broadcastScript1.addBrick(new WaitBrick(5));

		sprite.createAndAddActions(Sprite.INCLUDE_START_ACTIONS);

		executeAllActionsOrTimeoutAfter(20);

		assertThat("BroadcastScript does not restart itself",
				(int) sprite.look.getXInUserInterfaceDimensionUnit(), greaterThan(xMovement));
	}

	@Test
	public void testRestartingOfBroacastScriptWithBroadcastWaitBrick() {
		final int xMovement = 1;

		startScript.addBrick(new BroadcastBrick(MESSAGE1));
		broadcastScript1.addBrick(new ChangeXByNBrick(xMovement));
		broadcastScript1.addBrick(new BroadcastWaitBrick(MESSAGE2));

		BroadcastScript broadcastScript2 = new BroadcastScript(MESSAGE2);
		broadcastScript2.addBrick(new ChangeXByNBrick(xMovement));
		broadcastScript2.addBrick(new BroadcastWaitBrick(MESSAGE1));
		sprite.addScript(broadcastScript2);

		sprite.createAndAddActions(Sprite.INCLUDE_START_ACTIONS);

		executeAllActionsOrTimeoutAfter(20);

		assertThat("BroadcastScript does not restart itself with BroadcastWait call! ",
				(int) sprite.look.getXInUserInterfaceDimensionUnit(), greaterThan(5 * xMovement));
	}

	@Test
	public void testRestartingBroadcastWaitBrickWithBroadcastBrick() {
		final int xPosition = 123;

		startScript.addBrick(new WaitBrick(1));
		startScript.addBrick(new BroadcastBrick(MESSAGE1));

		broadcastScript1.addBrick(new WaitBrick(50));

		Script startScript2 = new StartScript();
		startScript2.addBrick(new BroadcastWaitBrick(MESSAGE1));
		startScript2.addBrick(new SetXBrick(xPosition));
		sprite.addScript(startScript2);

		sprite.createAndAddActions(Sprite.INCLUDE_START_ACTIONS);

		executeAllActionsOrTimeoutAfter(20);

		assertEquals("BroadcastScript started by broadcast and wait, restarted by broadcast (before finishing) does "
						+ "not terminate! ",
				(int) sprite.look.getXInUserInterfaceDimensionUnit(), xPosition);
	}

	private void executeAllActions() {
		executeAllActionsOrTimeoutAfter(0);
	}

	private void executeAllActionsOrTimeoutAfter(int ticks) {
		int loopCounter = 0;
		while (!allActionsOfAllSpritesAreFinished() && !(++loopCounter == ticks)) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}
	}

	private boolean allActionsOfAllSpritesAreFinished() {
		for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			if (!spriteOfList.look.getAllActionsAreFinished()) {
				return false;
			}
		}
		return true;
	}
}
