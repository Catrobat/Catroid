/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.test.AndroidTestCase;

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
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.LegacyFileUtils;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class BroadcastActionTest extends AndroidTestCase {

	Project project;
	Sprite sprite;
	List<Sprite> spritesOnStage;
	final String MESSAGE_1 = "message1";
	final String MESSAGE_2 = "message2";

	@Override
	public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		sprite = new SingleSprite("testSprite");
		project = createProjectWithSprite(sprite);
		spritesOnStage = new ArrayList<>();
		spritesOnStage.add(sprite);
		initializeStageListener();
	}

	private void initializeStageListener() {
		StageListener stageListener = Mockito.mock(StageListener.class);
		when(stageListener.getSpritesFromStage()).thenReturn(spritesOnStage);
		StageActivity.stageListener = stageListener;
	}

	private Project createProjectWithSprite(Sprite sprite) {
		Project project = new Project(getContext(), LegacyFileUtils.DEFAULT_TEST_PROJECT_NAME);
		ProjectManager.getInstance().setProject(project);
		project.getDefaultScene().addSprite(sprite);
		return project;
	}

	public void testBroadcast() {
		Script script = new StartScript();
		BroadcastBrick broadcastBrick = new BroadcastBrick(MESSAGE_1);
		script.addBrick(broadcastBrick);
		sprite.addScript(script);

		BroadcastScript broadcastScript = new BroadcastScript(MESSAGE_1);
		int testPosition = 100;
		SetXBrick testBrick = new SetXBrick(testPosition);
		broadcastScript.addBrick(testBrick);
		sprite.addScript(broadcastScript);

		sprite.initializeActionsIncludingStartActions(true);

		while (!allActionsOfAllSpritesAreFinished()) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertEquals("Simple broadcast failed", testPosition, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testBroadcastWait() {
		Script scriptWait = new StartScript();
		String message = "waitTest";
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(message);
		int testPosition = 100;
		SetXBrick setXBrick = new SetXBrick(testPosition);
		scriptWait.addBrick(broadcastWaitBrick);
		scriptWait.addBrick(setXBrick);
		sprite.addScript(scriptWait);

		BroadcastScript broadcastScript = new BroadcastScript(message);
		WaitBrick waitBrick = new WaitBrick(500);
		int setTestPosition = 20;
		SetXBrick setXBrick2 = new SetXBrick(setTestPosition);
		broadcastScript.addBrick(waitBrick);
		broadcastScript.addBrick(setXBrick2);
		sprite.addScript(broadcastScript);

		sprite.initializeActionsIncludingStartActions(true);

		while (!allActionsOfAllSpritesAreFinished()) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertEquals("Broadcast and wait failed", testPosition, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testWhenScriptRestartingItself() {
		Script script = new StartScript();

		BroadcastBrick broadcastBrick = new BroadcastBrick(MESSAGE_1);
		script.addBrick(broadcastBrick);
		sprite.addScript(script);

		BroadcastScript broadcastScript = new BroadcastScript(MESSAGE_1);

		final int xMovement = 1;
		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(xMovement);
		broadcastScript.addBrick(changeXByNBrick);

		BroadcastBrick broadcastBrickLoop = new BroadcastBrick(MESSAGE_1);
		broadcastScript.addBrick(broadcastBrickLoop);

		WaitBrick wb = new WaitBrick(5);
		broadcastScript.addBrick(wb);

		sprite.addScript(broadcastScript);

		sprite.initializeActionsIncludingStartActions(true);

		int loopCounter = 0;
		while (!allActionsOfAllSpritesAreFinished() && loopCounter++ < 20) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertTrue("When script does not restart itself!",
				(int) sprite.look.getXInUserInterfaceDimensionUnit() > xMovement);
	}

	public void testRestartingOfWhenScriptWithBroadcastWaitBrick() {
		final int xMovement = 1;

		Script startScript = new StartScript();
		BroadcastBrick startBroadcastBrick = new BroadcastBrick(MESSAGE_1);
		startScript.addBrick(startBroadcastBrick);
		sprite.addScript(startScript);

		BroadcastScript broadcastScriptMessageOne = new BroadcastScript(MESSAGE_1);
		ChangeXByNBrick changeXByNBrickOne = new ChangeXByNBrick(xMovement);
		BroadcastWaitBrick broadcastWaitBrickOne = new BroadcastWaitBrick(MESSAGE_2);
		broadcastScriptMessageOne.addBrick(changeXByNBrickOne);
		broadcastScriptMessageOne.addBrick(broadcastWaitBrickOne);
		sprite.addScript(broadcastScriptMessageOne);

		BroadcastScript broadcastScriptMessageTwo = new BroadcastScript(MESSAGE_2);
		ChangeXByNBrick changeXByNBrickTwo = new ChangeXByNBrick(xMovement);
		BroadcastWaitBrick broadcastWaitBrickTwo = new BroadcastWaitBrick(MESSAGE_1);
		broadcastScriptMessageTwo.addBrick(changeXByNBrickTwo);
		broadcastScriptMessageTwo.addBrick(broadcastWaitBrickTwo);
		sprite.addScript(broadcastScriptMessageTwo);

		sprite.initializeActionsIncludingStartActions(true);

		int loopCounter = 0;
		while (!allActionsOfAllSpritesAreFinished() && loopCounter++ < 20) {
			for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertTrue("When script does not restart itself when a BroadcastWait is sent! ",
				(int) sprite.look.getXInUserInterfaceDimensionUnit() > 5 * xMovement);
	}

	public boolean allActionsOfAllSpritesAreFinished() {
		for (Sprite spriteOfList : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			if (!spriteOfList.look.getAllActionsAreFinished()) {
				return false;
			}
		}
		return true;
	}
}
