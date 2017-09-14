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
import org.catrobat.catroid.content.Sprite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadcastWaitSequenceMap {
	private BroadcastEvent currentBroadcastEvent = null;
	private Map<Sprite, BroadcastSequenceMap> waitSequenceMap = new HashMap<>();

	public List<SequenceAction> put(String sceneName, String key, Sprite sprite, List<SequenceAction> values) {
		BroadcastSequenceMap map = waitSequenceMap.get(sprite);
		if (map == null) {
			map = new BroadcastSequenceMap();
			waitSequenceMap.put(sprite, map);
		}
		return map.put(sceneName, key, values);
	}

	public List<SequenceAction> get(String key, String sceneName, Sprite sprite) {
		BroadcastSequenceMap broadcastSequenceMap = waitSequenceMap.get(sprite);
		if (broadcastSequenceMap != null) {
			return broadcastSequenceMap.get(key, sceneName);
		}
		return null;
	}

	public boolean containsKey(String key, String sceneName, Sprite sprite) {
		return waitSequenceMap.get(sprite) != null && waitSequenceMap.get(sprite).containsKey(key, sceneName);
	}

	public void remove(String key, String sceneName) {
		for (BroadcastSequenceMap broadcastSequenceMap : waitSequenceMap.values()) {
			if (broadcastSequenceMap != null) {
				broadcastSequenceMap.remove(key, sceneName);
			}
		}
	}

	public BroadcastEvent getCurrentBroadcastEvent() {
		return currentBroadcastEvent;
	}

	public void setCurrentBroadcastEvent(BroadcastEvent broadcastEvent) {
		currentBroadcastEvent = broadcastEvent;
	}

	public void clearCurrentBroadcastEvent() {
		currentBroadcastEvent = null;
	}

	public void clear() {
		waitSequenceMap.clear();
	}

	public void clear(String sceneName, Sprite sprite) {
		BroadcastSequenceMap broadcastSequenceMap = waitSequenceMap.get(sprite);
		if (broadcastSequenceMap != null) {
			broadcastSequenceMap.clear(sceneName);
		}
	}
}
