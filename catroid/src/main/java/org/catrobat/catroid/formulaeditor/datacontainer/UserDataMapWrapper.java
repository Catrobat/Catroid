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

package org.catrobat.catroid.formulaeditor.datacontainer;

import android.util.Log;

import org.catrobat.catroid.formulaeditor.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDataMapWrapper<K, V extends UserData> {

	private static final String TAG = UserDataMapWrapper.class.getSimpleName();

	private Map<K, List<V>> map;

	UserDataMapWrapper(Map<K, List<V>> map) {
		this.map = map;
	}

	public boolean add(K key, V element) {
		return new UserDataListWrapper<>(get(key)).add(element);
	}

	public boolean contains(K key, String name) {
		return map.containsKey(key) && new UserDataListWrapper<>(map.get(key)).contains(name);
	}

	public boolean contains(String name) {
		for (List<V> value : map.values()) {
			if (new UserDataListWrapper<>(value).contains(name)) {
				return true;
			}
		}
		return false;
	}

	public V get(K key, String name) {
		return map.get(key) != null ? new UserDataListWrapper<>(map.get(key)).get(name) : null;
	}

	public List<V> get(K key) {
		if (key == null) {
			Log.e(TAG, "This map does not allow null keys.");
			return null;
		}
		if (map.containsKey(key)) {
			return map.get(key);
		}

		List<V> value = new ArrayList<>();
		map.put(key, value);
		return value;
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public boolean updateKey(K previousKey, K newKey) {
		if (map.containsKey(previousKey)) {
			List<V> value = map.get(previousKey);
			map.remove(previousKey);
			map.put(newKey, value);
			return true;
		}
		return false;
	}

	public List<V> remove(K key) {
		return map.remove(key);
	}

	public boolean remove(String name) {
		for (List<V> value : map.values()) {
			if (new UserDataListWrapper<>(value).remove(name)) {
				return true;
			}
		}
		return false;
	}
}
