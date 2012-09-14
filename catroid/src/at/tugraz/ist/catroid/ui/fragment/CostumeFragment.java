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
package at.tugraz.ist.catroid.ui.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter.OnCostumeEditListener;
import at.tugraz.ist.catroid.ui.dialogs.DeleteCostumeDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.UtilCamera;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.badlogic.gdx.graphics.Pixmap;

public class CostumeFragment extends SherlockListFragment implements OnCostumeEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, OnClickListener {

	private static final String BUNDLE_ARGUMENTS_SELECTED_COSTUME = "selected_costume";
	private static final String BUNDLE_ARGUMENTS_URI_IS_SET = "uri_is_set";
	private static final String LOADER_ARGUMENTS_IMAGE_URI = "image_uri";
	private static final int FOOTER_ADD_COSTUME_ALPHA_VALUE = 35;
	private static final int ID_LOADER_MEDIA_IMAGE = 1;

	private CostumeAdapter adapter;
	private ArrayList<CostumeData> costumeDataList;
	private CostumeData selectedCostumeData;

	private View viewBelowCostumelistNonScrollable;
	private View viewCameraNonScrollable;
	private View viewGalleryNonScrollable;
	private View costumelistFooterCamera;
	private View costumelistFooterGallery;

	private Uri costumeFromCameraUri = null;

	private CostumeDeletedReceiver costumeDeletedReceiver;
	private CostumeRenamedReceiver costumeRenamedReceiver;

	public static final int REQUEST_SELECT_IMAGE = 0;
	public static final int REQUEST_PAINTROID_EDIT_IMAGE = 1;
	public static final int REQUEST_TAKE_PICTURE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_costume, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			selectedCostumeData = (CostumeData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_SELECTED_COSTUME);

