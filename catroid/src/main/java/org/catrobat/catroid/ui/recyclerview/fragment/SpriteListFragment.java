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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.merge.ImportProjectHelper;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.MultiViewSpriteAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.Constants.TMP_IMAGE_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_OBJECT_URL;
import static org.catrobat.catroid.common.SharedPreferenceKeys.INDEXING_VARIABLE_PREFERENCE_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SPRITES_PREFERENCE_KEY;
import static org.catrobat.catroid.ui.WebViewActivity.INTENT_PARAMETER_URL;
import static org.catrobat.catroid.ui.WebViewActivity.MEDIA_FILE_PATH;

public class SpriteListFragment extends RecyclerViewFragment<Sprite> {

	public static final String TAG = SpriteListFragment.class.getSimpleName();
	private static final int IMPORT_OBJECT = 0;

	private SpriteController spriteController = new SpriteController();
	private Sprite currentSprite = null;

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
							Sprite convertedSprite = spriteController.convert(sprite);
							items.set(items.indexOf(sprite), convertedSprite);
						}
					}

					for (Sprite item : items) {
						if (item instanceof GroupSprite) {
							GroupSprite groupSprite = (GroupSprite) item;
							groupSprite.setCollapsed(groupSprite.isCollapsed());
						}
					}

					adapter.notifyDataSetChanged();
					break;
			}
		}
	}

	@Override
	boolean shouldShowEmptyView() {
		return adapter.getItemCount() == 1;
	}

	@Override
	public void onResume() {
		initializeAdapter();
		super.onResume();
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_objects);
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		String title;

		if (currentProject.getSceneList().size() < 2) {
			title = currentProject.getName();
		} else {
			Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
			title = currentProject.getName() + ": " + currentScene.getName();
		}

		PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putBoolean(INDEXING_VARIABLE_PREFERENCE_KEY, false).apply();

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
	public void onPrepareOptionsMenu(@NotNull Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.new_group).setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_group:
				showNewGroupDialog();
				break;
			default:
				super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void showNewGroupDialog() {
		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext());
		UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
		builder.setHint(getString(R.string.sprite_group_name_label))
				.setTextWatcher(new DuplicateInputTextWatcher<>(adapter.getItems()))
				.setText(uniqueNameProvider.getUniqueNameInNameables(getString(R.string.default_group_name), adapter.getItems()))
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> adapter.add(new GroupSprite(textInput)));

		builder.setTitle(R.string.new_group)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = SHOW_DETAILS_SPRITES_PREFERENCE_KEY;
		List<Sprite> items = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();
		adapter = new MultiViewSpriteAdapter(items);
		emptyView.setText(R.string.fragment_sprite_text_description);
		onAdapterReady();
	}

	@Override
	protected void packItems(List<Sprite> selectedItems) {
		setShowProgressBar(true);
		int packedItemCnt = 0;

		for (Sprite item : selectedItems) {
			try {
				BackpackListManager.getInstance().getSprites().add(spriteController.pack(item));
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
		return BackpackListManager.getInstance().getSprites().isEmpty();
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
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		int copiedItemCnt = 0;

		for (Sprite item : selectedItems) {
			try {
				adapter.add(adapter.getItems().indexOf(item) + 1,
						spriteController.copy(item, currentProject, currentScene));
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
					sprite.setConvertToSprite(true);
					Sprite convertedSprite = spriteController.convert(sprite);
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IMPORT_OBJECT && resultCode == RESULT_OK) {
			Activity activity = getActivity();
			Uri uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));

			final Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();

			String resolvedName;
			String resolvedFileName = StorageOperations.resolveFileName(activity.getContentResolver(), uri);

			final String lookFileName;

			boolean useDefaultSpriteName = resolvedFileName == null
					|| StorageOperations.getSanitizedFileName(resolvedFileName).equals(TMP_IMAGE_FILE_NAME);

			if (useDefaultSpriteName) {
				resolvedName = getString(R.string.default_sprite_name);
				lookFileName = resolvedName + CATROBAT_EXTENSION;
			} else {
				lookFileName = resolvedFileName;
			}

			ImportProjectHelper importProjectHelper = new ImportProjectHelper(lookFileName, currentScene, activity);

			if (!importProjectHelper.checkForConflicts()) {
				return;
			}
			if (currentSprite != null) {
				importProjectHelper.addObjectDataToNewSprite(currentSprite);
			} else {
				importProjectHelper.rejectImportDialog(null);
			}
		}
	}

	protected void addFromLibrary(Sprite selectedItem) {
		currentSprite = selectedItem;
		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		intent.putExtra(INTENT_PARAMETER_URL, LIBRARY_OBJECT_URL);
		startActivityForResult(intent, IMPORT_OBJECT);
	}

	@Override
	protected int getRenameDialogTitle() {
		return R.string.rename_sprite_dialog;
	}

	@Override
	protected int getRenameDialogHint() {
		return R.string.sprite_name_label;
	}

	@Override
	public void renameItem(Sprite item, String name) {
		item.rename(name);
		finishActionMode();
	}

	@Override
	public void onItemClick(Sprite item) {

		if (item instanceof GroupSprite) {
			GroupSprite groupSprite = (GroupSprite) item;
			groupSprite.setCollapsed(!groupSprite.isCollapsed());
			adapter.notifyDataSetChanged();
		} else {
			if (actionModeType == RENAME) {
				super.onItemClick(item);
				return;
			}
			if (actionModeType == NONE) {
				ProjectManager.getInstance().setCurrentSprite(item);
				Intent intent = new Intent(getActivity(), SpriteActivity.class);
				intent.putExtra(SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onItemLongClick(final Sprite item, CheckableVH holder) {
		super.onItemLongClick(item, holder);
	}

	@Override
	public void onSettingsClick(Sprite item, View view) {
		PopupMenu popupMenu = new PopupMenu(getContext(), view);
		List<Sprite> itemList = new ArrayList<>();
		itemList.add(item);

		popupMenu.getMenuInflater().inflate(R.menu.menu_project_activity, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.backpack:
						packItems(itemList);
						break;
					case R.id.copy:
						copyItems(itemList);
						break;
					case R.id.delete:
						deleteItems(itemList);
						break;
					case R.id.rename:
						showRenameDialog(item);
						break;
					case R.id.from_library:
						addFromLibrary(item);
						break;
					default:
						break;
				}
				return true;
			}
		});
		popupMenu.getMenu().findItem(R.id.backpack).setTitle(R.string.pack);
		popupMenu.getMenu().findItem(R.id.new_group).setVisible(false);
		popupMenu.getMenu().findItem(R.id.new_scene).setVisible(false);
		popupMenu.getMenu().findItem(R.id.show_details).setVisible(false);
		popupMenu.getMenu().findItem(R.id.project_options).setVisible(false);
		popupMenu.getMenu().findItem(R.id.from_library).setVisible(true);
		popupMenu.show();
	}

	public boolean isSingleVisibleSprite() {
		return adapter.getItems().size() == 2 && !(adapter.getItems().get(1) instanceof GroupSprite);
	}
}
