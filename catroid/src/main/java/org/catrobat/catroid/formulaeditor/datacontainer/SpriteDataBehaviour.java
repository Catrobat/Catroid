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

package org.catrobat.catroid.formulaeditor.datacontainer;

import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

abstract class SpriteDataBehaviour<K, V> {

	protected abstract void reset(List<V> dataList);
	protected abstract Map<K, List<V>> getDataMap();
	protected abstract V newInstance(String name);
	protected abstract V newInstance(String name, Object value);
	protected abstract String getDataName(V data);
	protected abstract void setDataName(V data, String name);
	protected abstract String getKeyName(K key);
	protected abstract void setValue(V dataToAdd, Object value);
	protected abstract boolean isClone(K key);

	V add(K key, String name) {
		List<V> dataList = getOrCreate(key);
		V dataToAdd = newInstance(name);
		dataList.add(dataToAdd);
		return dataToAdd;
	}

	V add(K key, String name, Object value) {
		List<V> dataList = getOrCreate(key);
		V dataToAdd = newInstance(name, value);
		dataList.add(dataToAdd);
		return dataToAdd;
	}

	V addIfNotExists(K key, String name) {
		List<V> list = getOrCreate(key);

		V data = find(key, name);
		if (data != null) {
			return data;
		}

		V addedData = newInstance(name);
		list.add(addedData);
		getDataMap().put(key, list);

		return addedData;
	}

	V addIfNotExists(K key, String name, Object value) {
		V dataToAdd = addIfNotExists(key, name);
		setValue(dataToAdd, value);
		return dataToAdd;
	}

	V get(K key, String name) {
		return find(key, name);
	}

	List<V> getOrCreate(K key) {
		List<V> data = getDataMap().get(key);

		if (data == null) {
			data = new ArrayList<>();
			getDataMap().put(key, data);
		}
		return data;
	}

	V find(K key, String name) {
		List<V> dataList = getOrCreate(key);

		for (V data : dataList) {
			if (getDataName(data).equals(name)) {
				return data;
			}
		}
		return null;
	}

	boolean exists(K key, V value) {
		List<V> list = getDataMap().get(key);
		return list != null && list.contains(value);
	}

	boolean exists(K key, String name) {
		return find(key, name) != null;
	}

	boolean existsAny(List<K> keys, String name) {
		for (K key : keys) {
			if (exists(key, name)) {
				return true;
			}
		}
		return false;
	}

	boolean delete(K key, String name) {
		if (key == null) {
			return false;
		}

		V dataToDelete = find(key, name);
		if (dataToDelete != null) {
			getOrCreate(key).remove(dataToDelete);
			return true;
		}
		return false;
	}

	Map<Sprite, List<V>> cloneForScene(Scene scene, Map<K, List<V>> dataMap) {
		Map<Sprite, List<V>> clonedMap = new HashMap<>();

		for (Map.Entry<K, List<V>> entry : dataMap.entrySet()) {
			List<V> newList = new ArrayList<>();
			for (V data : entry.getValue()) {
				newList.add(newInstance(getDataName(data)));
			}

			String keyName = getKeyName(entry.getKey());
			Sprite clonedInstanceOnStage = scene.getSpriteBySpriteName(keyName);
			clonedMap.put(clonedInstanceOnStage, newList);
		}

		return clonedMap;
	}

	V rename(K key, String oldName, String newName) {
		V dataToRename = find(key, oldName);
		setDataName(dataToRename, newName);
		return dataToRename;
	}

	void clean(K key) {
		getDataMap().remove(key);
	}

	void removeCloneData() {
		for (K key : new HashSet<>(getDataMap().keySet())) {
			if (isClone(key)) {
				getDataMap().remove(key);
			}
		}
	}
}
