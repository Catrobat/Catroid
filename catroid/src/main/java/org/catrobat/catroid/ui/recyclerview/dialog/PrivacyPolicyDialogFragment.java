/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.utils.Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PrivacyPolicyDialogFragment extends DialogFragment{

	public static final String TAG = PrivacyPolicyDialogFragment.class.getSimpleName();

	;
	private static final String BUNDLE_FORCE_ACCEPT = "forceAccept";

	private boolean forceAccept = false;
	private boolean showDeleteAccountDialog = false;

	

	public PrivacyPolicyDialogFragment newInstance(boolean forceAccept) {
		PrivacyPolicyDialogFragment fragment = new PrivacyPolicyDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(BUNDLE_FORCE_ACCEPT, forceAccept);
		fragment.setArguments(bundle);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		if (bundle != null) {
			forceAccept = bundle.getBoolean(BUNDLE_FORCE_ACCEPT, false);
		}

		View view = View.inflate(getContext(), R.layout.dialog_privacy_policy, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_privacy_policy_title)
				.setView(view);
		if (forceAccept) {
			builder.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					if (showDeleteAccountDialog) {
						showDeleteAccountDialog();
					}
				}
			});
		} else {
			builder.setPositiveButton(R.string.ok, null);
		}

		return builder.create();

	}

	private void showDeleteAccountDialog() {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_privacy_policy_title)
				.setMessage(R.string.dialog_privacy_policy_declined_text)
				.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.logoutUser(getActivity());
					}
				})
				.setNegativeButton(R.string.delete_account, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getContext(), WebViewActivity.class)
								.putExtra(WebViewActivity.INTENT_PARAMETER_URL, Constants.CATROBAT_DELETE_ACCOUNT_URL);
						getActivity().startActivity(intent);
					}
				})
				.show();
	}

	public void findViewById(int menu_privacy_policy) {
	}
}
