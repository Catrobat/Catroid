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

package org.catrobat.catroid.test.content.actions;

import android.graphics.PointF;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Queue;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PenUpActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float X_MOVEMENT = 100.0f;
	private Formula xMovement = new Formula(X_MOVEMENT);

	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		sprite = new Sprite("testSprite");
	}

	@Test
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createPenUpAction(null);
		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}

	@Test
	public void testSaveMultiplePositionChangesWithPenUpActionBetween() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createChangeXByNAction(sprite, xMovement).act(1.0f);

		sprite.getActionFactory().createPenUpAction(sprite).act(1.0f);
		sprite.getActionFactory().createChangeXByNAction(sprite, xMovement).act(1.0f);

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createChangeXByNAction(sprite, xMovement).act(1.0f);

		Queue<Queue<PointF>> positions = sprite.penConfiguration.getPositions();
		assertEquals(0f, positions.first().removeFirst().x);
		assertEquals(X_MOVEMENT, positions.first().removeFirst().x);
		assertEquals(X_MOVEMENT, positions.first().removeFirst().x);
		positions.removeFirst();
		assertEquals(X_MOVEMENT * 2, positions.first().removeFirst().x);
		assertEquals(X_MOVEMENT * 3, positions.first().removeFirst().x);
	}

	@Test
	public void testMultiplePenUpActions() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createPenUpAction(sprite).act(1.0f);
		sprite.getActionFactory().createPenUpAction(sprite).act(1.0f);

		Queue<Queue<PointF>> positions = sprite.penConfiguration.getPositions();
		assertEquals(0f, positions.first().removeFirst().x);
		assertEquals(0f, positions.first().removeFirst().x);
		assertTrue(positions.first().isEmpty());
	}
}
