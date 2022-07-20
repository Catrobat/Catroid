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
package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AboutDialogFragment extends DialogFragment {

	public static final String TAG = AboutDialogFragment.class.getSimpleName();

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_about, null);

		TextView developerUrlView = view.findViewById(R.id.dialog_about_text_view_url);
		developerUrlView.setMovementMethod(LinkMovementMethod.getInstance());

		String developerUrl = getString(R.string.about_link_template, Constants.ABOUT_POCKETCODE_LICENSE_URL,
				getString(R.string.dialog_about_license_link_text));

		developerUrlView.setText(Html.fromHtml(developerUrl));

		TextView aboutCatrobatView = view.findViewById(R.id.dialog_about_text_catrobat_url);
		aboutCatrobatView.setMovementMethod(LinkMovementMethod.getInstance());

		String aboutCatrobatUrl = getString(R.string.about_link_template, Constants.CATROBAT_ABOUT_URL,
				getString(R.string.dialog_about_catrobat_link_text));

		aboutCatrobatView.setText(Html.fromHtml(aboutCatrobatUrl));

		TextView aboutVersionNameTextView = view.findViewById(R.id.dialog_about_text_view_version_name);
		String versionCode = BuildConfig.FLAVOR.equals("pocketCodeBeta") ? "-" + BuildConfig.VERSION_CODE : "";
		String versionName = getString(R.string.app_name) + versionCode + " " + getString(R.string.dialog_about_version)
				+ " " + getString(R.string.android_version_prefix) + Utils.getVersionName(getActivity());
		aboutVersionNameTextView.setText(versionName);

		TextView aboutCatrobatVersionTextView = view.findViewById(R.id.dialog_about_text_view_catrobat_version_name);
		double catrobatVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
		String catrobatVersionName =
				getString(R.string.dialog_about_catrobat_language_version) + ": " + catrobatVersion;
		aboutCatrobatVersionTextView.setText(catrobatVersionName);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_about_title)
				.setView(view)
				.setPositiveButton(R.string.ok, null)
				.create();
	}
}
