/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.drone.DroneInitializer;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.SettingsActivity;

public class TermsOfUseDialogFragment extends DialogFragment {
	private static final String TAG = TermsOfUseDialogFragment.class.getSimpleName();

	public static final String DIALOG_FRAGMENT_TAG = "dialog_terms_of_use";
	public static final String DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT = "dialog_terms_of_use_accept";

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		Bundle fragmentDialogArguments = getArguments();
		boolean acceptTermsOfUse = false;
		if (fragmentDialogArguments != null) {
			acceptTermsOfUse = fragmentDialogArguments.getBoolean(DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT, false);
		}

		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_terms_of_use, null);

		final CheckBox checkBoxTermsOfUseAcceptedPermanently = (CheckBox) view
				.findViewById(R.id.dialog_terms_of_use_check_box_agree_permanently);

		TextView termsOfUseUrlTextView = (TextView) view.findViewById(R.id.dialog_terms_of_use_text_view_url);
		termsOfUseUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());

		String termsOfUseUrl = getString(R.string.terms_of_use_link_template, Constants.CATROBAT_TERMS_OF_USE_URL,
				getString(R.string.dialog_terms_of_use_link_text));

		termsOfUseUrlTextView.setText(Html.fromHtml(termsOfUseUrl));

		AlertDialog.Builder termsOfUseDialogBuilder = new AlertDialog.Builder(getActivity()).setView(view).setTitle(
				R.string.dialog_terms_of_use_title);

		if (!acceptTermsOfUse) {
			termsOfUseDialogBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		} else {
			termsOfUseDialogBuilder.setNegativeButton(R.string.dialog_terms_of_use_do_not_agree,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							getActivity().finish();
							dialog.dismiss();
						}
					});
			termsOfUseDialogBuilder.setPositiveButton(R.string.dialog_terms_of_use_agree,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							if (checkBoxTermsOfUseAcceptedPermanently.isChecked()) {
								SettingsActivity.setTermsOfServiceAgreedPermanently(getActivity(), true);
							}
							dialog.dismiss();
							DroneInitializer droneInitializer = ((PreStageActivity) getActivity())
									.getDroneInitializer();
							if (droneInitializer != null) {
								droneInitializer.initialiseDrone();
							}
						}
					});
			termsOfUseDialogBuilder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					Log.d(TAG, "prevent canceling the dialog with back button");
					return true;
				}
			});

			checkBoxTermsOfUseAcceptedPermanently.setVisibility(CheckBox.VISIBLE);
			checkBoxTermsOfUseAcceptedPermanently.setText(R.string.dialog_terms_of_use_agree_permanent);
			termsOfUseDialogBuilder.setCancelable(false);
		}

		AlertDialog termsOfUseDialog = termsOfUseDialogBuilder.create();
		if (!acceptTermsOfUse) {
			termsOfUseDialog.setCanceledOnTouchOutside(true);
		} else {
			termsOfUseDialog.setCancelable(false);
			termsOfUseDialog.setCanceledOnTouchOutside(false);
		}

		return termsOfUseDialog;
	}
}
