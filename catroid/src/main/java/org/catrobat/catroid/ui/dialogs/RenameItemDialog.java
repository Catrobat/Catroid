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

import android.os.Bundle;

import org.catrobat.catroid.ui.fragment.ListActivityFragment;
import org.catrobat.catroid.utils.Utils;

public class RenameItemDialog extends ListFragmentDialog {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename";

	private static final String BUNDLE_ARGUMENTS_DIALOG_TITLE = "title_id";
	private static final String BUNDLE_ARGUMENTS_INPUT_TITLE = "input_tag_id";
	private static final String BUNDLE_ARGUMENTS_DIALOG_ERROR = "error_id";
	private static final String BUNDLE_ARGUMENTS_CURRENT_NAME = "current_name";

	private int titleId;
	private int inputTitleId;
	private int errorId;
	private String currentName;

	public static RenameItemDialog newInstance(int titleId, int inputTitleId, int errorId, String currentName) {
		RenameItemDialog dialog = new RenameItemDialog();

		Bundle arguments = new Bundle();
		arguments.putInt(BUNDLE_ARGUMENTS_DIALOG_TITLE, titleId);
		arguments.putInt(BUNDLE_ARGUMENTS_INPUT_TITLE, inputTitleId);
		arguments.putInt(BUNDLE_ARGUMENTS_DIALOG_ERROR, errorId);
		arguments.putString(BUNDLE_ARGUMENTS_CURRENT_NAME, currentName);
		dialog.setArguments(arguments);

		return dialog;
	}

	@Override
	protected void initialize() {
		titleId = getArguments().getInt(BUNDLE_ARGUMENTS_DIALOG_TITLE);
		inputTitleId = getArguments().getInt(BUNDLE_ARGUMENTS_INPUT_TITLE);
		inputTitle.setText(inputTitleId);
		errorId = getArguments().getInt(BUNDLE_ARGUMENTS_DIALOG_ERROR);
		currentName = getArguments().getString(BUNDLE_ARGUMENTS_CURRENT_NAME);
		input.setText(currentName);
	}

	@Override
	protected boolean handleOkButton() {
		String newName = input.getText().toString().trim();

		if (newName.equals(currentName)) {
			dismiss();
			return false;
		}

		if (((ListActivityFragment) getTargetFragment()).itemNameExists(newName)) {
			Utils.showErrorDialog(getActivity(), errorId);
			return false;
		}

		if (newName.isEmpty()) {
			Utils.showErrorDialog(getActivity(), errorId);
			return false;
		}

		((ListActivityFragment) getTargetFragment()).renameItem(newName);
		return true;
	}

	@Override
	protected String getTitle() {
		return getString(titleId);
	}

	@Override
	protected String getHint() {
		return null;
	}
}
