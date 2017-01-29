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
package org.catrobat.catroid.ui.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DroneVideoLookData;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.fragment.LookListFragment;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class LookController {

	public static final String TAG = LookController.class.getSimpleName();

	public static boolean loadFromPocketPaint(Intent intent, LookData lookData) {
		Bundle bundle = intent.getExtras();
		String pathOfPocketPaintImage = bundle.getString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);

		int[] imageDimensions = ImageEditing.getImageDimensions(pathOfPocketPaintImage);

		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			return false;
		}

		String pocketPaintImageChecksum = Utils.md5Checksum(new File(pathOfPocketPaintImage));
		if (lookData.getChecksum().equalsIgnoreCase(pocketPaintImageChecksum)) {
			return true;
		}

		ProjectManager projectManager = ProjectManager.getInstance();

		File newLookFile = StorageHandler.copyFile(pathOfPocketPaintImage,
				projectManager.getCurrentScene().getSceneImageDirectoryPath(),
				projectManager.getFileChecksumContainer());

		StorageHandler.getInstance().deleteFile(lookData.getAbsolutePath(), false);

		lookData.setLookFilename(newLookFile.getName());
		lookData.resetThumbnailBitmap();

		if (projectManager.getCurrentSprite().hasCollision()) {
			lookData.getCollisionInformation().calculate();
		}
		return true;
	}

	public static boolean loadFromMediaLibrary(String mediaFilePath, List<LookData> lookDataList) {
		File sourceImage = new File(mediaFilePath);

		if (copyImage(mediaFilePath, lookDataList, false)) {
			sourceImage.delete();
			return true;
		}

		sourceImage.delete();
		return false;
	}

	public static boolean loadFromCamera(Uri lookFromCameraUri, List<LookData> lookDataList) {
		if (lookFromCameraUri == null) {
			return false;
		}

		String sourceImagePath = lookFromCameraUri.getPath();
		File sourceImage = new File(sourceImagePath);

		if (copyImage(sourceImagePath, lookDataList, false)) {
			sourceImage.delete();
			return true;
		}

		sourceImage.delete();
		return false;
	}

	public static boolean loadFromExternalApp(Intent intent, Activity activity, List<LookData> lookDataList) {
		String sourceImagePath = "";
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			sourceImagePath = bundle.getString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);
		}

		Uri imageUri = intent.getData();
		if (imageUri != null) {

			Cursor cursor = activity.getContentResolver().query(imageUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);

			if (cursor != null) {
				cursor.moveToFirst();
				sourceImagePath = cursor.getString(0);
				cursor.close();
			}
		}

		if (sourceImagePath == null) {
			return false;
		}

		if (sourceImagePath.isEmpty()) {
			Bundle arguments = new Bundle();
			arguments.putParcelable(LookListFragment.LOADER_ARGUMENTS_IMAGE_URI, intent.getData());
			return true;
		}

		return copyImage(sourceImagePath, lookDataList, false);
	}

	public static boolean loadDroneVideo(String imageName, Activity activity, List<LookData> lookDataList) {
		try {
			StorageHandler storageHandler = StorageHandler.getInstance();
			File imageFile = storageHandler.copyImageFromResourceToCatroid(activity, R.drawable.ic_video, imageName);
			addLookDataToList(imageName, imageFile.getName(), lookDataList, true);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static boolean copyImage(String sourceImagePath, List<LookData> lookDataList, boolean isDroneVideo) {
		int[] imageDimensions = ImageEditing.getImageDimensions(sourceImagePath);
		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			return false;
		}

		try {
			File sourceFile = new File(sourceImagePath);

			ProjectManager projectManager = ProjectManager.getInstance();
			File imageFile = StorageHandler.copyFile(sourceImagePath,
					projectManager.getCurrentScene().getSceneImageDirectoryPath(),
					projectManager.getFileChecksumContainer());

			String imageName;
			int extensionDotIndex = sourceFile.getName().lastIndexOf('.');
			if (extensionDotIndex > 0) {
				imageName = sourceFile.getName().substring(0, extensionDotIndex);
			} else {
				imageName = sourceFile.getName();
			}

			String imageFileName = imageFile.getName();
			Pixmap pixmap = Utils.getPixmapFromFile(imageFile);

			if (pixmap == null) {
				ImageEditing.overwriteImageFileWithNewBitmap(imageFile);
				pixmap = Utils.getPixmapFromFile(imageFile);

				if (pixmap == null) {
					StorageHandler.getInstance().deleteFile(imageFile.getAbsolutePath(), false);
					return false;
				}
			}
			addLookDataToList(imageName, imageFileName, lookDataList, isDroneVideo);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error loading image in copyImage IOException");
			return false;
		}
	}

	private static void addLookDataToList(String imageName, String imageFileName, List<LookData> lookDataList,
			boolean isDroneVideo) {
		LookData lookData;
		if (isDroneVideo) {
			lookData = new DroneVideoLookData();
		} else {
			lookData = new LookData();
		}

		lookData.setName(imageName);
		lookData.setLookFilename(imageFileName);
		lookDataList.add(lookData);
	}

	public static boolean checkIfPocketPaintIsInstalled(Intent intent, final Activity activity) {
		List<ResolveInfo> packageList = activity.getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		if (packageList.size() <= 0) {
			AlertDialog.Builder builder = new CustomAlertDialogBuilder(activity);
			builder.setTitle(R.string.pocket_paint_not_installed_title);
			builder.setMessage(R.string.pocket_paint_not_installed);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					Intent downloadPocketPaintIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POCKET_PAINT_DOWNLOAD_LINK));
					activity.startActivity(downloadPocketPaintIntent);
				}
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
		return true;
	}

	public static boolean otherLookDataItemsHaveAFileReference(LookData lookDataToCheck) {
		for (LookData lookData : BackPackListManager.getInstance().getAllBackPackedLooks()) {
			if (lookData.equals(lookDataToCheck)) {
				continue;
			}
			if (lookData.getLookFileName().equals(lookDataToCheck.getLookFileName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean existsInBackPack(List<LookData> lookDataList) {
		for (LookData lookData : lookDataList) {
			if (existsInBackPack(lookData)) {
				return true;
			}
		}
		return false;
	}

	public static boolean existsInBackPack(LookData lookData) {
		return BackPackListManager.getInstance().backPackedLooksContain(lookData, true);
	}

	public static boolean backpack(List<LookData> lookDataList, boolean visible) {
		for (LookData lookData : lookDataList) {
			if (!backpack(lookData, visible)) {
				return false;
			}
		}
		return true;
	}

	protected static boolean backpack(LookData lookData, boolean visible) {
		if (visible) {
			return backpackVisible(lookData) != null;
		} else {
			return backpackHidden(lookData) != null;
		}
	}

	public static LookData backpackVisible(LookData lookData) {
		BackPackListManager.getInstance().removeItemFromLookBackPackByLookName(lookData.getName());
		LookData backpackLookData = new LookData();
		backpackLookData.isBackpackLookData = true;

		if (!setLookFileNameForBackpackLookData(lookData, backpackLookData)) {
			return null;
		}

		backpackLookData.setName(lookData.getName());
		BackPackListManager.getInstance().addLookToBackPack(backpackLookData);
		return backpackLookData;
	}

	public static LookData backpackHidden(LookData lookData) {
		LookData backpackLookData = new LookData();
		backpackLookData.isBackpackLookData = true;

		if (!setLookFileNameForBackpackLookData(lookData, backpackLookData)) {
			return null;
		}

		backpackLookData.setName(Utils.getUniqueLookName(lookData, true));
		BackPackListManager.getInstance().addLookToHiddenBackPack(backpackLookData);
		return backpackLookData;
	}

	private static boolean setLookFileNameForBackpackLookData(LookData lookData, LookData newLookData) {
		for (LookData backpackedLookData : BackPackListManager.getInstance().getAllBackPackedLooks()) {
			if (backpackedLookData.getChecksum().equals(lookData.getChecksum())) {
				newLookData.setLookFilename(backpackedLookData.getLookFileName());
				return true;
			}
		}

		try {
			String lookFileName = lookData.getLookFileNameWithoutChecksumAndEnding();
			File file = StorageHandler.getInstance().copyImageBackPack(lookData, lookFileName, false);
			newLookData.setLookFilename(file.getName());
			return true;
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			return false;
		}
	}

	public static boolean unpack(List<LookData> lookDataList, boolean visible) {
		for (LookData lookData : lookDataList) {
			if (!unpack(lookData, visible)) {
				return false;
			}
		}
		return true;
	}

	public static boolean unpack(LookData lookData, boolean visible) {
		return visible ? (unpackVisible(lookData) != null) : unpackHidden(lookData) != null;
	}

	public static LookData unpackVisible(LookData lookData) {
		LookData lookDataToUnpack = new LookData();
		lookDataToUnpack.isBackpackLookData = true;

		if (!setLookFileNameForUnpackedLookData(lookData, lookDataToUnpack)) {
			return null;
		}

		lookDataToUnpack.setName(Utils.getUniqueLookName(lookData, false));
		ProjectManager.getInstance().getCurrentSprite().getLookDataList().add(lookDataToUnpack);
		return lookDataToUnpack;
	}

	public static LookData unpackHidden(LookData lookData) {
		if (ProjectManager.getInstance().getCurrentSprite().containsLookData(lookData)) {
			return lookData;
		}

		return unpackVisible(lookData);
	}

	private static boolean setLookFileNameForUnpackedLookData(LookData lookData, LookData newLookData) {
		for (Sprite sprite : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			for (LookData unpackedLookData : sprite.getLookDataList()) {
				if (lookData.getChecksum().equals(unpackedLookData.getChecksum())) {
					newLookData.setLookFilename(unpackedLookData.getLookFileName());
					return true;
				}
			}
		}

		try {
			String lookFileName = lookData.getLookFileNameWithoutChecksumAndEnding();
			File file = StorageHandler.getInstance().copyImageBackPack(lookData, lookFileName, true);
			newLookData.setLookFilename(file.getName());
			return true;
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			return false;
		}
	}
}
