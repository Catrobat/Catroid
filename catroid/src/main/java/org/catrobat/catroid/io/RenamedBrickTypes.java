/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

import org.catrobat.catroid.content.bricks.FaceNameTrainBrick;

import java.util.Collections;
import java.util.Map;

/**
 * Brick classes that were renamed. Projects, the backpack and the recent bricks
 * store a brick under its class name, so files saved before a rename still name
 * the old class; they are read as the class it has now.
 */
public final class RenamedBrickTypes {

	private static final Map<String, String> CURRENT_SIMPLE_NAMES = Collections.singletonMap(
			"FaceNameTrain", FaceNameTrainBrick.class.getSimpleName());

	private RenamedBrickTypes() {
	}

	/** The current simple class name for a saved one, which may be an old name. */
	public static String currentSimpleName(String simpleName) {
		String current = CURRENT_SIMPLE_NAMES.get(simpleName);
		return current != null ? current : simpleName;
	}

	/** The current fully qualified class name for a saved one, which may name an old class. */
	public static String currentClassName(String className) {
		int dot = className.lastIndexOf('.');
		return className.substring(0, dot + 1) + currentSimpleName(className.substring(dot + 1));
	}
}
