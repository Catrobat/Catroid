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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;

public class PrivacyPolicyDialogFragment extends DialogFragment {

	public interface DialogAction {
		void onClick();
	}

	public static final String DIALOG_FRAGMENT_TAG = "dialog_privacy_policy";
	private static final String PREFS_KEY_USER_ACCEPTED_PRIVACY_POLICY = "USER_ACCEPTED_PRIVACY_POLICY";

	private DialogAction onAccept;
	private boolean forceAccept = false;
	private boolean showDeleteAccountDialog = false;
	private Activity activity;

	public PrivacyPolicyDialogFragment() {
	}

	public PrivacyPolicyDialogFragment(final DialogAction onAccept, boolean showDeleteAccountDialog) {
		forceAccept = true;
		this.onAccept = onAccept;
		this.showDeleteAccountDialog = showDeleteAccountDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {

		activity = getActivity();
		View view = View.inflate(activity, R.layout.dialog_privacy_policy, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view).setTitle(R.string
				.dialog_privacy_policy_title);

		if (!forceAccept) {
			builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		} else {
			builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					setUserHasAcceptedPrivacyPolicy(activity, true);
					onAccept.onClick();
					dialog.cancel();
				}
			});
			builder.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();

					if (showDeleteAccountDialog) {
						showDeleteAccountDialog();
					}
				}
			});
		}
		return builder.create();
	}

	private void showDeleteAccountDialog() {
		CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(activity);

		builder.setTitle(R.string.dialog_privacy_policy_title);
		builder.setMessage(R.string.dialog_privacy_policy_declined_text);
		builder.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Utils.logoutUser(activity, false);
			}
		});
		builder.setNegativeButton(R.string.delete_account, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Intent deleteAccountIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants
						.CATROBAT_DELETE_ACCOUNT_URL));
				activity.startActivity(deleteAccountIntent);
			}
		});
		builder.create().show();
	}

	public static boolean userHasAcceptedPrivacyPolicy(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				PREFS_KEY_USER_ACCEPTED_PRIVACY_POLICY, false);
	}

	private static void setUserHasAcceptedPrivacyPolicy(Context context, boolean accepted) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(
				PREFS_KEY_USER_ACCEPTED_PRIVACY_POLICY, accepted).apply();
	}
}
