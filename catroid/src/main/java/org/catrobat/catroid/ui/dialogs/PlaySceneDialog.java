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
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;

public class PlaySceneDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_play_scene";

	private static final String TAG = PlaySceneDialog.class.getSimpleName();

	private RadioButton playFirstScene;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_play_scene, null);

		Dialog playSceneDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		playSceneDialog.setCanceledOnTouchOutside(true);
		playSceneDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		playSceneDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setEnabled(true);
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});
			}
		});

		playFirstScene = (RadioButton) dialogView.findViewById(R.id.play_default_scene_radiobutton);
		playFirstScene.setChecked(true);

		String firstSceneText = String.format(getString(R.string.play_scene_dialog_default), ProjectManager
				.getInstance().getCurrentProject().getDefaultScene().getName());
		String currentSceneText = String.format(getString(R.string.play_scene_dialog_current), ProjectManager
				.getInstance().getCurrentScene().getName());

		playFirstScene.setText(firstSceneText);
		((RadioButton) dialogView.findViewById(R.id.play_current_scene_radiobutton)).setText(currentSceneText);

		return playSceneDialog;
	}

	protected void handleOkButtonClick() {
		if (getActivity() == null) {
			Log.e(TAG, "handleOkButtonClick() Activity was null!");
			return;
		}
		if (playFirstScene.isChecked()) {
			ProjectManager.getInstance().setSceneToPlay(ProjectManager.getInstance().getCurrentProject().getDefaultScene());
		} else {
			ProjectManager.getInstance().setSceneToPlay(ProjectManager.getInstance().getCurrentScene());
		}

		ProjectManager.getInstance().setStartScene(ProjectManager.getInstance().getSceneToPlay());

		if (getActivity() instanceof ProjectActivity) {
			((ProjectActivity) getActivity()).startPreStageActivity();
		}
		if (getActivity() instanceof ScriptActivity) {
			((ScriptActivity) getActivity()).startPreStageActivity();
		}
		if (getActivity() instanceof ProgramMenuActivity) {
			((ProgramMenuActivity) getActivity()).startPreStageActivity();
		}
		dismiss();
	}
}
