/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.conditional.SetXBrick;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class BroadcastActionTest extends AndroidTestCase {

	public void testBroadcast() {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript(sprite);
		String message = "simpleTest";
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite, message);
		script.addBrick(broadcastBrick);
		sprite.addScript(script);

		BroadcastScript broadcastScript = new BroadcastScript(sprite, message);
		int testPosition = 100;
		SetXBrick testBrick = new SetXBrick(sprite, testPosition);
		broadcastScript.addBrick(testBrick);
		sprite.addScript(broadcastScript);

		Project project = new Project(getContext(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);

		sprite.createStartScriptActionSequence();

		while (!allActionsOfAllSpritesAreFinished()) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertEquals("Simple broadcast failed", testPosition, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testBroadcastWait() {
		Sprite sprite = new Sprite("spriteOne");
		Script scriptWait = new StartScript(sprite);
		String message = "waitTest";
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite, message);
		int testPosition = 100;
		SetXBrick setXBrick = new SetXBrick(sprite, testPosition);
		scriptWait.addBrick(broadcastWaitBrick);
		scriptWait.addBrick(setXBrick);
		sprite.addScript(scriptWait);

		BroadcastScript broadcastScript = new BroadcastScript(sprite, message);
		WaitBrick waitBrick = new WaitBrick(sprite, 500);
		int setTestPosition = 20;
		SetXBrick setXBrick2 = new SetXBrick(sprite, setTestPosition);
		broadcastScript.addBrick(waitBrick);
		broadcastScript.addBrick(setXBrick2);
		sprite.addScript(broadcastScript);

		Project project = new Project(getContext(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);

		sprite.createStartScriptActionSequence();

		while (!allActionsOfAllSpritesAreFinished()) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertEquals("Broadcast and wait failed", testPosition, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testWhenScriptRestartingItself() {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript(sprite);

		String message = "simpleTest";
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite, message);
		script.addBrick(broadcastBrick);
		sprite.addScript(script);

		BroadcastScript broadcastScript = new BroadcastScript(sprite, message);

		final int xMovement = 1;
		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(sprite, xMovement);
		broadcastScript.addBrick(changeXByNBrick);

		BroadcastBrick broadcastBrickLoop = new BroadcastBrick(sprite, message);
		broadcastScript.addBrick(broadcastBrickLoop);

		sprite.addScript(broadcastScript);

		Project project = new Project(getContext(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);

		sprite.createStartScriptActionSequence();

		int loopCounter = 0;
		while (!allActionsOfAllSpritesAreFinished() && loopCounter++ < 20) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertTrue("When script does not restart itself!",
				(int) sprite.look.getXInUserInterfaceDimensionUnit() > xMovement);
	}

	public void testRestartingOfWhenScriptWithBroadcastWaitBrick() {
		String messageOne = "messageOne";
		String messageTwo = "messageTwo";
		final int xMovement = 1;

		Sprite sprite = new Sprite("cat");
		Script startScript = new StartScript(sprite);
		BroadcastBrick startBroadcastBrick = new BroadcastBrick(sprite, messageOne);
		startScript.addBrick(startBroadcastBrick);
		sprite.addScript(startScript);

		BroadcastScript broadcastScriptMessageOne = new BroadcastScript(sprite, messageOne);
		ChangeXByNBrick changeXByNBrickOne = new ChangeXByNBrick(sprite, xMovement);
		BroadcastWaitBrick broadcastWaitBrickOne = new BroadcastWaitBrick(sprite, messageTwo);
		broadcastScriptMessageOne.addBrick(changeXByNBrickOne);
		broadcastScriptMessageOne.addBrick(broadcastWaitBrickOne);
		sprite.addScript(broadcastScriptMessageOne);

		BroadcastScript broadcastScriptMessageTwo = new BroadcastScript(sprite, messageTwo);
		ChangeXByNBrick changeXByNBrickTwo = new ChangeXByNBrick(sprite, xMovement);
		BroadcastWaitBrick broadcastWaitBrickTwo = new BroadcastWaitBrick(sprite, messageOne);
		broadcastScriptMessageTwo.addBrick(changeXByNBrickTwo);
		broadcastScriptMessageTwo.addBrick(broadcastWaitBrickTwo);
		sprite.addScript(broadcastScriptMessageTwo);

		Project project = new Project(getContext(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);

		sprite.createStartScriptActionSequence();

		int loopCounter = 0;
		while (!allActionsOfAllSpritesAreFinished() && loopCounter++ < 20) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertTrue("When script does not restart itself when a BroadcastWait is sent! ",
				(int) sprite.look.getXInUserInterfaceDimensionUnit() > 5 * xMovement);
	}

	public boolean allActionsOfAllSpritesAreFinished() {
		for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
			if (!spriteOfList.look.getAllActionsAreFinished()) {
				return false;
			}
		}
		return true;
	}
}
