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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Set;

@RunWith(Parameterized.class)
public class CompositeBrickWithSecondaryListBroadcastMessageTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{IfLogicBeginBrick.class.getSimpleName(), IfLogicBeginBrick.class},
				{PhiroIfLogicBeginBrick.class.getSimpleName(), PhiroIfLogicBeginBrick.class},
				{RaspiIfLogicBeginBrick.class.getSimpleName(), RaspiIfLogicBeginBrick.class},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	private static final String FIRST_MESSAGE = "Test";
	private static final String SECOND_MESSAGE = "NewName";

	private Scene scene;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		Project project = new Project();
		scene = new Scene();
		Sprite sprite = new Sprite();
		Script script = new WhenScript();
		CompositeBrick compositeBrick = compositeBrickClass.newInstance();
		BroadcastBrick primaryListBroadcastBrick = new BroadcastBrick();
		BroadcastBrick secondaryListBroadcastBrick = new BroadcastBrick();
		primaryListBroadcastBrick.setBroadcastMessage(FIRST_MESSAGE);
		secondaryListBroadcastBrick.setBroadcastMessage(SECOND_MESSAGE);

		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(script);
		script.addBrick(compositeBrick);
		compositeBrick.getNestedBricks().add(primaryListBroadcastBrick);
		compositeBrick.getSecondaryNestedBricks().add(secondaryListBroadcastBrick);

		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testCorrectBroadcastMessages() {
		Set<String> usedMessages = scene.getBroadcastMessagesInUse();
		Assert.assertTrue(usedMessages.contains(FIRST_MESSAGE));
		Assert.assertTrue(usedMessages.contains(SECOND_MESSAGE));
		Assert.assertTrue(usedMessages.size() == 2);
	}
}
