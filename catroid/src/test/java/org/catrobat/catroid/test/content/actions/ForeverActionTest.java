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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;

@RunWith(JUnit4.class)
public class ForeverActionTest {

	private static final int REPEAT_TIMES = 4;

	@Test
	public void testLoopDelay() {
		initializeStaticSingletonMethods();
		Project project = Mockito.mock(Project.class);
		ProjectManager.getInstance().setCurrentProject(project);
		Mockito.doReturn(null).when(project).getSpriteListWithClones();
		int deltaY = -10;

		Sprite sprite = new Sprite("testSprite");
		StartScript script = new StartScript();

		ForeverBrick foreverBrick = new ForeverBrick();
		foreverBrick.addBrick(new ChangeYByNBrick(deltaY));
		script.addBrick(foreverBrick);

		sprite.addScript(script);
		sprite.initializeEventThreads(EventId.START);

		float delayByContract = 0.020f;
		float delta = 0.005f;

		for (int index = 0; index < REPEAT_TIMES; index++) {
			for (double time = 0f; time < delayByContract; time += delta) {
				sprite.look.act(delta);
			}
		}

		assertEquals(deltaY * REPEAT_TIMES, (int) sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
