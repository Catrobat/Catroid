/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.LookListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.dialogs.NewLookDialog;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.dialogs.ReplaceInBackPackDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilCamera;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LookListFragment extends ListActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler<LookData>, NewLookDialog.AddLookInterface {

	public static final String TAG = LookListFragment.class.getSimpleName();
	public static final String BUNDLE_ARGUMENTS_LOOK_TO_EDIT = "look_to_edit";
	public static final String SHARED_PREFERENCE_NAME = "showLookDetails";

	public static final String LOADER_ARGUMENTS_IMAGE_URI = "image_uri";

	public static final int REQUEST_EDIT_IMAGE = 0;
	public static final int REQUEST_DRAW_IMAGE = 1;
	public static final int REQUEST_MEDIA_LIBRARY = 2;
	public static final int REQUEST_SELECT_IMAGE = 3;
	public static final int REQUEST_CAMERA_IMAGE = 4;
	public static final int REQUEST_DRONE_VIDEO = 5;


	private LookListAdapter lookAdapter;
	private DragAndDropListView listView;

	private LookData lookToEdit;
	List<LookData> lookDataList;

	private Uri lookFromCameraUri = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View lookListFragment = inflater.inflate(R.layout.fragment_look_list, container, false);
		listView = (DragAndDropListView) lookListFragment.findViewById(android.R.id.list);
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_looks);
		return lookListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.looks;
		deleteDialogTitle = R.plurals.dialog_delete_look;
		replaceDialogMessage = R.plurals.dialog_replace_look;

		if (savedInstanceState != null) {
			lookToEdit = (LookData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_LOOK_TO_EDIT);
		}

		initializeList();
	}

	private void initializeList() {
		lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();

		lookAdapter = new LookListAdapter(getActivity(), R.layout.list_item, lookDataList);

		setListAdapter(lookAdapter);
		lookAdapter.setListItemClickHandler(this);
		lookAdapter.setListItemLongClickHandler(listView);
		lookAdapter.setListItemCheckHandler(this);
		listView.setAdapterInterface(lookAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_LOOK_TO_EDIT, lookToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		putShowDetailsPreferences(SHARED_PREFERENCE_NAME);
		saveCurrentProject();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQUEST_EDIT_IMAGE:
					if (!LookController.loadFromPocketPaint(data, lookToEdit)) {
						ToastUtil.showError(getActivity(), R.string.error_load_image);
					}
					StorageHandler.getInstance().deleteTempImageCopy();
					break;
				case REQUEST_DRAW_IMAGE:
				case REQUEST_SELECT_IMAGE:
					if (!LookController.loadFromExternalApp(data, getActivity(), lookDataList)) {
						ToastUtil.showError(getActivity(), R.string.error_load_image);
					}
					break;
				case REQUEST_MEDIA_LIBRARY:
					String filePath = data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH);
					if (!LookController.loadFromMediaLibrary(filePath, lookDataList)) {
						ToastUtil.showError(getActivity(), R.string.error_load_image);
					}
					break;
				case REQUEST_CAMERA_IMAGE:
					String defaultLookName = getString(R.string.default_look_name);
					lookFromCameraUri = UtilCamera.rotatePictureIfNecessary(lookFromCameraUri, defaultLookName);
					if (!LookController.loadFromCamera(lookFromCameraUri, lookDataList)) {
						ToastUtil.showError(getActivity(), R.string.error_load_image);
					}
					break;
				case REQUEST_DRONE_VIDEO:
					String droneFilePath = getString(R.string.add_look_drone_video);
					if (!LookController.loadDroneVideo(droneFilePath, getActivity(),lookDataList)) {
						ToastUtil.showError(getActivity(), R.string.error_load_image);
					}
					break;
			}
		}
	}

	public void addLookDrawNewImage() {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
				Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		if (!LookController.checkIfPocketPaintIsInstalled(intent, getActivity())) {
			return;
		}

		Bundle bundleForPocketPaint = new Bundle();
		bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
		bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));
		intent.putExtras(bundleForPocketPaint);

		intent.addCategory("android.intent.category.LAUNCHER");
		startActivityForResult(intent, REQUEST_DRAW_IMAGE);
	}

	public void addLookMediaLibrary() {
		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		String url;
		if (ProjectManager.getInstance().getCurrentSprite().getName().equals(getString(R.string.background))) {
			url = ProjectManager.getInstance().isCurrentProjectLandscapeMode()
					? Constants.LIBRARY_BACKGROUNDS_URL_LANDSCAPE
					: Constants.LIBRARY_BACKGROUNDS_URL_PORTRAIT;
		} else {
			url = Constants.LIBRARY_LOOKS_URL;
		}
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		intent.putExtra(WebViewActivity.CALLING_ACTIVITY, TAG);
		startActivityForResult(intent, REQUEST_MEDIA_LIBRARY);
	}

	public void addLookChooseImage() {
		Intent intent = new Intent(Intent.ACTION_PICK);

		Bundle bundleForPocketCode = new Bundle();
		bundleForPocketCode.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
		bundleForPocketCode.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));

		intent.setType("image/*");
		intent.putExtras(bundleForPocketCode);

		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_gallery));
		startActivityForResult(chooser, REQUEST_SELECT_IMAGE);
	}

	public void addLookFromCamera() {
		lookFromCameraUri = UtilCamera.getDefaultLookFromCameraUri(getString(R.string.default_look_name));

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, lookFromCameraUri);

		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
		startActivityForResult(chooser, REQUEST_CAMERA_IMAGE);
	}

	public void addLookDroneVideo() {
		onActivityResult(REQUEST_DRONE_VIDEO, Activity.RESULT_OK, new Intent());
	}

	@Override
	public void handleAddButton() {
		NewLookDialog dialog = new NewLookDialog(this);
		dialog.setTargetFragment(this, 0);
		dialog.show(getFragmentManager(), NewLookDialog.TAG);
	}

	@Override
	public void handleOnItemClick(int position, View view, LookData listItem) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME, Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));
		sendPocketPaintIntent(listItem, intent);
	}

	public void sendPocketPaintIntent(LookData lookData, Intent intent) {
		if (!LookController.checkIfPocketPaintIsInstalled(intent, getActivity())) {
			return;
		}

		lookToEdit = lookData;
		Bundle bundleForPocketPaint = new Bundle();

		try {
			File tempCopy = StorageHandler.getInstance().makeTempImageCopy(lookData.getAbsolutePath());

			bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, tempCopy.getAbsolutePath());
			bundleForPocketPaint.putInt(Constants.EXTRA_X_VALUE_POCKET_PAINT, 0);
			bundleForPocketPaint.putInt(Constants.EXTRA_Y_VALUE_POCKET_PAINT, 0);
			intent.putExtras(bundleForPocketPaint);

			intent.addCategory("android.intent.category.LAUNCHER");
			startActivityForResult(intent, REQUEST_EDIT_IMAGE);
		} catch (Exception exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
			ToastUtil.showError(getActivity(), R.string.error_load_image);
		}
	}

	@Override
	public void deleteCheckedItems() {
		boolean success = true;
		for (LookData lookData : lookAdapter.getCheckedItems()) {
			lookToEdit = lookData;
			success &= deleteLook();
		}

		if (success) {
			saveCurrentProject();
		} else {
			ToastUtil.showError(getActivity(), R.string.error_delete_look);
		}
	}

	private boolean deleteLook() {
		if (!LookController.otherLookDataItemsHaveAFileReference(lookToEdit)) {
			StorageHandler.getInstance().deleteFile(lookToEdit.getAbsolutePath(), false);
		}
		lookAdapter.remove(lookToEdit);
		return true;
	}

	@Override
	protected void copyCheckedItems() {
		boolean success = true;
		for (LookData lookData : lookAdapter.getCheckedItems()) {
			lookToEdit = lookData;
			success &= copyLook();
		}

		if (success) {
			saveCurrentProject();
		} else {
			ToastUtil.showError(getActivity(), R.string.error_copy_look);
		}

		clearCheckedItems();
	}

	private boolean copyLook() {
		String projectName = ProjectManager.getInstance().getCurrentProject().getName();
		String sceneName = ProjectManager.getInstance().getCurrentScene().getName();

		try {
			StorageHandler.getInstance().copyImage(projectName, sceneName, lookToEdit.getAbsolutePath(), null);
			String newLookName = lookToEdit.getName().concat(getString(R.string.copied_item_suffix));
			LookData newLookData = new LookData(newLookName, lookToEdit.getLookFileName());

			lookAdapter.add(newLookData);

			if (ProjectManager.getInstance().getCurrentSprite().hasCollision()) {
				newLookData.getCollisionInformation().calculate();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void showRenameDialog() {
		lookToEdit = lookAdapter.getCheckedItems().get(0);
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_look_dialog, R.string.lookname, lookToEdit.getName(),
				this);
		dialog.show(getFragmentManager(), RenameItemDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean itemNameExists(String newName) {
		for (LookData lookData : lookDataList) {
			if (lookData.getName().equals(newName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void renameItem(String newName) {
		lookToEdit.setName(newName);
		lookAdapter.notifyDataSetChanged();
		clearCheckedItems();
	}

	@Override
	public void showReplaceItemsInBackPackDialog() {
		if (!LookController.existsInBackPack(lookAdapter.getCheckedItems())) {
			packCheckedItems();
			return;
		}

		String name = lookAdapter.getCheckedItems().get(0).getName();
		ReplaceInBackPackDialog dialog = new ReplaceInBackPackDialog(replaceDialogMessage, name, this);
		dialog.show(getFragmentManager(), ReplaceInBackPackDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void packCheckedItems() {
		setProgressCircleVisibility(true);
		boolean success = LookController.backpack(lookAdapter.getCheckedItems(), true);
		clearCheckedItems();

		if (success) {
			changeToBackPack();
			return;
		}

		setProgressCircleVisibility(false);
		ToastUtil.showError(getActivity(), R.string.error_backpack_look);
	}

	@Override
	protected boolean isBackPackEmpty() {
		return BackPackListManager.getInstance().getBackPackedLooks().isEmpty();
	}

	@Override
	protected void changeToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.FRAGMENT, BackPackLookListFragment.class);
		startActivity(intent);
	}

	private static String getNewValidLookName(String name, int nextNumber) {
		String newName;
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (LookData lookData : ProjectManager.getInstance().getCurrentSprite().getLookDataList()) {
			if (lookData.getName().equals(newName)) {
				return getNewValidLookName(name, ++nextNumber);
			}
		}
		return newName;
	}
}
