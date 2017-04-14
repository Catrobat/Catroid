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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;

public abstract class ListActivityFragment extends CheckBoxListFragment implements ListItemActionsInterface,
		RenameItemDialog.RenameItemInterface {

	public static final String TAG = ListActivityFragment.class.getSimpleName();

	protected ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeTitle = getString(R.string.delete);

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
			if (adapter.getCheckedItems().isEmpty()) {
				clearCheckedItems();
			} else {
				showDeleteDialog();
			}
		}
	};

	protected ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeTitle = getString(R.string.copy);

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
			if (!adapter.getCheckedItems().isEmpty()) {
				copyCheckedItems();
			}
			clearCheckedItems();
		}
	};

	protected ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_SINGLE);

			mode.setTitle(R.string.rename);

			isRenameActionMode = true;
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			isRenameActionMode = false;
			if (adapter.getCheckedItems().isEmpty()) {
				clearCheckedItems();
			} else {
				showRenameDialog();
			}
		}
	};

	protected ActionMode.Callback backPackModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeTitle = getString(R.string.backpack);

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
			if (adapter.getCheckedItems().isEmpty()) {
				clearCheckedItems();
			} else {
				packCheckedItems();
			}
		}
	};

	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack);
	}

	public void startCopyActionMode() {
		startActionMode(copyModeCallBack);
	}

	public void startRenameActionMode() {
		startActionMode(renameModeCallBack);
	}

	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack);
	}

	protected void startActionMode(ActionMode.Callback actionModeCallback) {
		if (isActionModeActive()) {
			return;
		}
		if (adapter.isEmpty()) {
			if (actionModeCallback.equals(copyModeCallBack)) {
				((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.copy));
			} else if (actionModeCallback.equals(deleteModeCallBack)) {
				((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
			} else if (actionModeCallback.equals(renameModeCallBack)) {
				((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.rename));
			}
		} else {
			actionMode = getActivity().startActionMode(actionModeCallback);
			BottomBar.hideBottomBar(getActivity());
			isRenameActionMode = actionModeCallback.equals(renameModeCallBack);
		}
	}

	public abstract void showDeleteDialog();

	protected abstract void deleteCheckedItems();

	protected abstract void copyCheckedItems();

	public abstract void showRenameDialog();

	public abstract boolean itemNameExists(String newName);

	public abstract void renameItem(String newName);

	protected abstract void packCheckedItems();

	@Override
	public boolean getActionModeActive() {
		return isActionModeActive();
	}

	@Override
	public void setActionModeActive(boolean actionModeActive) {
		throw new UnsupportedOperationException("Refactor INTERFACE!");
	}

	@Override
	public void handleAddButton() {
		throw new UnsupportedOperationException("Refactor INTERFACE!");
	}

	protected void showDeleteDialog(int titleId) {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_object_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedItems();
				clearCheckedItems();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
