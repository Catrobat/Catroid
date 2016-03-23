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

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BackPackSpriteAdapter;
import org.catrobat.catroid.ui.adapter.SpriteBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.dialogs.ConfirmUnpackBackgroundDialog;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

public class BackPackSpriteFragment extends BackPackActivityFragment implements Dialog.OnKeyListener, SpriteBaseAdapter.OnSpriteEditListener {

	public static final String TAG = BackPackSpriteFragment.class.getSimpleName();
	private static String actionModeTitle;
	private static String singleItemAppendixActionMode;
	private static String multipleItemAppendixActionMode;
	private BackPackSpriteAdapter adapter;
	private Sprite selectedSpriteBackPack;
	private int selectedSpritePosition;
	private ListView listView;
	private ActionMode actionMode;
	private View selectAllActionModeButton;
	private SpriteDeletedReceiver spriteDeletedReceiver;
	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.delete);
			singleItemAppendixActionMode = getString(R.string.sprite);
			multipleItemAppendixActionMode = getString(R.string.sprites);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedSpritesAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};
	private ActionMode.Callback unpackingModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTitle(R.string.unpack);

			actionModeTitle = getString(R.string.unpack);
			singleItemAppendixActionMode = getString(R.string.category_looks);
			multipleItemAppendixActionMode = getString(R.string.looks);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedSpritesAndEnableButtons();
			} else {
				showUnpackingConfirmationMessage();
			}
			adapter.onDestroyActionModeUnpacking(deleteUnpackedItems);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_back_pack_sprites_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();
		registerForContextMenu(listView);

		adapter = new BackPackSpriteAdapter(getActivity(), R.layout.fragment_script_backpack_item,
				R.id.fragment_script_backpack_item_name_text_view,
				BackPackListManager.getInstance().getBackPackedSprites(), this);
		setListAdapter(adapter);
		checkEmptyBackgroundBackPack();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(false);
		if (!BackPackListManager.getInstance().getBackPackedSprites().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(true);
			menu.findItem(R.id.unpacking_keep).setVisible(true);
		}
		BottomBar.hideBottomBar(getActivity());
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		selectedSpriteBackPack = adapter.getItem(selectedSpritePosition);
		menu.setHeaderTitle(selectedSpriteBackPack.getName());
		adapter.addCheckedSprite(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.context_menu_unpacking_keep:
				checkForBackgroundUnpacking(selectedSpriteBackPack, false, false, false);
				break;
			case R.id.context_menu_unpacking:
				checkForBackgroundUnpacking(selectedSpriteBackPack, true, false, false);
				break;
			case R.id.context_menu_delete:
				showConfirmDeleteDialog();
				break;
		}
		return super.onContextItemSelected(item);
	}

	private void checkForBackgroundUnpacking(Sprite selectedSprite, boolean delete, boolean keepCurrentSprite,
			boolean fromHiddenBackPack) {
		if (selectedSprite.isBackgroundObject) {
			ConfirmUnpackBackgroundDialog dialog = new ConfirmUnpackBackgroundDialog(selectedSprite, delete,
					keepCurrentSprite, fromHiddenBackPack);
			dialog.show(getFragmentManager(), ConfirmUnpackBackgroundDialog.DIALOG_FRAGMENT_TAG);
		} else {
			BackPackSpriteController.getInstance().unpack(selectedSprite, delete,
					keepCurrentSprite, fromHiddenBackPack);
			adapter.returnToProjectActivity();
		}
	}

	private void showConfirmDeleteDialog() {
		deleteCheckedSprites();
		clearCheckedSpritesAndEnableButtons();
		adapter.notifyDataSetChanged();
	}

	private void deleteCheckedSprites() {
		int numDeleted = 0;
		for (int position : adapter.getCheckedItems()) {
			selectedSpriteBackPack = (Sprite) getListView().getItemAtPosition(position - numDeleted);
			deleteSprite();
			numDeleted++;
		}
		checkEmptyBackgroundBackPack();
	}

	public void deleteSprite() {
		BackPackListManager.getInstance().removeItemFromSpriteBackPack(selectedSpriteBackPack);
		removeLooksAndSounds();
	}

	private void removeLooksAndSounds() {
		for (LookData currentLookData : selectedSpriteBackPack.getLookDataList()) {
			if (!LookController.getInstance().otherLookDataItemsHaveAFileReference(currentLookData)) {
				StorageHandler.getInstance().deleteFile(currentLookData.getAbsolutePath(), true);
			}
			BackPackListManager.getInstance().removeItemFromLookHiddenBackpack(currentLookData);
		}

		for (SoundInfo currentSoundInfo : selectedSpriteBackPack.getSoundList()) {
			if (!SoundController.getInstance().otherSoundInfoItemsHaveAFileReference(currentSoundInfo)) {
				StorageHandler.getInstance().deleteFile(currentSoundInfo.getAbsolutePath(), true);
			}
			BackPackListManager.getInstance().removeItemFromSoundHiddenBackpack(currentSoundInfo);
		}
	}

	public void clearCheckedSpritesAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.hideBottomBar(getActivity());
	}

	private void initClickListener() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedSpritePosition = position;
			}
		});
		adapter.setOnSpriteEditListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedSpriteBackPack);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		initClickListener();
	}

	@Override
	public boolean getShowDetails() {
		if (adapter != null) {
			return adapter.getShowDetails();
		}
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		if (adapter != null) {
			adapter.setShowDetails(showDetails);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);

		selectAllActionModeButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						for (int position = 0; position < adapter.getCount(); position++) {
							if (adapter.getItem(position).isBackgroundObject && actionModeTitle.equals(
									getString(R.string.unpack))) {
								continue;
							}
							adapter.addCheckedSprite(position);
						}
						adapter.notifyDataSetChanged();
					}
				});
	}

	@Override
	protected void showDeleteDialog() {
		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(selectedSpritePosition);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void startUnPackingActionMode(boolean deleteUnpackedItems) {
		startActionMode(unpackingModeCallBack, deleteUnpackedItems);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack, true);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback, boolean deleteUnpackedItems) {
		if (actionMode == null) {
			if (adapter.isEmpty()) {
				if (actionModeCallback.equals(unpackingModeCallBack)) {
					((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.unpack));
				} else if (actionModeCallback.equals(deleteModeCallBack)) {
					((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
				}
			} else {
				if (actionModeCallback.equals(unpackingModeCallBack)) {
					this.deleteUnpackedItems = deleteUnpackedItems;
					adapter.disableBackgroundSprites();
				}
				actionMode = getActivity().startActionMode(actionModeCallback);
				unregisterForContextMenu(listView);
				BottomBar.hideBottomBar(getActivity());
			}
		}
	}

	private void showUnpackingConfirmationMessage() {
		String messageForUser = getResources().getQuantityString(R.plurals.unpacking_items_plural,
				adapter.getAmountOfCheckedItems());
		ToastUtil.showSuccess(getActivity(), messageForUser);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (spriteDeletedReceiver == null) {
			spriteDeletedReceiver = new SpriteDeletedReceiver();
		}

		IntentFilter intentFilterDeleteLook = new IntentFilter(ScriptActivity.ACTION_LOOK_DELETED);
		getActivity().registerReceiver(spriteDeletedReceiver, intentFilterDeleteLook);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SpritesListFragment.SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		BackPackListManager.getInstance().saveBackpack();

		if (spriteDeletedReceiver != null) {
			getActivity().unregisterReceiver(spriteDeletedReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SpritesListFragment.SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	public BackPackSpriteAdapter getAdapter() {
		return adapter;
	}

	private void updateActionModeTitle() {
		if (actionMode == null) {
			return;
		}
		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(actionModeTitle);
		} else {
			String appendix = multipleItemAppendixActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = actionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = actionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	public ActionMode getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionMode actionMode) {
		this.actionMode = actionMode;
	}

	public void setSelectedSpritePosition(int selectedSpritePosition) {
		this.selectedSpritePosition = selectedSpritePosition;
	}

	private void checkEmptyBackgroundBackPack() {
		if (BackPackListManager.getInstance().getBackPackedSprites().isEmpty()) {
			TextView emptyViewHeading = (TextView) getActivity().findViewById(R.id.fragment_sprites_list_backpack_text_heading);
			emptyViewHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60.0f);
			emptyViewHeading.setText(R.string.backpack);
			TextView emptyViewDescription = (TextView) getActivity().findViewById(R.id.fragment_sprites_list_backpack_text_description);
			emptyViewDescription.setText(R.string.is_empty);
		}
	}

	@Override
	public void onSpriteChecked() {
		updateActionModeTitle();
		if (actionModeTitle.equals(getString(R.string.unpack))) {
			Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
					adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCountWithBackgroundSprites());
		} else {
			Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
					adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
		}
	}

	@Override
	public void onSpriteEdit(int position) {
	}

	private class SpriteDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SCRIPT_GROUP_DELETED)) {
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}
}
