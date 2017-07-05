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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadcastSequenceMap {
	private Map<String, Map<String, List<SequenceAction>>> broadcastSequenceMap = new HashMap<>();

	public boolean containsKey(String key, String sceneName) {
		return broadcastSequenceMap.get(sceneName) != null && broadcastSequenceMap.get(sceneName).containsKey(key);
	}

	public List<SequenceAction> get(String key, String sceneName) {
		Map<String, List<SequenceAction>> map = broadcastSequenceMap.get(sceneName);
		if (map != null) {
			return map.get(key);
		}
		return null;
	}

	public List<SequenceAction> put(String sceneName, String key, List<SequenceAction> values) {
		Map<String, List<SequenceAction>> map = broadcastSequenceMap.get(sceneName);
		if (map == null) {
			map = new HashMap<>();
			broadcastSequenceMap.put(sceneName, map);
		}
		return map.put(key, values);
	}

	public void remove(String key, String sceneName) {
		if (containsKey(key, sceneName)) {
			broadcastSequenceMap.get(sceneName).remove(key);
		}
	}

	public void clear() {
		broadcastSequenceMap.clear();
	}

	public void clear(String sceneName) {
		if (broadcastSequenceMap.containsKey(sceneName)) {
			broadcastSequenceMap.get(sceneName).clear();
		}
	}
}
