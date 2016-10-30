/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SpriteListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class BackPackSpriteListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler<Sprite>, CheckBoxListAdapter.ListItemLongClickHandler {

	public static final String TAG = BackPackSpriteListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SPRITE_TO_EDIT = "sprite_to_edit";

	private SpriteListAdapter spriteAdapter;
	private ListView listView;

	private Sprite spriteToEdit;
	private int selectedSpritePosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View backPackSpriteListFragment = inflater.inflate(R.layout.fragment_backpack, container, false);
		listView = (ListView) backPackSpriteListFragment.findViewById(android.R.id.list);

		return backPackSpriteListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(listView);

		singleItemTitle = getString(R.string.sprite);
		multipleItemsTitle = getString(R.string.sprites);

		if (savedInstanceState != null) {
			spriteToEdit = (Sprite) savedInstanceState.get(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT);
		}

		initializeList();
		checkEmptyBackgroundBackPack();
		BottomBar.hideAddButton(getActivity());
	}

	private void initializeList() {
		List<Sprite> spriteList = BackPackListManager.getInstance().getBackPackedSprites();

		spriteAdapter = new SpriteListAdapter(getActivity(), R.layout.list_item, spriteList);

		setListAdapter(spriteAdapter);
		spriteAdapter.setListItemClickHandler(this);
		spriteAdapter.setListItemCheckHandler(this);
		spriteAdapter.setListItemLongClickHandler(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT, spriteToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SpritesListFragment.SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		BackPackListManager.getInstance().saveBackpack();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SpritesListFragment.SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (BackPackListManager.getInstance().getBackPackedSprites().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(false);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		spriteToEdit = spriteAdapter.getItem(selectedSpritePosition);
		menu.setHeaderTitle(spriteToEdit.getName());

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking_sprite, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_unpacking_background:
				showUnpackAsBackgroundDialog();
				break;
			case R.id.context_menu_unpacking_object:
				unpackCheckedItems(true);
				break;
			case R.id.context_menu_delete:
				showDeleteDialog(true);
				break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void handleOnItemClick(int position, View view, Sprite listItem) {
		selectedSpritePosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		selectedSpritePosition = position;
		listView.showContextMenuForChild(view);
	}

	public void showDeleteDialog(boolean singleItem) {
		int titleId;
		if (spriteAdapter.getCheckedItems().size() == 1 || singleItem) {
			titleId = R.string.dialog_confirm_delete_object_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_objects_title;
		}
		showDeleteDialog(titleId, singleItem);
	}

	@Override
	protected void deleteCheckedItems(boolean singleItem) {
		if (singleItem) {
			deleteSprite();
			return;
		}
		for (Sprite sprite : spriteAdapter.getCheckedItems()) {
			spriteToEdit = sprite;
			deleteSprite();
		}
	}

	public void deleteSprite() {
		BackPackListManager.getInstance().removeItemFromSpriteBackPack(spriteToEdit);
		removeLooksAndSounds();
		checkEmptyBackgroundBackPack();
	}

	private void removeLooksAndSounds() {
		for (LookData currentLookData : spriteToEdit.getLookDataList()) {
			if (!LookController.getInstance().otherLookDataItemsHaveAFileReference(currentLookData)) {
				StorageHandler.getInstance().deleteFile(currentLookData.getAbsolutePath(), true);
			}
			BackPackListManager.getInstance().removeItemFromLookHiddenBackpack(currentLookData);
		}

		for (SoundInfo currentSoundInfo : spriteToEdit.getSoundList()) {
			if (!SoundController.getInstance().otherSoundInfoItemsHaveAFileReference(currentSoundInfo)) {
				StorageHandler.getInstance().deleteFile(currentSoundInfo.getAbsolutePath(), true);
			}
			BackPackListManager.getInstance().removeItemFromSoundHiddenBackpack(currentSoundInfo);
		}
	}

	protected void unpackCheckedItems(boolean singleItem) {
		if (singleItem) {
			BackPackSpriteController.getInstance().unpack(spriteToEdit, false, false, false, false);
			showUnpackingCompleteToast(1);
			return;
		}
		for (Sprite sprite : spriteAdapter.getCheckedItems()) {
			BackPackSpriteController.getInstance().unpack(sprite, false, false, false, false);
		}

		showUnpackingCompleteToast(spriteAdapter.getCheckedItems().size());
		clearCheckedItems();
	}

	private void showUnpackAsBackgroundDialog() {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(R.string.unpack);
		builder.setMessage(R.string.unpack_background);
		builder.setPositiveButton(R.string.main_menu_continue, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				unpackCheckedItems(true);
				clearCheckedItems();
			}
		});
		builder.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedItems();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				clearCheckedItems();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}
