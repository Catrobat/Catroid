/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.common;

import java.io.IOException;
import java.io.Serializable;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.Utils;

public class SoundInfo implements Serializable, Comparable<SoundInfo>, Cloneable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String fileName;
	public transient boolean isPlaying;

	public SoundInfo() {

	}

	@Override
	public SoundInfo clone() {
		SoundInfo cloneSoundInfo = new SoundInfo();

		cloneSoundInfo.name = this.name;
		cloneSoundInfo.fileName = this.fileName;

		return cloneSoundInfo;
	}

	public SoundInfo copySoundInfoForSprite(Sprite sprite) {
		SoundInfo cloneSoundInfo = new SoundInfo();

		cloneSoundInfo.name = this.name;

		try {
			cloneSoundInfo.fileName = StorageHandler
					.getInstance()
					.copySoundFile(
							Utils.buildPath(
									Utils.buildProjectPath(ProjectManager.INSTANCE.getCurrentProject().getName()),
									Constants.SOUND_DIRECTORY, fileName)).getName();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cloneSoundInfo;
	}

	public String getAbsolutePath() {
		if (fileName != null) {
			return Utils.buildPath(getPathToSoundDirectory(), fileName);
		} else {
			return null;
		}
	}

	public String getTitle() {
		return name;
	}

	public void setTitle(String title) {
		this.name = title;
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

	private String getPathToSoundDirectory() {
		return Utils.buildPath(Utils.buildProjectPath(ProjectManager.INSTANCE.getCurrentProject().getName()),
				Constants.SOUND_DIRECTORY);
	}

	@Override
	public int compareTo(SoundInfo soundInfo) {
		return name.compareTo(soundInfo.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
