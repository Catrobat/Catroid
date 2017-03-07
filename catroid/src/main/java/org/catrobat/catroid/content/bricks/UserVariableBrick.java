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

package org.catrobat.catroid.content.bricks;

import android.widget.Spinner;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;

public abstract class UserVariableBrick extends FormulaBrick implements NewDataDialog.NewVariableDialogListener {

	protected UserVariable userVariable;

	@XStreamOmitField
	protected BackPackedVariableData backPackedData;

	private void updateUserVariableIfDeleted(UserVariableAdapterWrapper userVariableAdapterWrapper) {
		if (userVariable != null && (userVariableAdapterWrapper.getPositionOfItem(userVariable) == 0)) {
			userVariable = null;
		}
	}

	protected void setSpinnerSelection(Spinner variableSpinner, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = (UserVariableAdapterWrapper) variableSpinner
				.getAdapter();

		updateUserVariableIfDeleted(userVariableAdapterWrapper);

		if (userVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(userVariable), true);
		} else if (newUserVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(newUserVariable), true);
			userVariable = newUserVariable;
		} else {
			variableSpinner.setSelection(userVariableAdapterWrapper.getCount() - 1, true);
			userVariable = userVariableAdapterWrapper.getItem(userVariableAdapterWrapper.getCount() - 1);
		}
	}

	@Override
	public void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = ((UserVariableAdapterWrapper) spinnerToUpdate
				.getAdapter());
		userVariableAdapterWrapper.notifyDataSetChanged();
		setSpinnerSelection(spinnerToUpdate, newUserVariable);

		for (Brick brick : ProjectManager.getInstance().getCurrentSprite().getAllBricks()) {
			if (brick instanceof UserVariableBrick && ((UserVariableBrick) brick).getUserVariable() == null) {
				if (brick instanceof SetVariableBrick) {
					Spinner spinner = (Spinner) ((SetVariableBrick) brick).view.findViewById(R.id.set_variable_spinner);
					setSpinnerSelection(spinner, newUserVariable);
				} else if (brick instanceof ShowTextBrick) {
					Spinner spinner = (Spinner) ((ShowTextBrick) brick).view.findViewById(R.id.show_variable_spinner);
					setSpinnerSelection(spinner, newUserVariable);
				} else if (brick instanceof HideTextBrick) {
					Spinner spinner = (Spinner) ((HideTextBrick) brick).view.findViewById(R.id.hide_variable_spinner);
					setSpinnerSelection(spinner, newUserVariable);
				} else if (brick instanceof ChangeVariableBrick) {
					Spinner spinner = (Spinner) ((ChangeVariableBrick) brick).view.findViewById(R.id
							.change_variable_spinner);
					setSpinnerSelection(spinner, newUserVariable);
				}
			}
		}
	}

	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
	}

	public UserVariable getUserVariable() {
		return userVariable;
	}

	public BackPackedVariableData getBackPackedData() {
		return backPackedData;
	}

	public void setBackPackedData(BackPackedVariableData backPackedData) {
		this.backPackedData = backPackedData;
	}

	void updateUserVariableReference(Scene into, Scene from) {
		UserVariable variable;
		if (from.existProjectVariable(userVariable)) {
			variable = into.getProjectVariableWithName(userVariable.getName());
			if (variable == null) {
				variable = into.getDataContainer().addProjectUserVariable(userVariable.getName());
			}
		} else {
			Sprite sprite = from.getSpriteByUserVariable(userVariable);
			if (sprite == null || !from.existSpriteVariable(userVariable, sprite)) {
				return;
			}
			variable = into.getDataContainer().addSpriteVariableIfDoesNotExist(userVariable.getName(),
					into.getSpriteBySpriteName(sprite.getName()));
		}
		if (variable != null) {
			userVariable = variable;
		}
	}

	@Override
	public boolean isEqualBrick(Brick brick, Scene mergeResult, Scene current) {
		if (!super.isEqualBrick(brick, mergeResult, current)) {
			return false;
		}
		UserVariable first = this.getUserVariable();
		UserVariable second = ((UserVariableBrick) brick).getUserVariable();
		if (!first.getName().equals(second.getName())) {
			return false;
		}
		boolean firstIsProjectVariable = mergeResult.getDataContainer().existProjectVariable(first);
		boolean secondIsProjectVariable = current.getDataContainer().existProjectVariable(second);

		return (firstIsProjectVariable && secondIsProjectVariable)
				|| (!firstIsProjectVariable && !secondIsProjectVariable);
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
		Integer type = DataContainer.USER_DATA_EMPTY;
		if (userVariable != null) {
			Scene currentScene = ProjectManager.getInstance().getCurrentScene();
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			DataContainer dataContainer = currentScene.getDataContainer();
			type = dataContainer.getTypeOfUserVariable(userVariable.getName(), currentSprite);
		}
		if (backPackedData == null) {
			backPackedData = new BackPackedVariableData();
		}
		this.backPackedData.userVariable = userVariable;
		this.backPackedData.userVariableType = type;
	}
}
