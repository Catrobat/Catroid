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
package org.catrobat.catroid.common;

import org.catrobat.catroid.content.Project;

public final class ScreenValues {

	private static final int DEFAULT_SCREEN_WIDTH = 1280;
	private static final int DEFAULT_SCREEN_HEIGHT = 768;

	// CHECKSTYLE DISABLE StaticVariableNameCheck FOR 2 LINES
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	public static final int CAST_SCREEN_WIDTH = 1280;
	public static final int CAST_SCREEN_HEIGHT = 720;

	// Suppress default constructor for noninstantiability
	private ScreenValues() {
		throw new AssertionError();
	}

	public static float getAspectRatio() {
		if (SCREEN_WIDTH == 0 || SCREEN_HEIGHT == 0) {
			setToDefaultScreenSize();
		}
		return (float) SCREEN_WIDTH / (float) SCREEN_HEIGHT;
	}

	public static void setToDefaultScreenSize() {
		SCREEN_WIDTH = DEFAULT_SCREEN_WIDTH;
		SCREEN_HEIGHT = DEFAULT_SCREEN_HEIGHT;
	}

	public static int getScreenHeightForProject(Project project) {
		if (project.isCastProject()) {
			return CAST_SCREEN_HEIGHT;
		}

		return SCREEN_HEIGHT;
	}

	public static int getScreenWidthForProject(Project project) {
		if (project.isCastProject()) {
			return CAST_SCREEN_WIDTH;
		}

		return SCREEN_WIDTH;
	}
}
