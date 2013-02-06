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
package org.catrobat.catroid.ui.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.LookAdapter;
import org.catrobat.catroid.ui.adapter.LookAdapter.OnLookEditListener;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.ui.dialogs.NewLookDialog;
import org.catrobat.catroid.ui.dialogs.RenameLookDialog;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilCamera;
import org.catrobat.catroid.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.badlogic.gdx.graphics.Pixmap;

public class LookFragment extends ScriptActivityFragment implements OnLookEditListener,
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String BUNDLE_ARGUMENTS_SELECTED_LOOK = "selected_look";
	private static final String BUNDLE_ARGUMENTS_URI_IS_SET = "uri_is_set";
	private static final String LOADER_ARGUMENTS_IMAGE_URI = "image_uri";
	private static final String SHARED_PREFERENCE_NAME = "showDetailsLooks";
	private static final int ID_LOADER_MEDIA_IMAGE = 1;

	private LookAdapter adapter;
	private ArrayList<LookData> lookDataList;
	private LookData selectedLookData;

	private Uri lookFromCameraUri = null;

	private LookDeletedReceiver lookDeletedReceiver;
	private LookRenamedReceiver lookRenamedReceiver;

	public static final int REQUEST_SELECT_IMAGE = 0;
	public static final int REQUEST_PAINTROID_EDIT_IMAGE = 1;
	public static final int REQUEST_TAKE_PICTURE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_look, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			selectedLookData = (LookData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_SELECTED_LOOK);

			boolean uriIsSet = savedInstanceState.getBoolean(BUNDLE_ARGUMENTS_URI_IS_SET);
			if (uriIsSet) {
				String defLookName = getString(R.string.default_look_name);
				lookFromCameraUri = UtilCamera.getDefaultLookFromCameraUri(defLookName);
			}
		}

		lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
		adapter = new LookAdapter(getActivity(), R.layout.fragment_look_looklist_item, lookDataList, false);
		adapter.setOnLookEditListener(this);
		setListAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(BUNDLE_ARGUMENTS_URI_IS_SET, (lookFromCameraUri != null));
		outState.putSerializable(BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedLookData);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (lookDeletedReceiver == null) {
			lookDeletedReceiver = new LookDeletedReceiver();
		}

		if (lookRenamedReceiver == null) {
			lookRenamedReceiver = new LookRenamedReceiver();
		}

		IntentFilter intentFilterDeleteLook = new IntentFilter(ScriptActivity.ACTION_LOOK_DELETED);
		getActivity().registerReceiver(lookDeletedReceiver, intentFilterDeleteLook);

		IntentFilter intentFilterRenameLook = new IntentFilter(ScriptActivity.ACTION_LOOK_RENAMED);
		getActivity().registerReceiver(lookRenamedReceiver, intentFilterRenameLook);

		reloadAdapter();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}

		if (lookDeletedReceiver != null) {
			getActivity().unregisterReceiver(lookDeletedReceiver);
		}

		if (lookRenamedReceiver != null) {
			getActivity().unregisterReceiver(lookRenamedReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	public void setSelectedLookData(LookData lookData) {
		selectedLookData = lookData;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
			case REQUEST_SELECT_IMAGE:
				if (data == null) {
					break;
				}
				loadImageIntoCatroid(data);
				break;
			case REQUEST_PAINTROID_EDIT_IMAGE:
				loadPaintroidImageIntoCatroid(data);
				break;
			case REQUEST_TAKE_PICTURE:
				String defLookName = getString(R.string.default_look_name);
				lookFromCameraUri = UtilCamera.rotatePictureIfNecessary(lookFromCameraUri, defLookName);
				loadPictureFromCameraIntoCatroid();
				break;
		}
	}

	@Override
	public void onLookEdit(View v) {
		handleEditLookButton(v);
	}

	@Override
	public void onLookRename(View v) {
		handleRenameLookButton(v);
	}

	@Override
	public void onLookDelete(View v) {
		handleDeleteLookButton(v);
	}

	@Override
	public void onLookCopy(View v) {
		handleCopyLookButton(v);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		Uri imageUri = null;
		if (arguments != null) {
			imageUri = (Uri) arguments.get(LOADER_ARGUMENTS_IMAGE_URI);
		}

		String[] projection = { MediaStore.MediaColumns.DATA };
		return new CursorLoader(getActivity(), imageUri, projection, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		String originalImagePath = "";
		CursorLoader cursorLoader = (CursorLoader) loader;

		if (data == null) {
			originalImagePath = cursorLoader.getUri().getPath();
		} else {
			int columnIndex = data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
			data.moveToFirst();
			try {
				originalImagePath = data.getString(columnIndex);
			} catch (CursorIndexOutOfBoundsException e) {
				Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
				return;
			}
		}

		if (data == null && originalImagePath.equals("")) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
			return;
		}

		copyImageToCatroid(originalImagePath);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private void updateLookAdapter(String name, String fileName) {
		name = Utils.getUniqueLookName(name);
		LookData lookData = new LookData();
		lookData.setLookFilename(fileName);
		lookData.setLookName(name);
		lookDataList.add(lookData);
		reloadAdapter();

		//scroll down the list to the new item:
		final ListView listView = getListView();
		listView.post(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});
	}

	private void reloadAdapter() {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (currentSprite != null) {
			lookDataList = currentSprite.getLookDataList();
			LookAdapter adapter = new LookAdapter(getActivity(), R.layout.fragment_look_looklist_item,
					lookDataList, false);
			adapter.setOnLookEditListener(this);
			setListAdapter(adapter);
		}
	}

	public void selectImageFromCamera() {
		lookFromCameraUri = UtilCamera.getDefaultLookFromCameraUri(getString(R.string.default_look_name));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, lookFromCameraUri);
		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
		startActivityForResult(chooser, REQUEST_TAKE_PICTURE);
	}

	public void selectImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, "");
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_NAME_PAINTROID, getString(R.string.default_look_name));

		intent.setType("image/*");
		intent.putExtras(bundleForPaintroid);
		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_gallery));
		startActivityForResult(chooser, REQUEST_SELECT_IMAGE);
	}

	private void copyImageToCatroid(String originalImagePath) {
		int[] imageDimensions = ImageEditing.getImageDimensions(originalImagePath);

		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
			return;
		}

		File oldFile = new File(originalImagePath);

		try {
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
					Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
							getString(R.string.error_load_image));
					StorageHandler.getInstance().deleteFile(imageFile.getAbsolutePath());
					return;
				}
			}
			pixmap = null;
			updateLookAdapter(imageName, imageFileName);
		} catch (IOException e) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
		}

		getLoaderManager().destroyLoader(ID_LOADER_MEDIA_IMAGE);
		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
	}

	private void loadImageIntoCatroid(Intent intent) {
		String originalImagePath = "";

		//get path of image - will work for most applications
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			originalImagePath = bundle.getString(Constants.EXTRA_PICTURE_PATH_PAINTROID);
		}
		if (originalImagePath == null || originalImagePath.equals("")) {
			Bundle arguments = new Bundle();
			arguments.putParcelable(LOADER_ARGUMENTS_IMAGE_URI, intent.getData());
			if (getLoaderManager().getLoader(ID_LOADER_MEDIA_IMAGE) == null) {
				getLoaderManager().initLoader(ID_LOADER_MEDIA_IMAGE, arguments, this);
			} else {
				getLoaderManager().restartLoader(ID_LOADER_MEDIA_IMAGE, arguments, this);
			}
		} else {
			copyImageToCatroid(originalImagePath);
		}
	}

	private void loadPaintroidImageIntoCatroid(Intent intent) {
		Bundle bundle = intent.getExtras();
		String pathOfPaintroidImage = bundle.getString(Constants.EXTRA_PICTURE_PATH_PAINTROID);

		int[] imageDimensions = ImageEditing.getImageDimensions(pathOfPaintroidImage);
		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			Utils.displayErrorMessageFragment(getFragmentManager(), this.getString(R.string.error_load_image));
			return;
		}

		String actualChecksum = Utils.md5Checksum(new File(pathOfPaintroidImage));

		// If look changed --> saving new image with new checksum and changing lookData
		if (!selectedLookData.getChecksum().equalsIgnoreCase(actualChecksum)) {
			String oldFileName = selectedLookData.getLookFileName();
			String newFileName = oldFileName.substring(oldFileName.indexOf('_') + 1);
			String projectName = ProjectManager.getInstance().getCurrentProject().getName();
			try {
				File newLookFile = StorageHandler.getInstance().copyImage(projectName, pathOfPaintroidImage,
						newFileName);
				File tempPicFileInPaintroid = new File(pathOfPaintroidImage);
				tempPicFileInPaintroid.delete(); //delete temp file in paintroid
				StorageHandler.getInstance().deleteFile(selectedLookData.getAbsolutePath()); //reduce usage in container or delete it
				selectedLookData.setLookFilename(newLookFile.getName());
				selectedLookData.resetThumbnailBitmap();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadPictureFromCameraIntoCatroid() {
		if (lookFromCameraUri != null) {
			String originalImagePath = lookFromCameraUri.getPath();
			int[] imageDimensions = ImageEditing.getImageDimensions(originalImagePath);
			if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
				Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
				return;
			}
			copyImageToCatroid(originalImagePath);

			File pictureOnSdCard = new File(lookFromCameraUri.getPath());
			pictureOnSdCard.delete();
		}
	}

	private void handleDeleteLookButton(View v) {
		int position = (Integer) v.getTag();
		selectedLookData = lookDataList.get(position);

		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(position);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}

	private void handleRenameLookButton(View v) {
		int position = (Integer) v.getTag();
		selectedLookData = lookDataList.get(position);

		RenameLookDialog renameLookDialog = RenameLookDialog.newInstance(selectedLookData.getLookName());
		renameLookDialog.show(getFragmentManager(), RenameLookDialog.DIALOG_FRAGMENT_TAG);
	}

	private void handleCopyLookButton(View v) {
		int position = (Integer) v.getTag();
		LookData lookData = lookDataList.get(position);
		try {
			String projectName = ProjectManager.getInstance().getCurrentProject().getName();
			StorageHandler.getInstance().copyImage(projectName, lookData.getAbsolutePath(), null);
			String imageName = lookData.getLookName() + "_" + getString(R.string.copy_look_addition);
			String imageFileName = lookData.getLookFileName();
			updateLookAdapter(imageName, imageFileName);
		} catch (IOException e) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
			e.printStackTrace();
		}

		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
	}

	private void handleEditLookButton(View v) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName("org.catrobat.paintroid", "org.catrobat.paintroid.MainActivity"));

		// Confirm if paintroid is installed else start dialog --------------------------
		List<ResolveInfo> packageList = getActivity().getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		if (packageList.size() <= 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.paintroid_not_installed)).setCancelable(false)
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							Intent downloadPaintroidIntent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(Constants.PAINTROID_DOWNLOAD_LINK));
							startActivity(downloadPaintroidIntent);
						}
					}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
		//-------------------------------------------------------------------------------

		int position = (Integer) v.getTag();
		selectedLookData = lookDataList.get(position);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, lookDataList.get(position)
				.getAbsolutePath());
		bundleForPaintroid.putInt(Constants.EXTRA_X_VALUE_PAINTROID, 0);
		bundleForPaintroid.putInt(Constants.EXTRA_Y_VALUE_PAINTROID, 0);
		intent.putExtras(bundleForPaintroid);
		intent.addCategory("android.intent.category.LAUNCHER");
		startActivityForResult(intent, REQUEST_PAINTROID_EDIT_IMAGE);
	}

	private class LookDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOK_DELETED)) {
				reloadAdapter();
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}

	private class LookRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOK_RENAMED)) {
				String newLookName = intent.getExtras().getString(RenameLookDialog.EXTRA_NEW_LOOK_NAME);

				if (newLookName != null && !newLookName.equalsIgnoreCase("")) {
					selectedLookData.setLookName(newLookName);
					reloadAdapter();
				}
			}
		}
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		// TODO CHANGE THIS!!! (was just a quick fix)
		if (adapter != null) {
			adapter.setShowDetails(showDetails);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean getShowDetails() {
		// TODO CHANGE THIS!!! (was just a quick fix)
		if (adapter != null) {
			return adapter.getShowDetails();
		} else {
			return false;
		}
	}

	@Override
	public void startRenameActionMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDeleteActionMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAddButton() {
		NewLookDialog dialog = new NewLookDialog();
		dialog.showDialog(getActivity().getSupportFragmentManager(), this);
	}

	@Override
	public boolean getActionModeActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelectMode(int selectMode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getSelectMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void showRenameDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showDeleteDialog() {
		// TODO Auto-generated method stub

	}
}
