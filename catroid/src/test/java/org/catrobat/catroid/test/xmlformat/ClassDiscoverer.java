/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.xmlformat;

import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public final class ClassDiscoverer {

	private ClassDiscoverer() {
		throw new AssertionError();
	}

	public static <T> Set<Class<? extends T>> getAllSubClassesOf(Class<T> clazz) {
		Reflections reflections = new Reflections("org.catrobat.catroid");
		return reflections.getSubTypesOf(clazz);
	}

	public static <T> Set<Class<? extends T>> removeAbstractClasses(Set<Class<? extends T>> classes) {
		Set<Class<? extends T>> filtered = new HashSet<>();

		for (Class<? extends T> clazz : classes) {
			boolean isAbstract = Modifier.isAbstract(clazz.getModifiers());
			if (!isAbstract) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	public static <T> Set<Class<? extends T>> removeInnerClasses(Set<Class<? extends T>> classes) {
		Set<Class<? extends T>> filtered = new HashSet<>();

		for (Class<? extends T> clazz : classes) {
			boolean isInnerClass = clazz.getEnclosingClass() != null;
			if (!isInnerClass) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	public static <T> Set<Class<? extends T>> removeEndBrick(Set<Class<? extends T>> classes) {
		Set<Class<? extends T>> filtered = new HashSet<>();

		for (Class<? extends T> clazz : classes) {
			if (!clazz.getName().contains("EndBrick")) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}
}
