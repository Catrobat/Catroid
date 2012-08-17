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
package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;

public class UtilCamera {

	private UtilCamera() {
	}

	public static Uri getDefaultCostumeFromCameraUri(String defCostumeName) {
		File pictureFile = new File(Constants.TMP_PATH, defCostumeName + ".jpg");
		return Uri.fromFile(pictureFile);
	}

	public static Uri rotatePictureIfNecessary(Uri costumeFromCameraUri, String defCostumeName) {
		Uri rotatedPictureUri;
		int rotate = getPhotoRotationDegree(costumeFromCameraUri, costumeFromCameraUri.getPath());

		if (rotate != 0) {
			Project project = ProjectManager.getInstance().getCurrentProject();
			File fullSizeImage = new File(costumeFromCameraUri.getPath());

			// Height and Width switched for proper scaling for portrait format photos from camera
			Bitmap fullSizeBitmap = ImageEditing.getScaledBitmapFromPath(fullSizeImage.getAbsolutePath(),
					project.virtualScreenHeight, project.virtualScreenWidth, true);
			Bitmap rotatedBitmap = ImageEditing.rotateBitmap(fullSizeBitmap, rotate);
			File downScaledCameraPicture = new File(Constants.TMP_PATH, defCostumeName + ".jpg");
			rotatedPictureUri = Uri.fromFile(downScaledCameraPicture);
			try {
				StorageHandler.saveBitmapToImageFile(downScaledCameraPicture, rotatedBitmap);
			} catch (FileNotFoundException e) {
				Log.e("CATROID", "Could not find file to save bitmap.", e);
			}

			return rotatedPictureUri;
		}

		return costumeFromCameraUri;
	}

	private static int getPhotoRotationDegree(Uri imageUri, String imagePath) {
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
