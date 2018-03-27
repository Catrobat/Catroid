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
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SoundAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.SoundController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

public class BackpackSoundFragment extends BackpackRecyclerViewFragment<SoundInfo> {

	public static final String TAG = BackpackSoundFragment.class.getSimpleName();

	private SoundController soundController = new SoundController();

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = "showDetailsSoundList";
		hasDetails = true;
		List<SoundInfo> items = BackPackListManager.getInstance().getBackPackedSounds();
		adapter = new SoundAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<SoundInfo> selectedItems) {
		setShowProgressBar(true);
		try {
			for (SoundInfo item : selectedItems) {
				Sprite dstSprite = ProjectManager.getInstance().getCurrentSprite();
				dstSprite.getSoundList().add(soundController.unpack(item,
						ProjectManager.getInstance().getCurrentScene(),
						dstSprite));
			}
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_sounds,
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
		return R.plurals.delete_sounds;
	}

	@Override
	protected void deleteItems(List<SoundInfo> selectedItems) {
		setShowProgressBar(true);
		for (SoundInfo item : selectedItems) {
			try {
				soundController.deleteFromBackpack(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_sounds,
				selectedItems.size(),
				selectedItems.size()));

		BackPackListManager.getInstance().saveBackpack();
		finishActionMode();
		if (adapter.getItems().isEmpty()) {
			getActivity().finish();
		}
	}

	@Override
	public String getItemName(SoundInfo item) {
		return item.getName();
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_sounds_title,
				selectedItemCnt,
				selectedItemCnt));
	}
}
