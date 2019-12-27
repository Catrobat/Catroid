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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.TestResult;

import static org.catrobat.catroid.stage.TestResult.STAGE_ACTIVITY_TEST_FAIL;

public class AssertEqualsAction extends Action {

	private Formula actualFormula = null;
	private Formula expectedFormula = null;
	private String position;
	private Sprite sprite;
	private static final String ASSERT_EQUALS_ERROR = "\nAssertEqualsError\n";

	@Override
	public boolean act(float delta) {
		if (actualFormula == null) {
			failWith("Actual is null");
			return false;
		}
		if (expectedFormula == null) {
			failWith("Expected is null");
			return false;
		}

		String actualValue = actualFormula.interpretObject(sprite).toString();
		String expectedValue = expectedFormula.interpretObject(sprite).toString();

		if (!equalValues(actualValue, expectedValue)) {
			failWith(formattedAssertEqualsError(actualValue, expectedValue));
			return false;
		}
		return true;
	}

	private boolean equalValues(String actual, String expected) {
		try {
			return Double.valueOf(actual).equals(Double.valueOf(expected));
		} catch (NumberFormatException numberFormatException) {
			return actual.equals(expected);
		}
	}

	private void failWith(String message) {
		StageActivity.finishTestWithResult(
				new TestResult(formattedPosition()
						+ ASSERT_EQUALS_ERROR + message, STAGE_ACTIVITY_TEST_FAIL));
	}

	private String formattedAssertEqualsError(Object actual, Object expected) {
		return "expected:<" + expected + "> but was:<" + actual + ">";
	}

	private String formattedPosition() {
		return "on sprite \"" + sprite.getName() + "\"\n" + position;
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

	public void setPosition(String position) {
		this.position = position;
	}
}
