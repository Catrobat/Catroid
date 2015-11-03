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

package org.catrobat.catroid.content;

import java.util.HashMap;
import java.util.Map;

public class SpriteHistory extends MediaHistory {
	private static final Map<String, SpriteHistory> INSTANCE = new HashMap<>();

	public static SpriteHistory getInstance(String projectName) {
		if (!INSTANCE.containsKey(projectName)) {
			INSTANCE.put(projectName, new SpriteHistory());
		}
		return INSTANCE.get(projectName);
	}

	public static void clearInstance() {
		INSTANCE.clear();
	}

	public static void updateMap(String oldName, String newName) {
		if (INSTANCE.containsKey(oldName)) {
			SpriteHistory historyToUpdate = INSTANCE.remove(oldName);
			INSTANCE.put(newName, historyToUpdate);
		}
	}

	public static boolean getAllUndoRedoStatus() {
		boolean result = false;

		for (SpriteHistory history : INSTANCE.values()) {
			result |= history.isRedoable();
			result |= history.isUndoable();
		}

		return result;
	}
}
