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
package org.catrobat.catroid.ui.controller;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.ui.NfcTagViewHolder;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.NfcTagBaseAdapter;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public final class NfcTagController {
	public static final String BUNDLE_ARGUMENTS_SELECTED_NFCTAG = "selected_nfctag";
	public static final String SHARED_PREFERENCE_NAME = "showDetailsNfcTags";

	private static final NfcTagController INSTANCE = new NfcTagController();

	private NfcTagController() {
	}

	public static NfcTagController getInstance() {
		return INSTANCE;
	}

	public void updateNfcTagLogic(final int position, final NfcTagViewHolder holder,
			final NfcTagBaseAdapter nfcTagAdapter) {
		final NfcTagData nfcTagData = nfcTagAdapter.getNfcTagDataItems().get(position);

		if (nfcTagData == null) {
			return;
		}
		holder.scanNewTagButton.setTag(position);
		holder.titleTextView.setText(nfcTagData.getNfcTagName());

		handleCheckboxes(position, holder, nfcTagAdapter);
		handleDetails(nfcTagAdapter, holder, nfcTagData);
		setClickListener(nfcTagAdapter, holder);
	}

	private void setClickListener(final NfcTagBaseAdapter nfcTagAdapter, final NfcTagViewHolder holder) {
		OnClickListener listItemOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (nfcTagAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
			}
		};

		holder.nfcTagFragmentButtonLayout.setOnClickListener(listItemOnClickListener);

		holder.nfcTagFragmentButtonLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent intent = new Intent(ScriptActivity.ACTION_NFC_TOUCH_ACTION_UP);
					nfcTagAdapter.getContext().sendBroadcast(intent);
				}
				return false;
			}
		});
	}

	private void handleDetails(NfcTagBaseAdapter nfcTagAdapter, NfcTagViewHolder holder, NfcTagData nfcTagData) {
		if (nfcTagAdapter.getShowDetails()) {
			holder.nfcTagUidTextView.setText(nfcTagData.getNfcTagUid());
			holder.nfcTagDetailsLinearLayout.setVisibility(TextView.VISIBLE);
		} else {
			holder.nfcTagDetailsLinearLayout.setVisibility(TextView.GONE);
		}
	}

	private void handleCheckboxes(final int position, NfcTagViewHolder holder, final NfcTagBaseAdapter nfcTagAdapter) {
		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (nfcTagAdapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE) {
						nfcTagAdapter.clearCheckedItems();
					}
					nfcTagAdapter.getCheckedItems().add(position);
				} else {
					nfcTagAdapter.getCheckedItems().remove(position);
				}
				nfcTagAdapter.notifyDataSetChanged();

				if (nfcTagAdapter.getOnNfcTagEditListener() != null) {
					nfcTagAdapter.getOnNfcTagEditListener().onNfcTagChecked();
				}
			}
		});

		if (nfcTagAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.nfcTagFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_shadowed);
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setVisibility(View.GONE);
			holder.nfcTagFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_selector);
			holder.checkbox.setChecked(false);
			nfcTagAdapter.clearCheckedItems();
		}

		if (nfcTagAdapter.getCheckedItems().contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
	}

	public NfcTagData copyNfcTag(NfcTagData selectedNfcTagData, List<NfcTagData> nfcTagDataList, NfcTagBaseAdapter adapter) {
		return updateNfcTagAdapter(selectedNfcTagData.getNfcTagName(), selectedNfcTagData.getNfcTagUid(), nfcTagDataList,
				adapter);
	}

	public NfcTagData copyNfcTag(int position, List<NfcTagData> nfcTagDataList, NfcTagBaseAdapter adapter) {
		NfcTagData nfcTagData = nfcTagDataList.get(position);
		return NfcTagController.getInstance().updateNfcTagAdapter(nfcTagData.getNfcTagName(), nfcTagData.getNfcTagUid(),
				nfcTagDataList, adapter);
	}

	public NfcTagData updateNfcTagAdapter(String name, String uid, List<NfcTagData> nfcTagDataList, NfcTagBaseAdapter adapter) {
		name = Utils.getUniqueNfcTagName(name);

		NfcTagData newNfcTagData = new NfcTagData();
		newNfcTagData.setNfcTagName(name);
		newNfcTagData.setNfcTagUid(uid);
		nfcTagDataList.add(newNfcTagData);

		adapter.notifyDataSetChanged();
		return newNfcTagData;
	}

	public void stopScanAndUpdateList(List<NfcTagData> nfcTagDataList,
			NfcTagBaseAdapter adapter) {
		adapter.notifyDataSetChanged();
	}

	public void switchToScriptFragment(NfcTagFragment nfcTagFragment) {
		ScriptActivity scriptActivity = (ScriptActivity) nfcTagFragment.getActivity();
		scriptActivity.setCurrentFragment(ScriptActivity.FRAGMENT_SCRIPTS);

		FragmentTransaction fragmentTransaction = scriptActivity.getFragmentManager().beginTransaction();
		fragmentTransaction.hide(nfcTagFragment);
		fragmentTransaction.show(scriptActivity.getFragmentManager().findFragmentByTag(ScriptFragment.TAG));
		fragmentTransaction.commit();

		scriptActivity.setIsNfcTagFragmentFromWhenNfcBrickNewFalse();
		scriptActivity.setIsNfcTagFragmentHandleAddButtonHandled(false);
	}
}
