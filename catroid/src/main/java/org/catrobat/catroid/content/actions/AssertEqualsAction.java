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

package org.catrobat.catroid.content.actions;

import android.content.Intent;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.AssertEqualsBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;

public class AssertEqualsAction extends TemporalAction {

	private Formula actualFormula;
	private Formula expectedFormula;

	private Sprite sprite;

	private UserVariable actualVariable;
	private UserVariable expectedVariable;
	private UserVariable setupVariable;

	public static final String MESSAGE = "ASSERTION_MESSAGE";

	@Override
	protected void update(float percent) {
		updateVariable(actualFormula, actualVariable);
		updateVariable(expectedFormula, expectedVariable);
		updateVariable(new Formula(AssertEqualsBrick.READY_VALUE), setupVariable);

		boolean success = false;
		String message = "";
		if (expectedVariable.getValue() instanceof Double
				&& actualVariable.getValue() instanceof Double) {
			success = ((Double) expectedVariable.getValue()).equals((Double) actualVariable.getValue());
			message = "AssertionFailedError:\n"
					+ "expected:<" + (Double) expectedVariable.getValue() + "> but was:<" + (Double) actualVariable.getValue() + ">";
		} else if (expectedVariable.getValue() instanceof String
				&& actualVariable.getValue() instanceof String) {
			success =  ((String) expectedVariable.getValue()).equals((String) actualVariable.getValue());
			message = "AssertionFailedError:\n"
					+ "expected:<[" + (String) expectedVariable.getValue() + "]> but was:<[" + (String) actualVariable.getValue() + "]>";
		} else {
			message = "Type error - expected and actual are mismatching types\n"
					+ "Got:"
					+ "\nactual = " + actualVariable.getValue().toString()
					+ "\nexpected = " + expectedVariable.getValue().toString() + "\n";
		}

		StageActivity stageActivity = StageActivity.activeStageActivity.get();
		if (stageActivity != null) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(MESSAGE, message);
			if (success) {
				stageActivity.setResult(StageActivity.STAGE_ACTIVITY_TEST_SUCCESS, resultIntent);
			} else {
				stageActivity.setResult(StageActivity.STAGE_ACTIVITY_TEST_FAIL, resultIntent);
			}
			stageActivity.finish();
		}
	}

	protected void updateVariable(Formula formula, UserVariable variable) {
		if (variable == null) {
			return;
		}
		Object value = formula == null ? Double.valueOf(0d) : formula.interpretObject(sprite);

		boolean isFirstLevelStringTree = false;
		if (formula != null && formula.getRoot().getElementType() == FormulaElement.ElementType.STRING) {
			isFirstLevelStringTree = true;
		}

		try {
			if (!isFirstLevelStringTree && value instanceof String) {
				value = Double.valueOf((String) value);
			}
		} catch (NumberFormatException numberFormatException) {
			Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatException);
		}
		variable.setValue(value);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setActual(Formula actual) {
		this.actualFormula = actual;
	}

	public void setExpected(Formula expected) {
		this.expectedFormula = expected;
	}

	public void setActualVariable(UserVariable actualVariable) {
		this.actualVariable = actualVariable;
	}

	public void setExpectedVariable(UserVariable expectedVariable) {
		this.expectedVariable = expectedVariable;
	}

	public void setSetupVariable(UserVariable setupVariable) {
		this.setupVariable = setupVariable;
	}
}
