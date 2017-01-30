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
package org.catrobat.catroid.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.LookViewHolder;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.OldLookController;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilCamera;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class LookFragment extends ScriptActivityFragment implements LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener {

	public static final String TAG = LookFragment.class.getSimpleName();
	private static int selectedLookPosition = Constants.NO_POSITION;
	private static String actionModeTitle;
	private static String singleItemAppendixActionMode;
	private static String multipleItemAppendixActionMode;
	public Intent lastReceivedIntent = null;
	private List<LookData> lookDataList;
	private LookData selectedLookData;
	private Uri lookFromCameraUri = null;
	private ListView listView;
	private LookDeletedReceiver lookDeletedReceiver;
	private LookRenamedReceiver lookRenamedReceiver;
	private LooksListInitReceiver looksListInitReceiver;
	private LookListTouchActionUpReceiver lookListTouchActionUpReceiver;
	private ActionMode actionMode;
	private View selectAllActionModeButton;
	private boolean isRenameActionMode;
	private boolean isResultHandled = false;
	private OnLookDataListChangedAfterNewListener lookDataListChangedAfterNewListener;
	private Lock viewSwitchLock = new ViewSwitchLock();
	private Activity activity;
	private ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.copy);
			singleItemAppendixActionMode = getString(R.string.look);
			multipleItemAppendixActionMode = getString(R.string.looks);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}
	};

	private ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_SINGLE);
			mode.setTitle(R.string.rename);

			setActionModeActive(true);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
	};

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.delete);
			singleItemAppendixActionMode = getString(R.string.look);
			multipleItemAppendixActionMode = getString(R.string.looks);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
	};

	private ActionMode.Callback backPackModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.backpack);
			singleItemAppendixActionMode = getString(R.string.look);
			multipleItemAppendixActionMode = getString(R.string.looks);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}
	};

	public void setOnLookDataListChangedAfterNewListener(OnLookDataListChangedAfterNewListener listener) {
		lookDataListChangedAfterNewListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_looks);

		return inflater.inflate(R.layout.fragment_look, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();

		if (listView != null) {
			registerForContextMenu(listView);
		}

		if (savedInstanceState != null) {
			selectedLookData = (LookData) savedInstanceState
					.getSerializable(OldLookController.BUNDLE_ARGUMENTS_SELECTED_LOOK);

			boolean uriIsSet = savedInstanceState.getBoolean(OldLookController.BUNDLE_ARGUMENTS_URI_IS_SET);
			if (uriIsSet) {
				String defLookName = getString(R.string.default_look_name);
				lookFromCameraUri = UtilCamera.getDefaultLookFromCameraUri(defLookName);
			}
		}

		try {
			lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, Log.getStackTraceString(nullPointerException));
			lookDataList = new ArrayList<>();
		}

		//((DynamicListView) getListView()).setDataList(lookDataList);

		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			TextView emptyViewHeading = (TextView) activity.findViewById(R.id.fragment_look_text_heading);
			emptyViewHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60.0f);
			emptyViewHeading.setText(R.string.backgrounds);
			TextView emptyViewDescription = (TextView) activity.findViewById(R.id.fragment_look_text_description);
			emptyViewDescription.setText(R.string.fragment_background_text_description);
		}

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(true);
		menu.findItem(R.id.unpack).setVisible(false);
		menu.findItem(R.id.backpack).setVisible(true);
		if (BackPackListManager.getInstance().getAllBackPackedLooks().isEmpty()) {
			StorageHandler.getInstance().clearBackPackLookDirectory();
		}
		menu.findItem(R.id.cut).setVisible(true);
		menu.findItem(R.id.rename).setVisible(true);
		menu.findItem(R.id.show_details).setVisible(true);
		menu.findItem(R.id.settings).setVisible(true);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(OldLookController.BUNDLE_ARGUMENTS_URI_IS_SET, (lookFromCameraUri != null));
		outState.putSerializable(OldLookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedLookData);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(activity)) {
			return;
		}

		if (BackPackListManager.getInstance().isBackpackEmpty()) {
			BackPackListManager.getInstance().loadBackpack();
		}

		if (lookRenamedReceiver == null) {
			lookRenamedReceiver = new LookRenamedReceiver();
		}

		if (lookDeletedReceiver == null) {
			lookDeletedReceiver = new LookDeletedReceiver();
		}

		if (looksListInitReceiver == null) {
			looksListInitReceiver = new LooksListInitReceiver();
		}

		if (lookListTouchActionUpReceiver == null) {
			lookListTouchActionUpReceiver = new LookListTouchActionUpReceiver();
		}

		IntentFilter intentFilterRenameLook = new IntentFilter(ScriptActivity.ACTION_LOOK_RENAMED);
		activity.registerReceiver(lookRenamedReceiver, intentFilterRenameLook);

		IntentFilter intentFilterDeleteLook = new IntentFilter(ScriptActivity.ACTION_LOOK_DELETED);
		activity.registerReceiver(lookDeletedReceiver, intentFilterDeleteLook);

		IntentFilter intentFilterLooksListInit = new IntentFilter(ScriptActivity.ACTION_LOOKS_LIST_INIT);
		activity.registerReceiver(looksListInitReceiver, intentFilterLooksListInit);

		IntentFilter intentFilterLookListTouchUp = new IntentFilter(ScriptActivity.ACTION_LOOK_TOUCH_ACTION_UP);
		activity.registerReceiver(lookListTouchActionUpReceiver, intentFilterLookListTouchUp);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity
				.getApplicationContext());

		setShowDetails(settings.getBoolean(OldLookController.SHARED_PREFERENCE_NAME, false));

		handleAddButtonFromNew();

		if (isResultHandled) {
			isResultHandled = false;

			ScriptActivity scriptActivity = (ScriptActivity) activity;
			if (scriptActivity.getIsLookFragmentFromSetLookBrickNew()
					&& scriptActivity.getIsLookFragmentHandleAddButtonHandled()) {
				OldLookController.getInstance().switchToScriptFragment(LookFragment.this, (ScriptActivity) activity);
			}
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			handleAddButtonFromNew();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(activity.getApplicationContext());
		}

		if (lookDeletedReceiver != null) {
			activity.unregisterReceiver(lookDeletedReceiver);
		}

		if (lookRenamedReceiver != null) {
			activity.unregisterReceiver(lookRenamedReceiver);
		}

		if (looksListInitReceiver != null) {
			activity.unregisterReceiver(looksListInitReceiver);
		}

		if (lookListTouchActionUpReceiver != null) {
			activity.unregisterReceiver(lookListTouchActionUpReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(OldLookController.SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	public void setSelectedLookData(LookData lookData) {
		selectedLookData = lookData;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		lastReceivedIntent = data;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case OldLookController.REQUEST_SELECT_OR_DRAW_IMAGE:
					if (data != null) {
						OldLookController.getInstance().loadImageIntoCatroid(data, activity, lookDataList, this);
					}
					break;
				case OldLookController.REQUEST_POCKET_PAINT_EDIT_IMAGE:
					if (data != null) {
						OldLookController.getInstance().loadPocketPaintImageIntoCatroid(data, activity,
								selectedLookData);
					}
					break;
				case OldLookController.REQUEST_TAKE_PICTURE:
					String defLookName = getString(R.string.default_look_name);
					//lookFromCameraUri = UtilCamera.rotatePictureIfNecessary(lookFromCameraUri, defLookName);
					OldLookController.getInstance().loadPictureFromCameraIntoCatroid(lookFromCameraUri, activity,
							lookDataList, this);
					break;
				case OldLookController.REQUEST_MEDIA_LIBRARY:
					String filePath = data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH);
					OldLookController.getInstance().loadPictureFromLibraryIntoCatroid(filePath, activity,
							lookDataList, this);
					break;
				case OldLookController.REQUEST_DRONE_VIDEO:
					String droneFilePath = getString(R.string.add_look_drone_video);
					OldLookController.getInstance().loadDroneVideoImageToProject(droneFilePath,
							R.drawable.ic_video, this.getActivity(), lookDataList, this);
			}
			isResultHandled = true;
		}

//		if (requestCode == OldLookController.REQUEST_POCKET_PAINT_EDIT_IMAGE) {
//			StorageHandler.getInstance().deleteTempImageCopy();
//		}
	}

	private void openBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.FRAGMENT, BackPackLookListFragment.class);
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		return OldLookController.getInstance().onCreateLoader(id, arguments, getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		OldLookController.getInstance().onLoadFinished(loader, data, activity, lookDataList, this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	public void addLookFromCamera() {
		lookFromCameraUri = UtilCamera.getDefaultLookFromCameraUri(getString(R.string.default_look_name));

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, lookFromCameraUri);

		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
		startActivityForResult(chooser, OldLookController.REQUEST_TAKE_PICTURE);
	}

	public void addLookDrawNewImage() {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
				Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		if (!OldLookController.getInstance().checkIfPocketPaintIsInstalled(intent, activity)) {
			return;
		}

		Bundle bundleForPocketPaint = new Bundle();
		bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
		bundleForPocketPaint
				.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));
		intent.putExtras(bundleForPocketPaint);

		intent.addCategory("android.intent.category.LAUNCHER");
		startActivityForResult(intent, OldLookController.REQUEST_SELECT_OR_DRAW_IMAGE);
	}

	public void addLookChooseImage() {
		Intent intent = new Intent(Intent.ACTION_PICK);

		Bundle bundleForPocketCode = new Bundle();
		bundleForPocketCode.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
		bundleForPocketCode.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));

		intent.setType("image/*");
		intent.putExtras(bundleForPocketCode);

		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_gallery));
		startActivityForResult(chooser, OldLookController.REQUEST_SELECT_OR_DRAW_IMAGE);
	}

	public void addLookMediaLibrary() {
		Intent intent = new Intent(activity, WebViewActivity.class);
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
		startActivityForResult(intent, OldLookController.REQUEST_MEDIA_LIBRARY);
	}

	public void addLookDroneVideo() {
		onActivityResult(OldLookController.REQUEST_DRONE_VIDEO, Activity.RESULT_OK, new Intent());
	}

	private void startActionMode(ActionMode.Callback actionModeCallback, boolean isRenameMode) {

	}

	private void handleAddButtonFromNew() {
		ScriptActivity scriptActivity = (ScriptActivity) activity;
		if (scriptActivity.getIsLookFragmentFromSetLookBrickNew()
				&& !scriptActivity.getIsLookFragmentHandleAddButtonHandled()) {
			scriptActivity.setIsLookFragmentHandleAddButtonHandled(true);
			handleAddButton();
		}
	}

	@Override
	public boolean getShowDetails() {
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {

	}

	@Override
	public void setSelectMode(int selectMode) {

	}

	@Override
	public int getSelectMode() {
		return 0;
	}

	@Override
	public void startCopyActionMode() {

	}

	@Override
	public void startCommentOutActionMode() {

	}

	@Override
	public void startRenameActionMode() {

	}

	@Override
	public void startDeleteActionMode() {

	}

	@Override
	public void startBackPackActionMode() {

	}

	@Override
	public void handleAddButton() {
	}

	@Override
	public void handleCheckBoxClick(View view) {

	}

	public void onLookEdit(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		handleEditLook(view);
	}

	public void onLookChecked() {
	}

	private void updateActionModeTitle() {

	}

	private void handleEditLook(View view) {
		int position = (Integer) view.getTag();
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
				Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		sendPocketPaintIntent(position, intent);
	}

	public void sendPocketPaintIntent(int selectedPosition, Intent intent) {

		if (!OldLookController.getInstance().checkIfPocketPaintIsInstalled(intent, activity)) {
			return;
		}

		selectedLookData = lookDataList.get(selectedPosition);

		Bundle bundleForPocketPaint = new Bundle();

		try {
			File tempCopy = StorageHandler.getInstance()
					.makeTempImageCopy(lookDataList.get(selectedPosition).getAbsolutePath());

			bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, tempCopy.getAbsolutePath());
			bundleForPocketPaint.putInt(Constants.EXTRA_X_VALUE_POCKET_PAINT, 0);
			bundleForPocketPaint.putInt(Constants.EXTRA_Y_VALUE_POCKET_PAINT, 0);
			intent.putExtras(bundleForPocketPaint);

			intent.addCategory("android.intent.category.LAUNCHER");
			startActivityForResult(intent, OldLookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, Log.getStackTraceString(nullPointerException));
			ToastUtil.showError(activity, R.string.error_load_image);
		}
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {

	}

	private void showConfirmDeleteDialog() {
	}

	public void clearCheckedLooksAndEnableButtons() {
	}

	@Override
	public void showRenameDialog() {
	}

	@Override
	public void showDeleteDialog() {
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				ScriptActivity scriptActivity = (ScriptActivity) activity;
				if (scriptActivity.getIsLookFragmentFromSetLookBrickNew()) {
					OldLookController.getInstance().switchToScriptFragment(this, (ScriptActivity) activity);

					return true;
				}
			default:
				break;
		}
		return false;
	}

	public View getView(int position, View convertView) {
		LookViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(activity, R.layout.fragment_look_looklist_item, null);

			holder = new LookViewHolder();

			holder.lookImageView = (ImageView) convertView.findViewById(R.id.fragment_look_item_image_view);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_look_item_checkbox);
			holder.lookNameTextView = (TextView) convertView.findViewById(R.id.fragment_look_item_name_text_view);
			holder.lookDetailsLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_look_item_detail_linear_layout);
			holder.lookFileSizeTextView = (TextView) holder.lookDetailsLinearLayout
					.findViewById(R.id.fragment_look_item_size_text_view);
			holder.lookMeasureTextView = (TextView) holder.lookDetailsLinearLayout
					.findViewById(R.id.fragment_look_item_measure_text_view);
			holder.lookElement = (RelativeLayout) convertView.findViewById(R.id.fragment_look_item_relative_layout);
			convertView.setTag(holder);
		} else {
			holder = (LookViewHolder) convertView.getTag();
		}

		return convertView;
	}

	public void updateLookAdapter(LookData lookData) {
	}

	public void initOrRestartLoader(Bundle arguments) {
		LoaderManager loaderManager = getLoaderManager();

		if (loaderManager.getLoader(OldLookController.ID_LOADER_MEDIA_IMAGE) == null) {
			loaderManager.initLoader(OldLookController.ID_LOADER_MEDIA_IMAGE, arguments, this);
		} else {
			loaderManager.restartLoader(OldLookController.ID_LOADER_MEDIA_IMAGE, arguments, this);
		}
	}

	public void destroyLoader() {
		getLoaderManager().destroyLoader(OldLookController.ID_LOADER_MEDIA_IMAGE);
	}

	public List<LookData> getLookDataList() {
		return lookDataList;
	}

	public interface OnLookDataListChangedAfterNewListener {

		void onLookDataListChangedAfterNew(LookData soundInfo);
	}

	private class LookDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		}
	}

	private class LookRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		}
	}

	private class LooksListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	}

	private class LookListTouchActionUpReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOK_TOUCH_ACTION_UP)) {

			}
		}
	}
}
