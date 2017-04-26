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
package org.catrobat.catroid.test.io;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.io.CatroidFieldKeySorter;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.io.XStreamMissingSerializableFieldException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatroidFieldKeySorterTest extends AndroidTestCase {

	private static class FieldKeySorterDecorator implements FieldKeySorter {

		private final FieldKeySorter catroidFieldKeySorter = new CatroidFieldKeySorter();
		private Map<Class, Map<FieldKey, Field>> sortResults = new HashMap<>();

		@Override
		public Map sort(Class type, Map keyedByFieldKey) {
			Map sortResult = catroidFieldKeySorter.sort(type, keyedByFieldKey);
			this.sortResults.put(type, sortResult);
			return sortResult;
		}

		public String[] getFieldNames(Class type) {
			return getFieldNames(sortResults.get(type));
		}

		public String[] getFieldNames(Map<FieldKey, Field> fieldKeys) {
			List<String> fieldNames = new ArrayList<>();
			for (Map.Entry<FieldKey, Field> fieldKeyEntry : fieldKeys.entrySet()) {
				FieldKey fieldKey = fieldKeyEntry.getKey();
				fieldNames.add(CatroidFieldKeySorter.getAliasOrFieldName(fieldKey));
			}
			return fieldNames.toArray(new String[fieldNames.size()]);
		}
	}

	private XStream xstream;
	private FieldKeySorterDecorator fieldKeySorter;
	// CHECKSTYLE DISABLE MemberName FOR 1000 LINES

	public void setUp() {
		fieldKeySorter = new FieldKeySorterDecorator();
		xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(fieldKeySorter)));
	}

	public void testSortTagsAlphabetically() {
		xstream.toXML(new BaseClass());

		MoreAsserts.assertEquals("Sorted fields differ",
				new String[] {"a", "x"}, fieldKeySorter.getFieldNames(BaseClass.class));
	}

	public void testSortTagsAlphabeticallyByClassHierarchy() {
		xstream.toXML(new SubClass());

		MoreAsserts.assertEquals("Sorted fields differ",
				new String[] {"a", "x", "b", "y", "z"}, fieldKeySorter.getFieldNames(SubClass.class));
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class BaseClass {
		private int x;
		private int a;
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class SubClass extends BaseClass {
		private int b;
		private int z;
		private int y;
	}

	public void testGetFieldName() {
		FieldKey fieldKey = new FieldKey("b", SortAlphabeticallyWithAliases.class, 0);
		String fieldName = CatroidFieldKeySorter.getAliasOrFieldName(fieldKey);

		assertEquals("Wrong field name", "b", fieldName);
	}

	public void testGetFieldAlias() {
		FieldKey fieldKeyWithAlias = new FieldKey("a", SortAlphabeticallyWithAliases.class, 0);
		String fieldAlias = CatroidFieldKeySorter.getAliasOrFieldName(fieldKeyWithAlias);

		assertEquals("Wrong field alias", "x", fieldAlias);
	}

	public void testSortAlphabeticallyWithAliases() {
		xstream.processAnnotations(SortAlphabeticallyWithAliases.class);
		xstream.toXML(new SortAlphabeticallyWithAliases());

		MoreAsserts.assertEquals("Sorted fields differ",
				new String[] {"b", "x", "y"}, fieldKeySorter.getFieldNames(SortAlphabeticallyWithAliases.class));
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class SortAlphabeticallyWithAliases {
		@XStreamAlias("x")
		private int a;
		private int y;
		private int b;
	}

	public void testSortByAnnotation() {
		xstream.toXML(new SortByAnnotation());

		MoreAsserts.assertEquals("Sorted fields differ",
				new String[] {"c", "a", "d", "b"}, fieldKeySorter.getFieldNames(SortByAnnotation.class));
	}

	// Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
	// CHECKSTYLE DISABLE IndentationCheck FOR 6 LINES
	@XStreamFieldKeyOrder({
			"c",
			"a",
			"d",
			"b"
	})
	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class SortByAnnotation {
		private int a;
		private int b;
		private int c;
		private int d;
	}

	public void testSortByAnnotationWithAliases() {
		xstream.toXML(new SortByAnnotationWithAliases());

		MoreAsserts.assertEquals("Sorted fields differ",
				new String[] {"x", "b"}, fieldKeySorter.getFieldNames(SortByAnnotationWithAliases.class));
	}

	// Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
	// CHECKSTYLE DISABLE IndentationCheck FOR 4 LINES
	@XStreamFieldKeyOrder({
			"x",
			"b"
	})
	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class SortByAnnotationWithAliases {
		private int b;
		@XStreamAlias("x")
		private int a;
	}

	public void testMissingFieldInAnnotationThrowsException() {
		try {
			xstream.toXML(new MissingFieldInAnnotation());
			fail("XStream didn't throw an exception for missing field b in annotation");
		} catch (XStreamMissingSerializableFieldException expected) {
		}
	}

	// Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
	// CHECKSTYLE DISABLE IndentationCheck FOR 3 LINES
	@XStreamFieldKeyOrder({
			"a"
	})
	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class MissingFieldInAnnotation {
		private int a;
		private int b;
	}

	public void testSortByAnnotationIsInBaseClass() {
		xstream.toXML(new SubClassWithoutAnnotation());

		MoreAsserts.assertEquals("Sorted fields differ",
				new String[] {"b", "a"}, fieldKeySorter.getFieldNames(SubClassWithoutAnnotation.class));
	}

	public void testMissingFieldInSubClassWithoutAnnotationThrowsException() {
		try {
			xstream.toXML(new SubClassWithNewMemberButWithoutAnnotation());
			fail("XStream didn't throw an exception for missing field c in annotation");
		} catch (XStreamMissingSerializableFieldException expected) {
		}
	}

	// Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
	// CHECKSTYLE DISABLE IndentationCheck FOR 4 LINES
	@XStreamFieldKeyOrder({
			"b",
			"a"
	})
	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class BaseClassWithAnnotation {
		private int a;
		private int b;
	}

	private static class SubClassWithoutAnnotation extends BaseClassWithAnnotation {
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	private static class SubClassWithNewMemberButWithoutAnnotation extends BaseClassWithAnnotation {
		private int c;
	}
}
