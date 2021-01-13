/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.test.content.messagecontainer;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.test.utils.TestUtils.clearBackPack;

@RunWith(AndroidJUnit4.class)
public class BackpackBroadcastMessageTest {

	private final String firstMessage = "firstMessage";
	private final String secondMessage = "secondMessage";
	private final String thirdMessage = "thirdMessage";
	private Script backpackedStartScript;
	private Scene secondScene;
	private BackpackListManager backpackListManager;

	@Before
	public void setUp() throws Exception {
		backpackListManager = BackpackListManager.getInstance();
		clearBackPack(backpackListManager);
		createProject(BackpackBroadcastMessageTest.class.getSimpleName());
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(BackpackBroadcastMessageTest.class.getSimpleName());
		clearBackPack(backpackListManager);
	}

	@Test
	public void testUnpackBroadcastMessagesIntoNewScene() throws CloneNotSupportedException {
		ScriptController scriptController = new ScriptController();
		scriptController.pack("Backpack", backpackedStartScript.getBrickList());
		scriptController.unpack(backpackedStartScript, ProjectManager.getInstance().getCurrentSprite());

		Set<String> usedMessages = secondScene.getBroadcastMessagesInUse();
		Assert.assertTrue(usedMessages.contains(firstMessage));
		Assert.assertTrue(usedMessages.contains(secondMessage));
		Assert.assertTrue(usedMessages.contains(thirdMessage));
		Assert.assertEquals(3, usedMessages.size());
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		String secondSceneName = "Scene 2";
		secondScene = new Scene(secondSceneName, project);
		project.addScene(secondScene);

		Sprite spriteOfFirstScene = new Sprite("firstSceneSprite");
		Sprite spriteOfSecondScene = new Sprite("secondSceneSprite");

		backpackedStartScript = new StartScript();
		BroadcastMessageBrick firstBroadcastBrick = new BroadcastBrick(firstMessage);
		firstBroadcastBrick.setBroadcastMessage(firstMessage);
		BroadcastMessageBrick secondBroadcastBrick = new BroadcastBrick(secondMessage);
		secondBroadcastBrick.setBroadcastMessage(secondMessage);
		backpackedStartScript.addBrick(firstBroadcastBrick);
		backpackedStartScript.addBrick(secondBroadcastBrick);
		spriteOfFirstScene.addScript(backpackedStartScript);
		project.getDefaultScene().addSprite(spriteOfFirstScene);

		Script secondStartScript = new StartScript();
		BroadcastMessageBrick thirdBroadcastBrick = new BroadcastBrick(thirdMessage);
		thirdBroadcastBrick.setBroadcastMessage(thirdMessage);
		secondStartScript.addBrick(thirdBroadcastBrick);
		spriteOfSecondScene.addScript(secondStartScript);
		secondScene.addSprite(spriteOfSecondScene);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(spriteOfSecondScene);
	}
}
