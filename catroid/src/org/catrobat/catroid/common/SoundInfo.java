/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.utils.Utils;

import java.io.FileNotFoundException;
import java.io.Serializable;

public class SoundInfo implements Serializable, Comparable<SoundInfo>, Cloneable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = SoundInfo.class.getSimpleName();
	public transient boolean isPlaying;
	public transient boolean isBackpackSoundInfo;
	private String name;
	private String fileName;

	public SoundInfo() {
		isBackpackSoundInfo = false;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SoundInfo)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		SoundInfo soundInfo = (SoundInfo) obj;
		if (soundInfo.fileName.equals(this.fileName) && soundInfo.name.equals(this.name)) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode() + fileName.hashCode() + super.hashCode();
	}

	@Override
	public SoundInfo clone() {
		SoundInfo cloneSoundInfo = new SoundInfo();

		cloneSoundInfo.name = this.name;
		cloneSoundInfo.fileName = this.fileName;
		cloneSoundInfo.isBackpackSoundInfo = false;

		try {
			ProjectManager.getInstance().getFileChecksumContainer().incrementUsage(getAbsolutePath());
		} catch (FileNotFoundException fileNotFoundexception) {
			Log.e(TAG, Log.getStackTraceString(fileNotFoundexception));
		}

		return cloneSoundInfo;
	}

	public String getAbsolutePath() {
		if (fileName != null) {
			if (isBackpackSoundInfo) {
				return Utils.buildPath(getPathToSoundDirectory(), fileName);
			} else {
				return Utils.buildPath(getPathToSoundDirectory(), fileName);
			}
		} else {
			return null;
		}
	}

	public String getAbsoluteProjectPath() {
		if (fileName != null) {
			return Utils.buildPath(getPathToSoundDirectory(), fileName);
		} else {
			return null;
		}
	}

	public String getAbsoluteBackPackPath() {
		if (fileName != null) {
			return Utils.buildPath(getPathToBackPackSoundDirectory(), fileName);
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

	public String getSoundFileName() {
		return fileName;
	}

	public void setSoundFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getChecksum() {
		if (fileName == null) {
			return null;
		}
		return fileName.substring(0, 32);
	}

	private String getPathToSoundDirectory() {
		return Utils.buildPath(Utils.buildProjectPath(ProjectManager.getInstance().getCurrentProject().getName()),
				Constants.SOUND_DIRECTORY);
	}

	private String getPathToBackPackSoundDirectory() {
		return Utils.buildPath(Constants.DEFAULT_ROOT, Constants.BACKPACK_DIRECTORY,
				Constants.BACKPACK_SOUND_DIRECTORY);
	}

	@Override
	public int compareTo(SoundInfo soundInfo) {
		return name.compareTo(soundInfo.name);
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isBackpackSoundInfo() {
		return isBackpackSoundInfo;
	}
}
