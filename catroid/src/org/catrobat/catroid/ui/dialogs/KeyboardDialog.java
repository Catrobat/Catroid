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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.SendToPcBrick;

public class KeyboardDialog extends DialogFragment {

	private Dialog dialog;
	private View dialogView;
	private SendToPcBrick sendToPcBrick;
	private int key;

	public KeyboardDialog() {
	}

	public void initialize(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		Activity activity = (Activity) context;
		dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_send_to_pc_keyboard, null);
		builder.setView(dialogView).setTitle(R.string.dialog_custom_keyboard)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						KeyboardDialog.this.sendToPcBrick.setKey(KeyboardDialog.this.key);
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	public void startDialog() {
		dialog.show();
	}

	public View getDialogView() {
		return dialogView;
	}

	public void setSendToPcBrick(SendToPcBrick sendToPcBrick) {
		this.sendToPcBrick = sendToPcBrick;
	}

	public void setKey(int key) {
		this.key = key;
	}
}
