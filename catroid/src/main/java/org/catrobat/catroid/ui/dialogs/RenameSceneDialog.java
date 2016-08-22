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

import android.content.Intent;
import android.os.Bundle;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.utils.Utils;

public class RenameSceneDialog extends TextDialog {

	private static final String BUNDLE_ARGUMENTS_OLD_SCENE_NAME = "old_scene_name";
	public static final String EXTRA_NEW_SCENE_NAME = "new_scene_name";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename_scene";

	private String oldSceneName;

	public static RenameSceneDialog newInstance(String oldSceneName) {
		RenameSceneDialog dialog = new RenameSceneDialog();

		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_OLD_SCENE_NAME, oldSceneName);
		dialog.setArguments(arguments);

		return dialog;
	}

	@Override
	protected void initialize() {
		oldSceneName = getArguments().getString(BUNDLE_ARGUMENTS_OLD_SCENE_NAME);
		input.setText(oldSceneName);
		inputTitle.setText(R.string.scene_name);
	}

	@Override
	protected boolean handleOkButton() {
		String newSceneName = input.getText().toString().trim();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (projectManager.sceneExists(newSceneName) && !newSceneName.equalsIgnoreCase(oldSceneName)) {
			Utils.showErrorDialog(getActivity(), R.string.scenename_already_exists);
			return false;
		}

		if (newSceneName.equals(oldSceneName)) {
			dismiss();
			return false;
		}

		if (!newSceneName.isEmpty() && !newSceneName.equalsIgnoreCase("")) {
			Intent intent = new Intent(ScriptActivity.ACTION_SCENE_RENAMED);
			intent.putExtra(EXTRA_NEW_SCENE_NAME, newSceneName);
			getActivity().sendBroadcast(intent);
		} else {
			Utils.showErrorDialog(getActivity(), R.string.scenename_invalid);
			return false;
		}

		return true;
	}

	@Override
	protected String getTitle() {
		return getString(R.string.rename_scene_dialog);
	}

	@Override
	protected String getHint() {
		return getString(R.string.new_scene_dialog_default_scene_name);
	}
}
