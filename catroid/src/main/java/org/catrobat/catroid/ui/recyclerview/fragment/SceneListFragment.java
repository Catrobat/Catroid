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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SceneAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SceneController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSceneDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameItemDialog;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SceneListFragment extends RecyclerViewFragment<Scene> {

	public static final String TAG = SceneListFragment.class.getSimpleName();

	private SceneController sceneController = new SceneController();

	@Override
	public void onResume() {
		super.onResume();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		ProjectManager.getInstance().setCurrentScene(currentProject.getDefaultScene());
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentProject.getName());
	}

	@Override
	protected void initializeAdapter() {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scenes);
		List<Scene> items = ProjectManager.getInstance().getCurrentProject().getSceneList();
		adapter = new SceneAdapter(items);
		onAdapterReady();
	}

	@Override
	public void handleAddButton() {
		NewSceneDialog dialog = new NewSceneDialog(this, ProjectManager.getInstance().getCurrentProject());
		dialog.show(getFragmentManager(), NewSceneDialog.TAG);
	}

	@Override
	public void addItem(Scene item) {
		adapter.add(item);
	}

	@Override
	protected void packItems(List<Scene> selectedItems) {
		finishActionMode();
		try {
			for (Scene item : selectedItems) {
				sceneController.pack(item);
			}
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.packed_scenes,
					selectedItems.size(),
					selectedItems.size()));

			switchToBackpack();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	protected boolean isBackpackEmpty() {
		return BackPackListManager.getInstance().getBackPackedScenes().isEmpty();
	}

	@Override
	protected void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCENES);
		startActivity(intent);
	}

	@Override
	protected void copyItems(List<Scene> selectedItems) {
		finishActionMode();
		for (Scene item : selectedItems) {
			try {
				adapter.add(sceneController.copy(item, ProjectManager.getInstance().getCurrentProject()));
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_scenes,
				selectedItems.size(),
				selectedItems.size()));
	}

	@Override
	protected int getDeleteAlertTitle() {
		return R.plurals.delete_scenes;
	}

	@Override
	protected void deleteItems(List<Scene> selectedItems) {
		finishActionMode();
		for (Scene item : selectedItems) {
			try {
				sceneController.delete(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}

		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_scenes,
				selectedItems.size(),
				selectedItems.size()));

		if (adapter.getItems().isEmpty()) {
			createDefaultScene();
		}
	}

	private void createDefaultScene() {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		adapter.add(new Scene(getActivity(), getString(R.string.default_scene_name, 1), currentProject));
	}

	@Override
	protected void showRenameDialog(List<Scene> selectedItems) {
		String name = selectedItems.get(0).getName();
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_scene_dialog, R.string.scene_name, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}

	@Override
	public boolean isNameUnique(String name) {
		Set<String> scope = new HashSet<>();
		for (Scene item : adapter.getItems()) {
			scope.add(item.getName());
		}
		return !scope.contains(name);
	}

	@Override
	public void renameItem(String name) {
		adapter.getSelectedItems().get(0).rename(name, getActivity(), false);
		finishActionMode();
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_scenes_title,
				selectedItemCnt,
				selectedItemCnt));
	}

	@Override
	public void onItemClick(Scene item) {
		if (actionModeType == NONE) {
			ProjectManager.getInstance().setCurrentScene(item);
			getFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, new SpriteListFragment(), SpriteListFragment.TAG)
					.addToBackStack(SpriteListFragment.TAG)
					.commit();
		}
	}
}
