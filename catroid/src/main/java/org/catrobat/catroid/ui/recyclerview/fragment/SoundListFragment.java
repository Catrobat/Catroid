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
import android.util.Log;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SoundAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SoundController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSoundDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameDialogFragment;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoundListFragment extends RecyclerViewFragment<SoundInfo> {

	public static final String TAG = SoundListFragment.class.getSimpleName();

	public static final int RECORD = 0;
	public static final int LIBRARY = 1;
	public static final int FILE = 2;

	private SoundController soundController = new SoundController();

	@Override
	protected void initializeAdapter() {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_sounds);
		sharedPreferenceDetailsKey = "showDetailsSoundList";
		hasDetails = true;
		List<SoundInfo> items = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		adapter = new SoundAdapter(items);
		emptyView.setText(R.string.fragment_sound_text_description);
		onAdapterReady();
	}

	@Override
	public void handleAddButton() {
		NewSoundDialogFragment dialog = new NewSoundDialogFragment(this,
				ProjectManager.getInstance().getCurrentScene(), ProjectManager.getInstance().getCurrentSprite());
		dialog.show(getFragmentManager(), NewSoundDialogFragment.TAG);
	}

	@Override
	public void addItem(SoundInfo item) {
		adapter.add(item);
	}

	@Override
	protected void packItems(List<SoundInfo> selectedItems) {
		finishActionMode();
		try {
			for (SoundInfo item : selectedItems) {
				soundController.pack(item, ProjectManager.getInstance().getCurrentScene());
			}
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.packed_sounds,
					selectedItems.size(),
					selectedItems.size()));

			switchToBackpack();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	protected boolean isBackpackEmpty() {
		return BackPackListManager.getInstance().getBackPackedSounds().isEmpty();
	}

	@Override
	protected void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SOUNDS);
		startActivity(intent);
	}

	@Override
	protected void copyItems(List<SoundInfo> selectedItems) {
		finishActionMode();
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		for (SoundInfo item : selectedItems) {
			try {
				soundController.copy(item, currentScene, currentScene, currentSprite);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_sounds,
				selectedItems.size(),
				selectedItems.size()));
	}

	@Override
	protected int getDeleteAlertTitle() {
		return R.plurals.delete_sounds;
	}

	@Override
	protected void deleteItems(List<SoundInfo> selectedItems) {
		finishActionMode();
		for (SoundInfo item : selectedItems) {
			try {
				soundController.delete(item, ProjectManager.getInstance().getCurrentScene());
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_sounds,
				selectedItems.size(),
				selectedItems.size()));
	}

	@Override
	protected void showRenameDialog(List<SoundInfo> selectedItems) {
		String name = selectedItems.get(0).getName();
		RenameDialogFragment dialog = new RenameDialogFragment(R.string.rename_sound_dialog, R.string.sound_name_label, name, this);
		dialog.show(getFragmentManager(), RenameDialogFragment.TAG);
	}

	@Override
	public boolean isNameUnique(String name) {
		Set<String> scope = new HashSet<>();
		for (SoundInfo item : adapter.getItems()) {
			scope.add(item.getName());
		}
		return !scope.contains(name);
	}

	@Override
	public void renameItem(String name) {
		SoundInfo item = adapter.getSelectedItems().get(0);
		if (!item.getName().equals(name)) {
			item.setName(name);
		}
		finishActionMode();
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_sounds_title,
				selectedItemCnt,
				selectedItemCnt));
	}

	@Override
	public void onItemClick(SoundInfo item) {
		if (actionModeType != NONE) {
			return;
		}

		if (!BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
			return;
		}

		if (item.getFileName().matches(".*MUS-.*\\.midi")) {
			Intent intent = new Intent(getActivity(), PocketMusicActivity.class);

			intent.putExtra("FILENAME", item.getFileName());
			intent.putExtra("TITLE", item.getName());

			startActivity(intent);
		}
	}
}
