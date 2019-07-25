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

import java.lang.reflect.Field;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class PhysicsLookAndLookDefaultFieldValueEqualityTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		Field[] fields = Look.class.getDeclaredFields();
		ArrayList<Object[]> parameterList = new ArrayList<>();
		for (Field field : fields) {
			if (!field.getName().equals("scheduler") && !field.getName().startsWith("$")) {
				parameterList.add(new Object[] {field.getName(), field});
			}
		}
		return parameterList;
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Field field;

	private static final Sprite SPRITE = mock(Sprite.class);
	private static final PhysicsWorld PHYSICS_WORLD = mock(PhysicsWorld.class);
	private static final PhysicsLook PHYSICS_LOOK = new PhysicsLook(SPRITE, PHYSICS_WORLD);
	private static final Look LOOK = new Look(SPRITE);

	@Before
	public void setUp() {
		field.setAccessible(true);
	}

	@Test
	public void testEquality() throws IllegalAccessException {
		Object lookValue = field.get(LOOK);
		Object physicsLookValue = field.get(PHYSICS_LOOK);
		assertEquals(lookValue, physicsLookValue);
	}
}
