/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.content.BroadcastEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class BroadcastWaitSequenceMap {
	private static Map<String, HashMap<String, ArrayList<SequenceAction>>> broadcastWaitSequenceMap = new HashMap<>();
	private static BroadcastEvent currentBroadcastEvent = null;

	private BroadcastWaitSequenceMap() {
		throw new AssertionError();
	}

	public static boolean containsKey(String key, String sceneName) {
		if (!broadcastWaitSequenceMap.containsKey(sceneName)) {
			return false;
		}
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.get(sceneName).containsKey(key);
	}

	public static ArrayList<SequenceAction> get(String key, String sceneName) {
		if (!broadcastWaitSequenceMap.containsKey(sceneName)) {
			return null;
		}
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.get(sceneName).get(key);
	}

	public static ArrayList<SequenceAction> put(String sceneName, String key, ArrayList<SequenceAction> value) {
		if (!broadcastWaitSequenceMap.containsKey(sceneName)) {
			HashMap<String, ArrayList<SequenceAction>> map = new HashMap<>();
			broadcastWaitSequenceMap.put(sceneName, map);
		}
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.get(sceneName).put(key, value);
	}

	public static ArrayList<SequenceAction> remove(String key, String sceneName) {
		if (!broadcastWaitSequenceMap.containsKey(sceneName)) {
			return null;
		}
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.get(sceneName).remove(key);
	}

	public static void clear() {
		BroadcastWaitSequenceMap.broadcastWaitSequenceMap.clear();
	}

	public static void clear(String sceneName) {
		if (!BroadcastWaitSequenceMap.broadcastWaitSequenceMap.containsKey(sceneName)) {
			return;
		}
		BroadcastWaitSequenceMap.broadcastWaitSequenceMap.get(sceneName).clear();
	}

	public static BroadcastEvent getCurrentBroadcastEvent() {
		return BroadcastWaitSequenceMap.currentBroadcastEvent;
	}

	public static void setCurrentBroadcastEvent(BroadcastEvent broadcastEvent) {
		BroadcastWaitSequenceMap.currentBroadcastEvent = broadcastEvent;
	}

	public static void clearCurrentBroadcastEvent() {
		BroadcastWaitSequenceMap.currentBroadcastEvent = null;
	}
}
