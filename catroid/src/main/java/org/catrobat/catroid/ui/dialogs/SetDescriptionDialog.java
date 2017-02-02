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

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.base.InputDialog;

public class SetDescriptionDialog extends InputDialog {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_set_description";

	private ChangeDescriptionInterface descriptionInterface;

	public SetDescriptionDialog(int title, int inputLabel, String previousText, ChangeDescriptionInterface
			descriptionInterface) {
		super(title, inputLabel, previousText, true);
		this.descriptionInterface = descriptionInterface;
	}

	@Override
	protected View inflateLayout() {
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_text_input, null);
		EditText input = (EditText) view.findViewById(R.id.edit_text);
		input.setSingleLine(false);
		return view;
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String newDescription = input.getText().toString().trim();
		descriptionInterface.setDescription(newDescription);
		return true;
	}

	@Override
	protected void handleNegativeButtonClick() {
	}

	public interface ChangeDescriptionInterface {

		void setDescription(String description);
	}
}
