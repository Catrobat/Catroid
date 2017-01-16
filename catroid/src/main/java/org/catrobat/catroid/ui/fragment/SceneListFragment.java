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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.SpriteListActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SceneListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.ui.dialogs.NewSceneDialog;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.dialogs.ReplaceInBackPackDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SceneListFragment extends ListActivityFragment implements CheckBoxListAdapter.ListItemClickHandler<Scene>,
		NewSceneDialog.NewSceneInterface {

	public static final String TAG = SceneListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SCENE_TO_EDIT = "scene_to_edit";

	private SceneListAdapter sceneAdapter;
	private Scene sceneToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scenes);
		return inflater.inflate(R.layout.fragment_scene_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		checkSceneCountAndSwitchToSpriteListActivity();

		itemIdentifier = R.plurals.scenes;
		deleteDialogTitle = R.plurals.dialog_delete_scene;
		replaceDialogMessage = R.plurals.dialog_replace_scene;

		if (savedInstanceState != null) {
			sceneToEdit = (Scene) savedInstanceState.get(BUNDLE_ARGUMENTS_SCENE_TO_EDIT);
		}

		initializeList();
	}

	private void initializeList() {
		List<Scene> sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();

		sceneAdapter = new SceneListAdapter(getActivity(), R.layout.list_item, sceneList);

		DragAndDropListView listView = (DragAndDropListView) getListView();

		setListAdapter(sceneAdapter);
		sceneAdapter.setListItemClickHandler(this);
		sceneAdapter.setListItemLongClickHandler(listView);
		sceneAdapter.setListItemCheckHandler(this);
		listView.setAdapterInterface(sceneAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SCENE_TO_EDIT, sceneToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentProject().getName());
		ProjectManager.getInstance().setCurrentScene(ProjectManager.getInstance().getCurrentProject().getDefaultScene());
	}

	@Override
	public void onPause() {
		super.onPause();
		saveCurrentProject();
	}

	@Override
	protected void startActionMode(ActionMode.Callback actionModeCallback) {
		if (isActionModeActive()) {
			return;
		}
		if (sceneAdapter.getCount() == 1) {
			if (actionModeCallback.equals(deleteModeCallBack)) {
				ToastUtil.showError(getActivity(), R.string.nothing_to_delete);
			} else if (actionModeCallback.equals(copyModeCallBack)) {
				ToastUtil.showError(getActivity(), R.string.nothing_to_copy);
			} else if (actionModeCallback.equals(renameModeCallBack)) {
				ToastUtil.showError(getActivity(), R.string.nothing_to_rename);
			} else if (actionModeCallback.equals(backPackModeCallBack)) {
				ToastUtil.showError(getActivity(), R.string.nothing_to_backpack_and_unpack);
			}
		} else {
			actionMode = getActivity().startActionMode(actionModeCallback);
			BottomBar.hideBottomBar(getActivity());
			isRenameActionMode = actionModeCallback.equals(renameModeCallBack);
		}
	}

	private void checkSceneCountAndSwitchToSpriteListActivity() {
		ProjectManager projectManager = ProjectManager.getInstance();

		if (projectManager.getCurrentProject().getSceneList().isEmpty()) {
			Scene emptyScene = new Scene(getActivity(), getString(R.string.default_scene_name, 1), projectManager.getCurrentProject());
			projectManager.getCurrentProject().addScene(emptyScene);
			projectManager.setCurrentScene(emptyScene);
		}

		if(!projectManager.getCurrentProject().isScenesEnabled()) {
			Intent intent = new Intent(getActivity(), SpriteListActivity.class);
			startActivity(intent);
			getActivity().finish();
		}
	}

	@Override
	public void handleAddButton() {
		String defaultSceneName = Utils.getUniqueSceneName(getString(R.string.default_scene_name), false);
		NewSceneDialog dialog = new NewSceneDialog(defaultSceneName, this);
		dialog.show(getFragmentManager(), NewSceneDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void addAndOpenNewScene(String sceneName) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		Scene scene = new Scene(getActivity(), sceneName, currentProject);
		currentProject.addScene(scene);
		saveCurrentProject();

		handleOnItemClick(sceneAdapter.getPosition(scene), null, scene);
	}

	@Override
	public void handleOnItemClick(int position, View view, Scene scene) {
		if (isActionModeActive()) {
			return;
		}

		ProjectManager.getInstance().setCurrentScene(scene);
		Intent intent = new Intent(getActivity(), SpriteListActivity.class);
		startActivity(intent);
	}

	@Override
	public void deleteCheckedItems() {
		boolean success = true;
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			success &= deleteScene();
		}

		if (success) {
			ProjectManager.getInstance().saveProject(getActivity());
			checkSceneCountAndSwitchToSpriteListActivity();
		} else {
			ToastUtil.showError(getActivity(), R.string.error_delete_scene);
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

	protected void copyCheckedItems() {
		boolean success = true;
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			success &= copyScene();
		}

		if (success) {
			ProjectManager.getInstance().saveProject(getActivity());
		} else {
			ToastUtil.showError(getActivity(), R.string.error_copy_scene);
		}

		clearCheckedItems();
	}

	private boolean copyScene() {
		ProjectManager projectManager = ProjectManager.getInstance();

		String sceneName = sceneToEdit.getName().concat(getString(R.string.copied_item_suffix));
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

	@Override
	public void showRenameDialog() {
		sceneToEdit = sceneAdapter.getCheckedItems().get(0);
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_scene_dialog, R.string.scene_name, sceneToEdit.getName(), this);
		dialog.show(getFragmentManager(), RenameItemDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean itemNameExists(String newName) {
		ProjectManager projectManager = ProjectManager.getInstance();
		return projectManager.sceneExists(newName);
	}

	public void renameItem(String newName) {
		List<String> sceneOrder = ProjectManager.getInstance().getCurrentProject().getSceneOrder();
		int pos = sceneOrder.indexOf(sceneToEdit.getName());
		ProjectManager.getInstance().getCurrentProject().getSceneOrder().set(pos, newName);
		sceneToEdit.rename(newName, getActivity(), true);
		clearCheckedItems();
		sceneAdapter.notifyDataSetChanged();
	}

	@Override
	public void showReplaceItemsInBackPackDialog() {
		if (!BackPackSceneController.existsInBackPack(sceneAdapter.getCheckedItems())) {
			packCheckedItems();
			return;
		}

		String name = sceneAdapter.getCheckedItems().get(0).getName();
		ReplaceInBackPackDialog dialog = new ReplaceInBackPackDialog(replaceDialogMessage, name, this);
		dialog.show(getFragmentManager(), ReplaceInBackPackDialog.DIALOG_FRAGMENT_TAG);
	}

	public void packCheckedItems() {
		setProgressCircleVisibility(true);
		boolean success = BackPackSceneController.backpack(sceneAdapter.getCheckedItems());
		clearCheckedItems();

		if (success) {
			changeToBackPack();
			return;
		}

		setProgressCircleVisibility(false);
		ToastUtil.showError(getActivity(), R.string.error_backpack_scene);
	}

	@Override
	protected boolean isBackPackEmpty() {
		return BackPackListManager.getInstance().getBackPackedScenes().isEmpty();
	}

	@Override
	protected void changeToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.FRAGMENT, BackPackSceneListFragment.class);
		startActivity(intent);
	}
}
