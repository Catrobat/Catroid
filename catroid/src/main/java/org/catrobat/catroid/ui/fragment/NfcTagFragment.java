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

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.NfcTagListAdapter;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class NfcTagFragment extends ListActivityFragment implements CheckBoxListAdapter.ListItemClickHandler {

	public static final String TAG = NfcTagFragment.class.getSimpleName();
	public static final String BUNDLE_ARGUMENTS_NFC_TAG_DATA = "nfc_tag_data";
	public static final String SHARED_PREFERENCE_NAME = "showNfcTagDetails";

	private NfcTagListAdapter nfcTagAdapter;
	private DragAndDropListView listView;

	private NfcTagData nfcTagToEdit;

	NfcAdapter nfcAdapter;
	PendingIntent pendingIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		pendingIntent = PendingIntent.getActivity(getActivity(), 0,
				new Intent(getActivity(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View nfcTagListFragment = inflater.inflate(R.layout.fragment_nfctag_list, container, false);
		listView = (DragAndDropListView) nfcTagListFragment.findViewById(android.R.id.list);
		return nfcTagListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.nfc_tags;
		deleteDialogTitle = R.plurals.dialog_delete_nfctag;

		if (savedInstanceState != null) {
			nfcTagToEdit = (NfcTagData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_NFC_TAG_DATA);
		}

		initializeList();
		Utils.loadProjectIfNeeded(getActivity());
	}

	private void initializeList() {
		List<NfcTagData> nfcTagDataList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();

		nfcTagAdapter = new NfcTagListAdapter(getActivity(), R.layout.list_item, nfcTagDataList);

		setListAdapter(nfcTagAdapter);
		nfcTagAdapter.setListItemClickHandler(this);
		nfcTagAdapter.setListItemLongClickHandler(listView);
		nfcTagAdapter.setListItemCheckHandler(this);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_NFC_TAG_DATA, nfcTagToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (nfcAdapter != null) {
			Log.d(TAG, "onResume()enableForegroundDispatch()");
			nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, null, null);
		}

		loadShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		Log.d(TAG, "activity:" + getActivity().getClass().getSimpleName());
		Log.d(TAG, "got intent:" + intent.getAction());
		String uid = NfcHandler.getTagIdFromIntent(intent);
		NfcHandler.setLastNfcTagId(uid);
		NfcHandler.setLastNfcTagMessage(NfcHandler.getMessageFromIntent(intent));
		if (uid != null) {
			NfcTagData newNfcTagData = new NfcTagData();
			String newTagName = Utils.getUniqueNfcTagName(getString(R.string.default_tag_name));
			newNfcTagData.setNfcTagName(newTagName);
			newNfcTagData.setNfcTagUid(uid);
			nfcTagAdapter.add(newNfcTagData);
			nfcTagAdapter.notifyDataSetChanged();
		} else {
			Log.d(TAG, "NO NFC tag found");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(getActivity());
		}
		putShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void handleAddButton(){
	}

	@Override
	public void handleOnItemClick(int position, View view, Object listItem) {
		//DO NOTHING onClick.
	}

	@Override
	public void deleteCheckedItems() {
		for (NfcTagData nfcTagData : nfcTagAdapter.getCheckedItems()) {
			nfcTagAdapter.remove(nfcTagData);
		}
		clearCheckedItems();
	}

	@Override
	protected void copyCheckedItems() {
		for (NfcTagData nfcTagData : nfcTagAdapter.getCheckedItems()) {
			copyNfcTag(nfcTagData);
		}
		clearCheckedItems();
	}

	private void copyNfcTag(NfcTagData nfcTagData) {
		String name = Utils.getUniqueNfcTagName(nfcTagData.getNfcTagName());
		String uid = nfcTagData.getNfcTagUid();

		NfcTagData newNfcTagData = new NfcTagData();
		newNfcTagData.setNfcTagName(name);
		newNfcTagData.setNfcTagUid(uid);
		nfcTagAdapter.add(newNfcTagData);
	}

	@Override
	public void showRenameDialog() {
		nfcTagToEdit = nfcTagAdapter.getCheckedItems().get(0);
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_nfctag_dialog, R.string.nfctag_name, nfcTagToEdit
				.getNfcTagName(), this);
		dialog.show(getFragmentManager(), RenameItemDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean itemNameExists(String newName) {
		return newName.equalsIgnoreCase(getString(R.string.brick_when_nfc_default_all));
	}

	@Override
	public void renameItem(String newName) {
		nfcTagToEdit.setNfcTagName(newName);
		clearCheckedItems();
		nfcTagAdapter.notifyDataSetChanged();
	}

	@Override
	public void showReplaceItemsInBackPackDialog() {
		//NO BACKPACK FOR NFC TAGS
	}

	@Override
	public void packCheckedItems() {
		//NO BACKPACK FOR NFC TAGS
	}

	@Override
	protected boolean isBackPackEmpty() {
		//NO BACKPACK FOR NFC TAGS
		return true;
	}

	@Override
	protected void changeToBackPack(){
		//NO BACKPACK FOR NFC TAGS
	}
}
