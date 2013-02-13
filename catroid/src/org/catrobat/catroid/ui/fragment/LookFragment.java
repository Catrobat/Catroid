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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.badlogic.gdx.graphics.Pixmap;

public class LookFragment extends ScriptActivityFragment implements OnLookEditListener,
		LoaderManager.LoaderCallbacks<Cursor> {

	public static final int REQUEST_SELECT_IMAGE = 0;
	public static final int REQUEST_PAINTROID_EDIT_IMAGE = 1;
	public static final int REQUEST_TAKE_PICTURE = 2;

	private static final int ID_LOADER_MEDIA_IMAGE = 1;

	private static int selectedLookPosition = Constants.NO_POSITION;

	private static final String BUNDLE_ARGUMENTS_SELECTED_LOOK = "selected_look";
	private static final String BUNDLE_ARGUMENTS_URI_IS_SET = "uri_is_set";
	private static final String LOADER_ARGUMENTS_IMAGE_URI = "image_uri";
	private static final String SHARED_PREFERENCE_NAME = "showDetailsLooks";

	private static String actionModeTitle;
	private static String singleItemAppendixActionMode;
	private static String multipleItemAppendixActionMode;

	private LookAdapter adapter;
	private ArrayList<LookData> lookDataList;
	private LookData selectedLookData;

	private Uri lookFromCameraUri = null;

	private ListView listView;

	private LookDeletedReceiver lookDeletedReceiver;
	private LookRenamedReceiver lookRenamedReceiver;

	private ActionMode actionMode;

	private boolean isRenameActionMode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_look, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();
		registerForContextMenu(listView);

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

		ScriptActivity scriptActivity = (ScriptActivity) getActivity();
		try {
			Utils.loadProjectIfNeeded(scriptActivity, scriptActivity);
		} catch (ClassCastException exception) {
			Log.e("CATROID", scriptActivity.toString() + " does not implement ErrorListenerInterface", exception);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(BUNDLE_ARGUMENTS_URI_IS_SET, (lookFromCameraUri != null));
		outState.putSerializable(BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedLookData);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		initClickListener();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (lookRenamedReceiver == null) {
			lookRenamedReceiver = new LookRenamedReceiver();
		}

		if (lookDeletedReceiver == null) {
			lookDeletedReceiver = new LookDeletedReceiver();
		}

		IntentFilter intentFilterRenameLook = new IntentFilter(ScriptActivity.ACTION_LOOK_RENAMED);
		getActivity().registerReceiver(lookRenamedReceiver, intentFilterRenameLook);

		IntentFilter intentFilterDeleteLook = new IntentFilter(ScriptActivity.ACTION_LOOK_DELETED);
		getActivity().registerReceiver(lookDeletedReceiver, intentFilterDeleteLook);

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

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQUEST_SELECT_IMAGE:
					if (data != null) {
						loadImageIntoCatroid(data);
					}
					break;
				case REQUEST_PAINTROID_EDIT_IMAGE:
					if (data != null) {
						loadPaintroidImageIntoCatroid(data);
					}
					break;
				case REQUEST_TAKE_PICTURE:
					String defLookName = getString(R.string.default_look_name);
					lookFromCameraUri = UtilCamera.rotatePictureIfNecessary(lookFromCameraUri, defLookName);
					loadPictureFromCameraIntoCatroid();
					break;
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		selectedLookData = adapter.getItem(selectedLookPosition);
		menu.setHeaderTitle(selectedLookData.getLookName());

		getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_default, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_copy:
				copyLook(selectedLookPosition);
				break;

			case R.id.context_menu_cut:
				break;

			case R.id.context_menu_insert_below:
				break;

			case R.id.context_menu_move:
				break;

			case R.id.context_menu_rename:
				showRenameDialog();
				break;

			case R.id.context_menu_delete:
				showDeleteDialog();
				break;
		}
		return super.onContextItemSelected(item);
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

		boolean catchedExpetion = false;

		if (data == null) {
			originalImagePath = cursorLoader.getUri().getPath();
		} else {
			int columnIndex = data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
			data.moveToFirst();

			try {
				originalImagePath = data.getString(columnIndex);
			} catch (CursorIndexOutOfBoundsException e) {
				catchedExpetion = true;
			}
		}

		if (catchedExpetion || (data == null && originalImagePath.equals(""))) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_load_image));
			return;
		}
		copyImageToCatroid(originalImagePath);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
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
	public void startCopyActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(copyModeCallBack);
			unregisterForContextMenu(listView);
			Utils.setBottomBarActivated(getActivity(), false);
			isRenameActionMode = false;
		}
	}

	@Override
	public void startRenameActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(renameModeCallBack);
			unregisterForContextMenu(listView);
			Utils.setBottomBarActivated(getActivity(), false);
			isRenameActionMode = true;
		}
	}

	@Override
	public void startDeleteActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);
			unregisterForContextMenu(listView);
			Utils.setBottomBarActivated(getActivity(), false);
			isRenameActionMode = false;
		}
	}

	@Override
	public void handleAddButton() {
		NewLookDialog dialog = new NewLookDialog();
		dialog.showDialog(getActivity().getSupportFragmentManager(), this);
	}

	@Override
	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	@Override
	public void onLookEdit(View view) {
		handleEditLook(view);
	}

	@Override
	public void onLookChecked() {
		if (isRenameActionMode || actionMode == null) {
			return;
		}

		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(actionModeTitle);
		} else {
			String appendix = multipleItemAppendixActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = actionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = actionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	private void updateLookAdapter(String name, String fileName) {
		name = Utils.getUniqueLookName(name);

		LookData lookData = new LookData();
		lookData.setLookFilename(fileName);
		lookData.setLookName(name);
		lookDataList.add(lookData);

		adapter.notifyDataSetChanged();

		//scroll down the list to the new item:
		final ListView listView = getListView();
		listView.post(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});
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

			LoaderManager loaderManager = getLoaderManager();

			if (loaderManager.getLoader(ID_LOADER_MEDIA_IMAGE) == null) {
				loaderManager.initLoader(ID_LOADER_MEDIA_IMAGE, arguments, this);
			} else {
				loaderManager.restartLoader(ID_LOADER_MEDIA_IMAGE, arguments, this);
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

	private void initClickListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedLookPosition = position;
				return false;
			}
		});
	}

	private void handleEditLook(View view) {
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
		int position = (Integer) view.getTag();
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
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	private ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(Constants.MULTI_SELECT);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.copy);
			singleItemAppendixActionMode = getString(R.string.look);
			multipleItemAppendixActionMode = getString(R.string.looks);

			mode.setTitle(actionModeTitle);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Set<Integer> checkedLooks = adapter.getCheckedItems();
			Iterator<Integer> iterator = checkedLooks.iterator();

			while (iterator.hasNext()) {
				int position = iterator.next();
				copyLook(position);
			}
			setSelectMode(Constants.SELECT_NONE);
			adapter.clearCheckedItems();

			actionMode = null;
			setActionModeActive(false);

			registerForContextMenu(listView);
			Utils.setBottomBarActivated(getActivity(), true);
		}
	};

	private ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(Constants.SINGLE_SELECT);
			mode.setTitle(getString(R.string.rename));

			setActionModeActive(true);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Set<Integer> checkedLooks = adapter.getCheckedItems();
			Iterator<Integer> iterator = checkedLooks.iterator();

			if (iterator.hasNext()) {
				int position = iterator.next();
				selectedLookData = (LookData) listView.getItemAtPosition(position);
				showRenameDialog();
			}
			setSelectMode(Constants.SELECT_NONE);
			adapter.clearCheckedItems();

			actionMode = null;
			setActionModeActive(false);

			registerForContextMenu(listView);
			Utils.setBottomBarActivated(getActivity(), true);
		}
	};

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(Constants.MULTI_SELECT);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.delete);
			singleItemAppendixActionMode = getString(R.string.look);
			multipleItemAppendixActionMode = getString(R.string.looks);

			mode.setTitle(actionModeTitle);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			SortedSet<Integer> checkedLooks = adapter.getCheckedItems();
			Iterator<Integer> iterator = checkedLooks.iterator();

			int numberDeleted = 0;

			while (iterator.hasNext()) {
				int position = iterator.next();
				deleteLook(position - numberDeleted);
				++numberDeleted;
			}
			setSelectMode(Constants.SELECT_NONE);
			adapter.clearCheckedItems();

			actionMode = null;
			setActionModeActive(false);

			registerForContextMenu(listView);
			Utils.setBottomBarActivated(getActivity(), true);
		}
	};

	private void deleteLook(int position) {
		StorageHandler.getInstance().deleteFile(lookDataList.get(position).getAbsolutePath());

		lookDataList.remove(position);
		ProjectManager.getInstance().getCurrentSprite().setLookDataList(lookDataList);

		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_LOOK_DELETED));
	}

	private void copyLook(int position) {
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

	@Override
	protected void showRenameDialog() {
		RenameLookDialog renameLookDialog = RenameLookDialog.newInstance(selectedLookData.getLookName());
		renameLookDialog.show(getFragmentManager(), RenameLookDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	protected void showDeleteDialog() {
		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(selectedLookPosition);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}
}
