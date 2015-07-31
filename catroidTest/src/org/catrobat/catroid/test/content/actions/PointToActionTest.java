/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.PointToAction;

public class PointToActionTest extends AndroidTestCase {

	private final float delta = 1e-7f;

	public void testPointTo() {
		Sprite sprite = new Sprite("sprite");
		Sprite pointedSprite = new Sprite("pointedSprite");
		Project project = new Project();
		project.addSprite(sprite);
		project.addSprite(pointedSprite);
		ProjectManager.getInstance().setProject(project);

		PointToAction pointToAction = new PointToAction();
		pointToAction.setSprite(sprite);
		pointToAction.setPointedSprite(pointedSprite);

		pointedSprite.look.setPosition(200f, 0f);
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);

		pointedSprite.look.setPosition(200f, 200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", 45f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);

		pointedSprite.look.setPosition(0f, 200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", 0f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);

		pointedSprite.look.setPosition(-200f, 200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", -45f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);

		pointedSprite.look.setPosition(-200f, 0f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", -90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);

		pointedSprite.look.setPosition(-200f, -200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", -135f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);

		pointedSprite.look.setPosition(0f, -200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", 180f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);

		pointedSprite.look.setPosition(200f, -200f);
		pointToAction.restart();
		pointToAction.act(1.0f);
		assertEquals("Wrong direction", 135f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), delta);
	}
}
