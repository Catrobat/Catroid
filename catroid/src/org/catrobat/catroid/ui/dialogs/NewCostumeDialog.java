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

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.CostumeFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class NewCostumeDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_costume";
	public static final String COSTUME_FRAGMENT_KEY = "dialog_new_costume_costume_fragment";

	public static final int FROM_CAMERA_INDEX = 0;
	public static final int FROM_GALLERY_INDEX = 1;

	private CostumeFragment costumeFragment = null;

	public void showDialog(FragmentManager fragmentManager, CostumeFragment fragment) {
		costumeFragment = fragment;
		show(fragmentManager, DIALOG_FRAGMENT_TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(R.string.new_costume_dialog_title).setItems(R.array.new_costume_chooser,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int index) {
						if (costumeFragment == null) {
							return;
						}
						switch (index) {
							case FROM_CAMERA_INDEX:
								costumeFragment.selectImageFromCamera();
								break;

							case FROM_GALLERY_INDEX:
								costumeFragment.selectImageFromGallery();
								break;
							default:
								break;
						}
					}
				});

		return builder.create();
	}
}
