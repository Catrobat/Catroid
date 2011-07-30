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

import android.graphics.Bitmap;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class CostumeData {

	private String costumeName;
	private String costumeFileName;
	private transient Bitmap thumbnailBitmap;

	public String getAbsolutePath() {
		if (costumeFileName != null) {
			return getPathWithoutFileName() + costumeFileName;
		} else {
			return null;
		}
	}

	public String getCostumeName() {
		return costumeName;
	}

	public void setCostumeName(String name) {
		this.costumeName = name;
	}

	public void setCostumeFilename(String fileName) {
		this.costumeFileName = fileName;
	}

	public String getCostumeFileName() {
		return costumeFileName;
	}

	public String getChecksum() {
		if (costumeFileName == null) {
			return null;
		}
		return costumeFileName.substring(0, 32);
	}

	public String getFileExtension() {
		if (costumeFileName == null) {
			return null;
		}
		String[] splittedFileName = costumeFileName.split(".");
		return splittedFileName[splittedFileName.length - 1];
	}

	public String getPathWithoutFileName() {
		return Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ Consts.IMAGE_DIRECTORY + "/";
	}

	public Bitmap getThumbnailBitmap() {
		if (thumbnailBitmap == null) {
			thumbnailBitmap = ImageEditing.getScaledBitmap(getAbsolutePath(), Consts.THUMBNAIL_HEIGHT,
					Consts.THUMBNAIL_WIDTH);
		}
		return thumbnailBitmap;
	}

	@Override
	public String toString() {
		return costumeName;
	}
}
