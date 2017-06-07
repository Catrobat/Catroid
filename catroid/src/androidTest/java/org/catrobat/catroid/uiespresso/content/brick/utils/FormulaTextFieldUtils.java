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

package org.catrobat.catroid.uiespresso.content.brick.utils;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public final class FormulaTextFieldUtils {
	private FormulaTextFieldUtils() {
		throw new AssertionError();
	}

	public static <V extends Number> void enterValueInFormulaTextFieldOnBrickAtPosition(V valueToBeEntered,
			int editTextResourceId, int position) {
		String valueToSet = "";

		if (valueToBeEntered instanceof Float) {
			valueToSet = Float.toString(valueToBeEntered.floatValue());
		} else if (valueToBeEntered instanceof Double) {
			valueToSet = Double.toString(valueToBeEntered.doubleValue());
		} else if (valueToBeEntered instanceof Integer) {
			valueToSet = Integer.toString(valueToBeEntered.intValue());
		}

		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.perform(CustomActions.typeInValue(valueToSet));
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());

		// When using double or float, but value is an integer, the textField will show it as an integer
		// e.g 12.0 -> 12
		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.check(matches(withText(valueToSet + " ")));
	}

	public static void enterStringInFormulaTextFieldOnBrickAtPosition(String stringToBeEntered,
			int editTextResourceId, int position) {

		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_string))
				.perform(click());
		onView(withId(R.id.formula_editor_string_name_edit_text))
				.perform(clearText(), typeText(stringToBeEntered));
		onView(withText(R.string.ok))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.check(matches(withText("'" + stringToBeEntered + "' ")));
	}
}
