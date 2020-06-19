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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import androidx.annotation.PluralsRes;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SPRITES_PREFERENCE_KEY;

public class BackpackSpriteFragment extends BackpackRecyclerViewFragment<Sprite> {

	public static final String TAG = BackpackSpriteFragment.class.getSimpleName();

	private SpriteController spriteController = new SpriteController();

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = SHOW_DETAILS_SPRITES_PREFERENCE_KEY;
		hasDetails = true;
		List<Sprite> items = BackpackListManager.getInstance().getSprites();
		adapter = new SpriteAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<Sprite> selectedItems) {
		setShowProgressBar(true);
		Project dstProject = ProjectManager.getInstance().getCurrentProject();
		Scene dstScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		int unpackedItemCnt = 0;

		for (Sprite item : selectedItems) {
			try {
				dstScene.getSpriteList().add(spriteController.unpack(item, dstProject, dstScene));
				unpackedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (unpackedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_sprites,
					unpackedItemCnt,
					unpackedItemCnt));
			getActivity().finish();
		}

		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_sprites;
	}

	@Override
	protected void deleteItems(List<Sprite> selectedItems) {
		setShowProgressBar(true);
		for (Sprite item : selectedItems) {
			spriteController.delete(item);
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_sprites,
				selectedItems.size(),
				selectedItems.size()));

		BackpackListManager.getInstance().saveBackpack();
		finishActionMode();
		if (adapter.getItems().isEmpty()) {
			getActivity().finish();
		}
	}

	@Override
	protected String getItemName(Sprite item) {
		return item.getName();
	}
}
