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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

public class CostumeData {

	private String costumeName;
	private String costumeFileName;
	private transient Bitmap thumbnailBitmap;
	private transient Integer width;
	private transient Integer height;
	private transient static final int THUMBNAIL_WIDTH = 150;
	private transient static final int THUMBNAIL_HEIGHT = 150;

	public String getAbsolutePath() {
		if (costumeFileName != null) {
			return Utils.buildPath(getPathWithoutFileName(), costumeFileName);
		} else {
			return null;
		}
	}

	public String getInternalPath() {
		if (costumeFileName != null) {
			return Consts.IMAGE_DIRECTORY + "/" + costumeFileName;
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

	public String getPathWithoutFileName() {
		return Utils.buildPath(Consts.DEFAULT_ROOT, ProjectManager.getInstance().getCurrentProject().getName(),
				Consts.IMAGE_DIRECTORY);
	}

	public Bitmap getThumbnailBitmap() {
		if (thumbnailBitmap == null) {
			thumbnailBitmap = ImageEditing.getScaledBitmap(getAbsolutePath(), THUMBNAIL_HEIGHT, THUMBNAIL_WIDTH);
		}
		return thumbnailBitmap;
	}

	public void resetThumbnailBitmap() {
		thumbnailBitmap = null;
	}

	public int[] getResolution() {
		if (width != null && height != null) {
			return new int[] { width, height };
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(getAbsolutePath(), options);
		width = options.outWidth;
		height = options.outHeight;

		return new int[] { width, height };
	}

	@Override
	public String toString() {
		return costumeName;
	}
}
