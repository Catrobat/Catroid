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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.koin.core.module.Module;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import kotlin.Lazy;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(Parameterized.class)
public class CompositeBrickBroadcastMessageTest {

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{IfThenLogicBeginBrick.class.getSimpleName(), IfThenLogicBeginBrick.class},
				{ForeverBrick.class.getSimpleName(), ForeverBrick.class},
				{RepeatBrick.class.getSimpleName(), RepeatBrick.class},
				{RepeatUntilBrick.class.getSimpleName(), RepeatUntilBrick.class},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	private static final String MESSAGETEXT = "Test";

	private Scene scene;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		initializeStaticSingletonMethods(dependencyModules);
		Project project = new Project();
		scene = new Scene();
		Sprite sprite = new Sprite();
		Script script = new WhenScript();
		CompositeBrick compositeBrick = compositeBrickClass.newInstance();
		BroadcastBrick primaryListBroadcastBrick = new BroadcastBrick();
		primaryListBroadcastBrick.setBroadcastMessage(MESSAGETEXT);

		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(script);
		script.addBrick(compositeBrick);
		compositeBrick.getNestedBricks().add(primaryListBroadcastBrick);

		projectManager.getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testCorrectBroadcastMessages() {
		Set<String> usedMessages = scene.getBroadcastMessagesInUse();
		Assert.assertTrue(usedMessages.contains(MESSAGETEXT) && usedMessages.size() == 1);
	}
}
