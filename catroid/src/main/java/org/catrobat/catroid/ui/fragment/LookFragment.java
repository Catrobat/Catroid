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
import android.app.AlertDialog;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.LookViewHolder;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.adapter.LookAdapter;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.ui.dialogs.NewLookDialog;
import org.catrobat.catroid.ui.dialogs.RenameLookDialog;
import org.catrobat.catroid.ui.dynamiclistview.DynamicListView;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilCamera;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class LookFragment extends ScriptActivityFragment implements LookBaseAdapter.OnLookEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener, LookController.OnBackpackLookCompleteListener {

	public static final String TAG = LookFragment.class.getSimpleName();
	private static int selectedLookPosition = Constants.NO_POSITION;
	private static String actionModeTitle;
	private static String singleItemAppendixActionMode;
	private static String multipleItemAppendixActionMode;
	public Intent lastReceivedIntent = null;
	private LookBaseAdapter adapter;
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
			for (int position : adapter.getCheckedItems()) {
				LookController.getInstance().copyLook(position, lookDataList, activity, LookFragment.this);
			}
			clearCheckedLooksAndEnableButtons();
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
			Set<Integer> checkedLooks = adapter.getCheckedItems();
			Iterator<Integer> iterator = checkedLooks.iterator();

			if (iterator.hasNext()) {
				int position = iterator.next();
				selectedLookData = (LookData) listView.getItemAtPosition(position);
				showRenameDialog();
			}
			clearCheckedLooksAndEnableButtons();
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
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedLooksAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
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
			((LookAdapter) adapter).onDestroyActionModeBackPack();
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	/*
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	*/

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();

		if (listView != null) {
			registerForContextMenu(listView);
		}

		if (savedInstanceState != null) {
			selectedLookData = (LookData) savedInstanceState
					.getSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK);

			boolean uriIsSet = savedInstanceState.getBoolean(LookController.BUNDLE_ARGUMENTS_URI_IS_SET);
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

		((DynamicListView) getListView()).setDataList(lookDataList);

		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			TextView emptyViewHeading = (TextView) activity.findViewById(R.id.fragment_look_text_heading);
			emptyViewHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60.0f);
			emptyViewHeading.setText(R.string.backgrounds);
			TextView emptyViewDescription = (TextView) activity.findViewById(R.id.fragment_look_text_description);
			emptyViewDescription.setText(R.string.fragment_background_text_description);
		}

		adapter = new LookAdapter(activity, R.layout.fragment_look_looklist_item,
				R.id.fragment_look_item_name_text_view, lookDataList, false);
		adapter.setOnLookEditListener(this);
		setListAdapter(adapter);
		((LookAdapter) adapter).setLookFragment(this);

		Utils.loadProjectIfNeeded(activity);

		BackPackListManager.getInstance().setCurrentLookAdapter(adapter);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(true);
		menu.findItem(R.id.unpacking).setVisible(false);
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
		outState.putBoolean(LookController.BUNDLE_ARGUMENTS_URI_IS_SET, (lookFromCameraUri != null));
		outState.putSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedLookData);
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

		setShowDetails(settings.getBoolean(LookController.SHARED_PREFERENCE_NAME, false));

		handleAddButtonFromNew();

		if (isResultHandled) {
			isResultHandled = false;

			ScriptActivity scriptActivity = (ScriptActivity) activity;
			if (scriptActivity.getIsLookFragmentFromSetLookBrickNew()
					&& scriptActivity.getIsLookFragmentHandleAddButtonHandled()) {
				LookController.getInstance().switchToScriptFragment(LookFragment.this, (ScriptActivity) activity);
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

		editor.putBoolean(LookController.SHARED_PREFERENCE_NAME, getShowDetails());
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
				case LookController.REQUEST_SELECT_OR_DRAW_IMAGE:
					if (data != null) {
						LookController.getInstance().loadImageIntoCatroid(data, activity, lookDataList, this);
					}
					break;
				case LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE:
					if (data != null) {
						LookController.getInstance().loadPocketPaintImageIntoCatroid(data, activity,
								selectedLookData);
					}
					break;
				case LookController.REQUEST_TAKE_PICTURE:
					String defLookName = getString(R.string.default_look_name);
					lookFromCameraUri = UtilCamera.rotatePictureIfNecessary(lookFromCameraUri, defLookName);
					LookController.getInstance().loadPictureFromCameraIntoCatroid(lookFromCameraUri, activity,
							lookDataList, this);
					break;
				case LookController.REQUEST_MEDIA_LIBRARY:
					String filePath = data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH);
					LookController.getInstance().loadPictureFromLibraryIntoCatroid(filePath, activity,
							lookDataList, this);
					break;
				case LookController.REQUEST_DRONE_VIDEO:
					String droneFilePath = getString(R.string.add_look_drone_video);
					LookController.getInstance().loadDroneVideoImageToProject(droneFilePath,
							R.drawable.ic_video, this.getActivity(), lookDataList, this);
			}
			isResultHandled = true;
		}

		if (requestCode == LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE) {
			StorageHandler.getInstance().deleteTempImageCopy();
		}
	}

	private void openBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_LOOKS);
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		return LookController.getInstance().onCreateLoader(id, arguments, getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		LookController.getInstance().onLoadFinished(loader, data, activity, lookDataList, this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	public void addLookFromCamera() {
		lookFromCameraUri = UtilCamera.getDefaultLookFromCameraUri(getString(R.string.default_look_name));

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, lookFromCameraUri);

		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
		startActivityForResult(chooser, LookController.REQUEST_TAKE_PICTURE);
	}

	public void addLookDrawNewImage() {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
				Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		if (!LookController.getInstance().checkIfPocketPaintIsInstalled(intent, activity)) {
			return;
		}

		Bundle bundleForPocketPaint = new Bundle();
		bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
		bundleForPocketPaint
				.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));
		intent.putExtras(bundleForPocketPaint);

		intent.addCategory("android.intent.category.LAUNCHER");
		startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);
	}

	public void addLookChooseImage() {
		Intent intent = new Intent(Intent.ACTION_PICK);

		Bundle bundleForPocketCode = new Bundle();
		bundleForPocketCode.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
		bundleForPocketCode.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));

		intent.setType("image/*");
		intent.putExtras(bundleForPocketCode);

		Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_gallery));
		startActivityForResult(chooser, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);
	}

	public void addLookMediaLibrary() {
		Intent intent = new Intent(activity, WebViewActivity.class);
		String url;

		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			url = ProjectManager.getInstance().isCurrentProjectLandscapeMode()
					? Constants.LIBRARY_BACKGROUNDS_URL_LANDSCAPE
					: Constants.LIBRARY_BACKGROUNDS_URL_PORTRAIT;
		} else {
			url = Constants.LIBRARY_LOOKS_URL;
		}
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		intent.putExtra(WebViewActivity.CALLING_ACTIVITY, TAG);
		startActivityForResult(intent, LookController.REQUEST_MEDIA_LIBRARY);
	}

	public void addLookDroneVideo() {
		onActivityResult(LookController.REQUEST_DRONE_VIDEO, Activity.RESULT_OK, new Intent());
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
	public void setShowDetails(boolean showDetails) {
		// TODO CHANGE THIS!!! (was just a quick fix)
		if (adapter != null) {
			adapter.setShowDetails(showDetails);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void startCopyActionMode() {
		startActionMode(copyModeCallBack, false);
	}

	@Override
	public void startCommentOutActionMode() {
		// Comment out not supported
	}

	@Override
	public void startRenameActionMode() {
		startActionMode(renameModeCallBack, true);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack, false);
	}

	@Override
	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack, false);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback, boolean isRenameMode) {
		if (actionMode == null) {
			if (adapter.isEmpty()) {
				if (actionModeCallback.equals(copyModeCallBack)) {
					((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.copy));
				} else if (actionModeCallback.equals(deleteModeCallBack)) {
					((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
				} else if (actionModeCallback.equals(backPackModeCallBack)) {
					if (BackPackListManager.getInstance().getBackPackedLooks().isEmpty()) {
						((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.backpack));
					} else {
						openBackPack();
					}
				} else if (actionModeCallback.equals(renameModeCallBack)) {
					((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.rename));
				}
			} else {
				actionMode = getActivity().startActionMode(actionModeCallback);
				unregisterForContextMenu(listView);
				BottomBar.hideBottomBar(getActivity());
				isRenameActionMode = isRenameMode;
			}
		}
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
	public void handleAddButton() {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		activity.getActionBar().setTitle(ProjectManager.getInstance().getCurrentSprite().getName());

		NewLookDialog dialog = NewLookDialog.newInstance();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (ProjectManager.getInstance().getComingFromScriptFragmentToLooksFragment()) {
					ProjectManager.getInstance().setComingFromScriptFragmentToLooksFragment(false);
					activity.sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
					isResultHandled = true;
					ProjectManager.getInstance().setCurrentSprite(ProjectManager.getInstance().getPreviousSprite());
					((ScriptActivity) activity).setSwitchToScriptFragment(true);
				}
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (ProjectManager.getInstance().getComingFromScriptFragmentToLooksFragment()) {
					ProjectManager.getInstance().setComingFromScriptFragmentToLooksFragment(false);
					Sprite sprite = ProjectManager.getInstance().getPreviousSprite();
					ProjectManager.getInstance().setCurrentSprite(sprite);
					activity.getActionBar().setTitle(sprite.getName());
					((ScriptActivity) activity).switchFromLookToScriptFragment();
				}
			}
		});
		dialog.showDialog(this);
	}

	@Override
	public void handleCheckBoxClick(View view) {
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLookEdit(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		handleEditLook(view);
	}

	@Override
	public void onLookChecked() {
		if (isRenameActionMode || actionMode == null) {
			return;
		}

		updateActionModeTitle();
		UtilUi.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	private void updateActionModeTitle() {
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

	private void handleEditLook(View view) {
		int position = (Integer) view.getTag();
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
				Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		sendPocketPaintIntent(position, intent);
	}

	public void sendPocketPaintIntent(int selectedPosition, Intent intent) {

		if (!LookController.getInstance().checkIfPocketPaintIsInstalled(intent, activity)) {
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
			startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, Log.getStackTraceString(nullPointerException));
			ToastUtil.showError(activity, R.string.error_load_image);
		}
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				for (int position = 0; position < lookDataList.size(); position++) {
					adapter.addCheckedItem(position);
					adapter.notifyDataSetChanged();
				}
				onLookChecked();
			}
		});
	}

	private void showConfirmDeleteDialog() {
		int titleId;
		if (adapter.getAmountOfCheckedItems() == 1) {
			titleId = R.string.dialog_confirm_delete_look_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_looks_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(activity);
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_look_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				adapter.addCheckedItemIfNotExists(selectedLookPosition);
				LookController.getInstance().deleteCheckedLooks(adapter, lookDataList, activity);
				clearCheckedLooksAndEnableButtons();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				clearCheckedLooksAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void clearCheckedLooksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();
		adapter.notifyDataSetChanged();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.showBottomBar(activity);
	}

	@Override
	public void showRenameDialog() {
		RenameLookDialog renameLookDialog = new RenameLookDialog(R.string.rename_look_dialog, R.string
				.lookname, selectedLookData.getLookName());
		renameLookDialog.show(getFragmentManager(), RenameLookDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showDeleteDialog() {
		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(selectedLookPosition);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				ScriptActivity scriptActivity = (ScriptActivity) activity;
				if (scriptActivity.getIsLookFragmentFromSetLookBrickNew()) {
					LookController.getInstance().switchToScriptFragment(this, (ScriptActivity) activity);

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

		LookController controller = LookController.getInstance();
		controller.updateLookLogic(position, holder, adapter);
		return convertView;
	}

	public void updateLookAdapter(LookData lookData) {
		adapter.notifyDataSetChanged();

		if (lookDataListChangedAfterNewListener != null) {
			lookDataListChangedAfterNewListener.onLookDataListChangedAfterNew(lookData);
		}

		//scroll down the list to the new item:
		final ListView listView = getListView();
		listView.post(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});
	}

	public void initOrRestartLoader(Bundle arguments) {
		LoaderManager loaderManager = getLoaderManager();

		if (loaderManager.getLoader(LookController.ID_LOADER_MEDIA_IMAGE) == null) {
			loaderManager.initLoader(LookController.ID_LOADER_MEDIA_IMAGE, arguments, this);
		} else {
			loaderManager.restartLoader(LookController.ID_LOADER_MEDIA_IMAGE, arguments, this);
		}
	}

	public void destroyLoader() {
		getLoaderManager().destroyLoader(LookController.ID_LOADER_MEDIA_IMAGE);
	}

	public List<LookData> getLookDataList() {
		return lookDataList;
	}

	@Override
	public void onBackpackLookComplete(boolean startBackpackActivity) {
		openBackPack();
	}

	public interface OnLookDataListChangedAfterNewListener {

		void onLookDataListChangedAfterNew(LookData soundInfo);
	}

	private class LookDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOK_DELETED)) {
				adapter.notifyDataSetChanged();
				activity.sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
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

	private class LooksListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOKS_LIST_INIT)) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	private class LookListTouchActionUpReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOK_TOUCH_ACTION_UP)) {
				((DynamicListView) getListView()).notifyListItemTouchActionUp();
			}
		}
	}
}
