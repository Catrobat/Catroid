/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileChecksumContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	private class FileInfo {
		private int usageCounter;
		private String path;
	}

	private Map<String, FileInfo> checksumFileInfoMap = new HashMap<String, FileInfo>();

	public boolean addChecksum(String checksum, String path) {
		if (checksumFileInfoMap.containsKey(checksum)) {
			FileInfo fileInfo = checksumFileInfoMap.get(checksum);
			++fileInfo.usageCounter;
			return false;
		} else {
			FileInfo fileInfo = new FileInfo();
			fileInfo.usageCounter = 1;
			fileInfo.path = path;
			checksumFileInfoMap.put(checksum, fileInfo);
			return true;
		}
	}

	public boolean containsChecksum(String checksum) {
		return checksumFileInfoMap.containsKey(checksum);
	}

	public String getPath(String checksum) {
		return checksumFileInfoMap.get(checksum).path;
	}

	public int getUsage(String checksum) {
		if (!checksumFileInfoMap.containsKey(checksum)) {
			return 0;
		} else {
			return checksumFileInfoMap.get(checksum).usageCounter;
		}
	}

	public void incrementUsage(String filepath) throws FileNotFoundException {
		String checksum = null;
		for (Map.Entry<String, FileInfo> entry : checksumFileInfoMap.entrySet()) {

			if (entry.getValue().path.equalsIgnoreCase(filepath)) {
				checksum = entry.getKey();
				break;
			}
		}
		if (checksum == null) {
			throw new FileNotFoundException();
		}
		FileInfo fileInfo = checksumFileInfoMap.get(checksum);
		fileInfo.usageCounter++;
	}

	public boolean decrementUsage(String filepath) throws FileNotFoundException {
		String checksum = null;
		for (Map.Entry<String, FileInfo> entry : checksumFileInfoMap.entrySet()) {

			if (entry.getValue().path.equalsIgnoreCase(filepath)) {
				checksum = entry.getKey();
				break;
			}
		}
		if (checksum == null) {
			throw new FileNotFoundException();
		}
		FileInfo fileInfo = checksumFileInfoMap.get(checksum);
		fileInfo.usageCounter--;
		if (fileInfo.usageCounter < 1) {
			checksumFileInfoMap.remove(checksum);
			return true;
		}
		return false;
	}
}
