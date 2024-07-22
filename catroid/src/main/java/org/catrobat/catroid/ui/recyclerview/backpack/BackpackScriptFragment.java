/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.ScriptAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.PluralsRes;

public class BackpackScriptFragment extends BackpackRecyclerViewFragment<String> {

	public static final String TAG = BackpackScriptFragment.class.getSimpleName();

	private final ScriptController scriptController = new ScriptController();

	@Override
	protected void initializeAdapter() {
		List<String> items = BackpackListManager.getInstance().getBackpackedScriptGroups();
		adapter = new ScriptAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<String> selectedItems) {
		setShowProgressBar(true);
		int unpackedItemCnt = 0;
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		for (String item : selectedItems) {
			List<UserDefinedBrick> userDefinedBricks = BackpackListManager.getInstance().getBackpackedUserDefinedBricks().get(item);
			if (userDefinedBricks != null) {
				sprite.addClonesOfUserDefinedBrickList(userDefinedBricks);
			}

			List<Script> scripts = BackpackListManager.getInstance().getBackpackedScripts().get(item);
			for (Script script : scripts) {
				try {
					scriptController.unpack(item, script, sprite);
					unpackedItemCnt++;
				} catch (CloneNotSupportedException exception) {
					Log.e(TAG, Log.getStackTraceString(exception));
				}
			}
		}

		if (unpackedItemCnt > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_scripts,
					unpackedItemCnt,
					unpackedItemCnt));
			getActivity().finish();
		}

		finishActionMode();
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_scripts;
	}

	@Override
	protected void deleteItems(List<String> selectedItems) {
		setShowProgressBar(true);
		removeLastDeletedItems();

		setCopiedStatus(BackpackListManager.getInstance().getBackpackedScripts());
		setLastDeletedItems(selectedItems);
		for (String item : selectedItems) {
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_scripts,
				selectedItems.size(),
				selectedItems.size()));

		BackpackListManager.getInstance().saveBackpack();
		finishActionMode();
	}

	@Override
	protected String getItemName(String item) {
		return item;
	}

	@Override
	public void undo() {
		BackpackListManager.getInstance().replaceBackpackedScripts(savedScripts);
		BackpackListManager.getInstance().saveBackpack();
		initializeAdapter();
		getLastDeletedItems().clear();
	}

	@Override
	public void removeLastDeletedItems() {
		for (String item : getLastDeletedItems()) {
			BackpackListManager.getInstance().removeItemFromScriptBackPack(item);
		}
		getLastDeletedItems().clear();
	}

	@Override
	public void saveStatus() {
		setCopiedStatus(BackpackListManager.getInstance().getBackpackedScripts());
	}

	public void setCopiedStatus(HashMap<String, List<Script>> map) {
		savedScripts.clear();
		savedScripts.putAll(map);
	}
}
