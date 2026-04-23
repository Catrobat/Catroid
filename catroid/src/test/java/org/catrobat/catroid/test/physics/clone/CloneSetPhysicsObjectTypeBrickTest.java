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

package org.catrobat.catroid.test.physics.clone;

import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.PhysicsObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CloneSetPhysicsObjectTypeBrickTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"DYNAMIC", PhysicsObject.Type.DYNAMIC},
				{"FIXED", PhysicsObject.Type.FIXED},
				{"NONE", PhysicsObject.Type.NONE},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public PhysicsObject.Type type;

	private SetPhysicsObjectTypeBrick brick;
	private SetPhysicsObjectTypeBrick clonedBrick;

	@Before
	public void setUp() throws CloneNotSupportedException {
		brick = new SetPhysicsObjectTypeBrick(type);
		clonedBrick = (SetPhysicsObjectTypeBrick) brick.clone();
	}

	@Test
	public void testSameType() {
		PhysicsObject.Type clonedType = clonedBrick.getType();
		assertEquals(type, clonedType);
	}

	@Test
	public void testSameInstance() {
		assertThat(clonedBrick, is(instanceOf(brick.getClass())));
	}

	@Test
	public void testOriginalAndCloneNotSame() {
		assertNotSame(clonedBrick, brick);
	}
}
