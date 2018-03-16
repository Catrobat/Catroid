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
import android.os.Bundle;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.HashSet;
import java.util.Set;

public class NewSceneDialogFragment extends TextInputDialogFragment {

	public static final String TAG = NewSceneDialogFragment.class.getSimpleName();

	private NewItemInterface<Scene> newItemInterface;
	private Project dstProject;

	public NewSceneDialogFragment(NewItemInterface<Scene> newItemInterface, Project dstProject) {
		super(R.string.new_scene_dialog, R.string.scene_name, null, false);
		this.newItemInterface = newItemInterface;
		this.dstProject = dstProject;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.text = getDefaultSceneName(getScope(dstProject));
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	protected boolean onPositiveButtonClick() {
		String name = inputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (getScope(dstProject).contains(name)) {
			inputLayout.setError(getString(R.string.name_already_exists));
			return false;
		} else {
			newItemInterface.addItem(new Scene(getActivity(), name, dstProject));
			return true;
		}
	}

	@Override
	protected void onNegativeButtonClick() {
	}

	private String getDefaultSceneName(Set<String> scope) {
		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			String name = getString(R.string.default_scene_name, i);
			if (!scope.contains(name)) {
				return name;
			}
		}
		return "";
	}

	private Set<String> getScope(Project project) {
		Set<String> scope = new HashSet<>();
		for (Scene item : project.getSceneList()) {
			scope.add(item.getName());
		}
		return scope;
	}
}
