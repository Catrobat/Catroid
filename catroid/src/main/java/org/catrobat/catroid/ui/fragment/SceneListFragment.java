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
import android.widget.ProgressBar;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SceneListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.DividerUtil;
import org.catrobat.catroid.utils.SnackBarUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SceneListFragment extends ListActivityFragment implements CheckBoxListAdapter.ListItemClickHandler<Scene> {

	public static final String TAG = SceneListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SCENE_TO_EDIT = "scene_to_edit";

	private SceneListAdapter sceneAdapter;
	private DragAndDropListView listView;

	private Scene sceneToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View sceneListFragment = inflater.inflate(R.layout.fragment_scenes_list, container, false);
		listView = (DragAndDropListView) sceneListFragment.findViewById(android.R.id.list);
		sceneListFragment.findViewById(R.id.sceneList_headline).setVisibility(View.VISIBLE);

		TextSizeUtil.enlargeViewGroup((ViewGroup) sceneListFragment);
		SnackBarUtil.showHintSnackBar(getActivity(), R.string.hint_scenes);

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

		initializeList();
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
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SCENE_TO_EDIT, sceneToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();

		getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentProject().getName());
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.progress_bar_activity_project).setVisibility(View.GONE);

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (BackPackListManager.getInstance().isBackpackEmpty()) {
			BackPackListManager.getInstance().loadBackpack();
		}

		StorageHandler.getInstance().fillChecksumContainer();

		ProjectManager.getInstance().setCurrentScene(ProjectManager.getInstance().getCurrentProject().getDefaultScene());

		DividerUtil.setDivider(getActivity(), getListView());
	}

	@Override
	public void onPause() {
		super.onPause();

		getActivity().getIntent().removeExtra(Constants.PROJECTNAME_TO_LOAD);

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
		}
	}

	@Override
	public void handleOnItemClick(int position, View view, Scene scene) {
		if (isActionModeActive()) {
			return;
		}
		ProjectManager.getInstance().setCurrentScene(scene);

		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
		startActivity(intent);
	}

	@Override
	protected void startActionMode(ActionMode.Callback actionModeCallback) {
		if (isActionModeActive()) {
			return;
		}
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
			isRenameActionMode = actionModeCallback.equals(renameModeCallBack);
		}
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

	public void switchToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_SCENES);
		startActivity(intent);
	}

	public void showDeleteDialog() {
		int titleId;
		if (sceneAdapter.getCheckedItems().size() == 1) {
			titleId = R.string.dialog_confirm_delete_scene_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_scenes_title;
		}
		showDeleteDialog(titleId);
	}

	protected void deleteCheckedItems() {
		boolean success = true;
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			success &= deleteScene();
		}

		if (success) {
			ProjectManager.getInstance().saveProject(getActivity());
			checkSceneCountAfterDeletion();
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

	protected void copyCheckedItems() {
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

	@Override
	public void showRenameDialog() {
		sceneToEdit = sceneAdapter.getCheckedItems().get(0);
		RenameItemDialog dialog = RenameItemDialog.newInstance(R.string.rename_scene_dialog, R.string.scene_name,
				R.string.error_scene_exists, sceneToEdit.getName());
		dialog.setTargetFragment(this, 0);
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

	protected void packCheckedItems() {
		List<Scene> sceneListToBackpack = sceneAdapter.getCheckedItems();
		boolean sceneAlreadyInBackpack = BackPackSceneController.getInstance().checkScenesReplaceInBackpack(sceneListToBackpack);

		if (!sceneAlreadyInBackpack) {
			showProgressCircle();
			packScenes(sceneListToBackpack);
		} else {
			SceneListFragment fragment = ((ProjectActivity) getActivity()).getSceneListFragment();
			BackPackSceneController.getInstance().showBackPackReplaceDialog(sceneListToBackpack, fragment);
		}
	}

	public void packScenes(List<Scene> sceneList) {
		boolean success = BackPackSceneController.getInstance().backpackScenes(sceneList);
		clearCheckedItems();

		if (success) {
			switchToBackPack();
			return;
		}

		showError(R.string.error_scene_backpack);
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
