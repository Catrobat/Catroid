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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.SoundFragment;

public class NewSoundDialog extends DialogFragment {

	public static final String TAG = "dialog_new_sound";

	private SoundFragment fragment = null;
	private DialogInterface.OnDismissListener onDismissListener;

	public static NewSoundDialog newInstance() {
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
		View dialogView = LayoutInflater.from(getActivity())
				.inflate(R.layout.dialog_new_sound, (ViewGroup) getView(), false);
		setupRecordButton(dialogView);
		setupGalleryButton(dialogView);
		setupMediaLibraryButton(dialogView);

		AlertDialog dialog;
		AlertDialog.Builder dialogBuilder = new CustomAlertDialogBuilder(getActivity()).setView(dialogView).setTitle(
				R.string.new_sound_dialog_title);

		dialog = createDialog(dialogBuilder);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
		this.onDismissListener = listener;
	}

	@Override
	public void onDismiss(final DialogInterface dialog) {
		super.onDismiss(dialog);
		if (onDismissListener != null) {
			onDismissListener.onDismiss(dialog);
		}
	}

	private AlertDialog createDialog(AlertDialog.Builder dialogBuilder) {
		return dialogBuilder.create();
	}

	private void setupRecordButton(View parentView) {
		View recordButton = parentView.findViewById(R.id.dialog_new_sound_recorder);

		recordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					fragment.addSoundRecord();
					NewSoundDialog.this.dismiss();
				}
		});
	}

	private void setupGalleryButton(View parentView) {
		View galleryButton = parentView.findViewById(R.id.dialog_new_sound_galery);

		galleryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				fragment.addSoundChooseFile();
				NewSoundDialog.this.dismiss();
			}
		});
	}

	private void setupMediaLibraryButton(View parentView) {
		View mediaLibraryButton = parentView.findViewById(R.id.dialog_new_sound_media_library);

		mediaLibraryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fragment.addSoundMediaLibrary();
				NewSoundDialog.this.dismiss();
			}
		});
	}
}
