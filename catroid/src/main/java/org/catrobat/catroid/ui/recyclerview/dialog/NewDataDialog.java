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
package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DialogInputWatcher;

import java.util.List;

public class NewDataDialog extends DialogFragment {

	public static final String TAG = NewDataDialog.class.getSimpleName();

	private NewDataInterface newDataInterface;

	protected TextInputLayout inputLayout;
	protected RadioGroup radioGroup;
	protected CheckBox makeList;

	public void setNewDataInterface(NewDataInterface newDataInterface) {
		this.newDataInterface = newDataInterface;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View root = View.inflate(getActivity(), R.layout.dialog_new_user_data, null);

		inputLayout = root.findViewById(R.id.input);
		inputLayout.setHint(getString(R.string.data_label));
		radioGroup = root.findViewById(R.id.radio_group);
		makeList = root.findViewById(R.id.make_list);

		makeList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				getDialog().setTitle(isChecked ? R.string.formula_editor_list_dialog_title
						: R.string.formula_editor_variable_dialog_title);
			}
		});

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.formula_editor_variable_dialog_title)
				.setView(root)
				.setPositiveButton(R.string.ok, null)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						onCancel(dialog);
					}
				})
				.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (handlePositiveButtonClick()) {
							dismiss();
						}
					}
				});
				inputLayout.getEditText()
						.addTextChangedListener(new DialogInputWatcher(inputLayout, buttonPositive, false));
			}
		});
		return alertDialog;
	}

	protected boolean handlePositiveButtonClick() {
		String name = inputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		boolean isGlobal = (radioGroup.getCheckedRadioButtonId() == R.id.global);
		if (makeList.isChecked()) {
			if (!isListNameValid(name, isGlobal)) {
				inputLayout.setError(getString(R.string.name_already_exists));
				return false;
			}

			if (isGlobal) {
				dataContainer.addProjectUserList(name);
			} else {
				dataContainer.addSpriteUserList(name);
			}
		} else {
			if (!isVariableNameValid(name, isGlobal)) {
				inputLayout.setError(getString(R.string.name_already_exists));
				return false;
			}
			if (isGlobal) {
				dataContainer.addProjectUserVariable(name);
			} else {
				dataContainer.addSpriteUserVariable(name);
			}
		}

		newDataInterface.onNewData();
		return true;
	}

	protected boolean isListNameValid(String name, boolean isGlobal) {
		DataContainer currentData = ProjectManager.getInstance().getCurrentScene().getDataContainer();

		if (isGlobal) {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentScene().getSpriteList();
			return !currentData.existListInAnySprite(sprites, name)
					&& !currentData.existProjectListWithName(name);
		} else {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			return !currentData.existProjectListWithName(name)
					&& !currentData.existSpriteListByName(currentSprite, name);
		}
	}

	protected boolean isVariableNameValid(String name, boolean isGlobal) {
		DataContainer currentData = ProjectManager.getInstance().getCurrentScene().getDataContainer();

		if (isGlobal) {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentScene().getSpriteList();
			return !currentData.variableExistsInAnySprite(sprites, name)
					&& !currentData.existProjectVariableWithName(name);
		} else {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			return !currentData.existProjectVariableWithName(name)
					&& !currentData.spriteVariableExistsByName(currentSprite, name);
		}
	}

	public interface NewDataInterface {

		void onNewData();
	}
}
