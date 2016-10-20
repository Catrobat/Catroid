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
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SceneListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.RenameSceneDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScenesListFragment extends CheckBoxListFragment implements CheckBoxListAdapter.ListItemClickHandler<Scene>,
		BackPackSceneController.OnBackpackSceneCompleteListener, ListItemActionsInterface{

	public static final String TAG = ScenesListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SCENE_TO_EDIT = "scene_to_edit";

	private SceneListAdapter sceneAdapter;
	private DragAndDropListView listView;

	private Scene sceneToEdit;

	private SceneRenamedReceiver sceneRenamedReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View sceneListFragment = inflater.inflate(R.layout.fragment_scenes_list, container, false);
		listView = (DragAndDropListView) sceneListFragment.findViewById(android.R.id.list);
		sceneListFragment.findViewById(R.id.sceneList_headline).setVisibility(View.VISIBLE);

		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scenes);

		return sceneListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());

		singleItemTitle = getString(R.string.scene);
		multipleItemsTitle = getString(R.string.scenes);

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
		initializeList();
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

		IntentFilter intentFilterSceneRenamed = new IntentFilter(ScriptActivity.ACTION_SCENE_RENAMED);
		getActivity().registerReceiver(sceneRenamedReceiver, intentFilterSceneRenamed);

		ProjectManager.getInstance().setCurrentScene(ProjectManager.getInstance().getCurrentProject().getDefaultScene());
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
	}

	private void initializeList() {
		List<Scene> sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();

		sceneAdapter = new SceneListAdapter(getActivity(), R.layout.list_item, sceneList);

		setListAdapter(sceneAdapter);
		sceneAdapter.setListItemClickHandler(this);
		sceneAdapter.setListItemLongClickHandler(listView);
		sceneAdapter.setListItemCheckHandler(this);
		listView.setAdapterInterface(sceneAdapter);
	}

	@Override
	public void handleOnItemClick(Scene scene) {
		if (isActionModeActive()) {
			return;
		}
		ProjectManager.getInstance().setCurrentScene(scene);

		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
		startActivity(intent);
	}

	public void startCopyActionMode() {
		startActionMode(copyModeCallBack, false);
	}

	public void startRenameActionMode() {
		startActionMode(renameModeCallBack, true);
	}

	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack, false);
	}

	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack, false);
	}

	@Override
	public void handleAddButton() {

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

	public void showRenameDialog() {
		RenameSceneDialog dialog = RenameSceneDialog.newInstance(sceneToEdit.getName());
		dialog.show(getFragmentManager(), RenameSceneDialog.DIALOG_FRAGMENT_TAG);
	}

	public SceneListAdapter getAdapter() {
		return sceneAdapter;
	}

	public void showDeleteDialog() {
		int titleId;
		if (sceneAdapter.getCheckedItems().size() == 1) {
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
				clearCheckedItems();
				checkSceneCountAfterDeletion();
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
			public void onCancel(DialogInterface dialogInterface) {
				clearCheckedItems();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void checkSceneCountAfterDeletion() {
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject().getSceneList().size() == 0) {
			Scene emptyScene = new Scene(getActivity(), getString(R.string.default_scene_name, 1), projectManager
					.getCurrentProject());
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

	public int getSelectMode() {
		return sceneAdapter.getSelectMode();
	}

	public void setSelectMode(int selectMode) {
		sceneAdapter.setSelectMode(selectMode);
		sceneAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean getActionModeActive() {
		return isActionModeActive();
	}

	@Override
	public void setActionModeActive(boolean actionModeActive) {
		throw new UnsupportedOperationException("Refactor INTERFACE!");
	}

	public boolean getShowDetails() {
		return false;
	}

	public void setShowDetails(boolean showDetails) {
	}

	@Override
	public void onBackpackSceneComplete(boolean startBackpackActivity, boolean success) {
		if (!success) {
			showError(R.string.error_scene_backpack);
		} else if (!sceneAdapter.getCheckedItems().isEmpty() && startBackpackActivity) {
			switchToBackPack();
		}
		clearCheckedItems();
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

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeTitle = getString(R.string.delete);

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
			if (sceneAdapter.getCheckedItems().size() != 0) {
				showDeleteDialog();
			} else {
				clearCheckedItems();
			}
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

			actionModeTitle = getString(R.string.copy);

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
			copyCheckedScenes();
			clearCheckedItems();
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
			if(!sceneAdapter.getCheckedItems().isEmpty()) {
				sceneToEdit = sceneAdapter.getCheckedItems().get(0);
				showRenameDialog();
			}
			clearCheckedItems();
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

			actionModeTitle = getString(R.string.backpack);

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
			List<Scene> sceneListToBackpack = sceneAdapter.getCheckedItems();

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
				clearCheckedItems();
			}
		}
	};

	private void deleteCheckedScenes() {
		boolean success = true;
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			success &= deleteScene();
		}

		if (success) {
			ProjectManager.getInstance().saveProject(getActivity());
		} else {
			showError(R.string.error_scene_not_deleted);
		}
	}

	private boolean deleteScene() {
		ProjectManager projectManager = ProjectManager.getInstance();
		try {
			projectManager.deleteScene(sceneToEdit.getProject().getName(), sceneToEdit.getName());
		} catch (IOException e) {
			Log.e(TAG, "Error while deleting Scene: ", e);
			return false;
		}

		if (projectManager.getCurrentScene() != null && projectManager.getCurrentScene().equals(sceneToEdit)) {
			projectManager.setCurrentScene(projectManager.getCurrentProject().getDefaultScene());
		}
		projectManager.getCurrentProject().getSceneList().remove(sceneToEdit);
		projectManager.getCurrentProject().getSceneOrder().remove(sceneToEdit.getName());

		return true;
	}

	private void copyCheckedScenes() {
		boolean success = true;
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			success &= copyScene();
		}

		if (success) {
			ProjectManager.getInstance().saveProject(getActivity());
		} else {
			showError(R.string.error_scene_not_copied);
		}
	}

	private boolean copyScene() {
		ProjectManager projectManager = ProjectManager.getInstance();

		String sceneName = getNewValidSceneName(sceneToEdit.getName().concat(getString(R.string.copy_sprite_name_suffix)), 0);
		String projectName = projectManager.getCurrentProject().getName();
		File sourceScene = new File(Utils.buildScenePath(projectName, sceneToEdit.getName()));
		File targetScene = new File(Utils.buildScenePath(projectName, sceneName));

		try {
			StorageHandler.copyDirectory(targetScene, sourceScene);
		} catch (IOException e) {
			Log.e(TAG, "Error while copying Scene: ", e);
			return false;
		}

		Scene copiedScene = sceneToEdit.clone();

		if (copiedScene == null) {
			return false;
		}

		copiedScene.setSceneName(sceneName);
		copiedScene.setProject(projectManager.getCurrentProject());
		projectManager.addScene(copiedScene);

		return true;
	}

	private void backPackAsynchronous(final List<Scene> scenes, final Activity activity) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
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

	private static String getNewValidSceneName(String name, int nextNumber) {
		String newName;
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			if (scene.getName().equals(newName)) {
				return getNewValidSceneName(name, ++nextNumber);
			}
		}
		return newName;
	}
}
