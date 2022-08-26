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

package org.catrobat.catroid.ui.filepicker;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.catrobat.catroid.R;

public class SelectActionModeCallback implements ActionMode.Callback {

	private ActionModeClickListener clickListener;

	public SelectActionModeCallback(ActionModeClickListener clickListener) {
		this.clickListener = clickListener;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater menuInflater = clickListener.getMenuInflater();
		menuInflater.inflate(R.menu.context_menu, menu);
		menu.findItem(R.id.overflow).setVisible(true);
		menu.findItem(R.id.toggle_selection).setVisible(true);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		menu.findItem(R.id.overflow).setVisible(true);
		MenuItem selectionToggle = menu.findItem(R.id.toggle_selection);
		selectionToggle.setVisible(true);
		if (clickListener.hasUnselectedItems()) {
			selectionToggle.setTitle(R.string.select_all);
		} else {
			selectionToggle.setTitle(R.string.deselect_all);
		}

		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.toggle_selection:
				clickListener.onToggleSelection();
				break;
			case R.id.confirm:
				clickListener.onConfirm();
				break;
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		clickListener.endMultiSelectionMode();
	}

	public interface ActionModeClickListener {
		MenuInflater getMenuInflater();
		boolean hasUnselectedItems();
		void onToggleSelection();
		void endMultiSelectionMode();
		void onConfirm();
	}
}
