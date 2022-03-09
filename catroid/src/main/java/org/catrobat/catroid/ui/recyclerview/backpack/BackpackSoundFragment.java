/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SoundAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.SoundController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import androidx.annotation.PluralsRes;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SOUNDS_PREFERENCE_KEY;
import static org.koin.java.KoinJavaComponent.inject;

public class BackpackSoundFragment extends BackpackRecyclerViewFragment<SoundInfo> {

	public static final String TAG = BackpackSoundFragment.class.getSimpleName();

	private SoundController soundController = new SoundController();

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = SHOW_DETAILS_SOUNDS_PREFERENCE_KEY;
		hasDetails = true;
		List<SoundInfo> items = BackpackListManager.getInstance().getBackpackedSounds();
		adapter = new SoundAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<SoundInfo> selectedItems) {
		setShowProgressBar(true);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		Sprite destinationSprite = projectManager.getCurrentSprite();
		int unpackedItemCnt = 0;

		for (SoundInfo item : selectedItems) {
			try {
				destinationSprite.getSoundList().add(soundController.unpack(item,
						projectManager.getCurrentlyEditedScene(),
						destinationSprite));
				unpackedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (unpackedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_sounds,
					unpackedItemCnt,
					unpackedItemCnt));
			getActivity().finish();
		}

		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_sounds;
	}

	@Override
	protected void deleteItems(List<SoundInfo> selectedItems) {
		setShowProgressBar(true);
		for (SoundInfo item : selectedItems) {
			try {
				soundController.delete(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_sounds,
				selectedItems.size(),
				selectedItems.size()));

		BackpackListManager.getInstance().saveBackpack();
		finishActionMode();
		if (adapter.getItems().isEmpty()) {
			getActivity().finish();
		}
	}

	@Override
	public String getItemName(SoundInfo item) {
		return item.getName();
	}
}
