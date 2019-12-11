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

	private Formula actualFormula = null;
	private Formula expectedFormula = null;

	private Sprite sprite;

	public static final String MESSAGE = "ASSERTION_MESSAGE";

	@Override
	protected void update(float percent) {
		if (actualFormula == null || expectedFormula == null) {
			String message = "AssertionFailedError:\n"
					+ "Some Formula is null";
			finishWithResult(message, StageActivity.STAGE_ACTIVITY_TEST_FAIL);
		}
		Object actualValue = actualFormula.interpretObject(sprite);
		Object expectedValue = expectedFormula.interpretObject(sprite);

		if (expectedValue.equals(actualValue)) {
			finishWithResult("", StageActivity.STAGE_ACTIVITY_TEST_SUCCESS);
		} else {
			String message = formatMessage(actualValue, expectedValue);
			finishWithResult(message, StageActivity.STAGE_ACTIVITY_TEST_FAIL);
		}
	}

	private void finishWithResult(String message, int status) {
		StageActivity stageActivity = StageActivity.activeStageActivity.get();
		if (stageActivity != null) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(MESSAGE, message);
			stageActivity.setResult(status, resultIntent);
			stageActivity.finish();
		}
	}

	private String formatMessage(Object actual, Object expected) {
		return "AssertionFailedError:\n"
				+ "expected:<" + expected + "> but was:<" + actual + ">";
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
}
