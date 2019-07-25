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

package org.catrobat.catroid.test.physics.look;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class PhysicsLookAndLookMethodValueEqualityTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		Method[] methods = Look.class.getDeclaredMethods();
		ArrayList<Object[]> parameterList = new ArrayList<>();
		for (Method method : methods) {
			if (method.getParameterCount() == 0 && method.getReturnType() != void.class
					&& !method.getName().equals("getCurrentCollisionPolygon") && !method.getName().startsWith("$")) {
				parameterList.add(new Object[]{method.getName(), method});
			}
		}
		return parameterList;
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Method method;

	private PhysicsLook physicsLook;
	private Look look;

	@Before
	public void setUp() {
		Sprite sprite = mock(Sprite.class);
		PhysicsWorld physicsWorld = new PhysicsWorld(1920, 1600);
		physicsLook = new PhysicsLook(sprite, physicsWorld);
		look = new Look(sprite);
		method.setAccessible(true);
	}

	@Test
	public void testEquality() throws InvocationTargetException, IllegalAccessException {
		Object lookValue = method.invoke(look);
		Object physicsLookValue = method.invoke(physicsLook);
		assertEquals(lookValue, physicsLookValue);
	}
}
