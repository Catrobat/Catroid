/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.utils.SnackbarUtil;

public class BrickListFragment extends ListActivityFragment implements CheckBoxListAdapter.ListItemClickHandler<Brick> {

	public static final String TAG = BrickListFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scripts);
		return inflater.inflate(R.layout.fragment_script, container, false);
	}

	@Override
	public void handleAddButton() {

	}

	@Override
	public void handleOnItemClick(int position, View view, Brick listItem) {

	}

	@Override
	public void deleteCheckedItems() {

	}

	@Override
	protected void copyCheckedItems() {

	}

	@Override
	public void showRenameDialog() {

	}

	@Override
	public boolean itemNameExists(String newName) {
		return false;
	}

	@Override
	public void renameItem(String newName) {

	}

	@Override
	public void showReplaceItemsInBackPackDialog() {

	}

	@Override
	public void packCheckedItems() {

	}

	@Override
	protected boolean isBackPackEmpty() {
		return false;
	}

	@Override
	protected void changeToBackPack() {

	}
}
