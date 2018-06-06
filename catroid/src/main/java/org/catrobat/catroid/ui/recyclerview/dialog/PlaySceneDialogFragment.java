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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;

public class PlaySceneDialogFragment extends DialogFragment {

	public static final String TAG = PlaySceneDialogFragment.class.getSimpleName();

	private PlaySceneInterface playSceneInterface;
	private RadioGroup radioGroup;

	public PlaySceneDialogFragment(PlaySceneInterface playSceneInterface) {
		this.playSceneInterface = playSceneInterface;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		radioGroup = (RadioGroup) View.inflate(getActivity(), R.layout.dialog_play_scene, null);

		String defaultScene = String.format(getString(R.string.play_scene_dialog_default), ProjectManager
				.getInstance().getCurrentProject().getDefaultScene().getName());
		String currentScene = String.format(getString(R.string.play_scene_dialog_current), ProjectManager
				.getInstance().getCurrentlyEditedScene().getName());

		((RadioButton) radioGroup.findViewById(R.id.play_default_scene)).setText(defaultScene);
		((RadioButton) radioGroup.findViewById(R.id.play_current_scene)).setText(currentScene);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.play_scene_dialog_title)
				.setView(radioGroup)
				.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButtonClick();
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.create();
	}

	protected void handleOkButtonClick() {
		ProjectManager projectManager = ProjectManager.getInstance();

		switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.play_default_scene:
				projectManager.setCurrentlyPlayingScene(projectManager.getCurrentProject().getDefaultScene());
				break;
			case R.id.play_current_scene:
				projectManager.setCurrentlyPlayingScene(projectManager.getCurrentlyEditedScene());
				break;
			default:
				throw new IllegalStateException(TAG + ": Cannot find RadioButton.");
		}

		projectManager.setStartScene(ProjectManager.getInstance().getCurrentlyPlayingScene());
		playSceneInterface.startPreStageActivity();
	}

	public interface PlaySceneInterface {

		void startPreStageActivity();
	}
}
