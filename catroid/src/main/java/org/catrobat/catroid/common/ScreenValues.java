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
package org.catrobat.catroid.common;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.utils.Resolution;

public final class ScreenValues {
	private static final Resolution DEFAULT_SCREEN_RESOLUTION = new Resolution(1280, 768);
	public static final Resolution CAST_SCREEN_RESOLUTION = new Resolution(1280, 720);
	public static Resolution currentScreenResolution;

	private ScreenValues() {
		throw new AssertionError();
	}

	public static void setToDefaultScreenSize() {
		currentScreenResolution = DEFAULT_SCREEN_RESOLUTION;
	}

	public static Resolution getResolutionForProject(Project project) {
		if (project.isCastProject()) {
			return CAST_SCREEN_RESOLUTION;
		}

		return currentScreenResolution;
	}
}
