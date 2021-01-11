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

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SoundAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SoundController;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import androidx.annotation.PluralsRes;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SOUNDS_PREFERENCE_KEY;

public class SoundListFragment extends RecyclerViewFragment<SoundInfo> {

	public static final String TAG = SoundListFragment.class.getSimpleName();

	private SoundController soundController = new SoundController();

	@Override
	protected void initializeAdapter() {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_sounds);
		sharedPreferenceDetailsKey = SHOW_DETAILS_SOUNDS_PREFERENCE_KEY;
		List<SoundInfo> items = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		adapter = new SoundAdapter(items);
		emptyView.setText(R.string.fragment_sound_text_description);
		onAdapterReady();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		menu.findItem(R.id.catblocks_reorder_scripts).setVisible(false);
		menu.findItem(R.id.catblocks).setVisible(false);
	}

	@Override
	protected void packItems(List<SoundInfo> selectedItems) {
		setShowProgressBar(true);
		int packedItemCnt = 0;

		for (SoundInfo item : selectedItems) {
			try {
				BackpackListManager.getInstance().getBackpackedSounds().add(soundController.pack(item));
				BackpackListManager.getInstance().saveBackpack();
				packedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (packedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.packed_sounds,
					packedItemCnt,
					packedItemCnt));
			switchToBackpack();
		}

		finishActionMode();
	}

	@Override
	protected boolean isBackpackEmpty() {
		return BackpackListManager.getInstance().getBackpackedSounds().isEmpty();
	}

	@Override
	protected void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SOUNDS);
		startActivity(intent);
	}

	@Override
	protected void copyItems(List<SoundInfo> selectedItems) {
		setShowProgressBar(true);
		int copiedItemCnt = 0;

		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		for (SoundInfo item : selectedItems) {
			try {
				adapter.add(soundController.copy(item, currentScene, currentSprite));
				copiedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (copiedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_sounds,
					copiedItemCnt,
					copiedItemCnt));
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
		finishActionMode();
	}

	@Override
	protected int getRenameDialogTitle() {
		return R.string.rename_sound_dialog;
	}

	@Override
	protected int getRenameDialogHint() {
		return R.string.sound_name_label;
	}

	@Override
	public void onItemClick(SoundInfo item) {
		if (actionModeType == RENAME) {
			super.onItemClick(item);
			return;
		}

		if (actionModeType != NONE) {
			return;
		}

		if (!BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
			return;
		}

		if (item.getFile().getName().matches(".*MUS-.*\\.midi")) {
			Intent intent = new Intent(getActivity(), PocketMusicActivity.class);

			intent.putExtra(PocketMusicActivity.TITLE, item.getName());
			intent.putExtra(PocketMusicActivity.ABSOLUTE_FILE_PATH, item.getFile().getAbsolutePath());

			startActivity(intent);
		}
	}
}
