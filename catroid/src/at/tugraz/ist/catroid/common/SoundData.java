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

/**
 * @author ainulhusna
 * 
 */
public class SoundData {
	private String soundAbsolutePath;
	private String soundName;
	private String soundFileName;

	public String getSoundAbsolutePath() {
		return soundAbsolutePath;
	}

	public void setSoundAbsolutePath(String absolutePath) {
		this.soundAbsolutePath = absolutePath;
	}

	public String getSoundName() {
		return soundName;
	}

	public void setSoundName(String soundName) {
		this.soundName = soundName;
	}

	public void setSoundFileName(String soundFileName) {
		this.soundFileName = soundFileName;
	}

	public String getSoundFileName() {
		return soundFileName;
	}

	public String getChecksum() {
		return soundFileName.substring(0, 32);
	}

	public String getFileExtension() {
		return soundFileName.substring(soundFileName.length() - 4, soundFileName.length());
	}

	public String getPathWithoutTitle() {
		return soundAbsolutePath.substring(0, soundAbsolutePath.length() - soundFileName.length());
	}

}
