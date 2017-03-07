/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.utils.Utils;

public class NewSceneDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_scene";

	public static final String TAG = NewSceneDialog.class.getSimpleName();

	private EditText newSceneEditText;
	private Dialog newSceneDialog;
	private OnNewSceneListener onNewSceneListener;
	private boolean forBrick;
	private boolean fromSpriteOverview;

	public NewSceneDialog(boolean forBrick, boolean fromSpriteOverview) {
		this.forBrick = forBrick;
		this.fromSpriteOverview = fromSpriteOverview;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_scene, null);

		newSceneEditText = (EditText) dialogView.findViewById(R.id.scene_name_edittext);

		String sceneName = Utils.searchForNonExistingSceneName(getString(R.string.default_scene_name), 1, false);
		newSceneEditText.setHint(sceneName);

		newSceneDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.new_scene_dialog_title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		newSceneDialog.setCanceledOnTouchOutside(true);
		newSceneDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		newSceneDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		newSceneDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(newSceneEditText, InputMethodManager.SHOW_IMPLICIT);

				((AlertDialog) newSceneDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);

				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});
			}
		});

		return newSceneDialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (!forBrick) {
			return;
		}
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Brick brick : sprite.getListWithAllBricks()) {
					if (brick instanceof SceneStartBrick) {
						((SceneStartBrick) brick).setSpinnerSelection();
					}
					if (brick instanceof SceneTransitionBrick) {
						((SceneTransitionBrick) brick).setSpinnerSelection();
					}
				}
			}
		}
	}

	protected void handleOkButtonClick() {
		final Project currentProject = ProjectManager.getInstance().getCurrentProject();
		String sceneName = newSceneEditText.getText().toString().trim();

		if (getActivity() == null) {
			Log.e(TAG, "handleOkButtonClick() Activity was null!");
			return;
		}

		if (sceneName.isEmpty()) {
			sceneName = Utils.searchForNonExistingSceneName(getString(R.string.default_scene_name), 1,
					false);
		}

		if (currentProject.getSceneOrder().contains(sceneName)) {
			Utils.showErrorDialog(getActivity(), R.string.name_exists, R.string.error_scene_exists);
			return;
		}

		Scene scene = new Scene(getActivity(), sceneName, currentProject);
		currentProject.addScene(scene);
		setSceneAndOpenIt(scene);
		dismiss();
	}

	public interface OnNewSceneListener {
		void onNewScene(Scene scene);
	}

	public void setOnNewSceneListener(OnNewSceneListener listener) {
		onNewSceneListener = listener;
	}

	private void setSceneAndOpenIt(Scene scene) {
		ProjectManager.getInstance().saveProject(getActivity());
		ProjectManager.getInstance().setCurrentScene(scene);

		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

		if (forBrick) {
			onNewSceneListener.onNewScene(scene);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (ProjectManager.getInstance().getCurrentProject().getSceneList().size() == 2) {
				ProjectManager.getInstance().setHandleNewSceneFromScriptActivity();
			}
		}
		Intent intentPrev = getActivity().getIntent();
		intentPrev.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
		getActivity().finish();
		if (!fromSpriteOverview && ProjectManager.getInstance().getCurrentProject().getSceneList().size() > 1) {
			startActivity(intentPrev);
		}
		startActivity(intent);
	}
}
