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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.CapitalizedTextView;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SceneAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.RenameSceneDialog;
import org.catrobat.catroid.ui.dynamiclistview.DynamicListView;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ScenesListFragment extends ScriptActivityFragment implements SceneAdapter.OnSceneEditListener,
		BackPackSceneController.OnBackpackSceneCompleteListener {

	public static final String TAG = ScenesListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SCENE_TO_EDIT = "scene_to_edit";

	private static String multiSelectActionModeTitle;
	private static String singleItemAppendixMultiSelectActionMode;
	private static String multipleItemAppendixMultiSelectActionMode;
	private SceneAdapter sceneAdapter;
	private ArrayList<Scene> sceneList;
	private Scene sceneToEdit;
	private SceneRenamedReceiver sceneRenamedReceiver;
	private SceneListChangedReceiver sceneListChangedReceiver;
	private SceneListInitReceiver sceneListInitReceiver;
	private SceneListTouchActionUpReceiver sceneListTouchActionUpReceiver;

	private ActionMode actionMode;
	private View selectAllActionModeButton;
	private boolean isRenameActionMode;
	private boolean selectAll = true;
	public boolean lockBackButtonForAsync = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View sceneListFragment = inflater.inflate(R.layout.fragment_scenes_list, container, false);
		sceneListFragment.findViewById(R.id.sceneList_headline).setVisibility(View.VISIBLE);
		return sceneListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());
		if (savedInstanceState != null) {
			sceneToEdit = (Scene) savedInstanceState.get(BUNDLE_ARGUMENTS_SCENE_TO_EDIT);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SCENE_TO_EDIT, sceneToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();

		getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentProject().getName());
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.progress_bar_activity_project).setVisibility(View.GONE);

		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (BackPackListManager.getInstance().isBackpackEmpty()) {
			BackPackListManager.getInstance().loadBackpack();
		}

		StorageHandler.getInstance().fillChecksumContainer();

		if (sceneRenamedReceiver == null) {
			sceneRenamedReceiver = new SceneRenamedReceiver();
		}

		if (sceneListChangedReceiver == null) {
			sceneListChangedReceiver = new SceneListChangedReceiver();
		}

		if (sceneListInitReceiver == null) {
			sceneListInitReceiver = new SceneListInitReceiver();
		}

		if (sceneListTouchActionUpReceiver == null) {
			sceneListTouchActionUpReceiver = new SceneListTouchActionUpReceiver();
		}

		IntentFilter intentFilterSceneRenamed = new IntentFilter(ScriptActivity.ACTION_SCENE_RENAMED);
		getActivity().registerReceiver(sceneRenamedReceiver, intentFilterSceneRenamed);

		IntentFilter intentFilterSceneListChanged = new IntentFilter(ScriptActivity.ACTION_SCENE_LIST_CHANGED);
		getActivity().registerReceiver(sceneListChangedReceiver, intentFilterSceneListChanged);

		IntentFilter intentFilterSceneListInit = new IntentFilter(ScriptActivity.ACTION_SCENE_LIST_INIT);
		getActivity().registerReceiver(sceneListInitReceiver, intentFilterSceneListInit);

		IntentFilter intentFilterSceneTouchUp = new IntentFilter(ScriptActivity.ACTION_SCENE_TOUCH_ACTION_UP);
		getActivity().registerReceiver(sceneListTouchActionUpReceiver, intentFilterSceneTouchUp);
	}

	@Override
	public void onPause() {
		super.onPause();

		getActivity().getIntent().removeExtra(Constants.PROJECTNAME_TO_LOAD);

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
		}

		if (sceneRenamedReceiver != null) {
			getActivity().unregisterReceiver(sceneRenamedReceiver);
		}

		if (sceneListChangedReceiver != null) {
			getActivity().unregisterReceiver(sceneListChangedReceiver);
		}

		if (sceneListInitReceiver != null) {
			getActivity().unregisterReceiver(sceneListInitReceiver);
		}

		if (sceneListTouchActionUpReceiver != null) {
			getActivity().unregisterReceiver(sceneListTouchActionUpReceiver);
		}
	}

	@Override
	public void onSceneChecked() {
		if (isRenameActionMode || actionMode == null) {
			return;
		}

		updateActionModeTitle();
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = sceneAdapter.getAmountOfCheckedScenes();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(multiSelectActionModeTitle);
		} else {
			String appendix = multipleItemAppendixMultiSelectActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixMultiSelectActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = multiSelectActionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = multiSelectActionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	@Override
	public void onSceneEdit(int position, View view) {
		if (isRenameActionMode) {
			sceneToEdit = sceneAdapter.getItem(position);
			showRenameDialog();
		} else {
			ProjectManager.getInstance().setCurrentScene(sceneAdapter.getItem(position));

			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
			startActivity(intent);
		}
	}

	@Override
	public void startCopyActionMode() {
		startActionMode(copyModeCallBack, false);
	}

	@Override
	public void startCommentOutActionMode() {
		// not possible here
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
			if (sceneAdapter.getCount() == 1) {
				if (actionModeCallback.equals(copyModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.copy));
				} else if (actionModeCallback.equals(deleteModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
				} else if (actionModeCallback.equals(renameModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.rename));
				}
			} else {
				actionMode = getActivity().startActionMode(actionModeCallback);
				BottomBar.hideBottomBar(getActivity());
				isRenameActionMode = isRenameMode;
			}
		}
	}

	@Override
	public void handleAddButton() {
		//handled in ProjectActivity
	}

	@Override
	public void handleCheckBoxClick(View view) {
		int position = getListView().getPositionForView(view);
		getListView().setItemChecked(position, ((CheckBox) view.findViewById(R.id.scene_checkbox)).isChecked());
	}

	public boolean copyScene() {
		ProjectManager projectManager = ProjectManager.getInstance();

		String sceneName = getSceneName(sceneToEdit.getName().concat(getString(R.string.copy_sprite_name_suffix)), 0);
		File sourceScene = new File(Utils.buildScenePath(projectManager.getCurrentProject().getName(), sceneToEdit.getName()));
		File targetScene = new File(Utils.buildScenePath(projectManager.getCurrentProject().getName(), sceneName));
		try {
			StorageHandler.copyDirectory(targetScene, sourceScene);
		} catch (IOException e) {
			Log.e(TAG, "Error while copying Scene files!", e);
			return false;
		}
		Scene copiedScene = sceneToEdit.clone();
		if (copiedScene == null) {
			return false;
		}

		copiedScene.setSceneName(sceneName);
		copiedScene.setProject(projectManager.getCurrentProject());

		projectManager.addScene(copiedScene);
		projectManager.setCurrentScene(copiedScene);

		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_SCENE_LIST_CHANGED));
		Log.d(TAG, "copiedScene: " + copiedScene.getName());
		return true;
	}

	@Override
	public void showRenameDialog() {
		RenameSceneDialog dialog = RenameSceneDialog.newInstance(sceneToEdit.getName());
		dialog.show(getFragmentManager(), RenameSceneDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showDeleteDialog() {
	}

	private void showConfirmDeleteDialog() {
		int titleId;
		if (sceneAdapter.getAmountOfCheckedScenes() == 1) {
			titleId = R.string.dialog_confirm_delete_scene_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_scenes_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_object_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedScenes();
				clearCheckedScenesAndEnableButtons();
				checkSceneCountAfterDeletion();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedScenesAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void checkSceneCountAfterDeletion() {
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject().getSceneList().size() == 0) {
			Scene emptyScene = new Scene(getActivity(), String.format(getString(R.string.default_scene_name), 1), projectManager.getCurrentProject());
			projectManager.getCurrentProject().addScene(emptyScene);
			projectManager.setCurrentScene(emptyScene);
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
			getActivity().finish();
			startActivity(intent);
		} else if (projectManager.getCurrentProject().getSceneList().size() == 1) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
			getActivity().finish();
			startActivity(intent);
		}
	}

	public boolean deleteScene() {
		ProjectManager projectManager = ProjectManager.getInstance();
		try {
			projectManager.deleteScene(sceneToEdit.getProject().getName(), sceneToEdit.getName());
		} catch (IOException e) {
			Log.e(TAG, "Delete scene Exception: ", e);
			return false;
		}

		if (projectManager.getCurrentScene() != null && projectManager.getCurrentScene().equals(sceneToEdit)) {
			projectManager.setCurrentScene(projectManager.getCurrentProject().getDefaultScene());
		}
		projectManager.getCurrentProject().getSceneList().remove(sceneToEdit);
		projectManager.getCurrentProject().getSceneOrder().remove(sceneToEdit.getName());

		return true;
	}

	private void deleteCheckedScenes() {
		int numDeleted = 0;
		boolean success = true;
		for (int position : sceneAdapter.getCheckedScenes()) {
			sceneToEdit = (Scene) getListView().getItemAtPosition(position - numDeleted);
			success &= deleteScene();
			numDeleted++;
		}

		if (!success) {
			showError(R.string.error_scene_not_deleted);
		} else {
			ProjectManager.getInstance().saveProject(getActivity());
		}
	}

	private void clearCheckedScenesAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		sceneAdapter.clearCheckedScenes();

		actionMode = null;
		actionModeActive = false;

		BottomBar.showBottomBar(getActivity());
	}

	@Override
	public int getSelectMode() {
		return sceneAdapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		sceneAdapter.setSelectMode(selectMode);
		sceneAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean getShowDetails() {
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
	}

	private void addSelectAllActionModeButton(final ActionMode mode, Menu menu) {
		selectAll = true;
		selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			CapitalizedTextView selectAllView = (CapitalizedTextView) selectAllActionModeButton.findViewById(R.id.select_all);

			@Override
			public void onClick(View view) {
				if (selectAll) {
					int startPosition = 0;
					while (startPosition < sceneList.size()) {
						sceneAdapter.addCheckedScene(startPosition);
						startPosition++;
					}
					sceneAdapter.notifyDataSetChanged();
					onSceneChecked();
					selectAll = false;
					selectAllView.setText(R.string.deselect_all);
				} else {
					sceneAdapter.clearCheckedScenes();
					sceneAdapter.notifyDataSetChanged();
					onSceneChecked();
					selectAll = true;
					selectAllView.setText(R.string.select_all);
				}
			}
		});
	}

	@Override
	public void onBackpackSceneComplete(boolean startBackpackActivity, boolean success) {
		if (!success) {
			showError(R.string.error_scene_backpack);
		} else if (!sceneAdapter.getCheckedScenes().isEmpty() && startBackpackActivity) {
			switchToBackPack();
		}
		clearCheckedScenesAndEnableButtons();
		lockBackButtonForAsync = false;
	}

	public void switchToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_SCENES);
		startActivity(intent);
	}

	private class SceneRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SCENE_RENAMED)) {
				String newSceneName = intent.getExtras().getString(RenameSceneDialog.EXTRA_NEW_SCENE_NAME);
				List<String> sceneOrder = ProjectManager.getInstance().getCurrentProject().getSceneOrder();
				int pos = sceneOrder.indexOf(sceneToEdit.getName());
				ProjectManager.getInstance().getCurrentProject().getSceneOrder().set(pos, newSceneName);
				sceneToEdit.rename(newSceneName, getActivity(), true);
				sceneAdapter.notifyDataSetChanged();
			}
		}
	}

	private class SceneListChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SCENE_LIST_CHANGED)) {
				sceneAdapter.notifyDataSetChanged();
				final ListView listView = getListView();
				listView.post(new Runnable() {
					@Override
					public void run() {
						listView.setSelection(listView.getCount() - 1);
					}
				});
			}
		}
	}

	private class SceneListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SCENE_LIST_INIT)) {
				sceneAdapter.notifyDataSetChanged();
			}
		}
	}

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeActive = true;

			multiSelectActionModeTitle = getString(R.string.delete);
			singleItemAppendixMultiSelectActionMode = getString(R.string.scene);
			multipleItemAppendixMultiSelectActionMode = getString(R.string.scenes);

			mode.setTitle(multiSelectActionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (sceneAdapter.getAmountOfCheckedScenes() == 0) {
				clearCheckedScenesAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
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
			actionModeActive = true;
			isRenameActionMode = true;
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			isRenameActionMode = false;
			Set<Integer> checkedScenes = sceneAdapter.getCheckedScenes();
			Iterator<Integer> iterator = checkedScenes.iterator();
			if (iterator.hasNext()) {
				int position = iterator.next();
				sceneToEdit = (Scene) getListView().getItemAtPosition(position);
				showRenameDialog();
			}
			clearCheckedScenesAndEnableButtons();
		}
	};

	private ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeActive = true;

			multiSelectActionModeTitle = getString(R.string.copy);
			singleItemAppendixMultiSelectActionMode = getString(R.string.scene);
			multipleItemAppendixMultiSelectActionMode = getString(R.string.scenes);

			mode.setTitle(multiSelectActionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			List<Scene> toCopy = new ArrayList<>();
			for (int position : sceneAdapter.getCheckedScenes()) {
				toCopy.add((Scene) getListView().getItemAtPosition(position));
			}
			copySceneAsynchronous(toCopy, getActivity());
			clearCheckedScenesAndEnableButtons();
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

			multiSelectActionModeTitle = getString(R.string.backpack);
			singleItemAppendixMultiSelectActionMode = getString(R.string.scene);
			multipleItemAppendixMultiSelectActionMode = getString(R.string.scenes);

			mode.setTitle(multiSelectActionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			List<Scene> sceneListToBackpack = new ArrayList<>();
			for (Integer position : sceneAdapter.getCheckedItems()) {
				sceneToEdit = (Scene) getListView().getItemAtPosition(position);
				sceneListToBackpack.add(sceneToEdit);
			}

			boolean sceneAlreadyInBackpack = BackPackSceneController.getInstance().checkScenesReplaceInBackpack(sceneListToBackpack);

			if (!sceneListToBackpack.isEmpty()) {
				if (!sceneAlreadyInBackpack) {
					showProgressCircle();
					backPackAsynchronous(sceneListToBackpack, getActivity());
				} else {
					BackPackSceneController.getInstance().setOnBackpackSceneCompleteListener(ScenesListFragment.this);
					ScenesListFragment fragment = ((ProjectActivity) getActivity()).getScenesListFragment();
					BackPackSceneController.getInstance().showBackPackReplaceDialog(sceneListToBackpack, fragment);
				}
			} else {
				clearCheckedScenesAndEnableButtons();
			}
		}
	};

	private class SceneListTouchActionUpReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SCENE_TOUCH_ACTION_UP)) {
				((DynamicListView) getListView()).notifyListItemTouchActionUp();
			}
		}
	}

	private void copySceneAsynchronous(final List<Scene> scenesToCopy, final Activity activity) {
		final View footerView = View.inflate(activity, R.layout.activity_scenes_list_item, null);
		footerView.findViewById(R.id.activity_scenes_list_item_image_view).setVisibility(View.GONE);
		footerView.findViewById(R.id.activity_scenes_list_item_text_view).setVisibility(View.GONE);
		footerView.findViewById(R.id.activity_scenes_list_item_spinner).setVisibility(View.VISIBLE);
		getListView().addFooterView(footerView);
		final ListView listView = getListView();
		listView.post(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});
		Runnable r = new Runnable() {
			@Override
			public void run() {
				lockBackButtonForAsync = true;
				boolean success = true;
				for (Scene scene : scenesToCopy) {
					sceneToEdit = scene;
					success &= copyScene();
				}
				final boolean finalSuccess = success;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getListView().removeFooterView(footerView);
						sceneAdapter.notifyDataSetChanged();
						if (!finalSuccess) {
							showError(R.string.error_scene_not_copied);
						} else {
							ProjectManager.getInstance().saveProject(getActivity());
						}
						lockBackButtonForAsync = false;
					}
				});
			}
		};
		(new Thread(r)).start();
	}

	private void backPackAsynchronous(final List<Scene> scenes, final Activity activity) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				lockBackButtonForAsync = true;
				boolean success = BackPackSceneController.getInstance().backpackScenes(scenes);
				final boolean finalSuccess = success;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onBackpackSceneComplete(true, finalSuccess);
					}
				});
			}
		};
		(new Thread(r)).start();
	}

	private void initListeners() {
		sceneList = (ArrayList<Scene>) ProjectManager.getInstance().getCurrentProject().getSceneList();
		((DynamicListView) getListView()).setDataList(sceneList);
		sceneAdapter = new SceneAdapter(getActivity(), R.layout.activity_scenes_list_item,
				R.id.activity_scenes_list_item_text_view, sceneList);

		sceneAdapter.setOnSceneEditListener(this);
		setListAdapter(sceneAdapter);
		getListView().setTextFilterEnabled(true);
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
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

	public void showProgressCircle() {
		ProgressBar progressCircle = (ProgressBar) getActivity().findViewById(R.id.progress_bar_activity_project);
		progressCircle.setVisibility(View.VISIBLE);
		progressCircle.bringToFront();
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
		BottomBar.showBottomBar(getActivity());
	}

	private static String getSceneName(String name, int nextNumber) {
		String newName;
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			if (scene.getName().equals(newName)) {
				return getSceneName(name, ++nextNumber);
			}
		}
		return newName;
	}
}