			boolean uriIsSet = savedInstanceState.getBoolean(BUNDLE_ARGUMENTS_URI_IS_SET);
			if (uriIsSet) {
				String defCostumeName = getString(R.string.default_costume_name);
				costumeFromCameraUri = UtilCamera.getDefaultCostumeFromCameraUri(defCostumeName);
			}
		}

		viewBelowCostumelistNonScrollable = getActivity().findViewById(R.id.view_below_costumelist_non_scrollable);
		viewCameraNonScrollable = viewBelowCostumelistNonScrollable.findViewById(R.id.view_camera_non_scrollable);
		viewGalleryNonScrollable = viewBelowCostumelistNonScrollable.findViewById(R.id.view_gallery_non_scrollable);
		viewCameraNonScrollable.setOnClickListener(this);
		viewGalleryNonScrollable.setOnClickListener(this);

		View footerView = getActivity().getLayoutInflater().inflate(R.layout.fragment_costume_costumelist_footer,
				getListView(), false);
		costumelistFooterCamera = footerView.findViewById(R.id.costumelist_footerview_camera);
		ImageView footerAddImageCamera = (ImageView) footerView
				.findViewById(R.id.costumelist_footerview_camera_add_image);
		footerAddImageCamera.setAlpha(FOOTER_ADD_COSTUME_ALPHA_VALUE);
		costumelistFooterCamera.setOnClickListener(this);
		costumelistFooterGallery = footerView.findViewById(R.id.costumelist_footerview_gallery);
		ImageView footerAddImageGallery = (ImageView) footerView
				.findViewById(R.id.costumelist_footerview_gallery_add_image);
		footerAddImageGallery.setAlpha(FOOTER_ADD_COSTUME_ALPHA_VALUE);
		costumelistFooterGallery.setOnClickListener(this);
		getListView().addFooterView(footerView);

		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		adapter = new CostumeAdapter(getActivity(), R.layout.fragment_costume_costumelist_item, costumeDataList);
		adapter.setOnCostumeEditListener(this);
		setListAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(BUNDLE_ARGUMENTS_URI_IS_SET, (costumeFromCameraUri != null));
		outState.putSerializable(BUNDLE_ARGUMENTS_SELECTED_COSTUME, selectedCostumeData);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(getActivity())) {
			return;
		}

		if (costumeDeletedReceiver == null) {
			costumeDeletedReceiver = new CostumeDeletedReceiver();
		}

		if (costumeRenamedReceiver == null) {
			costumeRenamedReceiver = new CostumeRenamedReceiver();
		}

		IntentFilter intentFilterDeleteCostume = new IntentFilter(ScriptTabActivity.ACTION_COSTUME_DELETED);
		getActivity().registerReceiver(costumeDeletedReceiver, intentFilterDeleteCostume);

		IntentFilter intentFilterRenameCostume = new IntentFilter(ScriptTabActivity.ACTION_COSTUME_RENAMED);
		getActivity().registerReceiver(costumeRenamedReceiver, intentFilterRenameCostume);

		reloadAdapter();
		addCostumeViewsSetClickableFlag(true);
	}

	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}

		if (costumeDeletedReceiver != null) {
			getActivity().unregisterReceiver(costumeDeletedReceiver);
		}

		if (costumeRenamedReceiver != null) {
			getActivity().unregisterReceiver(costumeRenamedReceiver);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_scripttab_costumes, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.menu_scripttab_costumes, menu);

		int addButtonIcon;
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(currentSprite) == 0) {
			addButtonIcon = R.drawable.ic_background;
		} else {
			addButtonIcon = R.drawable.ic_actionbar_shirt;
		}
		menu.findItem(R.id.menu_add).setIcon(addButtonIcon);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			case R.id.menu_add_costume_from_camera: {
				selectImageFromCamera();
				return true;
			}
			case R.id.menu_add_costume_from_gallery: {
				selectImageFromGallery();
				return true;
			}
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
	}

	public void setSelectedCostumeData(CostumeData costumeData) {
		selectedCostumeData = costumeData;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
			case REQUEST_SELECT_IMAGE:
				loadImageIntoCatroid(data);
				break;
			case REQUEST_PAINTROID_EDIT_IMAGE:
				loadPaintroidImageIntoCatroid(data);
				break;
			case REQUEST_TAKE_PICTURE:
				String defCostumeName = getString(R.string.default_costume_name);
				costumeFromCameraUri = UtilCamera.rotatePictureIfNecessary(costumeFromCameraUri, defCostumeName);
				loadPictureFromCameraIntoCatroid();
				break;
		}
	}

	@Override
	public void onCostumeEdit(View v) {
		handleEditCostumeButton(v);
	}

	@Override
	public void onCostumeRename(View v) {
		handleRenameCostumeButton(v);
	}

	@Override
	public void onCostumeDelete(View v) {
		handleDeleteCostumeButton(v);
	}

	@Override
	public void onCostumeCopy(View v) {
		handleCopyCostumeButton(v);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.view_camera_non_scrollable:
				addCostumeViewsSetClickableFlag(false);
				selectImageFromCamera();
				break;
			case R.id.view_gallery_non_scrollable:
				addCostumeViewsSetClickableFlag(false);
				selectImageFromGallery();
				break;
			case R.id.costumelist_footerview_camera:
				addCostumeViewsSetClickableFlag(false);
				selectImageFromCamera();
				break;
			case R.id.costumelist_footerview_gallery:
				addCostumeViewsSetClickableFlag(false);
				selectImageFromGallery();
				break;
		}
	}

	private void addCostumeViewsSetClickableFlag(boolean setClickableFlag) {
		viewCameraNonScrollable.setClickable(setClickableFlag);
		viewGalleryNonScrollable.setClickable(setClickableFlag);
		costumelistFooterCamera.setClickable(setClickableFlag);
		costumelistFooterGallery.setClickable(setClickableFlag);

	}

	private void updateCostumeAdapter(String name, String fileName) {
		name = Utils.getUniqueCostumeName(name);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(fileName);
		costumeData.setCostumeName(name);
		costumeDataList.add(costumeData);
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
			costumeDataList = currentSprite.getCostumeDataList();
			CostumeAdapter adapter = new CostumeAdapter(getActivity(), R.layout.fragment_costume_costumelist_item,
					costumeDataList);
			adapter.setOnCostumeEditListener(this);
			setListAdapter(adapter);
		}
	}

	private void selectImageFromCamera() {
		costumeFromCameraUri = UtilCamera.getDefaultCostumeFromCameraUri(getString(R.string.default_costume_name));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, costumeFromCameraUri);
		Intent chooser = Intent.createChooser(intent, getString(R.string.select_costume_from_camera));
		startActivityForResult(chooser, REQUEST_TAKE_PICTURE);
	}

	private void selectImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, "");
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_NAME_PAINTROID, getString(R.string.default_costume_name));

		intent.setType("image/*");
		intent.putExtras(bundleForPaintroid);
		Intent chooser = Intent.createChooser(intent, getString(R.string.select_costume_from_gallery));
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
			updateCostumeAdapter(imageName, imageFileName);
		} catch (IOException e) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
		}

		getLoaderManager().destroyLoader(ID_LOADER_MEDIA_IMAGE);
		getActivity().sendBroadcast(new Intent(ScriptTabActivity.ACTION_BRICK_LIST_CHANGED));
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

		// If costume changed --> saving new image with new checksum and changing costumeData
		if (!selectedCostumeData.getChecksum().equalsIgnoreCase(actualChecksum)) {
			String oldFileName = selectedCostumeData.getCostumeFileName();
			String newFileName = oldFileName.substring(oldFileName.indexOf('_') + 1);
			String projectName = ProjectManager.getInstance().getCurrentProject().getName();
			try {
				File newCostumeFile = StorageHandler.getInstance().copyImage(projectName, pathOfPaintroidImage,
						newFileName);
				File tempPicFileInPaintroid = new File(pathOfPaintroidImage);
				tempPicFileInPaintroid.delete(); //delete temp file in paintroid
				StorageHandler.getInstance().deleteFile(selectedCostumeData.getAbsolutePath()); //reduce usage in container or delete it
				selectedCostumeData.setCostumeFilename(newCostumeFile.getName());
				selectedCostumeData.resetThumbnailBitmap();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadPictureFromCameraIntoCatroid() {
		if (costumeFromCameraUri != null) {
			String originalImagePath = costumeFromCameraUri.getPath();
			int[] imageDimensions = ImageEditing.getImageDimensions(originalImagePath);
			if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
				Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
				return;
			}
			copyImageToCatroid(originalImagePath);

			File pictureOnSdCard = new File(costumeFromCameraUri.getPath());
			pictureOnSdCard.delete();
		}
	}

	private void handleDeleteCostumeButton(View v) {
		int position = (Integer) v.getTag();
		selectedCostumeData = costumeDataList.get(position);

		DeleteCostumeDialog deleteCostumeDialog = DeleteCostumeDialog.newInstance(position);
		deleteCostumeDialog.show(getFragmentManager(), DeleteCostumeDialog.DIALOG_FRAGMENT_TAG);
	}

	private void handleRenameCostumeButton(View v) {
		int position = (Integer) v.getTag();
		selectedCostumeData = costumeDataList.get(position);

		RenameCostumeDialog renameCostumeDialog = RenameCostumeDialog.newInstance(selectedCostumeData.getCostumeName());
		renameCostumeDialog.show(getFragmentManager(), RenameCostumeDialog.DIALOG_FRAGMENT_TAG);
	}

	private void handleCopyCostumeButton(View v) {
		int position = (Integer) v.getTag();
		CostumeData costumeData = costumeDataList.get(position);
		try {
			String projectName = ProjectManager.getInstance().getCurrentProject().getName();
			StorageHandler.getInstance().copyImage(projectName, costumeData.getAbsolutePath(), null);
			String imageName = costumeData.getCostumeName() + "_" + getString(R.string.copy_costume_addition);
			String imageFileName = costumeData.getCostumeFileName();
			updateCostumeAdapter(imageName, imageFileName);
		} catch (IOException e) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
			e.printStackTrace();
		}

		getActivity().sendBroadcast(new Intent(ScriptTabActivity.ACTION_BRICK_LIST_CHANGED));
	}

	private void handleEditCostumeButton(View v) {
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
		selectedCostumeData = costumeDataList.get(position);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, costumeDataList.get(position)
				.getAbsolutePath());
		bundleForPaintroid.putInt(Constants.EXTRA_X_VALUE_PAINTROID, 0);
		bundleForPaintroid.putInt(Constants.EXTRA_Y_VALUE_PAINTROID, 0);
		intent.putExtras(bundleForPaintroid);
		intent.addCategory("android.intent.category.LAUNCHER");
		startActivityForResult(intent, REQUEST_PAINTROID_EDIT_IMAGE);
	}

	private class CostumeDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptTabActivity.ACTION_COSTUME_DELETED)) {
				reloadAdapter();
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptTabActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}

	private class CostumeRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptTabActivity.ACTION_COSTUME_RENAMED)) {
				String newCostumeName = intent.getExtras().getString(RenameCostumeDialog.EXTRA_NEW_COSTUME_NAME);

				if (newCostumeName != null && !newCostumeName.equalsIgnoreCase("")) {
					selectedCostumeData.setCostumeName(newCostumeName);
					reloadAdapter();
				}
			}
		}
	}
}
