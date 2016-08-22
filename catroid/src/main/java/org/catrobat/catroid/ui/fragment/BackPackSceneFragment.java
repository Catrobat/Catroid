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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.adapter.SceneAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;

public class BackPackSceneFragment extends BackPackActivityFragment implements Dialog.OnKeyListener, SceneAdapter
		.OnSceneEditListener {

	public static final String TAG = BackPackSceneFragment.class.getSimpleName();
	private static String actionModeTitle;
	private static String singleItemAppendixActionMode;
	private static String multipleItemAppendixActionMode;
	private SceneAdapter adapter;
	private Scene selectedSceneBackPack;
	private int selectedScenePosition;
	private ListView listView;
	private ActionMode actionMode;
	private View selectAllActionModeButton;
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
			singleItemAppendixActionMode = getString(R.string.scene);
			multipleItemAppendixActionMode = getString(R.string.scenes);

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
				clearCheckedScenesAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};
	private ActionMode.Callback unpackingModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.unpack);
			singleItemAppendixActionMode = getString(R.string.scene);
			multipleItemAppendixActionMode = getString(R.string.scenes);

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
			if (adapter.getAmountOfCheckedScenes() == 0) {
				clearCheckedScenesAndEnableButtons();
			} else {
				showDifferentResolutionDialog(getActivity(), false);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_back_pack_sprites_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();
		registerForContextMenu(listView);

		adapter = new SceneAdapter(getActivity(), R.layout.activity_scenes_list_item, R.id
				.activity_scenes_list_item_text_view, BackPackListManager.getInstance().getBackPackedScenes());
		setListAdapter(adapter);
		checkEmptyBackgroundBackPack();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(false);
		menu.findItem(R.id.show_details).setVisible(false);

		if (!BackPackListManager.getInstance().getBackPackedScenes().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(true);
		}

		menu.findItem(R.id.unpacking_keep).setVisible(false);
		BottomBar.hideBottomBar(getActivity());
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		selectedSceneBackPack = adapter.getItem(selectedScenePosition);
		menu.setHeaderTitle(selectedSceneBackPack.getName());
		adapter.addCheckedScene(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_unpacking:
				showDifferentResolutionDialog(getActivity(), true);
				break;
			case R.id.context_menu_delete:
				showConfirmDeleteDialog();
				break;
		}
		return super.onContextItemSelected(item);
	}

	private void showConfirmDeleteDialog() {
		deleteCheckedScenes();
		clearCheckedScenesAndEnableButtons();
		adapter.notifyDataSetChanged();
	}

	private void showError(int messageID) {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.error)
				.setMessage(messageID)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int id) {
					}
				})
				.setCancelable(false)
				.show();
	}

	private void deleteCheckedScenes() {
		int numDeleted = 0;
		for (int position : adapter.getCheckedItems()) {
			selectedSceneBackPack = (Scene) getListView().getItemAtPosition(position - numDeleted);
			deleteScene();
			numDeleted++;
		}
		checkEmptyBackgroundBackPack();
	}

	public void deleteScene() {
		ArrayList<Scene> hiddenScenes = new ArrayList<>();
		BackPackListManager.searchForHiddenScenes(selectedSceneBackPack, hiddenScenes, true);
		hiddenScenes.remove(selectedSceneBackPack);
		for (Scene scene : hiddenScenes) {
			BackPackListManager.getInstance().removeItemFromSceneBackPackByName(scene.getName(), true);
		}
		BackPackListManager.getInstance().removeItemFromSceneBackPackByName(selectedSceneBackPack.getName(), false);
	}

	public void clearCheckedScenesAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.hideBottomBar(getActivity());
	}

	private void initClickListener() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedScenePosition = position;
			}
		});
		adapter.setOnSceneEditListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedSceneBackPack);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		initClickListener();
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
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
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);

		selectAllActionModeButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						for (int position = 0; position < adapter.getCount(); position++) {
							adapter.addCheckedScene(position);
						}
						adapter.notifyDataSetChanged();
					}
				});
	}

	@Override
	protected void showDeleteDialog() {
		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(selectedScenePosition);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void startUnPackingActionMode(boolean deleteUnpackedItems) {
		startActionMode(unpackingModeCallBack, deleteUnpackedItems);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack, true);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback, boolean deleteUnpackedItems) {
		if (actionMode != null) {
			return;
		}

		if (adapter.isEmpty()) {
			if (actionModeCallback.equals(unpackingModeCallBack)) {
				((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.unpack));
			} else if (actionModeCallback.equals(deleteModeCallBack)) {
				((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
			}
		} else {
			if (actionModeCallback.equals(unpackingModeCallBack)) {
				this.deleteUnpackedItems = deleteUnpackedItems;
			}
			actionMode = getActivity().startActionMode(actionModeCallback);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	private void showUnpackingConfirmationMessage(int count) {
		String messageForUser = getResources().getQuantityString(R.plurals.unpacking_items_plural, count);
		ToastUtil.showSuccess(getActivity(), messageForUser);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.progress_bar_activity_script).setVisibility(View.GONE);

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		BackPackListManager.getInstance().saveBackpack();
	}

	public SceneAdapter getAdapter() {
		return adapter;
	}

	private void updateActionModeTitle() {
		if (actionMode == null) {
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

	public ActionMode getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionMode actionMode) {
		this.actionMode = actionMode;
	}

	public void setSelectedScenePosition(int selectedScenePosition) {
		this.selectedScenePosition = selectedScenePosition;
	}

	private void checkEmptyBackgroundBackPack() {
		if (BackPackListManager.getInstance().getBackPackedScenes().isEmpty()) {
			TextView emptyViewHeading = (TextView) getActivity().findViewById(R.id.fragment_sprites_list_backpack_text_heading);
			emptyViewHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60.0f);
			emptyViewHeading.setText(R.string.backpack);
			TextView emptyViewDescription = (TextView) getActivity().findViewById(R.id.fragment_sprites_list_backpack_text_description);
			emptyViewDescription.setText(R.string.is_empty);
		}
	}

	@Override
	public void onSceneChecked() {
		updateActionModeTitle();
		Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	@Override
	public void onSceneEdit(int position, View view) {
		setSelectedScenePosition(position);
		getListView().showContextMenuForChild(view);
	}

	private void unPackAsynchronous(final Activity activity, final boolean single) {
		showProgressCircle();
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				final int count = adapter.getAmountOfCheckedScenes();
				final boolean success;
				if (single) {
					success = BackPackSceneController.getInstance().unpackScene(selectedSceneBackPack) != null;
				} else {
					success = adapter.onDestroyActionModeUnpacking();
				}
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (success) {
							ProjectManager.getInstance().checkNestingBrickReferences(false, false);
							if (!single) {
								showUnpackingConfirmationMessage(count);
								clearCheckedScenesAndEnableButtons();
							}
							adapter.returnToProjectActivity();
						} else {
							showError(R.string.error_scene_backpack);
						}
					}
				});
			}
		};
		(new Thread(r)).start();
	}

	private void showDifferentResolutionDialog(final Activity activity, final boolean singleUnpacking) {
		ArrayList<Scene> scenesToCheck = new ArrayList<>();
		XmlHeader currentHeader = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
		boolean showDialog = false;
		if (singleUnpacking) {
			scenesToCheck.add(selectedSceneBackPack);
		} else {
			for (Integer checkedPosition : adapter.getCheckedScenes()) {
				scenesToCheck.add(adapter.getItem(checkedPosition));
			}
		}

		for (Scene scene : scenesToCheck) {
			if (scene.getOriginalHeight() != currentHeader.virtualScreenHeight || scene.getOriginalWidth()
					!= currentHeader.virtualScreenWidth) {
				showDialog = true;
				break;
			}
		}

		if (!showDialog) {
			unPackAsynchronous(activity, singleUnpacking);
			return;
		}

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						unPackAsynchronous(activity, singleUnpacking);
						break;
					default:
						clearCheckedScenesAndEnableButtons();
						break;
				}
			}
		};

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(activity);
		builder.setTitle(R.string.warning)
				.setMessage(activity.getString(R.string.error_unpack_scene_with_different_resolution))
				.setPositiveButton(activity.getString(R.string.main_menu_continue), dialogClickListener)
				.setNegativeButton(activity.getString(R.string.abort), dialogClickListener);
		Dialog errorDialog = builder.create();
		errorDialog.show();
	}

	public void showProgressCircle() {
		ProgressBar progressCircle = (ProgressBar) getActivity().findViewById(R.id.progress_bar_activity_script);
		progressCircle.setVisibility(View.VISIBLE);
		progressCircle.bringToFront();
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
	}
}
