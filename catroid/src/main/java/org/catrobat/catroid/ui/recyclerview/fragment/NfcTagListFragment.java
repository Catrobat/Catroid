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

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.PluralsRes;
import android.util.Log;
import android.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.recyclerview.adapter.NfcTagAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameDialogFragment;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NfcTagListFragment extends RecyclerViewFragment<NfcTagData> {

	public static final String TAG = NfcTagListFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

		if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
			ToastUtil.showError(getActivity(), R.string.nfc_not_activated);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
				startActivity(intent);
			} else {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		} else if (nfcAdapter == null) {
			ToastUtil.showError(getActivity(), R.string.no_nfc_available);
		}
	}

	@Override
	protected void initializeAdapter() {
		List<NfcTagData> items = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
		sharedPreferenceDetailsKey = "showDetailsNfcTags";
		adapter = new NfcTagAdapter(items);
		emptyView.setText(R.string.fragment_nfctag_text_description);
		onAdapterReady();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.backpack).setVisible(false);
	}

	@Override
	public void handleAddButton() {
		// NFC Tags cannot be added Directly.
	}

	public void onNewIntent(Intent intent) {
		String uid = NfcHandler.getTagIdFromIntent(intent);
		NfcHandler.setLastNfcTagId(uid);
		NfcHandler.setLastNfcTagMessage(NfcHandler.getMessageFromIntent(intent));
		if (uid != null) {
			NfcTagData item = new NfcTagData();
			String name = uniqueNameProvider.getUniqueName(getString(R.string.default_tag_name), getScope());
			item.setNfcTagName(name);
			item.setNfcTagUid(uid);
		} else {
			Log.e(TAG, "NFC Tag does not have a UID.");
		}
	}

	@Override
	public void addItem(NfcTagData item) {
		adapter.add(item);
	}

	@Override
	protected void packItems(List<NfcTagData> selectedItems) {
		throw new IllegalStateException(TAG + ": NfcTags cannot be backpacked.");
	}

	@Override
	protected boolean isBackpackEmpty() {
		return true;
	}

	@Override
	protected void switchToBackpack() {
		throw new IllegalStateException(TAG + ": NfcTags cannot be backpacked.");
	}

	@Override
	protected void copyItems(List<NfcTagData> selectedItems) {
		setShowProgressBar(true);
		for (NfcTagData item : selectedItems) {
			String name = uniqueNameProvider.getUniqueName(item.getNfcTagName(), getScope());

			NfcTagData newItem = new NfcTagData();
			newItem.setNfcTagName(name);
			newItem.setNfcTagUid(item.getNfcTagUid());

			adapter.add(newItem);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_nfc_tags,
				selectedItems.size(),
				selectedItems.size()));
		finishActionMode();
	}

	protected Set<String> getScope() {
		Set<String> scope = new HashSet<>();
		for (NfcTagData item : adapter.getItems()) {
			scope.add(item.getNfcTagName());
		}
		return scope;
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_nfc_tags;
	}

	@Override
	protected void deleteItems(List<NfcTagData> selectedItems) {
		setShowEmptyView(true);

		for (NfcTagData item : selectedItems) {
			adapter.remove(item);
		}

		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_nfc_tags,
				selectedItems.size(),
				selectedItems.size()));
		finishActionMode();
	}

	@Override
	protected void showRenameDialog(List<NfcTagData> selectedItems) {
		String name = selectedItems.get(0).getNfcTagName();
		RenameDialogFragment dialog = new RenameDialogFragment(R.string.rename_nfctag_dialog, R.string.new_nfc_tag, name, this);
		dialog.show(getFragmentManager(), RenameDialogFragment.TAG);
	}

	@Override
	public boolean isNameUnique(String name) {
		return !getScope().contains(name);
	}

	@Override
	public void renameItem(String name) {
		NfcTagData item = adapter.getSelectedItems().get(0);
		if (!item.getNfcTagName().equals(name)) {
			item.setNfcTagName(name);
		}
		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getActionModeTitleId(@ActionModeType int actionModeType) {
		switch (actionModeType) {
			case COPY:
				return R.plurals.am_copy_nfc_tags_title;
			case DELETE:
				return R.plurals.am_delete_nfc_tags_title;
			case RENAME:
				return R.plurals.am_rename_nfc_tags_title;
			case BACKPACK:
			case NONE:
			default:
				throw new IllegalStateException("ActionModeType not set correctly");
		}
	}

	@Override
	public void onItemClick(NfcTagData item) {
	}
}
