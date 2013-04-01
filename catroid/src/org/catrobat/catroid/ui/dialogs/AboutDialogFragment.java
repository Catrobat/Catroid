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

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutDialogFragment extends DialogFragment {
	public static final String DIALOG_FRAGMENT_TAG = "dialog_about_catroid";

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_about, null);

		TextView aboutUrlTextView = (TextView) view.findViewById(R.id.dialog_about_text_view_url);
		aboutUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());

		String aboutUrl = getString(R.string.about_link_template, getString(R.string.about_catroid_license_url),
				getString(R.string.dialog_about_catroid_license_link_text));

		aboutUrlTextView.setText(Html.fromHtml(aboutUrl));

		TextView aboutVersionNameTextView = (TextView) view.findViewById(R.id.dialog_about_text_view_version_name);
		String versionName = Utils.getVersionName(getActivity());
		aboutVersionNameTextView.setText(versionName);

		Dialog aboutDialog = new AlertDialog.Builder(getActivity()).setView(view)
				.setTitle(getString(R.string.dialog_about_title))
				.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).create();
		aboutDialog.setCanceledOnTouchOutside(true);

		return aboutDialog;
	}
}
