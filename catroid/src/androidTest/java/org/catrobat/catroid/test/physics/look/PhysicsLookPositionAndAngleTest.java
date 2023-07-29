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

package org.catrobat.catroid.test.physics.look;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class PhysicsLookPositionAndAngleTest {
	private PhysicsObject physicsObject;
	private PhysicsLook physicsLook;
	private PhysicsWorld physicsWorldSpy;

	@Before
	public void setUp() throws Exception {
		Sprite sprite = mock(Sprite.class);
		physicsObject = mock(PhysicsObject.class);
		physicsWorldSpy = Mockito.spy(new PhysicsWorld(1920, 1600));
		when(physicsWorldSpy.getPhysicsObject(sprite)).thenReturn(physicsObject);
		physicsLook = new PhysicsLook(sprite, physicsWorldSpy);
	}

	@Test
	public void testPositionSetX() {
		float x = 0.5f;
		physicsLook.setX(x);
		verify(physicsObject, times(1)).setX(eq(x));
		Mockito.verifyNoMoreInteractions(physicsObject);
	}

	@Test
	public void testPositionGetX() {
		float x = 0.5f;
		when(physicsObject.getX()).thenReturn(x);
		assertEquals(x, physicsLook.getX());
	}

	@Test
	public void testPositionSetY() {
		float y = 0.5f;
		physicsLook.setY(y);
		verify(physicsObject, times(1)).setY(eq(y));
		Mockito.verifyNoMoreInteractions(physicsObject);
	}

	@Test
	public void testPositionGetY() {
		float y = 0.5f;
		when(physicsObject.getY()).thenReturn(y);
		assertEquals(y, physicsLook.getY());
	}

	@Test
	public void testSetPosition() {
		float x = 5.6f;
		float y = 7.8f;
		physicsLook.setPosition(x, y);
		verify(physicsObject, times(1)).setX(eq(x));
		verify(physicsObject, times(1)).setY(eq(y));
		verifyNoMoreInteractions(physicsObject);
	}

	@Test
	public void testSetRotation() {
		float rotation = 9.0f;
		physicsLook.setRotation(rotation);
		verify(physicsObject, times(1)).setDirection(eq(rotation));
	}

	@Test
	public void testCloneValues() {
		Sprite cloneSprite = mock(Sprite.class);
		PhysicsObject clonePhysicsObject = mock(PhysicsObject.class);

		when(physicsWorldSpy.getPhysicsObject(cloneSprite)).thenReturn(clonePhysicsObject);
		PhysicsLook cloneLook = new PhysicsLook(cloneSprite, physicsWorldSpy);

		PhysicsWorld.activeArea = new Vector2(0.0f, 0.0f);
		when(clonePhysicsObject.getMassCenter()).thenReturn(new Vector2(0.0f, 0.0f));
		when(clonePhysicsObject.getCircumference()).thenReturn(0.0f);
		doNothing().when(clonePhysicsObject).setFixedRotation(false);

		physicsLook.setBrightnessInUserInterfaceDimensionUnit(32);
		physicsLook.copyTo(cloneLook);

		assertEquals(physicsLook.getBrightnessInUserInterfaceDimensionUnit(), cloneLook.getBrightnessInUserInterfaceDimensionUnit());
		verify(physicsObject, times(1)).copyTo(clonePhysicsObject);
	}
}
