/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CatroidFieldKeySorter implements FieldKeySorter {

	private static final String TAG = CatroidFieldKeySorter.class.getSimpleName();

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Map sort(final Class type, final Map keyedByFieldKey) {
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

}
