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

package org.catrobat.catroid.test.embroidery;

import android.util.Pair;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.TatamiContour;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StartTatamiContourTest {
	private Sprite sprite;
	private Look spriteLook;

	@Before
	public void setUp() {
		sprite = new Sprite("testSprite");
		sprite.resetSprite();
		spriteLook = sprite.look;
	}

	@Test
	public void testIsEmptyAtStart() {
		TatamiContour tatamiContour = sprite.getTatamiContour();
		List<Pair<Float, Float>> coordinates = tatamiContour.getCoordinates();
		assertTrue(coordinates.isEmpty());
	}

	@Test
	public void testHasStartCoordinate() {
		sprite.getActionFactory().createStartTatamiContourAction(sprite).act(1f);

		TatamiContour tatamiContour = sprite.getTatamiContour();
		List<Pair<Float, Float>> coordinates = tatamiContour.getCoordinates();
		float currentSpriteCoordinates = spriteLook.getXInUserInterfaceDimensionUnit();

		assertTrue(coordinates.size() == 1 && coordinates.get(0).first == currentSpriteCoordinates);
	}

	@Test
	public void testSimpleMovementHorizontal() {
		sprite.getActionFactory().createStartTatamiContourAction(sprite).act(1f);
		sprite.getActionFactory().createChangeXByNAction(sprite, new SequenceAction(),
				new Formula(100)).act(1.0f);

		TatamiContour tatamiContour = sprite.getTatamiContour();
		List<Pair<Float, Float>> coordinates = tatamiContour.getCoordinates();
		float currentSpriteCoordinatesX = spriteLook.getXInUserInterfaceDimensionUnit();

		assertEquals(coordinates.get(1).first, currentSpriteCoordinatesX, 0.0);
	}

	@Test
	public void testSimpleMovementVertical() {
		sprite.getActionFactory().createStartTatamiContourAction(sprite).act(1f);
		sprite.getActionFactory().createChangeYByNAction(sprite, new SequenceAction(),
				new Formula(100)).act(1.0f);

		TatamiContour tatamiContour = sprite.getTatamiContour();
		List<Pair<Float, Float>> coordinates = tatamiContour.getCoordinates();
		float currentSpriteCoordinatesY = spriteLook.getYInUserInterfaceDimensionUnit();

		assertEquals(coordinates.get(1).second, currentSpriteCoordinatesY, 0.0);
	}

	@Test
	public void testSimpleMovementDiagonal() {
		sprite.getActionFactory().createStartTatamiContourAction(sprite).act(1f);
		sprite.getActionFactory().createChangeXByNAction(sprite, new SequenceAction(),
				new Formula(100)).act(1.0f);
		sprite.getActionFactory().createChangeYByNAction(sprite, new SequenceAction(),
				new Formula(200)).act(1.0f);

		TatamiContour tatamiContour = sprite.getTatamiContour();
		List<Pair<Float, Float>> coordinates = tatamiContour.getCoordinates();

		float currentSpriteCoordinatesX = spriteLook.getXInUserInterfaceDimensionUnit();
		float currentSpriteCoordinatesY = spriteLook.getYInUserInterfaceDimensionUnit();

		assertEquals(coordinates.get(1).first, currentSpriteCoordinatesX, 0.0);
		assertEquals(coordinates.get(2).second, currentSpriteCoordinatesY, 0.0);
		assertEquals(coordinates.get(2).first, currentSpriteCoordinatesX, 0.0);
	}
}
