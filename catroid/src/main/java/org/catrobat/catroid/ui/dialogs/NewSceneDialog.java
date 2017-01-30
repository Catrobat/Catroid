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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;

public class NewSceneDialog extends TextDialog {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_scene";
	private NewSceneInterface newSceneInterface;

	public NewSceneDialog(String defaultSceneName, NewSceneInterface newSceneInterface) {
		super(R.string.new_scene_dialog_title, R.string.scene_name, "", true, defaultSceneName);
		this.newSceneInterface = newSceneInterface;
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String newName = input.getText().toString().trim();

		if (newName.isEmpty()) {
			newName = hint;
		}

		if (ProjectManager.getInstance().getCurrentProject().getSceneOrder().contains(newName)) {
			input.setError(getString(R.string.name_already_exists));
			return false;
		}

		newSceneInterface.addAndOpenNewScene(newName);
		return true;
	}

	@Override
	protected void handleNegativeButtonClick() {
	}

	public interface NewSceneInterface {

		void addAndOpenNewScene(String sceneName);
	}
}
