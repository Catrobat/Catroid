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
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.LookFragment;

public class NewLookDialog extends DialogFragment {

	public static final String TAG = "dialog_new_look";

	private LookFragment fragment = null;
	private DialogInterface.OnDismissListener onDismissListener;

	public static NewLookDialog newInstance() {
		return new NewLookDialog();
	}

	public void showDialog(Fragment fragment) {
		if (!(fragment instanceof LookFragment)) {
			throw new RuntimeException("This dialog (NewLookDialog) can only be called by the LookFragment.");
		}
		this.fragment = (LookFragment) fragment;
		show(fragment.getActivity().getSupportFragmentManager(), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_look, null);
		setupPaintroidButton(dialogView);
		setupGalleryButton(dialogView);
		setupCameraButton(dialogView);
		setupMediaLibraryButton(dialogView);

		AlertDialog dialog;
		AlertDialog.Builder dialogBuilder = new CustomAlertDialogBuilder(getActivity()).setView(dialogView).setTitle(
				R.string.new_look_dialog_title);

		dialog = createDialog(dialogBuilder);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
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

	private void setupPaintroidButton(View parentView) {
		View paintroidButton = parentView.findViewById(R.id.dialog_new_look_paintroid);

		final Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
				Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		paintroidButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (LookController.getInstance().checkIfPocketPaintIsInstalled(intent, getActivity())) {
					fragment.addLookDrawNewImage();
					NewLookDialog.this.dismiss();
				}
			}
		});
	}

	private void setupGalleryButton(View parentView) {
		View galleryButton = parentView.findViewById(R.id.dialog_new_look_gallery);

		galleryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				fragment.addLookChooseImage();
				NewLookDialog.this.dismiss();
			}
		});
	}

	private void setupCameraButton(View parentView) {
		View cameraButton = parentView.findViewById(R.id.dialog_new_look_camera);

		cameraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				fragment.addLookFromCamera();
				NewLookDialog.this.dismiss();
			}
		});
	}

	private void setupMediaLibraryButton(View parentView) {
		View mediaLibraryButton = parentView.findViewById(R.id.dialog_new_look_media_library);

		mediaLibraryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fragment.addLookMediaLibrary();
				NewLookDialog.this.dismiss();
			}
		});
	}
}
