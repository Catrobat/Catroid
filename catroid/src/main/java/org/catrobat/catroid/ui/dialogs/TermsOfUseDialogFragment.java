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
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.drone.ardrone.DroneInitializer;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.SettingsActivity;

public class TermsOfUseDialogFragment extends DialogFragment {
	private static final String TAG = TermsOfUseDialogFragment.class.getSimpleName();

	public static final String DIALOG_FRAGMENT_TAG = "dialog_terms_of_use";
	public static final String DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT = "dialog_terms_of_use_accept";

	CheckBox checkboxTermsOfUseAcceptedPermanently = null;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		Bundle fragmentDialogArguments = getArguments();
		boolean isOnPreStageActivity = false;
		if (fragmentDialogArguments != null) {
			isOnPreStageActivity = fragmentDialogArguments.getBoolean(DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT, false);
		}

		View view = View.inflate(getActivity(), R.layout.dialog_terms_of_use, null);

		TextView termsOfUseTextView = (TextView) view.findViewById(R.id.dialog_terms_of_use_text_view_info);
		TextView termsOfUseUrlTextView = (TextView) view.findViewById(R.id.dialog_terms_of_use_text_view_url);

		termsOfUseUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());

		String termsOfUseUrlStringText = null;

		AlertDialog.Builder termsOfUseDialogBuilder = new AlertDialog.Builder(getActivity()).setView(view).setTitle(
				R.string.dialog_terms_of_use_title);

		termsOfUseDialogBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (getActivity() instanceof PreStageActivity) {
					if (checkboxTermsOfUseAcceptedPermanently != null
							&& checkboxTermsOfUseAcceptedPermanently.isChecked()) {
						SettingsActivity.setTermsOfServiceAgreedPermanently(getActivity(), true);
						SettingsActivity.setTermsOfServiceJSAgreedPermanently(getActivity(), true);
					}
					DroneInitializer droneInitializer = ((PreStageActivity) getActivity()).getDroneInitialiser();
					if (droneInitializer != null && droneInitializer.checkRequirements()) {
						droneInitializer.checkDroneConnectivity();
					}
				}

				dialog.dismiss();
			}
		});

		termsOfUseDialogBuilder.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Log.d(TAG, "prevent canceling the dialog with back button");
				return true;
			}
		});

		if (isOnPreStageActivity) {
			checkboxTermsOfUseAcceptedPermanently = (CheckBox) view
					.findViewById(R.id.dialog_terms_of_use_check_box_agree_permanently);
			checkboxTermsOfUseAcceptedPermanently.setVisibility(CheckBox.VISIBLE);
			checkboxTermsOfUseAcceptedPermanently.setText(R.string.dialog_terms_of_use_parrot_reminder_do_not_remind_again);
			termsOfUseDialogBuilder.setCancelable(false);
			termsOfUseTextView.setText(R.string.dialog_terms_of_use_parrot_reminder_text);
			termsOfUseUrlStringText = getString(R.string.dialog_terms_of_use_link_text_parrot_reminder);
		} else {
			termsOfUseTextView.setText(R.string.dialog_terms_of_use_info);
			termsOfUseUrlStringText = getString(R.string.dialog_terms_of_use_link_text);
		}

		String termsOfUseUrl = getString(R.string.terms_of_use_link_template, Constants.CATROBAT_TERMS_OF_USE_URL,
				termsOfUseUrlStringText);

		termsOfUseUrlTextView.setText(Html.fromHtml(termsOfUseUrl));

		AlertDialog termsOfUseDialog = termsOfUseDialogBuilder.create();
		if (!isOnPreStageActivity) {
			termsOfUseDialog.setCanceledOnTouchOutside(true);
		} else {
			termsOfUseDialog.setCancelable(false);
			termsOfUseDialog.setCanceledOnTouchOutside(false);
		}

		return termsOfUseDialog;
	}
}
