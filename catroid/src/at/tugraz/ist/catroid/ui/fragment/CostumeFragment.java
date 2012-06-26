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
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class CostumeFragment extends SherlockListFragment {
	
	private CostumeAdapter adapter;
	private ArrayList<CostumeData> costumeDataList;
	
	private CostumeDeletedReceiver costumeDeletedReceiver;

	public static final int REQUEST_SELECT_IMAGE = 0;
	public static final int REQUEST_PAINTROID_EDIT_IMAGE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_costume, null);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		adapter = new CostumeAdapter(getActivity(), R.layout.activity_costume_costumelist_item, costumeDataList);
		setListAdapter(adapter);
		setHasOptionsMenu(true);
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
		
		IntentFilter intentFilter = new IntentFilter(ScriptTabActivity.ACTION_COSTUME_DELETED);
		getActivity().registerReceiver(costumeDeletedReceiver, intentFilter);
		
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
						CostumeFragment.this.getString(R.string.default_costume_name));

				intent.setType("image/*");
				intent.putExtras(bundleForPaintroid);
				Intent chooser = Intent.createChooser(intent, getString(R.string.select_image));
				startActivityForResult(chooser, REQUEST_SELECT_IMAGE);

				return true;
			}
		});
	}
	
	public void updateCostumeAdapter(String name, String fileName) {
		name = Utils.getUniqueCostumeName(name);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(fileName);
		costumeData.setCostumeName(name);
		costumeDataList.add(costumeData);
		((CostumeAdapter) getListAdapter()).notifyDataSetChanged();

		//scroll down the list to the new item:
		final ListView listView = getListView();
		listView.post(new Runnable() {
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});
	}

	private void reloadAdapter() {
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		setListAdapter(new CostumeAdapter(getActivity(), R.layout.activity_costume_costumelist_item, costumeDataList));
		((CostumeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		//TODO refactor this mess! (please)
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

	private void loadImageIntoCatroid(Intent intent) {
		Activity parent = getActivity();
		
		String originalImagePath = "";
		//get path of image - will work for most applications
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			originalImagePath = bundle.getString(Constants.EXTRA_PICTURE_PATH_PAINTROID);
		}
		if (originalImagePath == null || originalImagePath.equals("")) {
			Uri imageUri = intent.getData();

			String[] projection = { MediaStore.MediaColumns.DATA };
			Cursor cursor = parent.managedQuery(imageUri, projection, null, null, null);
			if (cursor == null) {
				originalImagePath = imageUri.getPath();
			} else {
				int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
				cursor.moveToFirst();
				try {
					originalImagePath = cursor.getString(columnIndex);
				} catch (CursorIndexOutOfBoundsException e) {
					Utils.displayErrorMessage(parent, this.getString(R.string.error_load_image));
					return;
				}
			}

			if (cursor == null && originalImagePath.equals("")) {
				Utils.displayErrorMessage(parent, this.getString(R.string.error_load_image));
				return;
			}

		}
		//-----------------------------------------------------

		int[] imageDimensions = ImageEditing.getImageDimensions(originalImagePath);
		if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
			Utils.displayErrorMessage(parent, this.getString(R.string.error_load_image));
			return;
		}

		File oldFile = new File(originalImagePath);

		//copy image to catroid:
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
			Utils.displayErrorMessage(parent, this.getString(R.string.error_load_image));
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

		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getActivity();
		CostumeData selectedCostumeData = scriptTabActivity.selectedCostumeData;
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

	public void handleDeleteCostumeButton(View v) {
		int position = (Integer) v.getTag();
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getActivity();
		scriptTabActivity.selectedCostumeData = costumeDataList.get(position);
		scriptTabActivity.selectedPosition = position;
		scriptTabActivity.showDialog(ScriptTabActivity.DIALOG_DELETE_COSTUME);
	}

	public void handleRenameCostumeButton(View v) {
		int position = (Integer) v.getTag();
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getActivity();
		scriptTabActivity.selectedCostumeData = costumeDataList.get(position);
		scriptTabActivity.showDialog(ScriptTabActivity.DIALOG_RENAME_COSTUME);
	}

	public void handleCopyCostumeButton(View v) {
		int position = (Integer) v.getTag();
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
	}

	public void handleEditCostumeButton(View v) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName("at.tugraz.ist.paintroid", "at.tugraz.ist.paintroid.MainActivity"));

		// Confirm if paintroid is installed else start dialog --------------------------
		List<ResolveInfo> packageList = getActivity().getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		if (packageList.size() <= 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.paintroid_not_installed)).setCancelable(false)
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent downloadPaintroidIntent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(Constants.PAINTROID_DOWNLOAD_LINK));
							startActivity(downloadPaintroidIntent);
						}
					}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getActivity();
		scriptTabActivity.selectedCostumeData = costumeDataList.get(position);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, costumeDataList.get(position)
				.getAbsolutePath());
		bundleForPaintroid.putInt(Constants.EXTRA_X_VALUE_PAINTROID, 0);
		bundleForPaintroid.putInt(Constants.EXTRA_X_VALUE_PAINTROID, 0);
		intent.putExtras(bundleForPaintroid);
		intent.addCategory("android.intent.category.LAUNCHER");
		startActivityForResult(intent, REQUEST_PAINTROID_EDIT_IMAGE);
	}
	
	private class CostumeDeletedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptTabActivity.ACTION_COSTUME_DELETED)) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
