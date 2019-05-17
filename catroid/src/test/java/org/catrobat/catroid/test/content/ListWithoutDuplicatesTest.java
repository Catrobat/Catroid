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

package org.catrobat.catroid.test.content;

import org.catrobat.catroid.content.ListWithoutDuplicates;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ListWithoutDuplicatesTest<T> {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Integer", Integer.class,
						new Integer[]{1, 2, 3},
						new Integer[]{4, 5},
						new Integer[]{1, 2}},
				{"String", String.class,
						new String[]{"a", "b", "c"},
						new String[]{"d", "e"},
						new String[]{"a", "b"}},
				{"UserVariable", UserVariable.class,
						new UserVariable[]{new UserVariable("x"), new UserVariable("y"), new UserVariable("z")},
						new UserVariable[]{new UserVariable("q"), new UserVariable("r")},
						new UserVariable[]{new UserVariable("x"), new UserVariable("y")}},
				{"UserList", UserList.class,
						new UserList[]{new UserList("list1"), new UserList("list2"), new UserList("list3")},
						new UserList[]{new UserList("list4"), new UserList("list5")},
						new UserList[]{new UserList("list1"), new UserList("list2")}},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<T> clazz;

	@Parameterized.Parameter(2)
	public T[] initialValues;

	@Parameterized.Parameter(3)
	public T[] itemsCurrentlyNotInList;

	@Parameterized.Parameter(4)
	public T[] itemsCurrentlyInList;

	private ListWithoutDuplicates<T> testListWithoutDuplicates;
	private Set<T> comparingHashSet;

	@Before
	public void setUp() {
		testListWithoutDuplicates = new ListWithoutDuplicates<T>();
		testListWithoutDuplicates.addAll(Arrays.asList(initialValues));

		comparingHashSet = new HashSet<T>();
		comparingHashSet.addAll(Arrays.asList(initialValues));

		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void addNonExistingItem() {
		boolean addNonExistingItem = testListWithoutDuplicates.add(itemsCurrentlyNotInList[0]);
		assertTrue(addNonExistingItem);
		assertEquals(addNonExistingItem, comparingHashSet.add(itemsCurrentlyNotInList[0]));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void addExistingItem() {
		boolean addExistingItem = testListWithoutDuplicates.add(itemsCurrentlyInList[0]);
		assertFalse(addExistingItem);
		assertEquals(addExistingItem, comparingHashSet.add(itemsCurrentlyInList[0]));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void addIndexOutOfBoundsBehind() {
		testListWithoutDuplicates.add(5, itemsCurrentlyNotInList[0]);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void addIndexOutOfBoundsNegative() {
		testListWithoutDuplicates.add(-1, itemsCurrentlyNotInList[0]);
	}

	@Test
	public void addIndexNonExistingItem() {
		testListWithoutDuplicates.add(0, itemsCurrentlyNotInList[0]);
		comparingHashSet.add(itemsCurrentlyNotInList[0]);
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
		assertEquals(testListWithoutDuplicates.get(0), itemsCurrentlyNotInList[0]);
	}

	@Test
	public void addIndexExistingItem() {
		T itemAtIndex = testListWithoutDuplicates.get(0);
		testListWithoutDuplicates.add(0, itemsCurrentlyInList[0]);
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
		assertEquals(testListWithoutDuplicates.get(0), itemAtIndex);
	}

	@Test
	public void addAllNonExistingItems() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyNotInList[0], itemsCurrentlyNotInList[1]);
		boolean addAllNonExistingItem = testListWithoutDuplicates.addAll(listToAdd);
		assertTrue(addAllNonExistingItem);
		assertEquals(addAllNonExistingItem, comparingHashSet.addAll(listToAdd));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void addAllNonAndExistingItem() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyNotInList[0]);
		boolean addAllNonAndExistingItem = testListWithoutDuplicates.addAll(listToAdd);
		assertTrue(addAllNonAndExistingItem);
		assertEquals(addAllNonAndExistingItem, comparingHashSet.addAll(listToAdd));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void addAllExistingItem() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyInList[1]);
		boolean addAllExistingItem = testListWithoutDuplicates.addAll(listToAdd);
		assertFalse(addAllExistingItem);
		assertEquals(addAllExistingItem, comparingHashSet.addAll(listToAdd));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void addAllIndexOutOfBoundsBehind() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyNotInList[0], itemsCurrentlyNotInList[1]);
		testListWithoutDuplicates.addAll(5, listToAdd);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void addAllIndexOutOfBoundsNegative() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyNotInList[0], itemsCurrentlyNotInList[1]);
		testListWithoutDuplicates.addAll(-1, listToAdd);
	}

	@Test
	public void addAllIndexNonExistingItem() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyNotInList[0], itemsCurrentlyNotInList[1]);
		assertTrue(comparingHashSet.addAll(listToAdd));
		assertTrue(testListWithoutDuplicates.addAll(0, listToAdd));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
		assertEquals(testListWithoutDuplicates.get(0), itemsCurrentlyNotInList[0]);
		assertEquals(testListWithoutDuplicates.get(1), itemsCurrentlyNotInList[1]);
	}

	@Test
	public void addAllIndexNonAndExistingItem() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyNotInList[0]);
		T itemAtIndex = testListWithoutDuplicates.get(0);
		assertTrue(comparingHashSet.addAll(listToAdd));
		assertTrue(testListWithoutDuplicates.addAll(0, listToAdd));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
		assertEquals(testListWithoutDuplicates.get(0), itemsCurrentlyNotInList[0]);
		assertEquals(testListWithoutDuplicates.get(1), itemAtIndex);
	}

	@Test
	public void addAllIndexExistingItem() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyInList[1]);
		T itemAtIndex = testListWithoutDuplicates.get(0);
		assertFalse(comparingHashSet.addAll(listToAdd));
		assertFalse(testListWithoutDuplicates.addAll(0, listToAdd));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
		assertEquals(testListWithoutDuplicates.get(0), itemAtIndex);
	}

	@Test
	public void addEmptyCollection() {
		List<T> emptyList = new ArrayList<>();
		boolean addEmptyCollection = testListWithoutDuplicates.addAll(emptyList);
		assertFalse(addEmptyCollection);
		assertEquals(addEmptyCollection, comparingHashSet.addAll(emptyList));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void addIndexExceptionTest() {
		try {
			testListWithoutDuplicates.add(4, itemsCurrentlyNotInList[0]);
		} catch (IndexOutOfBoundsException ioobException) {
			assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
			testListWithoutDuplicates.add(0, itemsCurrentlyNotInList[0]);
			comparingHashSet.add(itemsCurrentlyNotInList[0]);
			assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
			assertEquals(testListWithoutDuplicates.get(0), itemsCurrentlyNotInList[0]);
		}
	}

	@Test
	public void addAllIndexExceptionTest() {
		List<T> listToAdd = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyNotInList[0]);
		try {
			testListWithoutDuplicates.addAll(5, listToAdd);
		} catch (IndexOutOfBoundsException ioobException) {
			assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
			testListWithoutDuplicates.addAll(0, listToAdd);
			comparingHashSet.addAll(listToAdd);
			assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
			assertEquals(testListWithoutDuplicates.get(0), itemsCurrentlyNotInList[0]);
		}
	}

	@Test
	public void clearTest() {
		testListWithoutDuplicates.clear();
		comparingHashSet.clear();
		assertTrue(testListWithoutDuplicates.isEmpty());
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void removeByIndex() {
		T removedObjectAtIndex = testListWithoutDuplicates.get(0);
		comparingHashSet.remove(removedObjectAtIndex);
		assertEquals(testListWithoutDuplicates.remove(0), removedObjectAtIndex);
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void removeByIndexOutOfBounds() {
		testListWithoutDuplicates.remove(5);
	}

	@Test
	public void removeByObject() {
		T removeObject = initialValues[0];
		assertEquals(testListWithoutDuplicates.remove(removeObject), comparingHashSet.remove(removeObject));
		assertEquals(testListWithoutDuplicates.remove(removeObject), comparingHashSet.remove(removeObject));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void removeAllEmptyCollection() {
		List<T> emptyList = new ArrayList<>();
		assertEquals(testListWithoutDuplicates.removeAll(emptyList), comparingHashSet.removeAll(emptyList));
		assertFalse(testListWithoutDuplicates.isEmpty());
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void removeAllNonAndExistingItems() {
		List<T> listToRemove = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyNotInList[0]);
		assertEquals(testListWithoutDuplicates.removeAll(listToRemove), comparingHashSet.removeAll(listToRemove));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void removeAllNonExistingItems() {
		List<T> listToRemove = Arrays.asList(itemsCurrentlyNotInList[0], itemsCurrentlyNotInList[1]);
		assertEquals(testListWithoutDuplicates.removeAll(listToRemove), comparingHashSet.removeAll(listToRemove));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void removeAllExistingItems() {
		List<T> listToRemove = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyInList[1]);
		assertEquals(testListWithoutDuplicates.removeAll(listToRemove), comparingHashSet.removeAll(listToRemove));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void retainAllEmptyList() {
		List<T> emptyList = new ArrayList<>();
		assertEquals(testListWithoutDuplicates.retainAll(emptyList), comparingHashSet.retainAll(emptyList));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void retainAllNonExistingItems() {
		List<T> listToRetain = Arrays.asList(itemsCurrentlyNotInList[0], itemsCurrentlyNotInList[1]);
		assertEquals(testListWithoutDuplicates.retainAll(listToRetain), comparingHashSet.retainAll(listToRetain));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test
	public void retainAllNonAndExistingItems() {
		List<T> listToRetain = Arrays.asList(itemsCurrentlyInList[0], itemsCurrentlyInList[1]);
		assertEquals(testListWithoutDuplicates.retainAll(listToRetain), comparingHashSet.retainAll(listToRetain));
		assertThat(testListWithoutDuplicates, containsInAnyOrder(comparingHashSet.toArray()));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void setElementAtIndexOutOfBounds() {
		testListWithoutDuplicates.set(5, itemsCurrentlyNotInList[0]);
	}

	@Test
	public void setElementAtIndexToExisting() {
		testListWithoutDuplicates.set(0, itemsCurrentlyInList[0]);
		assertArrayEquals(testListWithoutDuplicates.toArray(), initialValues);
	}

	@Test
	public void setElementAtIndexToNonExisting() {
		testListWithoutDuplicates.set(0, itemsCurrentlyNotInList[0]);
		assertEquals(testListWithoutDuplicates.get(0), itemsCurrentlyNotInList[0]);
		assertEquals(testListWithoutDuplicates.get(1), initialValues[1]);
		assertEquals(testListWithoutDuplicates.get(2), initialValues[2]);
	}
}
