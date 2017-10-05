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

package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.Locale;

public class RestrictedLoginDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_restricted_login";

	private AlertDialog restrictedLoginDialog;

	public RestrictedLoginDialog() {
	}

	public static RestrictedLoginDialog newInstance() {
		return new RestrictedLoginDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_restricted_login_dialog, null);

		TextView text = (TextView) rootView.findViewById(R.id.dialog_restricted_login_text);
		String link = Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())
				? getString(R.string.schoolreg_link_german)
				: getString(R.string.schoolreg_link);

		text.setText(text.getText().toString().replace("%1$s", link));
		Linkify.addLinks(text, Linkify.WEB_URLS);

		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
				.setView(rootView)
				.setTitle(R.string.dialog_restricted_login_title)
				.setPositiveButton(R.string.ok, null);

		restrictedLoginDialog = dialogBuilder.create();
		restrictedLoginDialog.setCanceledOnTouchOutside(false);

		restrictedLoginDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button okButton = restrictedLoginDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				okButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
								.putBoolean(Constants.RESTRICTED_LOGIN_ACCEPTED, true)
								.apply();

						if (!Utils.isUserLoggedIn(getActivity()) || !Utils.isCreateAtSchoolUser(getActivity())) {
							Utils.showPrivacyPolicyOrLoginDialog(getActivity());
						}
						dismiss();
					}
				});

				TextSizeUtil.enlargeViewGroup((ViewGroup) rootView.getRootView());
			}
		});

		return restrictedLoginDialog;
	}

	@Override
	public void onResume() {
		super.onResume();
		getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {
				return keyCode == android.view.KeyEvent.KEYCODE_BACK;
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}
}
