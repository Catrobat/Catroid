/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * 		This file incorporates work covered by the following copyright and  
 * 		permission notice:  
 * 
 * 		Copyright (C) 2010 Tani Group 
 * 		http://android-demo.blogspot.com/
 *
 * 		Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * 		Unless required by applicable law or agreed to in writing, software
 * 		distributed under the License is distributed on an "AS IS" BASIS,
 * 		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 		See the License for the specific language governing permissions and
 * 		limitations under the License.
 */

package at.tugraz.ist.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.adapter.IconMenuAdapter;
import at.tugraz.ist.catroid.ui.adapter.IconMenuAdapter.CustomContextMenuItem;

public class CustomIconContextMenu extends DialogFragment implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

	private static final String ARGS_MENU_TITLE = "menu_title";
	
	private IconMenuAdapter menuAdapter;
	private IconContextMenuOnClickListener clickListener;
	
	public static CustomIconContextMenu newInstance(String menuTitle) {
		CustomIconContextMenu dialog = new CustomIconContextMenu();
		
		Bundle args = new Bundle();
		args.putString(ARGS_MENU_TITLE, menuTitle);
		dialog.setArguments(args);
		
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
		if (getDialog() != null && getRetainInstance())
			getDialog().setOnDismissListener(null);
		super.onDestroyView();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String menuTitle = getArguments().getString(ARGS_MENU_TITLE);
		
		Dialog dialog = new AlertDialog.Builder(getActivity())
			.setTitle(menuTitle)
			.setIcon(R.drawable.ic_dialog_menu_generic)
			.setAdapter(menuAdapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int position) {
					CustomContextMenuItem item = (CustomContextMenuItem) menuAdapter.getItem(position);
	
					if (clickListener != null) {
						clickListener.onClick(item.contextMenuItemId);
					}
				}
			})
			.setInverseBackgroundForced(true)
			.create();
		
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
