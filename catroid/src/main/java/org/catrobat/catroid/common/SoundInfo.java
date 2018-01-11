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

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;
import java.io.Serializable;

public class SoundInfo implements Serializable, Comparable<SoundInfo>, Cloneable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = SoundInfo.class.getSimpleName();

	private String name;
	private String fileName;

	public transient boolean isBackpackSoundInfo = false;

	public SoundInfo() {
	}

	public SoundInfo(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String title) {
		this.name = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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
		return (soundInfo.fileName.equals(this.fileName) && soundInfo.name.equals(this.name));
	}

	@Override
	public int hashCode() {
		return fileName.hashCode() + super.hashCode();
	}

	@Override
	public SoundInfo clone() {
		String copiedFileName;
		try {
			copiedFileName = StorageHandler.copyFile(getAbsolutePath()).getName();
		} catch (IOException e) {
			Log.e(TAG, "Could not copy file: " + fileName + ", fallback to shallow clone.");
			copiedFileName = fileName;
		}

		return new SoundInfo(name, copiedFileName);
	}

	public String getAbsolutePath() {
		if (fileName != null) {
			if (isBackpackSoundInfo) {
				return Utils.buildPath(getPathToBackPackSoundDirectory(), fileName);
			} else {
				return Utils.buildPath(getPathToSoundDirectory(), fileName);
			}
		} else {
			return null;
		}
	}

	private String getPathToSoundDirectory() {
		return Utils.buildPath(Utils.buildProjectPath(ProjectManager.getInstance().getCurrentProject().getName()),
				getSceneNameBySoundInfo(), Constants.SOUND_DIRECTORY);
	}

	private String getSceneNameBySoundInfo() {
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				if (sprite.getSoundList().contains(this)) {
					return scene.getName();
				}
			}
		}
		return ProjectManager.getInstance().getCurrentScene().getName();
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
}
