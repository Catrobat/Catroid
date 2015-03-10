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
package org.catrobat.catroid.utils;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class UtilCamera {

	// Suppress default constructor for noninstantiability
	private UtilCamera() {
		throw new AssertionError();
	}

	public static Uri getDefaultLookFromCameraUri(String defLookName) {
		File pictureFile = new File(Constants.DEFAULT_ROOT, defLookName + ".jpg");
		return Uri.fromFile(pictureFile);
	}

	public static Uri rotatePictureIfNecessary(Uri lookFromCameraUri, String defLookName) {
		Uri rotatedPictureUri;
		int rotate = getPhotoRotationDegree(lookFromCameraUri.getPath());

		if (rotate != 0) {
			Project project = ProjectManager.getInstance().getCurrentProject();
			File fullSizeImage = new File(lookFromCameraUri.getPath());

			// Height and Width switched for proper scaling for portrait format photos from camera
			Bitmap fullSizeBitmap = ImageEditing.getScaledBitmapFromPath(fullSizeImage.getAbsolutePath(),
					project.getXmlHeader().virtualScreenHeight, project.getXmlHeader().virtualScreenWidth,
					ImageEditing.ResizeType.FILL_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
			Bitmap rotatedBitmap = ImageEditing.rotateBitmap(fullSizeBitmap, rotate);
			File downScaledCameraPicture = new File(Constants.DEFAULT_ROOT, defLookName + ".jpg");
			rotatedPictureUri = Uri.fromFile(downScaledCameraPicture);
			try {
				StorageHandler.saveBitmapToImageFile(downScaledCameraPicture, rotatedBitmap);
			} catch (FileNotFoundException e) {
				Log.e("CATROID", "Could not find file to save bitmap.", e);
			}

			return rotatedPictureUri;
		}

		return lookFromCameraUri;
	}

	private static int getPhotoRotationDegree(String imagePath) {
		int rotate = 0;
		try {
			File imageFile = new File(imagePath);
			ExifInterface exifDataReader = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exifDataReader.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
			}
		} catch (IOException e) {
			Log.e("CATROID", "Could not find file to initialize ExifInterface.", e);
		}
		return rotate;
	}
}
