/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class TermsOfUseDialogFragment extends DialogFragment {

	public static final String TAG = TermsOfUseDialogFragment.class.getSimpleName();

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_terms_of_use, null);

		TextView termsOfUseTextView = view.findViewById(R.id.dialog_terms_of_use_text_view_info);
		TextView termsOfUseUrlTextView = view.findViewById(R.id.dialog_terms_of_use_text_view_url);

		termsOfUseUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());

		String termsOfUseUrlStringText;

		AlertDialog.Builder termsOfUseDialogBuilder = new AlertDialog.Builder(getActivity())
				.setView(view)
				.setTitle(R.string.dialog_terms_of_use_title);

		termsOfUseDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		termsOfUseTextView.setText(R.string.dialog_terms_of_use_info);
		termsOfUseUrlStringText = getString(R.string.dialog_terms_of_use_link_text);

		String termsOfUseUrl = getString(R.string.terms_of_use_link_template, Constants.CATROBAT_TERMS_OF_USE_URL,
				termsOfUseUrlStringText);

		termsOfUseUrlTextView.setText(Html.fromHtml(termsOfUseUrl));

		AlertDialog termsOfUseDialog = termsOfUseDialogBuilder.create();
		termsOfUseDialog.setCanceledOnTouchOutside(true);

		return termsOfUseDialog;
	}
}
