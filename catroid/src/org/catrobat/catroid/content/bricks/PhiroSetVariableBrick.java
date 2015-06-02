/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.content.Context;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;

public class PhiroSetVariableBrick extends SetVariableBrick {

	public PhiroSetVariableBrick(Formula variableFormula, UserVariable userVariable) {
		this.userVariable = userVariable;
		initializeBrickFields(variableFormula);
	}

	private void initializeBrickFields(Formula variableFormula) {
		addAllowedBrickField(BrickField.VARIABLE);
		setFormulaWithBrickField(BrickField.VARIABLE, variableFormula);
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_set_variable, null);
		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.set_variable_spinner);
		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getDefinitionBrick().getUserBrickId());

		variableSpinner.setFocusableInTouchMode(false);
		variableSpinner.setFocusable(false);
		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.createDataAdapter(context, userBrickId, ProjectManager.getInstance().getCurrentSprite(), inUserBrick);

		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);

		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner, null);

		TextView textSetVariable = (TextView) prototypeView.findViewById(R.id.brick_set_variable_prototype_view);

		if (this.getFormula().getRoot().getValue().equals(Sensors.PHIRO_FRONT_LEFT.toString())) {
			textSetVariable.setText(context.getResources().getString(
					R.string.formula_editor_phiro_sensor_front_left));
		} else if (this.getFormula().getRoot().getValue().equals(Sensors.PHIRO_FRONT_RIGHT.toString())) {
			textSetVariable.setText(context.getResources().getString(
					R.string.formula_editor_phiro_sensor_front_right));
		} else if (this.getFormula().getRoot().getValue().equals(Sensors.PHIRO_SIDE_LEFT.toString())) {
			textSetVariable.setText(context.getResources().getString(
					R.string.formula_editor_phiro_sensor_side_left));
		} else if (this.getFormula().getRoot().getValue().equals(Sensors.PHIRO_SIDE_RIGHT.toString())) {
			textSetVariable.setText(context.getResources().getString(
					R.string.formula_editor_phiro_sensor_side_right));
		} else if (this.getFormula().getRoot().getValue().equals(Sensors.PHIRO_BOTTOM_LEFT.toString())) {
			textSetVariable.setText(context.getResources().getString(
					R.string.formula_editor_phiro_sensor_bottom_left));
		} else if (this.getFormula().getRoot().getValue().equals(Sensors.PHIRO_BOTTOM_RIGHT.toString())) {
			textSetVariable.setText(context.getResources().getString(
					R.string.formula_editor_phiro_sensor_bottom_right));
		} else {
			textSetVariable.setText(String.valueOf(BrickValues.SET_VARIABLE));
		}

		return prototypeView;
	}

}
