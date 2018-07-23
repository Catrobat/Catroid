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

import org.catrobat.catroid.formulaeditor.UserData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserDataListWrapper<E extends UserData> {

	private List<E> elements = new ArrayList<>();

	UserDataListWrapper() {
	}

	public UserDataListWrapper(List<E> elements) {
		this.elements = elements;
	}

	public boolean add(E element) {
		return !contains(element.getName()) && elements.add(element);
	}

	public boolean contains(String name) {
		for (E element : elements) {
			if (element.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public E get(String name) {
		for (E element : elements) {
			if (element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}

	public boolean remove(String name) {
		for (Iterator<E> iterator = elements.iterator(); iterator.hasNext(); ) {
			E element = iterator.next();
			if (element.getName().equals(name)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	public int size() {
		return elements.size();
	}

	public List<E> getList() {
		return elements;
	}
}
