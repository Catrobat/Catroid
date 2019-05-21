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

package org.catrobat.catroid.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ListWithoutDuplicates<T> extends ArrayList<T> {

	private final Set<T> comparingSet = new HashSet<T>();

	private Collection<T> getUniqueValues(Collection<? extends T> collection) {
		Collection<T> uniqueValues = new ArrayList<T>();
		for (T element : collection) {
			if (comparingSet.add(element)) {
				uniqueValues.add(element);
			}
		}

		return uniqueValues;
	}

	@Override
	public boolean add(T t) {
		return comparingSet.add(t) && super.add(t);
	}

	@Override
	public void add(int index, T element) {
		if (comparingSet.add(element)) {
			try {
				super.add(index, element);
			} catch (IndexOutOfBoundsException ioobException) {
				comparingSet.remove(element);
				throw ioobException;
			}
		}
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		Collection<T> uniqueValuesToAdd = getUniqueValues(c);
		if (uniqueValuesToAdd.isEmpty()) {
			return false;
		}

		return super.addAll(uniqueValuesToAdd);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		Collection<T> uniqueValuesToAdd = getUniqueValues(c);
		if (uniqueValuesToAdd.isEmpty()) {
			return false;
		}

		try {
			return super.addAll(index, uniqueValuesToAdd);
		} catch (IndexOutOfBoundsException ioobException) {
			comparingSet.removeAll(uniqueValuesToAdd);
			throw ioobException;
		}
	}

	@Override
	public void clear() {
		comparingSet.clear();
		super.clear();
	}

	@Override
	public T remove(int index) {
		try {
			T removedObject = super.remove(index);
			comparingSet.remove(removedObject);
			return removedObject;
		} catch (IndexOutOfBoundsException ioobException) {
			throw ioobException;
		}
	}

	@Override
	public boolean remove(Object o) {
		return comparingSet.remove(o) && super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return comparingSet.removeAll(c) && super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return comparingSet.retainAll(c) && super.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		try {

			if (element.equals(this.get(index))) {
				comparingSet.clear();
				comparingSet.addAll(this);
				return element;
			}

			T oldObject = null;
			if (comparingSet.add(element)) {
				oldObject = super.set(index, element);
				comparingSet.remove(oldObject);
			}

			return oldObject;
		} catch (IndexOutOfBoundsException ioobException) {
			throw ioobException;
		}
	}
}
