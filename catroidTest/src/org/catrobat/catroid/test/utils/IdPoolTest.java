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

package org.catrobat.catroid.test.utils;

import android.test.AndroidTestCase;

import com.google.common.primitives.Ints;

import org.catrobat.catroid.utils.IdPool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdPoolTest extends AndroidTestCase {

	public void testUniqueIds() {
		IdPool idPool = new IdPool();
		Set<Integer> usedIds = new HashSet<>();

		for (int i = 0; i < 128; i++) {
			int newId = idPool.getNewId();
			assertFalse("IdPool doesn't return unique ids", usedIds.contains(newId));
			usedIds.add(newId);
		}
	}

	public void testVoidIds() {
		IdPool idPool = new IdPool();
		List<Integer> voidedIds = Ints.asList(1, 3, 5, 7, 9, 15, 20);

		for (int id : voidedIds) {
			idPool.voidId(id);
		}

		for (int i = 0; i < 24; i++) {
			int newId = idPool.getNewId();
			assertFalse("IdPool returned voided id", voidedIds.contains(newId));
		}
	}
}
