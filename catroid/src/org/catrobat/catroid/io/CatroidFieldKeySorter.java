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
package org.catrobat.catroid.io;

import android.util.Log;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class CatroidFieldKeySorter implements FieldKeySorter {

	private static final String TAG = CatroidFieldKeySorter.class.getSimpleName();

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map sort(final Class type, final Map keyedByFieldKey) {
		if (type.equals(Project.class)) {
			return sortProjectFields(keyedByFieldKey);
		}

		if (type.equals(Sprite.class)) {
			return sortSpriteFields(keyedByFieldKey);
		}

		final Map map = new TreeMap(new Comparator() {

			@Override
			public int compare(final Object objectOne, final Object objectTwo) {
				final FieldKey fieldKeyOne = (FieldKey) objectOne;
				final FieldKey fieldKeyTwo = (FieldKey) objectTwo;
				int fieldKeyComparator = fieldKeyOne.getDepth() - fieldKeyTwo.getDepth();
				if (fieldKeyComparator == 0) {
					String fieldNameOrAlias1 = getFieldNameOrAlias(fieldKeyOne);
					String fieldNameOrAlias2 = getFieldNameOrAlias(fieldKeyTwo);
					fieldKeyComparator = fieldNameOrAlias1.compareTo(fieldNameOrAlias2);
				}
				return fieldKeyComparator;
			}
		});
		map.putAll(keyedByFieldKey);
		return map;
	}

	private String getFieldNameOrAlias(FieldKey fieldKey) {
		String fieldName = fieldKey.getFieldName();
		try {
			Field field = fieldKey.getDeclaringClass().getDeclaredField(fieldName);

			XStreamAlias alias = field.getAnnotation(XStreamAlias.class);
			if (alias != null) {
				return alias.value();
			} else {
				return fieldName;
			}
		} catch (SecurityException securityException) {
			Log.e(TAG, Log.getStackTraceString(securityException));
		} catch (NoSuchFieldException noSuchFieldException) {
			Log.e(TAG, Log.getStackTraceString(noSuchFieldException));
		}
		return fieldName;
	}

	private Map sortProjectFields(Map map) {
		Map orderedMap = new OrderRetainingMap();
		FieldKey[] fieldKeyOrder = new FieldKey[map.size()];
		Iterator<FieldKey> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			FieldKey fieldKey = iterator.next();
			if (fieldKey.getFieldName().equals("xmlHeader")) {
				fieldKeyOrder[0] = fieldKey;
			} else if (fieldKey.getFieldName().equals("spriteList")) {
				fieldKeyOrder[1] = fieldKey;
			} else if (fieldKey.getFieldName().equals("dataContainer")) {
				fieldKeyOrder[2] = fieldKey;
			} else if (fieldKey.getFieldName().equals("serialVersionUID")) {
				fieldKeyOrder[3] = fieldKey;
			} else if (fieldKey.getFieldName().equals("settings")) {
				fieldKeyOrder[4] = fieldKey;
			}
		}
		for (FieldKey fieldKey : fieldKeyOrder) {
			orderedMap.put(fieldKey, map.get(fieldKey));
		}
		return orderedMap;
	}

	private Map sortSpriteFields(Map map) {
		Map orderedMap = new OrderRetainingMap();
		FieldKey[] fieldKeyOrder = new FieldKey[map.size()];
		Iterator<FieldKey> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			FieldKey fieldKey = iterator.next();
			if (fieldKey.getFieldName().equals("TAG")) {
				fieldKeyOrder[0] = fieldKey;
			} else if (fieldKey.getFieldName().equals("serialVersionUID")) {
				fieldKeyOrder[1] = fieldKey;
			} else if (fieldKey.getFieldName().equals("look")) {
				fieldKeyOrder[2] = fieldKey;
			} else if (fieldKey.getFieldName().equals("name")) {
				fieldKeyOrder[3] = fieldKey;
			} else if (fieldKey.getFieldName().equals("isPaused")) {
				fieldKeyOrder[4] = fieldKey;
			} else if (fieldKey.getFieldName().equals("lookList")) {
				fieldKeyOrder[5] = fieldKey;
			} else if (fieldKey.getFieldName().equals("soundList")) {
				fieldKeyOrder[6] = fieldKey;
			} else if (fieldKey.getFieldName().equals("scriptList")) {
				fieldKeyOrder[7] = fieldKey;
			} else if (fieldKey.getFieldName().equals("userBricks")) {
				fieldKeyOrder[8] = fieldKey;
			} else if (fieldKey.getFieldName().equals("newUserBrickNext")) {
				fieldKeyOrder[9] = fieldKey;
			} else if (fieldKey.getFieldName().equals("nfcTagList")) {
				fieldKeyOrder[10] = fieldKey;
			}
		}
		for (FieldKey fieldKey : fieldKeyOrder) {
			orderedMap.put(fieldKey, map.get(fieldKey));
		}
		return orderedMap;
	}
}
