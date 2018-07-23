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

package org.catrobat.catroid.utils;

import java.io.File;

import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT_DIRECTORY;

public final class PathBuilder {

	private PathBuilder() {
		throw new AssertionError();
	}

	public static String buildPath(String... elements) {
		StringBuilder result = new StringBuilder("/");

		for (String pathElement : elements) {
			result.append(pathElement).append('/');
		}

		String returnValue = result.toString().replaceAll("/+", "/");

		if (returnValue.endsWith("/")) {
			returnValue = returnValue.substring(0, returnValue.length() - 1);
		}

		return returnValue;
	}

	public static String buildProjectPath(String projectName) {
		return new File(DEFAULT_ROOT_DIRECTORY,
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)).getAbsolutePath();
	}

	public static String buildScenePath(String projectName, String sceneName) {
		return buildPath(buildProjectPath(projectName),
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(sceneName));
	}
}
