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
package org.catrobat.catroid.test.content.sprite;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class LookTest {

	private Look look;
	private Sprite sprite;

	private float width = 32;
	private float height = 16;

	@Before
	public void setUp() {
		Group parentGroup = new Group();
		sprite = new Sprite("test");
		parentGroup.addActor(sprite.look);
		look = sprite.look;
	}

	@Test
	public void testConstructor() {
		assertEquals(0f, look.getX());
		assertEquals(0f, look.getY());
		assertEquals(0f, look.getHeight());
		assertEquals(0f, look.getWidth());
		assertEquals(0f, look.getOriginX());
		assertEquals(0f, look.getOriginY());
		assertEquals(0f, look.getRotation());
		assertEquals(1f, look.getScaleX());
		assertEquals(1f, look.getScaleY());
		assertEquals(0f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals(100f, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0f, look.getColorInUserInterfaceDimensionUnit());
		assertEquals(100f, look.getSizeInUserInterfaceDimensionUnit());
		assertEquals(0, look.getZIndex());
		assertTrue(look.isVisible());
		assertEquals(Touchable.enabled, look.getTouchable());
		assertEquals("", look.getImagePath());
	}

	@Test
	public void testPositions() {
		float x = 3f;
		float y = 10f;
		look.setWidth(width);
		look.setHeight(height);

		look.setXInUserInterfaceDimensionUnit(x);
		checkX(x);

		look.setYInUserInterfaceDimensionUnit(y);
		checkY(y);

		look.changeXInUserInterfaceDimensionUnit(x);
		checkX(2 * x);

		look.changeYInUserInterfaceDimensionUnit(y);
		checkY(2 * y);

		x = 5f;
		y = -3f;
		look.setPositionInUserInterfaceDimensionUnit(x, y);
		checkX(x);
		checkY(y);
	}

	private void checkX(float x) {
		assertEquals(x, look.getXInUserInterfaceDimensionUnit());
		assertEquals(x - width / 2, look.getX());
	}

	private void checkY(float y) {
		assertEquals(y, look.getYInUserInterfaceDimensionUnit());
		assertEquals(y - height / 2, look.getY());
	}

	@Test
	public void testDirection() {
		look.setMotionDirectionInUserInterfaceDimensionUnit(90f);
		look.changeDirectionInUserInterfaceDimensionUnit(10f);

		assertEquals(100f, look.getMotionDirectionInUserInterfaceDimensionUnit());
		assertEquals(-10f, look.getRotation());

		look.setMotionDirectionInUserInterfaceDimensionUnit(90f);
		look.changeDirectionInUserInterfaceDimensionUnit(360f);

		assertEquals(90f, look.getMotionDirectionInUserInterfaceDimensionUnit());
		assertEquals(0f, look.getRotation());
	}

	@Test
	public void testSize() {
		float size = 30f;
		look.setSizeInUserInterfaceDimensionUnit(size);
		assertEquals(size, look.getSizeInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals(size / 100f, look.getScaleX());
		assertEquals(size / 100f, look.getScaleY());

		look.changeSizeInUserInterfaceDimensionUnit(size);
		assertEquals(2 * size, look.getSizeInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals(2 * size / 100f, look.getScaleX());
		assertEquals(2 * size / 100f, look.getScaleY());

		look.setSizeInUserInterfaceDimensionUnit(-10f);
		assertEquals(0f, look.getSizeInUserInterfaceDimensionUnit());
	}

	@Test
	public void testTransparency() {
		float transparency = 20f;
		look.setTransparencyInUserInterfaceDimensionUnit(transparency);
		assertEquals(transparency, look.getTransparencyInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals(0.8f, look.getAlpha());

		look.changeTransparencyInUserInterfaceDimensionUnit(transparency);
		assertEquals(2 * transparency, look.getTransparencyInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals(0.6f, look.getAlpha());

		look.setTransparencyInUserInterfaceDimensionUnit(-10f);
		assertEquals(0f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals(1f, look.getAlpha());

		look.setTransparencyInUserInterfaceDimensionUnit(200f);
		assertEquals(100f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals(0f, look.getAlpha());
	}

	@Test
	public void testBrightness() {
		float brightness = 42f;
		look.setBrightnessInUserInterfaceDimensionUnit(brightness);
		assertEquals(brightness, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0.42f, look.getBrightness());

		look.changeBrightnessInUserInterfaceDimensionUnit(brightness);
		assertEquals(2 * brightness, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0.84f, look.getBrightness());

		look.setBrightnessInUserInterfaceDimensionUnit(-10);
		assertEquals(0f, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0f, look.getBrightness());
	}

	@Test
	public void testColor() {
		int red = -40;
		int green = 80;

		look.setColorInUserInterfaceDimensionUnit(red);
		assertEquals(160.0f, look.getColorInUserInterfaceDimensionUnit());

		look.changeColorInUserInterfaceDimensionUnit(green);
		assertEquals(40.0f, look.getColorInUserInterfaceDimensionUnit());
	}

	@Test
	public void testCloneValues() {
		Look origin = new Look(null);
		origin.setSizeInUserInterfaceDimensionUnit(12);
		origin.setPositionInUserInterfaceDimensionUnit(4, 12);
		origin.setColorInUserInterfaceDimensionUnit(42);
		origin.setTransparencyInUserInterfaceDimensionUnit(7);
		origin.setRotationMode(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY);
		origin.setBrightnessInUserInterfaceDimensionUnit(3);
		origin.setMotionDirectionInUserInterfaceDimensionUnit(8);
		origin.setLookVisible(false);

		Look clone = new Look(null);
		origin.copyTo(clone);

		assertEquals(origin.getSizeInUserInterfaceDimensionUnit(), clone.getSizeInUserInterfaceDimensionUnit());

		assertEquals(origin.getXInUserInterfaceDimensionUnit(), clone.getXInUserInterfaceDimensionUnit());
		assertEquals(origin.getYInUserInterfaceDimensionUnit(), clone.getYInUserInterfaceDimensionUnit());

		assertEquals(origin.getColorInUserInterfaceDimensionUnit(), clone.getColorInUserInterfaceDimensionUnit());
		assertEquals(origin.getTransparencyInUserInterfaceDimensionUnit(), clone.getTransparencyInUserInterfaceDimensionUnit());

		assertEquals(origin.getRotationMode(), clone.getRotationMode());

		assertEquals(origin.getBrightnessInUserInterfaceDimensionUnit(), clone.getBrightnessInUserInterfaceDimensionUnit());

		assertEquals(origin.getMotionDirectionInUserInterfaceDimensionUnit(), clone.getMotionDirectionInUserInterfaceDimensionUnit());

		assertEquals(origin.isLookVisible(), clone.isLookVisible());
	}
}
