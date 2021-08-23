/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.test.content.script;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.TouchUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class WhenScriptTest {

	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	private Sprite sprite;
	private Script whenScript;
	private final OrthographicCamera camera = Mockito.spy(new OrthographicCamera());

	@Before
	public void setUp() {
		sprite = createSprite();

		whenScript = new WhenScript();
		sprite.addScript(whenScript);
		Mockito.doNothing().when(camera).update();

		createProjectWithSprite(sprite);
		TouchUtil.reset();
	}

	private Sprite createSprite() {
		Sprite sprite = new Sprite("testSprite");
		sprite.look = new Look(sprite) {
			{
				pixmap = TestUtils.createRectanglePixmap(WIDTH, HEIGHT, Color.RED);
			}
		};

		sprite.look.setSize(WIDTH, HEIGHT);
		sprite.look.setXInUserInterfaceDimensionUnit(0);
		sprite.look.setYInUserInterfaceDimensionUnit(0);
		return sprite;
	}

	private Project createProjectWithSprite(Sprite sprite) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);
		project.getDefaultScene().addSprite(sprite);
		return project;
	}

	@Test
	public void basicWhenScriptTest() {
		whenScript.addBrick(new ChangeXByNBrick(10));
		sprite.initializeEventThreads(EventId.START);

		tapSprite();
		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals((float) 10, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	private void tapSprite() {
		sprite.look.doTouchDown(0, 0, 0);
	}

	@Test
	public void whenScriptRestartTest() {
		whenScript.addBrick(new WaitBrick(50));
		whenScript.addBrick(new ChangeXByNBrick(10));
		sprite.initializeEventThreads(EventId.START);

		tapSprite();
		tapSprite();

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals((float) 10, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void movedCameraPosition() {
		camera.position.set(1000.0f, 600.0f, 0.0f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(1000.0f, 600.0f);

		whenScript.addBrick(new ChangeXByNBrick(10));
		sprite.initializeEventThreads(EventId.START);

		tapSprite();
		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals((float) 1010, sprite.look.getXInUserInterfaceDimensionUnit());
	}
}
