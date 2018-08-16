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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.PluralsRes;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.LookAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.LookController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewLookDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameDialogFragment;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_LOOKS_PREFERENCE_KEY;

public class LookListFragment extends RecyclerViewFragment<LookData> {

	public static final String TAG = LookListFragment.class.getSimpleName();

	public static final int POCKET_PAINT = 0;
	public static final int LIBRARY = 1;
	public static final int FILE = 2;
	public static final int CAMERA = 3;
	public static final int DRONE = 4;

	private LookController lookController = new LookController();

	@Override
	protected void initializeAdapter() {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_looks);
		sharedPreferenceDetailsKey = SHOW_DETAILS_LOOKS_PREFERENCE_KEY;
		List<LookData> items = ProjectManager.getInstance().getCurrentSprite().getLookList();
		adapter = new LookAdapter(items);
		emptyView.setText(R.string.fragment_look_text_description);
		onAdapterReady();
	}

	@Override
	public void handleAddButton() {
		NewLookDialogFragment dialog = new NewLookDialogFragment(this,
				ProjectManager.getInstance().getCurrentlyEditedScene(), ProjectManager.getInstance().getCurrentSprite());
		dialog.show(getFragmentManager(), NewLookDialogFragment.TAG);
	}

	@Override
	public void addItem(LookData item) {
		if (ProjectManager.getInstance().getCurrentSprite().hasCollision()) {
			item.getCollisionInformation().calculate();
		}
		adapter.add(item);
	}

	@Override
	protected void packItems(List<LookData> selectedItems) {
		setShowProgressBar(true);
		int packedItemCnt = 0;

		for (LookData item : selectedItems) {
			try {
				BackpackListManager.getInstance().getBackpackedLooks().add(
						lookController.pack(item));
				BackpackListManager.getInstance().saveBackpack();
				packedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (packedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.packed_looks,
					packedItemCnt,
					packedItemCnt));
			switchToBackpack();
		}

		finishActionMode();
	}

	@Override
	protected boolean isBackpackEmpty() {
		return BackpackListManager.getInstance().getBackpackedLooks().isEmpty();
	}

	@Override
	protected void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_LOOKS);
		startActivity(intent);
	}

	@Override
	protected void copyItems(List<LookData> selectedItems) {
		setShowProgressBar(true);
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		int copiedItemCnt = 0;

		for (LookData item : selectedItems) {
			try {
				adapter.add(lookController.copy(item, currentScene, currentSprite));
				copiedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (copiedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_looks,
					copiedItemCnt,
					copiedItemCnt));
		}

		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_looks;
	}

	@Override
	protected void deleteItems(List<LookData> selectedItems) {
		setShowProgressBar(true);

		for (LookData item : selectedItems) {
			try {
				lookController.delete(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}

		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_looks,
				selectedItems.size(),
				selectedItems.size()));
		finishActionMode();
	}

	@Override
	protected void showRenameDialog(List<LookData> selectedItems) {
		String name = selectedItems.get(0).getName();
		RenameDialogFragment dialog = new RenameDialogFragment(R.string.rename_look_dialog, R.string.look_name_label, name, this);
		dialog.show(getFragmentManager(), RenameDialogFragment.TAG);
	}

	@Override
	public boolean isNameUnique(String name) {
		Set<String> scope = new HashSet<>();
		for (LookData item : adapter.getItems()) {
			scope.add(item.getName());
		}
		return !scope.contains(name);
	}

	@Override
	public void renameItem(String name) {
		LookData item = adapter.getSelectedItems().get(0);
		if (!item.getName().equals(name)) {
			item.setName(name);
		}
		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getActionModeTitleId(@ActionModeType int actionModeType) {
		switch (actionModeType) {
			case BACKPACK:
				return R.plurals.am_pack_looks_title;
			case COPY:
				return R.plurals.am_copy_looks_title;
			case DELETE:
				return R.plurals.am_delete_looks_title;
			case RENAME:
				return R.plurals.am_rename_looks_title;
			case NONE:
			default:
				throw new IllegalStateException("ActionModeType not set correctly");
		}
	}

	@Override
	public void onItemClick(LookData item) {
		if (actionModeType != NONE) {
			return;
		}

		item.invalidateThumbnailBitmap();

		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(getActivity(), Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));
		Bundle bundle = new Bundle();
		bundle.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, item.getFile().getAbsolutePath());
		intent.putExtras(bundle);
		intent.addCategory("android.intent.category.LAUNCHER");

		startActivityForResult(intent, POCKET_PAINT);
	}
}
