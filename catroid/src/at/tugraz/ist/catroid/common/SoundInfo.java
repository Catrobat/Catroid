/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import at.tugraz.ist.catroid.ProjectManager;

public class SoundInfo implements Comparable<SoundInfo> {

	private int id;
	private String title;
	private String fileName;
	public boolean isPlaying;

	public SoundInfo() {
		isPlaying = false;
	}

	public String getAbsolutePath() {
		if (fileName != null) {
			return getPathWithoutFileName() + fileName;
		} else {
			return null;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSoundFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSoundFileName() {
		return fileName;
	}

	public String getChecksum() {
		if (fileName == null) {
			return null;
		}
		return fileName.substring(0, 32);
	}

	public String getFileExtension() {
		if (fileName == null) {
			return null;
		}
		return fileName.substring(fileName.length() - 4, fileName.length());
	}

	public String getPathWithoutFileName() {
		return Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ Consts.SOUND_DIRECTORY + "/";
	}

	public int compareTo(SoundInfo soundInfo) {
		return title.compareTo(soundInfo.title);
	}

	@Override
	public String toString() {
		return title;
	}
}
