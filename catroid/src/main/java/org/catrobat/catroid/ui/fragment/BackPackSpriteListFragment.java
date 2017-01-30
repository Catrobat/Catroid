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
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.adapter.BackPackSpriteListAdapter;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.controller.OldLookController;
import org.catrobat.catroid.ui.controller.OldSoundController;

import java.util.List;

public class BackPackSpriteListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler<Sprite> {

	public static final String TAG = BackPackSpriteListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SPRITE_TO_EDIT = "sprite_to_edit";

	private BackPackSpriteListAdapter spriteAdapter;
	private Sprite spriteToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_backpack, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.sprites;
		deleteDialogTitle = R.plurals.dialog_delete_sprite;

		if (savedInstanceState != null) {
			spriteToEdit = (Sprite) savedInstanceState.get(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT);
		}

		initializeList();
	}

	private void initializeList() {
		List<Sprite> spriteList = BackPackListManager.getInstance().getBackPackedSprites();

		spriteAdapter = new BackPackSpriteListAdapter(getActivity(), R.layout.list_item, spriteList);

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
		loadShowDetailsPreferences(SpriteListFragment.SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		putShowDetailsPreferences(SpriteListFragment.SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		spriteToEdit = spriteAdapter.getItem(selectedItemPosition);
		menu.setHeaderTitle(spriteToEdit.getName());

		getActivity().getMenuInflater().inflate(R.menu.context_menu_backpack_sprite, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.unpack_as_background:
				showUnpackAsBackgroundDialog();
				break;
			case R.id.unpack_as_object:
				unpackCheckedItems();
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return true;
	}

	@Override
	public void handleOnItemClick(int position, View view, Sprite listItem) {
		selectedItemPosition = position;
		getListView().showContextMenuForChild(view);
	}

	@Override
	public void deleteCheckedItems() {
		if (spriteAdapter.getCheckedItems().isEmpty()) {
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
		spriteAdapter.notifyDataSetChanged();
	}

	private void removeLooksAndSounds() {
		for (LookData currentLookData : spriteToEdit.getLookDataList()) {
			if (!OldLookController.getInstance().otherLookDataItemsHaveAFileReference(currentLookData)) {
				StorageHandler.deleteFile(currentLookData.getAbsolutePath());
			}
			BackPackListManager.getInstance().removeItemFromLookHiddenBackpack(currentLookData);
		}

		for (SoundInfo currentSoundInfo : spriteToEdit.getSoundList()) {
			if (!OldSoundController.getInstance().otherSoundInfoItemsHaveAFileReference(currentSoundInfo)) {
				StorageHandler.deleteFile(currentSoundInfo.getAbsolutePath());
			}
			BackPackListManager.getInstance().removeItemFromSoundHiddenBackpack(currentSoundInfo);
		}
	}

	protected void unpackCheckedItems() {
		if (spriteAdapter.getCheckedItems().isEmpty()) {
			BackPackSpriteController.unpack(spriteToEdit, false, false);
			showUnpackingCompleteToast(1);
			return;
		}
		for (Sprite sprite : spriteAdapter.getCheckedItems()) {
			BackPackSpriteController.unpack(sprite, false, false);
		}

		showUnpackingCompleteToast(spriteAdapter.getCheckedItems().size());
		clearCheckedItems();
	}

	private void showUnpackAsBackgroundDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.unpack);
		builder.setMessage(R.string.unpack_background);
		builder.setPositiveButton(R.string.main_menu_continue, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				unpackCheckedItems();
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
