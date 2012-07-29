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
import android.view.ViewGroup;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter.OnCostumeCheckedListener;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter.OnCostumeEditListener;
import at.tugraz.ist.catroid.ui.dialogs.DeleteCostumeDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class CostumeFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
		OnCostumeCheckedListener, OnCostumeEditListener {

	private static final String ARGS_SELECTED_COSTUME = "selected_costume";
	private static final String ARGS_IMAGE_URI = "image_uri";
	private static final int ID_LOADER_MEDIA_IMAGE = 1;

	private CostumeAdapter adapter;
	private ArrayList<CostumeData> costumeDataList;
	private CostumeData selectedCostumeData;

	private ActionMode actionMode;

	private CostumeDeletedReceiver costumeDeletedReceiver;
	private CostumeRenamedReceiver costumeRenamedReceiver;

	public static final int REQUEST_SELECT_IMAGE = 0;
	public static final int REQUEST_PAINTROID_EDIT_IMAGE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_costume, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			selectedCostumeData = (CostumeData) savedInstanceState.getSerializable(ARGS_SELECTED_COSTUME);
		}

		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		adapter = new CostumeAdapter(getActivity(), R.layout.activity_costume_costumelist_item, costumeDataList);
		adapter.setOnCostumeEditListener(this);
		adapter.setOnCostumeCheckedListener(this);
		setListAdapter(adapter);
		setHasOptionsMenu(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(ARGS_SELECTED_COSTUME, selectedCostumeData);
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
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		final MenuItem addItem = menu.findItem(R.id.menu_add);

		int addButtonIcon;
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(currentSprite) == 0) {
			addButtonIcon = R.drawable.ic_background;
		} else {
			addButtonIcon = R.drawable.ic_actionbar_shirt;
		}
		addItem.setIcon(addButtonIcon);

		addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

				Bundle bundleForPaintroid = new Bundle();
				bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, "");
				bundleForPaintroid.putString(Constants.EXTRA_PICTURE_NAME_PAINTROID,
						getString(R.string.default_costume_name));

				intent.setType("image/*");
				intent.putExtras(bundleForPaintroid);
				Intent chooser = Intent.createChooser(intent, getString(R.string.select_image));
				startActivityForResult(chooser, REQUEST_SELECT_IMAGE);

				return true;
			}
		});
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
		}
	}

	@Override
	public void onCostumeRename(int position) {
		handleRenameCostumeButton(position);
	}

	@Override
	public void onCostumeEditPaintroid(int position) {
		handleEditCostumeButton(position);
	}

	@Override
	public void onCostumeChecked(int position, boolean isChecked) {
		int checkedCostumesCount = adapter.getCheckedCostumesCount();
		if (checkedCostumesCount > 0) {
			if (actionMode == null) {
				actionMode = getSherlockActivity().startActionMode(new CostumeEditCallback());
			}
		} else if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}

		if (actionMode != null) {
			actionMode.getMenu().findItem(R.id.menu_costume_edit).setVisible(checkedCostumesCount < 2);
			actionMode.getMenu().findItem(R.id.menu_costume_paintroid).setVisible(checkedCostumesCount < 2);
		}

		if (actionMode != null) {
			//TODO move to strings.xml
			actionMode.setTitle(checkedCostumesCount + " selected");
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri imageUri = null;
		if (args != null) {
			imageUri = (Uri) args.get(ARGS_IMAGE_URI);
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
				Utils.displayErrorMessage(getActivity(), getString(R.string.error_load_image));
				return;
			}
		}

		if (data == null && originalImagePath.equals("")) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_load_image));
			return;
		}

		copyImageToCatroid(originalImagePath);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	public void setSelectedCostumeData(CostumeData costumeData) {
		selectedCostumeData = costumeData;
	}

	private void copyImageToCatroid(String originalImagePath) {
		int[] imageDimensions = ImageEditing.getImageDimensions(originalImagePath);

		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_load_image));
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
			updateCostumeAdapter(imageName, imageFileName);
		} catch (IOException e) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_load_image));
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
			Bundle args = new Bundle();
			args.putParcelable(ARGS_IMAGE_URI, intent.getData());
			if (getLoaderManager().getLoader(ID_LOADER_MEDIA_IMAGE) == null) {
				getLoaderManager().initLoader(ID_LOADER_MEDIA_IMAGE, args, this);
			} else {
				getLoaderManager().restartLoader(ID_LOADER_MEDIA_IMAGE, args, this);
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
			Utils.displayErrorMessage(getActivity(), this.getString(R.string.error_load_image));
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

	private void handleDeleteCostume(int[] positions) {
		selectedCostumeData = costumeDataList.get(positions[positions.length - 1]);

		DeleteCostumeDialog deleteCostumeDialog = DeleteCostumeDialog.newInstance(positions);
		deleteCostumeDialog.show(getFragmentManager(), "dialog_delete_costume");
	}

	private void handleRenameCostumeButton(int position) {
		selectedCostumeData = costumeDataList.get(position);

		RenameCostumeDialog renameCostumeDialog = RenameCostumeDialog.newInstance(selectedCostumeData.getCostumeName());
		renameCostumeDialog.show(getFragmentManager(), "dialog_rename_costume");
	}

	private void handleCopyCostumeButton(int position) {
		CostumeData costumeData = costumeDataList.get(position);
		try {
			String projectName = ProjectManager.getInstance().getCurrentProject().getName();
			StorageHandler.getInstance().copyImage(projectName, costumeData.getAbsolutePath(), null);
			String imageName = costumeData.getCostumeName() + "_" + getString(R.string.copy_costume_addition);
			String imageFileName = costumeData.getCostumeFileName();
			updateCostumeAdapter(imageName, imageFileName);
		} catch (IOException e) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_load_image));
			e.printStackTrace();
		}

		getActivity().sendBroadcast(new Intent(ScriptTabActivity.ACTION_BRICK_LIST_CHANGED));
	}

	private void handleEditCostumeButton(int position) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName("at.tugraz.ist.paintroid", "at.tugraz.ist.paintroid.MainActivity"));

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

		selectedCostumeData = costumeDataList.get(position);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, costumeDataList.get(position)
				.getAbsolutePath());
		bundleForPaintroid.putInt(Constants.EXTRA_X_VALUE_PAINTROID, 0);
		bundleForPaintroid.putInt(Constants.EXTRA_X_VALUE_PAINTROID, 0);
		intent.putExtras(bundleForPaintroid);
		intent.addCategory("android.intent.category.LAUNCHER");
		startActivityForResult(intent, REQUEST_PAINTROID_EDIT_IMAGE);
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
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		adapter = new CostumeAdapter(getActivity(), R.layout.activity_costume_costumelist_item, costumeDataList);
		adapter.setOnCostumeEditListener(this);
		adapter.setOnCostumeCheckedListener(this);
		setListAdapter(adapter);
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
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	private class CostumeEditCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu_costumes, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int itemId = item.getItemId();
			switch (itemId) {
				case R.id.menu_costume_copy: {
					for (Integer checkedCostume : adapter.getCheckedCostumes()) {
						handleCopyCostumeButton(checkedCostume);
					}

					actionMode.finish();
					actionMode = null;

					return true;
				}
				case R.id.menu_costume_delete: {
					int[] positions = new int[adapter.getCheckedCostumesCount()];
					int i = 0;
					for (Integer checkedCostume : adapter.getCheckedCostumes()) {
						positions[i] = checkedCostume;
						i++;
					}
					handleDeleteCostume(positions);

					actionMode.finish();
					actionMode = null;

					return true;
				}
				case R.id.menu_costume_edit: {
					handleRenameCostumeButton(adapter.getSingleCheckedCostume());
					actionMode.finish();
					actionMode = null;

					return true;
				}
				case R.id.menu_costume_paintroid: {
					handleEditCostumeButton(adapter.getSingleCheckedCostume());
					actionMode.finish();
					actionMode = null;

					return true;
				}
				default: {
					return false;
				}
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			adapter.uncheckAllCostumes();
		}
	}
}
