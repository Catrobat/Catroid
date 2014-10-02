/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.ui.fragment.SoundFragment;


public class NewSoundDialog extends DialogFragment{

	public static final String TAG = "dialog_new_sound";

	private SoundFragment fragment = null;

	public static NewSoundDialog newInstance(){
		return new NewSoundDialog();
	}

	public void showDialog(Fragment fragment) {
		if (!(fragment instanceof SoundFragment)) {
			throw new RuntimeException("This dialog (NewSoundDialog) can only be called by the SoundFragment.");
		}
		this.fragment = (SoundFragment) fragment;
		show(fragment.getActivity().getSupportFragmentManager(), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_sound, null);

		setupRecordSoundButton(dialogView);
		setupSelectSoundButton(dialogView);

		AlertDialog dialog;
		AlertDialog.Builder dialogBuilder = new CustomAlertDialogBuilder(getActivity()).setView(dialogView).setTitle(
				R.string.new_sound_dialog_title);

		dialog = createDialog(dialogBuilder);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;

	}

	private void setupRecordSoundButton(View parentView){
		View recordSoundButton = parentView.findViewById(R.id.dialog_new_sound_record_sound);

		recordSoundButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.addSoundRecordNewSound();
				NewSoundDialog.this.dismiss();
			}
		});

	}

	private void setupSelectSoundButton(View parentView){
		View selectSoundButton = parentView.findViewById(R.id.dialog_new_sound_open_chooser);

		selectSoundButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				fragment.addSoundSelectSound();
				NewSoundDialog.this.dismiss();
			}
		});
	}

	private AlertDialog createDialog(AlertDialog.Builder dialogBuilder) {
		return dialogBuilder.create();
	}
}
