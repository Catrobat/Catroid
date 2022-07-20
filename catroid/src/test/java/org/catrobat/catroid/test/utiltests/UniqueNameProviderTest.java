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

package org.catrobat.catroid.test.utiltests;

import com.google.common.collect.Lists;

import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class UniqueNameProviderTest {

	private UniqueNameProvider uniqueNameProvider;

	@Before
	public void setUp() {
		uniqueNameProvider = new UniqueNameProvider();
	}

	@Test
	public void testNewName() {
		assertEquals("Lion",
				uniqueNameProvider.getUniqueName("Lion", Lists.newArrayList("Zebra", "Giraffe")));
	}

	@Test
	public void testNewNameWithEmptyScope() {
		assertEquals("Object",
				uniqueNameProvider.getUniqueName("Object", new ArrayList<String>()));
	}

	@Test
	public void testNameInScope() {
		assertEquals("Object (1)",
				uniqueNameProvider.getUniqueName("Object", Lists.newArrayList("Object")));
	}

	@Test
	public void testNameAndEnumerationsInScope() {
		assertEquals("Object (3)",
				uniqueNameProvider.getUniqueName("Object", Lists.newArrayList("Object", "Object (1)", "Object (2)")));
	}

	@Test
	public void testGapInEnumeration() {
		assertEquals("Object (2)",
				uniqueNameProvider.getUniqueName("Object", Lists.newArrayList("Object", "Object (1)", "Object (3)")));
	}

	@Test
	public void testNewNameEnumerationGreaterThanOrigin() {
		assertEquals("Object (4)",
				uniqueNameProvider.getUniqueName("Object (3)", Lists.newArrayList("Object (3)")));
	}
}
