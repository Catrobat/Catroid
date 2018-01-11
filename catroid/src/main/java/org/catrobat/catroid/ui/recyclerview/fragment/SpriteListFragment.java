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
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.SpriteAttributesActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.ViewHolder;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSpriteDialogWrapper;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameItemDialog;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpriteListFragment extends RecyclerViewFragment<Sprite> {

	public static final String TAG = SpriteListFragment.class.getSimpleName();

	private SpriteController spriteController = new SpriteController();

	@Override
	public void onResume() {
		super.onResume();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		String title = currentProject.getName() + ": " + currentScene.getName();
		getActivity().getActionBar().setTitle(title);
	}

	@Override
	protected void initializeAdapter() {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_objects);
		sharedPreferenceDetailsKey = "showDetailsSpriteList";
		hasDetails = true;
		List<Sprite> items = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		adapter = new SpriteAdapter(items) {

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);

				if (holder.getAdapterPosition() == 0) {
					holder.background.setOnLongClickListener(null);
					holder.checkBox.setVisibility(View.GONE);
				}
			}

			@Override
			public boolean onItemMove(int fromPosition, int toPosition) {
				return fromPosition == 0 || toPosition == 0 || super.onItemMove(fromPosition, toPosition);
			}
		};
		onAdapterReady();
	}

	@Override
	public void handleAddButton() {
		NewSpriteDialogWrapper dialogWrapper = new NewSpriteDialogWrapper(
				this, ProjectManager.getInstance().getCurrentScene());
		dialogWrapper.showDialog(getFragmentManager());
	}

	@Override
	public void addItem(Sprite item) {
		adapter.add(item);
	}

	@Override
	protected void packItems(List<Sprite> selectedItems) {
		finishActionMode();
		try {
			for (Sprite item : selectedItems) {
				spriteController.pack(item);
			}
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.packed_sprites,
					selectedItems.size(),
					selectedItems.size()));

			switchToBackpack();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	protected boolean isBackpackEmpty() {
		return BackPackListManager.getInstance().getBackPackedSprites().isEmpty();
	}

	@Override
	protected void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SPRITES);
		startActivity(intent);
	}

	@Override
	protected void copyItems(List<Sprite> selectedItems) {
		finishActionMode();
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		for (Sprite item : selectedItems) {
			try {
				adapter.add(spriteController.copy(item, currentScene, currentScene));
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_sprites,
				selectedItems.size(),
				selectedItems.size()));
	}

	@Override
	protected int getDeleteAlertTitle() {
		return R.plurals.delete_sprites;
	}

	@Override
	protected void deleteItems(List<Sprite> selectedItems) {
		finishActionMode();
		for (Sprite item : selectedItems) {
			spriteController.delete(item, ProjectManager.getInstance().getCurrentScene());
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_sprites,
				selectedItems.size(),
				selectedItems.size()));
	}

	@Override
	protected void showRenameDialog(List<Sprite> selectedItems) {
		String name = selectedItems.get(0).getName();
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_sprite_dialog, R.string.sprite_name, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}

	@Override
	public boolean isNameUnique(String name) {
		Set<String> scope = new HashSet<>();
		for (Sprite item : adapter.getItems()) {
			scope.add(item.getName());
		}
		return !scope.contains(name);
	}

	@Override
	public void renameItem(String name) {
		Sprite item = adapter.getSelectedItems().get(0);
		if (!item.getName().equals(name)) {
			item.setName(name);
		}
		finishActionMode();
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_sprites_title,
				selectedItemCnt,
				selectedItemCnt));
	}

	@Override
	public void onItemClick(Sprite item) {
		if (actionModeType == NONE) {
			ProjectManager.getInstance().setCurrentSprite(item);
			Intent intent = new Intent(getActivity(), SpriteAttributesActivity.class);
			startActivity(intent);
		}
	}
}
