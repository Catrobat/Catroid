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
package org.catrobat.catroid.ui.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.badlogic.gdx.graphics.Pixmap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.LookViewHolder;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class LookController {
	public static final int REQUEST_SELECT_OR_DRAW_IMAGE = 0;
	public static final int REQUEST_POCKET_PAINT_EDIT_IMAGE = 1;
	public static final int REQUEST_TAKE_PICTURE = 2;
	public static final int REQUEST_MEDIA_LIBRARY = 3;
	public static final int ID_LOADER_MEDIA_IMAGE = 1;
	public static final String BUNDLE_ARGUMENTS_SELECTED_LOOK = "selected_look";
	public static final String BUNDLE_ARGUMENTS_URI_IS_SET = "uri_is_set";
	public static final String LOADER_ARGUMENTS_IMAGE_URI = "image_uri";
	public static final String SHARED_PREFERENCE_NAME = "showDetailsLooks";

	private static final String TAG = LookController.class.getSimpleName();
	private static final LookController INSTANCE = new LookController();

	private LookController() {
	}

	public static LookController getInstance() {
		return INSTANCE;
	}

	public void updateLookLogic(final int position, final LookViewHolder holder, final LookBaseAdapter lookAdapter) {
		final LookData lookData = lookAdapter.getLookDataItems().get(position);

		if (lookData == null) {
			return;
		}
		holder.lookNameTextView.setTag(position);
		holder.lookElement.setTag(position);
		holder.lookImageView.setImageBitmap(lookData.getThumbnailBitmap());
		holder.lookNameTextView.setText(lookData.getLookName());

		boolean checkboxIsVisible = handleCheckboxes(position, holder, lookAdapter);
		handleDetails(lookData, holder, lookAdapter);

		// Disable ImageView on active ActionMode
		if (checkboxIsVisible) {
			holder.lookImageView.setEnabled(false);
		} else {
			holder.lookImageView.setEnabled(true);
		}
		if (holder.lookElement.isClickable()) {
			holder.lookElement.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (lookAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
						holder.checkbox.setChecked(!holder.checkbox.isChecked());
					} else if (lookAdapter.getOnLookEditListener() != null) {
						lookAdapter.getOnLookEditListener().onLookEdit(view);
					}
				}
			});
		} else {
			holder.lookElement.setOnClickListener(null);
		}
	}

	private void handleDetails(LookData lookData, LookViewHolder holder, LookBaseAdapter lookAdapter) {
		if (lookAdapter.getShowDetails()) {
			if (lookData.getAbsolutePath() != null) {
				holder.lookFileSizeTextView.setText(UtilFile.getSizeAsString(new File(lookData.getAbsolutePath())));
			}
			int[] measure = lookData.getMeasure();
			String measureString = measure[0] + " x " + measure[1];

			holder.lookMeasureTextView.setText(measureString);
			holder.lookDetailsLinearLayout.setVisibility(TextView.VISIBLE);
		} else {
			holder.lookDetailsLinearLayout.setVisibility(TextView.GONE);
		}
	}

	private boolean handleCheckboxes(final int position, LookViewHolder holder, final LookBaseAdapter lookAdapter) {
		holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (lookAdapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE) {
						lookAdapter.clearCheckedItems();
					}
					lookAdapter.getCheckedItems().add(position);
				} else {
					lookAdapter.getCheckedItems().remove(position);
				}
				lookAdapter.notifyDataSetChanged();

				if (lookAdapter.getOnLookEditListener() != null) {
					lookAdapter.getOnLookEditListener().onLookChecked();
				}
			}
		});

		boolean checkboxIsVisible = false;

		if (lookAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.lookArrowView.setVisibility(View.GONE);
			holder.lookElement.setBackgroundResource(R.drawable.button_background_shadowed);
			checkboxIsVisible = true;
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			holder.lookArrowView.setVisibility(View.VISIBLE);
			holder.lookElement.setBackgroundResource(R.drawable.button_background_selector);
			lookAdapter.clearCheckedItems();
		}

		if (lookAdapter.getCheckedItems().contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		return checkboxIsVisible;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle arguments, FragmentActivity activity) {
		Uri imageUri = null;

		if (arguments != null) {
			imageUri = (Uri) arguments.get(LOADER_ARGUMENTS_IMAGE_URI);
		}
		String[] projection = { MediaStore.MediaColumns.DATA };
		return new CursorLoader(activity, imageUri, projection, null, null, null);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data, Activity activity, ArrayList<LookData> lookDataList,
			LookFragment fragment) {
		String originalImagePath = "";
		CursorLoader cursorLoader = (CursorLoader) loader;

		boolean catchedException = false;

		if (data == null) {
			originalImagePath = cursorLoader.getUri().getPath();
		} else {
			int columnIndex = data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
			data.moveToFirst();

			try {
				originalImagePath = data.getString(columnIndex);
			} catch (CursorIndexOutOfBoundsException e) {
				catchedException = true;
			}
		}

		if (catchedException || (data == null && originalImagePath.equals(""))) {
			Utils.showErrorDialog(activity, R.string.error_load_image);
			return;
		}
		copyImageToCatroid(originalImagePath, activity, lookDataList, fragment);
	}

	private void updateLookAdapter(String name, String fileName, ArrayList<LookData> lookDataList, LookFragment fragment) {
		name = Utils.getUniqueLookName(name);

		LookData lookData = new LookData();
		lookData.setLookFilename(fileName);
		lookData.setLookName(name);
		lookDataList.add(lookData);

		fragment.updateLookAdapter(lookData);
	}

	private void copyImageToCatroid(String originalImagePath, Activity activity, ArrayList<LookData> lookDataList,
			LookFragment fragment) {
		try {

			int[] imageDimensions = ImageEditing.getImageDimensions(originalImagePath);

			if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
				Utils.showErrorDialog(activity, R.string.error_load_image);
				return;
			}

			File oldFile = new File(originalImagePath);

			if (originalImagePath.equals("")) {
				throw new IOException();
			}

			String projectName = ProjectManager.getInstance().getCurrentProject().getName();
			File imageFile = StorageHandler.getInstance().copyImage(projectName, originalImagePath, null);

			String imageName;
			int extensionDotIndex = oldFile.getName().lastIndexOf('.');
			if (extensionDotIndex > 0) {
				imageName = oldFile.getName().substring(0, extensionDotIndex);
			} else {
				imageName = oldFile.getName();
			}

			String imageFileName = imageFile.getName();
			// if pixmap cannot be created, image would throw an Exception in stage
			// so has to be loaded again with other Config
			Pixmap pixmap = null;
			pixmap = Utils.getPixmapFromFile(imageFile);

			if (pixmap == null) {
				ImageEditing.overwriteImageFileWithNewBitmap(imageFile);
				pixmap = Utils.getPixmapFromFile(imageFile);

				if (pixmap == null) {
					Utils.showErrorDialog(activity, R.string.error_load_image);
					StorageHandler.getInstance().deleteFile(imageFile.getAbsolutePath());
					return;
				}
			}
			pixmap = null;
			updateLookAdapter(imageName, imageFileName, lookDataList, fragment);
		} catch (IOException e) {
			Utils.showErrorDialog(activity, R.string.error_load_image);
		} catch (NullPointerException e) {
			Log.e("NullPointerException", "probably originalImagePath null; message: " + e.getMessage());
			Utils.showErrorDialog(activity, R.string.error_load_image);
		}
		fragment.destroyLoader();
		activity.sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
	}

	public void loadImageIntoCatroid(Intent intent, Activity activity, ArrayList<LookData> lookDataList,
			LookFragment fragment) {
		String originalImagePath = "";

		//get path of image - will work for most applications
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			originalImagePath = bundle.getString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);
		}

		Uri imageUri = intent.getData();
		if (imageUri != null) {

			Cursor cursor = activity.getContentResolver().query(imageUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);

			if (cursor != null) {
				cursor.moveToFirst();
				originalImagePath = cursor.getString(0);
				cursor.close();
			}
		}

		if (originalImagePath == null || originalImagePath.equals("")) {
			Bundle arguments = new Bundle();
			arguments.putParcelable(LOADER_ARGUMENTS_IMAGE_URI, intent.getData());
			fragment.initOrRestartLoader(arguments);
		} else {
			copyImageToCatroid(originalImagePath, activity, lookDataList, fragment);
		}
	}

	public void loadPocketPaintImageIntoCatroid(Intent intent, Activity activity, LookData selectedLookData) {
		Bundle bundle = intent.getExtras();
		String pathOfPocketPaintImage = bundle.getString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);

		int[] imageDimensions = ImageEditing.getImageDimensions(pathOfPocketPaintImage);
		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			Utils.showErrorDialog(activity, R.string.error_load_image);
			return;
		}

		String actualChecksum = Utils.md5Checksum(new File(pathOfPocketPaintImage));

		// If look changed --> saving new image with new checksum and changing lookData
		if (!selectedLookData.getChecksum().equalsIgnoreCase(actualChecksum)) {
			String oldFileName = selectedLookData.getLookFileName();
			String newFileName = oldFileName.substring(oldFileName.indexOf('_') + 1);

			//HACK for https://github.com/Catrobat/Catroid/issues/81
			if (!newFileName.endsWith(".png")) {
				newFileName = newFileName + ".png";
			}

			String projectName = ProjectManager.getInstance().getCurrentProject().getName();

			try {
				File newLookFile = StorageHandler.getInstance().copyImage(projectName, pathOfPocketPaintImage,
						newFileName);

				selectedLookData.setLookFilename(newLookFile.getName());
				selectedLookData.resetThumbnailBitmap();
			} catch (IOException ioException) {
				Log.e(TAG, Log.getStackTraceString(ioException));
			}
		}
	}

	public void loadPictureFromCameraIntoCatroid(Uri lookFromCameraUri, Activity activity,
			ArrayList<LookData> lookData, LookFragment fragment) {
		if (lookFromCameraUri != null) {
			String originalImagePath = lookFromCameraUri.getPath();

			int[] imageDimensions = ImageEditing.getImageDimensions(originalImagePath);
			if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
				Utils.showErrorDialog(activity, R.string.error_load_image);
				return;
			}
			copyImageToCatroid(originalImagePath, activity, lookData, fragment);

			File pictureOnSdCard = new File(lookFromCameraUri.getPath());
			pictureOnSdCard.delete();
		}
	}

	public void loadPictureFromLibraryIntoCatroid(String filePath, Activity activity,
			ArrayList<LookData> lookData, LookFragment fragment) {
		File mediaImage = null;
		mediaImage = new File(filePath);
		copyImageToCatroid(mediaImage.toString(), activity, lookData, fragment);
		File pictureOnSdCard = new File(mediaImage.getPath());
		pictureOnSdCard.delete();
	}

	public boolean checkIfPocketPaintIsInstalled(Intent intent, final Activity activity) {
		// Confirm if Pocket Paint is installed else start dialog --------------------------

		List<ResolveInfo> packageList = activity.getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		if (packageList.size() <= 0) {
			AlertDialog.Builder builder = new CustomAlertDialogBuilder(activity);
			builder.setTitle(R.string.pocket_paint_not_installed_title);
			builder.setMessage(R.string.pocket_paint_not_installed).setCancelable(false)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							Intent downloadPocketPaintIntent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(Constants.POCKET_PAINT_DOWNLOAD_LINK));
							activity.startActivity(downloadPocketPaintIntent);
						}
					})
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

	public void copyLook(int position, ArrayList<LookData> lookDataList, final Activity activity, LookFragment fragment) {
		LookData lookData = lookDataList.get(position);

		try {
			String projectName = ProjectManager.getInstance().getCurrentProject().getName();

			StorageHandler.getInstance().copyImage(projectName, lookData.getAbsolutePath(), null);

			String imageName = lookData.getLookName() + "_" + activity.getString(R.string.copy_addition);
			String imageFileName = lookData.getLookFileName();

			updateLookAdapter(imageName, imageFileName, lookDataList, fragment);
		} catch (IOException ioException) {
			Utils.showErrorDialog(activity, R.string.error_load_image);
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
		activity.sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
	}

	public void switchToScriptFragment(LookFragment fragment, ScriptActivity scriptActivity) {
		scriptActivity.setCurrentFragment(ScriptActivity.FRAGMENT_SCRIPTS);

		FragmentTransaction fragmentTransaction = scriptActivity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.hide(fragment);
		fragmentTransaction.show(scriptActivity.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG));
		fragmentTransaction.commitAllowingStateLoss();

		scriptActivity.setIsLookFragmentFromSetLookBrickNewFalse();
		scriptActivity.setIsLookFragmentHandleAddButtonHandled(false);
	}
}
