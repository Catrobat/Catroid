/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Reisenberger, Johannes Iber
 * 
 */
public class FileChecksumContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	private class FileInfo {
		private int usageCounter;
		private String path;
	}

	private Map<String, FileInfo> checksumFileInfoMap = new HashMap<String, FileInfo>(); //checksum / FileInfo

	/**
	 * @param checksum
	 * @param path
	 * @return true if a new File is added and false if the file already exists
	 */
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

	/**
	 * @param filepath
	 * @return true if this was the last usage and false if there is another usage
	 * @throws FileNotFoundException
	 *             if no entry for this path exists
	 */
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
