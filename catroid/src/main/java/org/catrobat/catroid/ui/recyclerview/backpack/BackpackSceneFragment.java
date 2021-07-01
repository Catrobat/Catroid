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
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SceneAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.SceneController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import androidx.annotation.PluralsRes;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SCENES_PREFERENCE_KEY;

public class BackpackSceneFragment extends BackpackRecyclerViewFragment<Scene> {

	public static final String TAG = BackpackSceneFragment.class.getSimpleName();

	private SceneController sceneController = new SceneController();

	@Override
	protected void initializeAdapter() {
		List<Scene> items = BackpackListManager.getInstance().getScenes();
		sharedPreferenceDetailsKey = SHOW_DETAILS_SCENES_PREFERENCE_KEY;
		hasDetails = true;
		adapter = new SceneAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<Scene> selectedItems) {
		setShowProgressBar(true);
		Project destinationProject = ProjectManager.getInstance().getCurrentProject();
		int unpackedItemCnt = 0;

		for (Scene item : selectedItems) {
			try {
				destinationProject.addScene(sceneController.unpack(item, destinationProject));
				unpackedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (unpackedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_scenes,
					unpackedItemCnt,
					unpackedItemCnt));
			getActivity().finish();
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

		BackpackListManager.getInstance().saveBackpack();
		finishActionMode();
		if (adapter.getItems().isEmpty()) {
			getActivity().finish();
		}
	}

	@Override
	protected String getItemName(Scene item) {
		return item.getName();
	}
}

