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
import android.os.Bundle;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;

public class NewLookDialog extends DialogFragment {

	public static final String TAG = "dialog_new_look";
	private AddLookInterface addLookInterface;

	public NewLookDialog(AddLookInterface addLookInterface) {
		this.addLookInterface = addLookInterface;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = View.inflate(getActivity(), R.layout.dialog_new_look, null);

		setupPaintroidButton(dialogView);
		setupMediaLibraryButton(dialogView);
		setupGalleryButton(dialogView);
		setupCameraButton(dialogView);
		setupDroneVideoButton(dialogView);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(dialogView);
		builder.setTitle(R.string.new_look_dialog_title);

		return builder.create();
	}

	private void setupPaintroidButton(View parentView) {
		View paintroidButton = parentView.findViewById(R.id.dialog_new_look_paintroid);

		paintroidButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addLookInterface.addLookDrawNewImage();
				NewLookDialog.this.dismiss();
			}
		});
	}

	private void setupMediaLibraryButton(View parentView) {
		View mediaLibraryButton = parentView.findViewById(R.id.dialog_new_look_media_library);

		mediaLibraryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addLookInterface.addLookMediaLibrary();
				dismiss();
			}
		});
	}

	private void setupGalleryButton(View parentView) {
		View galleryButton = parentView.findViewById(R.id.dialog_new_look_gallery);

		galleryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				addLookInterface.addLookChooseImage();
				dismiss();
			}
		});
	}

	private void setupCameraButton(View parentView) {
		View cameraButton = parentView.findViewById(R.id.dialog_new_look_camera);

		cameraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				addLookInterface.addLookFromCamera();
				dismiss();
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
				addLookInterface.addLookDroneVideo();
				dismiss();
			}
		});
	}

	public interface AddLookInterface {

		void addLookDrawNewImage();
		void addLookMediaLibrary();
		void addLookChooseImage();
		void addLookFromCamera();
		void addLookDroneVideo();
	}
}
