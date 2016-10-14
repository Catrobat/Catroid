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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

public class UserBrickNameDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_userbrick_name";

	private static final String TAG = UserBrickNameDialog.class.getSimpleName();

	private EditText newUserBrickEditText;
	private UserBrickNameDialogInterface userBrickNameDialogInterface;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_userbrick_name, null);

		newUserBrickEditText = (EditText) dialogView.findViewById(R.id.dialog_userbrick_name_edittext);

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		String text = getActivity().getString(R.string.new_user_brick) + " " + currentSprite.getNextNewUserBrickId();
		newUserBrickEditText.setText(text);

		final Dialog newUserBrickDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.new_user_brick)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		newUserBrickDialog.setCanceledOnTouchOutside(true);
		newUserBrickDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		newUserBrickDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		newUserBrickDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(newUserBrickEditText, InputMethodManager.SHOW_IMPLICIT);

				newUserBrickEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (newUserBrickEditText.length() == 0) {
							((AlertDialog) newUserBrickDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
						} else {
							((AlertDialog) newUserBrickDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
						}
					}
				});

				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});

				TextSizeUtil.enlargeViewGroup((ViewGroup) newUserBrickDialog.getWindow().getDecorView().getRootView());
			}
		});

		return newUserBrickDialog;
	}

	protected void handleOkButtonClick() {
		String userBrickName = newUserBrickEditText.getText().toString().trim();

		if (getActivity() == null) {
			Log.e(TAG, "handleOkButtonClick() Activity was null!");
			return;
		}

		if (userBrickName.isEmpty()) {
			Utils.showErrorDialog(getActivity(), R.string.no_name, R.string.error_no_userbrick_name_entered);
			return;
		}

		dismiss();
		userBrickNameDialogInterface.onUserBrickNameEntered(userBrickName);
	}

	public void setUserBrickNameDialogInterface(UserBrickNameDialogInterface userBrickNameDialogInterface) {
		this.userBrickNameDialogInterface = userBrickNameDialogInterface;
	}

	public interface UserBrickNameDialogInterface {
		void onUserBrickNameEntered(String userBrickName);
	}
}
