/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.scratchconverter.protocol.message;

import java.util.HashMap;
import java.util.Map;

public abstract class Message {

	public enum CategoryType {
		BASE(0),
		JOB(1);

		private int categoryID;
		private static Map<Integer, CategoryType> map = new HashMap<>();
		static {
			for (CategoryType legEnum : CategoryType.values()) {
				map.put(legEnum.categoryID, legEnum);
			}
		}
		CategoryType(final int categoryID) {
			this.categoryID = categoryID;
		}

		public static CategoryType valueOf(int categoryID) {
			return map.get(categoryID);
		}

		public int getCategoryID() {
			return categoryID;
		}
	}
}
