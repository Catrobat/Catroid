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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.LookAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.LookController;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;

import static android.app.Activity.RESULT_OK;

import static org.catrobat.catroid.common.Constants.EXTRA_PICTURE_PATH_POCKET_PAINT;
import static org.catrobat.catroid.common.Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_LOOKS_PREFERENCE_KEY;
import static org.catrobat.catroid.ui.SpriteActivity.EDIT_LOOK;

public class LookListFragment extends RecyclerViewFragment<LookData> {

	public static final String TAG = LookListFragment.class.getSimpleName();

	private LookController lookController = new LookController();

	private LookData currentItem;

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
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		menu.findItem(R.id.catblocks_reorder_scripts).setVisible(false);
		menu.findItem(R.id.catblocks).setVisible(false);
	}

	@Override
	protected void packItems(List<LookData> selectedItems) {
		setShowProgressBar(true);
		int packedItemCnt = 0;

		for (LookData item : selectedItems) {
			try {
				BackpackListManager.getInstance().getBackpackedLooks().add(lookController.pack(item));
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
		int copiedItemCnt = 0;

		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

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

	private void disposeItem() {
		if (Constants.TEMP_LOOK_FILE.exists()) {
			Constants.TEMP_LOOK_FILE.delete();
			currentItem = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		disposeItem();
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
	protected int getRenameDialogTitle() {
		return R.string.rename_look_dialog;
	}

	@Override
	protected int getRenameDialogHint() {
		return R.string.look_name_label;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_LOOK && resultCode == RESULT_OK) {
			Activity activity = getActivity();
			if (activity instanceof SpriteActivity) {
				((SpriteActivity) getActivity()).setUndoMenuItemVisibility(true);
			}
		}
	}

	public boolean undo() {
		if (currentItem != null) {
			try {
				StorageOperations.copyFile(Constants.TEMP_LOOK_FILE, currentItem.getFile());
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			currentItem.invalidateThumbnailBitmap();
			adapter.notifyDataSetChanged();
			disposeItem();
			return true;
		}
		return false;
	}

	public void deleteItem(LookData lookData) {
		deleteItems(Collections.singletonList(lookData));
	}

	@Override
	public void onItemClick(LookData item) {
		if (actionModeType == RENAME) {
			super.onItemClick(item);
			return;
		}
		if (actionModeType != NONE) {
			return;
		}

		currentItem = item;

		item.invalidateThumbnailBitmap();
		item.clearCollisionInformation();

		try {
			StorageOperations.copyFile(currentItem.getFile(), Constants.TEMP_LOOK_FILE);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(getActivity(), POCKET_PAINT_INTENT_ACTIVITY_NAME));
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_PICTURE_PATH_POCKET_PAINT, item.getFile().getAbsolutePath());
		intent.putExtras(bundle);
		intent.addCategory("android.intent.category.LAUNCHER");

		startActivityForResult(intent, EDIT_LOOK);
	}
}
