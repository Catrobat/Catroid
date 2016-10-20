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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;

public class ConfirmUnpackBackgroundDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_unpack_background";

	private static final String TAG = ConfirmUnpackBackgroundDialog.class.getSimpleName();

	private Dialog confirmUnpackBackgroundDialog;

	private Sprite selectedSprite;
	private boolean delete;
	private boolean keepCurrentSprite;
	private boolean fromHiddenBackPack;
	private boolean asBackground;

	public ConfirmUnpackBackgroundDialog(Sprite selectedSprite, boolean delete, boolean keepCurrentSprite, boolean fromHiddenBackPack, boolean asBackground) {
		this.selectedSprite = selectedSprite;
		this.delete = delete;
		this.keepCurrentSprite = keepCurrentSprite;
		this.fromHiddenBackPack = fromHiddenBackPack;
		this.asBackground = asBackground;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_unpack_background, null);

		confirmUnpackBackgroundDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.unpack)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		confirmUnpackBackgroundDialog.setCanceledOnTouchOutside(true);
		confirmUnpackBackgroundDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		confirmUnpackBackgroundDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		confirmUnpackBackgroundDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});
			}
		});

		return confirmUnpackBackgroundDialog;
	}

	protected void handleOkButtonClick() {
		if (getActivity() == null) {
			Log.e(TAG, "handleOkButtonClick() Activity was null!");
			return;
		}

		Sprite unpackedSprite = BackPackSpriteController.getInstance().unpack(selectedSprite, delete,
				keepCurrentSprite, fromHiddenBackPack, asBackground);
		unpackedSprite.setName(getActivity().getString(R.string.background));
		returnToProjectActivity();

		dismiss();
	}

	private void returnToProjectActivity() {
		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getActivity().startActivity(intent);
	}
}
