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
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.catrobat.catroid.R;

public class RatePocketCodeDialogFragment extends DialogFragment {

	public static final String TAG = RatePocketCodeDialogFragment.class.getSimpleName();

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_rate_pocketcode, null);
		return new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.rating_dialog_title))
				.setView(view)
				.setPositiveButton(R.string.rating_dialog_rate_now, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
											+ getActivity().getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
						} catch (ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW,
									Uri.parse("https://play.google.com/store/apps/details?id="
											+ getActivity().getPackageName())));
						}
					}
				})

				.setNeutralButton(getString(R.string.rating_dialog_rate_later), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
								.putInt(UploadProgressDialogFragment.NUMBER_OF_UPLOADED_PROJECTS, 0).commit();
					}
				})

				.setNegativeButton(getString(R.string.rating_dialog_rate_never), null)
				.setCancelable(false)
				.create();
	}
}
