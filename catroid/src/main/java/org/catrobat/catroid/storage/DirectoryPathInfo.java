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

package org.catrobat.catroid.storage;

import java.io.File;

public class DirectoryPathInfo extends PathInfo {

	public DirectoryPathInfo(DirectoryPathInfo parent, String directoryName) {
		super(parent, directoryName);
	}

	public DirectoryPathInfo(String rootDirectoryPath) {
		this(null, rootDirectoryPath);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	void checkPathSanity() throws IllegalArgumentException {
		if (parent != null) {
			// this is not a root directory, check relative path
			if (relativePath.contains(File.separator) || relativePath.contains("\\")) {
				throw new IllegalArgumentException("directoryName may not contain path separator characters.");
			}
		}

		File dir = new File(getAbsolutePath());
		// Note: Android API 26 will support java.nio.file package, which will make this check
		// way better.
		if (dir.exists() && !dir.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory.");
		}
	}
}
