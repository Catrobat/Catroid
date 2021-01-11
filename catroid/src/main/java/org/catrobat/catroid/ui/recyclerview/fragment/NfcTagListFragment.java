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

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.recyclerview.adapter.ExtendedRVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.NfcTagAdapter;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.List;

import androidx.annotation.PluralsRes;

public class NfcTagListFragment extends RecyclerViewFragment<NfcTagData> {

	public static final String TAG = NfcTagListFragment.class.getSimpleName();
	private PendingIntent pendingIntent;
	private NfcAdapter nfcAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		Intent nfcIntent = new Intent(getActivity(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(getActivity(), 0, nfcIntent, 0);
		if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
			ToastUtil.showError(getActivity(), R.string.nfc_not_activated);
			Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
			startActivity(intent);
		} else if (nfcAdapter == null) {
			ToastUtil.showError(getActivity(), R.string.no_nfc_available);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, null, null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(getActivity());
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

	public void onNewIntent(Intent intent) {
		String uid = NfcHandler.getTagIdFromIntent(intent);
		NfcHandler.setLastNfcTagId(uid);
		NfcHandler.setLastNfcTagMessage(NfcHandler.getMessageFromIntent(intent));
		if (uid != null) {
			NfcTagData item = new NfcTagData();
			String name = uniqueNameProvider
					.getUniqueNameInNameables(getString(R.string.default_tag_name), adapter.getItems());
			item.setName(name);
			item.setNfcTagUid(uid);
			if (!addItem(item, adapter)) {
				Log.e(TAG, "NFC Tag has already been added.");
			}
		} else {
			Log.e(TAG, "NFC Tag does not have a UID.");
		}
	}

	public boolean addItem(NfcTagData item, ExtendedRVAdapter<NfcTagData> localAdapter) {
		if (!localAdapter.getItems().contains(item)) {
			return localAdapter.add(item);
		} else {
			return false;
		}
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
			String name = uniqueNameProvider.getUniqueNameInNameables(item.getName(), adapter.getItems());

			NfcTagData newItem = new NfcTagData();
			newItem.setName(name);
			newItem.setNfcTagUid(item.getNfcTagUid());

			adapter.add(newItem);
		}

		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.copied_nfc_tags,
				selectedItems.size(),
				selectedItems.size()));
		finishActionMode();
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
	protected int getRenameDialogTitle() {
		return R.string.rename_nfctag_dialog;
	}

	@Override
	protected int getRenameDialogHint() {
		return R.string.nfc_tag_name_label;
	}

	@Override
	public void onItemClick(NfcTagData item) {
		if (actionModeType == RENAME) {
			super.onItemClick(item);
		}
	}
}
