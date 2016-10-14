/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.utils.TextSizeUtil;

public class NewLookDialog extends DialogFragment {

	public static final String TAG = "dialog_new_look";

	private LookFragment fragment = null;
	private DialogInterface.OnDismissListener onDismissListener;
	private DialogInterface.OnCancelListener onCancelListener;

	public static NewLookDialog newInstance() {
		return new NewLookDialog();
	}

	public void showDialog(Fragment fragment) {
		if (!(fragment instanceof LookFragment)) {
			throw new RuntimeException("This dialog (NewLookDialog) can only be called by the LookFragment.");
		}
		this.fragment = (LookFragment) fragment;
		show(fragment.getActivity().getFragmentManager(), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = View.inflate(getActivity(), R.layout.dialog_new_look, null);
		setupPaintroidButton(dialogView);
		setupGalleryButton(dialogView);
		setupCameraButton(dialogView);
		setupMediaLibraryButton(dialogView);
		setupDroneVideoButton(dialogView);

		AlertDialog.Builder dialogBuilder = new CustomAlertDialogBuilder(getActivity()).setView(dialogView).setTitle(
				R.string.new_look_dialog_title);

		final AlertDialog dialog = createDialog(dialogBuilder);
		dialog.setCanceledOnTouchOutside(true);

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				TextSizeUtil.enlargeViewGroup((ViewGroup) dialog.getWindow().getDecorView().getRootView());
			}
		});

		return dialog;
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
		this.onCancelListener = onCancelListener;
	}

	@Override
	public void onDismiss(final DialogInterface dialog) {
		super.onDismiss(dialog);
		if (onDismissListener != null) {
			onDismissListener.onDismiss(dialog);
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (onCancelListener != null) {
			onCancelListener.onCancel(dialog);
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

	private void setupDroneVideoButton(View parentView) {
		View droneVideoButton = parentView.findViewById(R.id.dialog_new_look_drone_video);
		View droneDialogItem = parentView.findViewById(R.id.dialog_new_look_drone);

		if (!SettingsActivity.isDroneSharedPreferenceEnabled(getActivity())) {
			droneVideoButton.setVisibility(View.GONE);
			droneDialogItem.setVisibility(View.GONE);
			return;
		}
		droneDialogItem.setVisibility(View.VISIBLE);
		droneVideoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fragment.addLookDroneVideo();
				NewLookDialog.this.dismiss();
			}
		});
	}
}
