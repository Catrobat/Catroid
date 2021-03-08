/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.util.Log;
import android.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SceneAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SceneController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;

import static org.catrobat.catroid.common.Constants.Z_INDEX_BACKGROUND;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SCENES_PREFERENCE_KEY;

public class SceneListFragment extends RecyclerViewFragment<Scene> implements ProjectLoadTask.ProjectLoadListener {

	public static final String TAG = SceneListFragment.class.getSimpleName();

	private SceneController sceneController = new SceneController();

	@Override
	public void onResume() {
		super.onResume();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		if (currentProject.getSceneList().size() < 2) {
			ProjectManager.getInstance().setCurrentlyEditedScene(currentProject.getDefaultScene());
			switchToSpriteListFragment();
		}

		ProjectManager.getInstance().setCurrentlyEditedScene(currentProject.getDefaultScene());
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentProject.getName());
	}

	private void switchToSpriteListFragment() {
		getFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, new SpriteListFragment(), SpriteListFragment.TAG)
				.commit();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.new_group).setVisible(false);
		menu.findItem(R.id.new_scene).setVisible(false);
	}

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = SHOW_DETAILS_SCENES_PREFERENCE_KEY;
		List<Scene> items = ProjectManager.getInstance().getCurrentProject().getSceneList();
		adapter = new SceneAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void packItems(List<Scene> selectedItems) {
		setShowProgressBar(true);
		int packedItemCnt = 0;

		for (Scene item : selectedItems) {
			try {
				BackpackListManager.getInstance().getScenes().add(sceneController.pack(item));
				BackpackListManager.getInstance().saveBackpack();
				packedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (packedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.packed_scenes,
					packedItemCnt,
					packedItemCnt));
			switchToBackpack();
		}

		finishActionMode();
	}

	@Override
	protected boolean isBackpackEmpty() {
		return BackpackListManager.getInstance().getScenes().isEmpty();
	}

	@Override
	protected void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCENES);
		startActivity(intent);
	}

	@Override
	protected void copyItems(List<Scene> selectedItems) {
		setShowProgressBar(true);
		int copiedItemCnt = 0;

		for (Scene item : selectedItems) {
			try {
				adapter.add(sceneController.copy(item, ProjectManager.getInstance().getCurrentProject()));
				copiedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (copiedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_scenes,
					copiedItemCnt,
					copiedItemCnt));
		}

		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_scenes;
	}

	@Override
	protected void deleteItems(List<Scene> selectedItems) {
		setShowProgressBar(true);

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
		finishActionMode();

		if (adapter.getItems().isEmpty()) {
			createEmptySceneWithDefaultName();
		}

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject.getSceneList().size() < 2) {
			ProjectManager.getInstance().setCurrentlyEditedScene(currentProject.getDefaultScene());
			switchToSpriteListFragment();
		}
	}

	private void createEmptySceneWithDefaultName() {
		setShowProgressBar(true);

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene scene = new Scene(getString(R.string.default_scene_name, 1), currentProject);

		Sprite backgroundSprite = new Sprite(getString(R.string.background));
		backgroundSprite.look.setZIndex(Z_INDEX_BACKGROUND);
		scene.addSprite(backgroundSprite);

		adapter.add(scene);
		setShowProgressBar(false);
	}

	@Override
	protected int getRenameDialogTitle() {
		return R.string.rename_scene_dialog;
	}

	@Override
	protected int getRenameDialogHint() {
		return R.string.scene_name_label;
	}

	@Override
	public void renameItem(Scene item, String name) {
		if (!item.getName().equals(name)) {
			if (sceneController.rename(item, name)) {
				Project currentProject = ProjectManager.getInstance().getCurrentProject();
				new ProjectSaveTask(currentProject, getContext())
						.execute();
				new ProjectLoadTask(currentProject.getDirectory(), getContext())
						.setListener(this)
						.execute();
			} else {
				ToastUtil.showError(getActivity(), R.string.error_rename_scene);
			}
		}
		finishActionMode();
	}

	@Override
	public void onItemClick(Scene item) {
		if (actionModeType == RENAME) {
			super.onItemClick(item);
			return;
		}
		if (actionModeType == NONE) {
			ProjectManager.getInstance().setCurrentlyEditedScene(item);
			getFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, new SpriteListFragment(), SpriteListFragment.TAG)
					.addToBackStack(SpriteListFragment.TAG)
					.commit();
		}
	}

	@Override
	public void onLoadFinished(boolean success) {
		if (!success) {
			ToastUtil.showError(getActivity(), R.string.error_load_project);
			return;
		}
		adapter.setItems(ProjectManager.getInstance().getCurrentProject().getSceneList());
	}
}
