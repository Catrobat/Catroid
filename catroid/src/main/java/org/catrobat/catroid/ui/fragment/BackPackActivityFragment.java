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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.DividerUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;

public abstract class BackPackActivityFragment extends CheckBoxListFragment {

	protected ListView listView;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		DividerUtil.setDivider(getActivity(), listView);
	}

	protected ActionMode.Callback unpackModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			mode.setTitle(R.string.unpack);

			actionModeTitle = getString(R.string.unpack);
			addSelectAllActionModeButton(mode, menu);

			TextSizeUtil.enlargeActionMode(mode);
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
				unpackCheckedItems(false);
			}
		}
	};

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

			TextSizeUtil.enlargeActionMode(mode);
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
				showDeleteDialog(false);
			}
		}
	};

	public void startUnpackActionMode() {
		startActionMode(unpackModeCallBack);
	}

	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack);
	}

	protected void startActionMode(ActionMode.Callback actionModeCallback) {
		if (isActionModeActive()) {
			return;
		}

		if (adapter.isEmpty()) {
			if (actionModeCallback.equals(unpackModeCallBack)) {
				((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.unpack));
			} else if (actionModeCallback.equals(deleteModeCallBack)) {
				((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
			}
		} else {
			actionMode = getActivity().startActionMode(actionModeCallback);
			unregisterForContextMenu(getListView());
			BottomBar.hideBottomBar(getActivity());
		}
	}

	protected abstract void unpackCheckedItems(boolean singleItem);

	protected abstract void showDeleteDialog(boolean singleItem);

	protected abstract void deleteCheckedItems(boolean singleItem);

	protected void showDeleteDialog(int titleId, final boolean singleItem) {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_object_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedItems(singleItem);
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

	protected void checkEmptyBackgroundBackPack() {
		if (adapter.isEmpty()) {
			TextView emptyViewHeading = (TextView) getActivity().findViewById(R.id.backpack_text_heading);
			emptyViewHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60.0f);
			emptyViewHeading.setText(R.string.backpack);
			TextView emptyViewDescription = (TextView) getActivity().findViewById(R.id.backpack_text_description);
			emptyViewDescription.setText(R.string.is_empty);
		}
	}

	@Override
	public void clearCheckedItems() {
		super.clearCheckedItems();
		registerForContextMenu(getListView());
	}

	protected void showUnpackingCompleteToast(int itemCount) {
		String message = itemCount == 1 ? singleItemTitle : multipleItemsTitle;
		message += " " + getResources().getQuantityString(R.plurals.unpacking_items_plural, itemCount);
		ToastUtil.showSuccess(getActivity(), message);
	}
}
