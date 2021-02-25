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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class PointToActionTest {

	private static final float DELTA = 1e-7f;

	private Sprite sprite;
	private Sprite pointedSprite;

	@Before
	public void setUp() throws Exception {
		createProject();
	}

	@Test
	public void testPointTo() {
		Action pointToAction = createPointToAction(sprite, pointedSprite);

		pointedSprite.look.setPosition(200f, 0f);
		pointToAction.act(1.0f);
		assertEquals(90f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);

		pointedSprite.look.setPosition(200f, 200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals(45f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);

		pointedSprite.look.setPosition(0f, 200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals(0f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);

		pointedSprite.look.setPosition(-200f, 200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals(-45f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);

		pointedSprite.look.setPosition(-200f, 0f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals(-90f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);

		pointedSprite.look.setPosition(-200f, -200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals(-135f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);

		pointedSprite.look.setPosition(0f, -200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals(180f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);

		pointedSprite.look.setPosition(200f, -200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals(135f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);
	}

	@Test
	public void testPointToBothSpritesOnSamePosition() {
		pointedSprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(45);

		createPointToAction(sprite, pointedSprite).act(1.0f);

		assertEquals(90f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);
	}

	@Test
	public void testPointedSpriteNull() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(33);
		final float previousDirection = sprite.look.getMotionDirectionInUserInterfaceDimensionUnit();

		createPointToAction(sprite, null).act(1.0f);

		assertEquals(previousDirection, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);
	}

	@Test
	public void testSpriteNotInScene() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(33);
		final float previousDirection = sprite.look.getMotionDirectionInUserInterfaceDimensionUnit();

		createPointToAction(sprite, new Sprite("Sprite not in Scene")).act(1.0f);

		assertEquals(previousDirection, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit(), DELTA);
	}

	private Action createPointToAction(Sprite sprite, Sprite pointedSprite) {
		return sprite.getActionFactory().createPointToAction(sprite, pointedSprite);
	}

	private void createProject() {
		sprite = new Sprite("sprite");
		pointedSprite = new Sprite("pointedSprite");
		Project project = new Project();
		Scene scene = new Scene();
		project.addScene(scene);
		project.getDefaultScene().addSprite(sprite);
		project.getDefaultScene().addSprite(pointedSprite);
		ProjectManager.getInstance().setCurrentProject(project);
	}
}
