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
import com.thoughtworks.xstream.io.StreamException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CatroidFieldKeySorter implements FieldKeySorter {
	private static final String TAG = CatroidFieldKeySorter.class.getSimpleName();

	@Override
	public Map sort(final Class type, final Map keyedByFieldKey) {
		XStreamFieldKeyOrder fieldKeyOrderAnnotation =
				(XStreamFieldKeyOrder) type.getAnnotation(XStreamFieldKeyOrder.class);
		if (fieldKeyOrderAnnotation != null) {
			List<String> fieldOrder = Arrays.asList(fieldKeyOrderAnnotation.value());
			return sortByList(fieldOrder, keyedByFieldKey);
		} else {
			return sortByFieldDepth(keyedByFieldKey);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map sortByList(final List<String> fieldOrder, final Map keyedByFieldKey) {
		checkForMissingSerializedFields(fieldOrder, keyedByFieldKey.entrySet());
		final Map map = new TreeMap(new Comparator() {

			@Override
			public int compare(final Object objectOne, final Object objectTwo) {
				final FieldKey fieldKeyOne = (FieldKey) objectOne;
				final FieldKey fieldKeyTwo = (FieldKey) objectTwo;

				int fieldKeyOneIndex = getFieldKeyIndex(fieldOrder, fieldKeyOne.getFieldName());
				int fieldKeyTwoIndex = getFieldKeyIndex(fieldOrder, fieldKeyTwo.getFieldName());

				return fieldKeyOneIndex - fieldKeyTwoIndex;
			}
		});
		map.putAll(keyedByFieldKey);
		return map;
	}

	private int getFieldKeyIndex(List<String> fieldOrder, String fieldName) {
		return fieldOrder.contains(fieldName) ? fieldOrder.indexOf(fieldName) : Integer.MAX_VALUE;
	}

	private void checkForMissingSerializedFields(List<String> fieldOrder, Set<Map.Entry<FieldKey, Field>> fields)
			throws StreamException {
		String missingFieldsClass = null;
		List<String> missingFields = new ArrayList<>();
		for (Map.Entry<FieldKey, Field> fieldEntry : fields) {
			final FieldKey fieldKey = fieldEntry.getKey();
			if (!fieldOrder.contains(fieldKey.getFieldName()) && isSerializable(fieldEntry.getValue())) {
				missingFieldsClass = fieldKey.getDeclaringClass().getSimpleName();
				missingFields.add(fieldKey.getFieldName());
			}
		}
		if (!missingFields.isEmpty()) {
			throw new StreamException("You have not given the annotation XStreamFieldKeyOrder a list of all fields to "
					+ "be serialized. Missing fields in class " + missingFieldsClass + " are " + missingFields);
		}
	}

	private boolean isSerializable(Field field) {
		int modifiers = field.getModifiers();
		return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map sortByFieldDepth(final Map keyedByFieldKey) {
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
		} catch (SecurityException | NoSuchFieldException securityException) {
			Log.e(TAG, Log.getStackTraceString(securityException));
		}
		return fieldName;
	}
}
