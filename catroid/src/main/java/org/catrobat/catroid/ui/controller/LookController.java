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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.List;

import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;

public final class LookController {

	public static final String TAG = LookController.class.getSimpleName();

	public static File loadFromExternal(String srcPath) {
		int[] imageDimensions = ImageEditing.getImageDimensions(srcPath);

		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			return null;
		}

		String currentImageDir = ProjectManager.getInstance().getCurrentScene().getSceneImageDirectoryPath();
		FileChecksumContainer checksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		return StorageHandler.copyFile(srcPath, currentImageDir, checksumContainer);
	}

	public static boolean loadFromPocketPaint(Intent intent, LookData lookData) {
		String pathOfPocketPaintImage = intent.getStringExtra(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);

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

	public static File loadFromExternalApp(Intent intent, Activity activity) {
		String sourceImagePath = "";
		Uri imageUri = intent.getData();
		if (imageUri != null) {

			Cursor cursor = activity.getContentResolver().query(imageUri, new String[] { android.provider.MediaStore.Images
					.ImageColumns.DATA }, null, null, null);

			if (cursor != null) {
				cursor.moveToFirst();
				sourceImagePath = cursor.getString(0);
				cursor.close();
			}
		}

		return loadFromExternal(sourceImagePath);
	}

	public static File loadDroneVideo(String droneVideoName) {
//		try {
//			StorageHandler storageHandler = StorageHandler.getInstance();
//			File imageFile = storageHandler.copyImageFromResourceToCatroid(activity, R.drawable.ic_video, imageName);
//		} catch (IOException e) {
//			return false;
//		}
		return null;
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

		String newLookFileName = getLookFileNameForBackpack(lookData);
		LookData backpackLookData = new LookData(lookData.getName(), newLookFileName);
		backpackLookData.isBackpackLookData = true;

		BackPackListManager.getInstance().addLookToBackPack(backpackLookData);
		return backpackLookData;
	}

	public static LookData backpackHidden(LookData lookData) {
		String newLookFileName = getLookFileNameForBackpack(lookData);
		LookData backpackLookData = new LookData(Utils.getUniqueLookName(lookData, true), newLookFileName);
		backpackLookData.isBackpackLookData = true;

		BackPackListManager.getInstance().addLookToHiddenBackPack(backpackLookData);
		return backpackLookData;
	}

	private static String getLookFileNameForBackpack(LookData lookData) {
		for (LookData backpackedLookData : BackPackListManager.getInstance().getAllBackPackedLooks()) {
			if (backpackedLookData.getChecksum().equals(lookData.getChecksum())) {
				return backpackedLookData.getLookFileName();
			}
		}

		FileChecksumContainer checksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		File file = StorageHandler.copyFile(lookData.getAbsolutePath(), BACKPACK_IMAGE_DIRECTORY, checksumContainer);
		return file.getName();
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
		String newLookFileName = getLookFileNameForUnpack(lookData);

		LookData lookDataToUnpack = new LookData(Utils.getUniqueLookName(lookData, false), newLookFileName);
		lookDataToUnpack.isBackpackLookData = true;
		ProjectManager.getInstance().getCurrentSprite().getLookDataList().add(lookDataToUnpack);

		return lookDataToUnpack;
	}

	public static LookData unpackHidden(LookData lookData) {
		if (ProjectManager.getInstance().getCurrentSprite().containsLookData(lookData)) {
			return lookData;
		}

		return unpackVisible(lookData);
	}

	private static String getLookFileNameForUnpack(LookData lookData) {
		for (Sprite sprite : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			for (LookData unpackedLookData : sprite.getLookDataList()) {
				if (unpackedLookData.getChecksum().equals(lookData.getChecksum())) {
					return unpackedLookData.getLookFileName();
				}
			}
		}

		FileChecksumContainer checksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		String currentImageDir = ProjectManager.getInstance().getCurrentScene().getSceneImageDirectoryPath();
		File file = StorageHandler.copyFile(lookData.getAbsolutePath(), currentImageDir, checksumContainer);
		return file.getName();
	}
}
