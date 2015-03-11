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
package org.catrobat.catroid.common;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.ArrayList;
import java.util.HashMap;

public final class BroadcastSequenceMap {
	private static HashMap<String, ArrayList<SequenceAction>> broadcastSequenceMap = new HashMap<String, ArrayList<SequenceAction>>();

	private BroadcastSequenceMap() {
		throw new AssertionError();
	}

	public static boolean containsKey(String key) {
		return BroadcastSequenceMap.broadcastSequenceMap.containsKey(key);
	}

	public static ArrayList<SequenceAction> get(String key) {
		return BroadcastSequenceMap.broadcastSequenceMap.get(key);
	}

	public static ArrayList<SequenceAction> put(String key, ArrayList<SequenceAction> value) {
		return BroadcastSequenceMap.broadcastSequenceMap.put(key, value);
	}

	public static void clear() {
		BroadcastSequenceMap.broadcastSequenceMap.clear();
	}
}
