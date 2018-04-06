/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.backpack;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SceneAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.SceneController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

public class BackpackSceneFragment extends BackpackRecyclerViewFragment<Scene> {

	public static final String TAG = BackpackSceneFragment.class.getSimpleName();

	private SceneController sceneController = new SceneController();

	@Override
	protected void initializeAdapter() {
		List<Scene> items = BackPackListManager.getInstance().getBackPackedScenes();
		sharedPreferenceDetailsKey = "showDetailsSceneList";
		hasDetails = true;
		adapter = new SceneAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<Scene> selectedItems) {
		setShowProgressBar(true);
		try {
			for (Scene item : selectedItems) {
				Project dstProject = ProjectManager.getInstance().getCurrentProject();
				dstProject.addScene(sceneController.unpack(item, dstProject));
			}
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_scenes,
					selectedItems.size(),
					selectedItems.size()));
			getActivity().finish();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			finishActionMode();
		}
	}

	@Override
	protected int getDeleteAlertTitle() {
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

		BackPackListManager.getInstance().saveBackpack();
		finishActionMode();
		if (adapter.getItems().isEmpty()) {
			getActivity().finish();
		}
	}

	@Override
	protected String getItemName(Scene item) {
		return item.getName();
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_scenes_title,
				selectedItemCnt,
				selectedItemCnt));
	}
}

