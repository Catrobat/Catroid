package org.catrobat.catroid.io;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;

public class CatroidFieldKeySorter implements FieldKeySorter {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map sort(final Class type, final Map keyedByFieldKey) {
		final Map map = new TreeMap(new Comparator() {

			@Override
			public int compare(final Object o1, final Object o2) {
				final FieldKey fieldKey1 = (FieldKey) o1;
				final FieldKey fieldKey2 = (FieldKey) o2;
				int i = fieldKey1.getDepth() - fieldKey2.getDepth();
				if (i == 0) {
					String fieldNameOrAlias1 = getFieldNameOrAlias(fieldKey1);
					String fieldNameOrAlias2 = getFieldNameOrAlias(fieldKey2);
					i = fieldNameOrAlias1.compareTo(fieldNameOrAlias2);
				}
				return i;
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

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return fieldName;
	}

}