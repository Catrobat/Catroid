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
package org.catrobat.catroid.uiespresso.content.brick;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterValueInFormulaTextFieldOnBrickAtPosition;

public class VariableBrickTest {
	private int setBrickPosition;
	private int changeBrickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		setBrickPosition = 1;
		changeBrickPosition = 2;
		Script script = BrickTestUtils.createProjectAndGetStartScript("variableBricksTest");
		script.addBrick(new SetVariableBrick());
		script.addBrick(new ChangeVariableBrick());
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testVariableBricks() {
		int intToChange = 5;
		double doubleToChange = 10.6;
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(setBrickPosition, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsString(changeBrickPosition, R.string.brick_change_variable);

		enterValueInFormulaTextFieldOnBrickAtPosition(intToChange, R.id.brick_set_variable_edit_text,
				setBrickPosition);
		enterValueInFormulaTextFieldOnBrickAtPosition(doubleToChange, R.id.brick_set_variable_edit_text,
				setBrickPosition);

		enterValueInFormulaTextFieldOnBrickAtPosition(intToChange, R.id.brick_change_variable_edit_text,
				changeBrickPosition);
		enterValueInFormulaTextFieldOnBrickAtPosition(doubleToChange, R.id.brick_change_variable_edit_text,
				changeBrickPosition);
	}

	@Test
	public void testCreatingNewVariableAndChangeValue() {
		final String variableName = "testVariable";
		final int intToChange = 28;
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(setBrickPosition, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsString(changeBrickPosition, R.string.brick_change_variable);
		onView(withId(R.id.set_variable_spinner)).perform(click());
		onView(withId(R.id.dialog_formula_editor_data_name_edit_text)).perform(typeText(variableName));
		onView(withText(R.string.ok)).perform(click());
		onView(withId(R.id.set_variable_spinner)).perform(click());
		onView(withText(variableName)).perform(click());
		enterValueInFormulaTextFieldOnBrickAtPosition(intToChange, R.id.brick_set_variable_edit_text,
				setBrickPosition);
	}

	@Test
	public void testNewVariableCanceling() {
		onView(withId(R.id.set_variable_spinner)).perform(click());
		onView(withText(R.string.cancel)).perform(click());
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(setBrickPosition, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsString(changeBrickPosition, R.string.brick_change_variable);
	}
}
