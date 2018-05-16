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

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.PluralsRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SpriteAttributesActivity;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.MultiViewSpriteAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewGroupDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSceneDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSpriteDialogWrapper;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpriteListFragment extends RecyclerViewFragment<Sprite> {

	public static final String TAG = SpriteListFragment.class.getSimpleName();

	private SpriteController spriteController = new SpriteController();

	private NewItemInterface<Scene> newSceneInterface = new NewItemInterface<Scene>() {
		@Override
		public void addItem(Scene item) {
			ProjectManager.getInstance().getCurrentProject().addScene(item);
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			startActivity(intent);
			getActivity().finish();
		}
	};

	class MultiViewTouchHelperCallback extends TouchHelperCallback {

		MultiViewTouchHelperCallback(TouchHelperAdapterInterface adapterInterface) {
			super(adapterInterface);
		}

		@Override
		public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
			super.onSelectedChanged(viewHolder, actionState);

			switch (actionState) {
				case ItemTouchHelper.ACTION_STATE_IDLE:
					List<Sprite> items = adapter.getItems();

					for (Sprite sprite : items) {
						if (sprite instanceof GroupSprite) {
							continue;
						}
						if (sprite.toBeConverted()) {
							Sprite convertedSprite = sprite.clone();
							items.set(items.indexOf(sprite), convertedSprite);
						}
					}

					for (Sprite item : items) {
						if (item instanceof GroupSprite) {
							((GroupSprite) item).setCollapsed(((GroupSprite) item).collapsed);
						}
					}

					adapter.notifyDataSetChanged();
					break;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		String title;

		if (currentProject.getSceneList().size() < 2) {
			title = currentProject.getName();
		} else {
			Scene currentScene = ProjectManager.getInstance().getCurrentScene();
			title = currentProject.getName() + ": " + currentScene.getName();
		}

		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
	}

	@Override
	public void onAdapterReady() {
		super.onAdapterReady();
		ItemTouchHelper.Callback callback = new MultiViewTouchHelperCallback(adapter);
		touchHelper = new ItemTouchHelper(callback);
		touchHelper.attachToRecyclerView(recyclerView);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.new_group).setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_group:
				Scene currentScene = ProjectManager.getInstance().getCurrentScene();
				new NewGroupDialogFragment(this, currentScene)
						.show(getFragmentManager(), NewGroupDialogFragment.TAG);
				break;
			case R.id.new_scene:
				Project currentProject = ProjectManager.getInstance().getCurrentProject();
				new NewSceneDialogFragment(newSceneInterface, currentProject)
						.show(getFragmentManager(), NewSceneDialogFragment.TAG);
				break;
			default:
				super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void initializeAdapter() {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_objects);
		sharedPreferenceDetailsKey = "showDetailsSpriteList";
		List<Sprite> items = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		adapter = new MultiViewSpriteAdapter(items);
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
		setShowProgressBar(true);
		int packedItemCnt = 0;

		for (Sprite item : selectedItems) {
			try {
				BackpackListManager.getInstance().getBackpackedSprites().add(spriteController.pack(item));
				BackpackListManager.getInstance().saveBackpack();
				packedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (packedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.packed_sprites,
					packedItemCnt,
					packedItemCnt));
			switchToBackpack();
		}

		finishActionMode();
	}

	@Override
	protected boolean isBackpackEmpty() {
		return BackpackListManager.getInstance().getBackpackedSprites().isEmpty();
	}

	@Override
	protected void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SPRITES);
		startActivity(intent);
	}

	@Override
	protected void copyItems(List<Sprite> selectedItems) {
		setShowProgressBar(true);
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		int copiedItemCnt = 0;

		for (Sprite item : selectedItems) {
			try {
				adapter.add(spriteController.copy(item, currentScene, currentScene));
				copiedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (copiedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_sprites,
					copiedItemCnt,
					copiedItemCnt));
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
			if (item instanceof GroupSprite) {
				for (Sprite sprite : ((GroupSprite) item).getGroupItems()) {
					sprite.setConvertToSingleSprite(true);
					Sprite convertedSprite = sprite.clone();
					adapter.getItems().set(adapter.getItems().indexOf(sprite), convertedSprite);
				}
				adapter.notifyDataSetChanged();
			}
			spriteController.delete(item);
			adapter.remove(item);
		}

		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_sprites,
				selectedItems.size(),
				selectedItems.size()));
		finishActionMode();
	}

	@Override
	protected void showRenameDialog(List<Sprite> selectedItems) {
		String name = selectedItems.get(0).getName();
		RenameDialogFragment dialog = new RenameDialogFragment(R.string.rename_sprite_dialog, R.string.sprite_name_label, name, this);
		dialog.show(getFragmentManager(), RenameDialogFragment.TAG);
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
	@PluralsRes
	protected int getActionModeTitleId(@ActionModeType int actionModeType) {
		switch (actionModeType) {
			case BACKPACK:
				return R.plurals.am_pack_sprites_title;
			case COPY:
				return R.plurals.am_copy_sprites_title;
			case DELETE:
				return R.plurals.am_delete_sprites_title;
			case RENAME:
				return R.plurals.am_rename_sprites_title;
			case NONE:
			default:
				throw new IllegalStateException("ActionModeType not set correctly");
		}
	}

	@Override
	public void onItemClick(Sprite item) {
		if (item instanceof GroupSprite) {
			((GroupSprite) item).setCollapsed(!((GroupSprite) item).collapsed);
			adapter.notifyDataSetChanged();
		} else if (actionModeType == NONE) {
			ProjectManager.getInstance().setCurrentSprite(item);
			Intent intent = new Intent(getActivity(), SpriteAttributesActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onItemLongClick(final Sprite item, CheckableVH holder) {
		if (item instanceof GroupSprite) {
			CharSequence[] items = new CharSequence[] {
					getString(R.string.delete),
					getString(R.string.rename),
			};
			new AlertDialog.Builder(getActivity())
					.setTitle(item.getName())
					.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
								case 0:
									showDeleteAlert(new ArrayList<>(Collections.singletonList(item)));
									break;
								case 1:
									adapter.setSelection(item, true);
									showRenameDialog(adapter.getSelectedItems());
									break;
								default:
									dialog.dismiss();
							}
						}
					})
					.show();
		} else {
			super.onItemLongClick(item, holder);
		}
	}
}
