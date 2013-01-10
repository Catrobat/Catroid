/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.ui.adapter.IconMenuAdapter;
import org.catrobat.catroid.ui.adapter.IconMenuAdapter.CustomContextMenuItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import org.catrobat.catroid.R;

public class CustomIconContextMenu extends DialogFragment implements DialogInterface.OnCancelListener,
		DialogInterface.OnDismissListener {

	private static final String BUNDLE_ARGUMENTS_MENU_TITLE = "menu_title";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_custom_icon_context_menu";

	private IconMenuAdapter menuAdapter;
	private IconContextMenuOnClickListener clickListener;

	public static CustomIconContextMenu newInstance(String menuTitle) {
		CustomIconContextMenu dialog = new CustomIconContextMenu();

		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_MENU_TITLE, menuTitle);
		dialog.setArguments(arguments);

		return dialog;
	}

	public void setAdapter(IconMenuAdapter adapter) {
		menuAdapter = adapter;
	}

	public void setOnClickListener(IconContextMenuOnClickListener listener) {
		clickListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setOnDismissListener(null);
		}
		super.onDestroyView();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String menuTitle = getArguments().getString(BUNDLE_ARGUMENTS_MENU_TITLE);

		Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle(menuTitle)
				.setIcon(R.drawable.ic_dialog_menu_generic)
				.setAdapter(menuAdapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int position) {
						CustomContextMenuItem item = (CustomContextMenuItem) menuAdapter.getItem(position);

						if (clickListener != null) {
							clickListener.onClick(item.contextMenuItemId);
						}
					}
				}).setInverseBackgroundForced(true).create();

		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dismiss();
			}
		});

		return dialog;
	}

	public interface IconContextMenuOnClickListener {
		public abstract void onClick(int menuId);
	}
}
