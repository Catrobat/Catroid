/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.backpack;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.LookAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;
import org.catrobat.catroid.ui.recyclerview.controller.LookController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import androidx.annotation.PluralsRes;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_LOOKS_PREFERENCE_KEY;
import static org.koin.java.KoinJavaComponent.inject;

public class BackpackLookFragment extends BackpackRecyclerViewFragment<LookData> {

	public static final String TAG = BackpackLookFragment.class.getSimpleName();
	public final ProjectManager projectManager = inject(ProjectManager.class).getValue();

	private LookController lookController = new LookController();

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = SHOW_DETAILS_LOOKS_PREFERENCE_KEY;
		hasDetails = true;
		List<LookData> items = BackpackListManager.getInstance().getBackpackedLooks();
		adapter = new LookAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<LookData> selectedItems) {
		setShowProgressBar(true);
		Sprite destinationSprite = projectManager.getCurrentSprite();
		int unpackedItemCnt = 0;

		for (LookData item : selectedItems) {
			try {
				destinationSprite.getLookList().add(lookController.unpack(item,
						ProjectManager.getInstance().getCurrentlyEditedScene(),
						destinationSprite));
				unpackedItemCnt++;
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		if (unpackedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_looks,
					unpackedItemCnt,
					unpackedItemCnt));
			getActivity().finish();
		}

		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_looks;
	}

	@Override
	protected void deleteItems(List<LookData> selectedItems) {
		setShowProgressBar(true);
		for (LookData item : selectedItems) {
			try {
				lookController.delete(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_looks,
				selectedItems.size(),
				selectedItems.size()));

		BackpackListManager.getInstance().saveBackpack();
		finishActionMode();
		if (adapter.getItems().isEmpty()) {
			getActivity().finish();
		}
	}

	@Override
	public void onItemClick(final LookData item, MultiSelectionManager selectionManager) {
		super.onItemClick(item, selectionManager);
	}

	@Override
	protected String getItemName(LookData item) {
		return item.getName();
	}
}
