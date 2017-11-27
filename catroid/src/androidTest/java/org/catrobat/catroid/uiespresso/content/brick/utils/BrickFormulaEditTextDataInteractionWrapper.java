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

import android.support.test.espresso.DataInteraction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.wrappers.DataInteractionWrapper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class BrickFormulaEditTextDataInteractionWrapper extends DataInteractionWrapper {
	public BrickFormulaEditTextDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public BrickFormulaEditTextDataInteractionWrapper checkShowsText(String text) {
		dataInteraction.check(matches(withText("'" + text + "' ")));
		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public <V extends Number> BrickFormulaEditTextDataInteractionWrapper checkShowsNumber(V value) {
		dataInteraction.check(matches(withText((value + " ").replace("-", "- "))));
		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public <V extends Number> BrickFormulaEditTextDataInteractionWrapper performEnterNumber(V valueToBeEntered) {
		dataInteraction.perform(click());
		String valueToSet = "";

		if (valueToBeEntered instanceof Float) {
			valueToSet = Float.toString(valueToBeEntered.floatValue());
		} else if (valueToBeEntered instanceof Double) {
			valueToSet = Double.toString(valueToBeEntered.doubleValue());
		} else if (valueToBeEntered instanceof Integer) {
			valueToSet = Integer.toString(valueToBeEntered.intValue());
		}

		onView(withId(R.id.formula_editor_edit_field))
				.perform(CustomActions.typeInValue(valueToSet));
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());

		// When using double or float, but value is an integer, the textField will show it as an integer
		// e.g 12.0 -> 12
		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public BrickFormulaEditTextDataInteractionWrapper performEnterString(String stringToBeEntered) {
		dataInteraction.perform(click());

		onView(withId(R.id.formula_editor_keyboard_string))
				.perform(click());
		onView(withId(R.id.formula_editor_string_name_edit_text))
				.perform(clearText(), typeText(stringToBeEntered));
		onView(withText(R.string.ok))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public BrickFormulaEditTextDataInteractionWrapper performEnterString(int stringResourceId) {
		dataInteraction.perform(click());

		onView(withId(R.id.formula_editor_keyboard_string))
				.perform(click());
		onView(withId(R.id.formula_editor_string_name_edit_text))
				.perform(typeText(UiTestUtils.getResourcesString(stringResourceId)));
		onView(withText(R.string.ok))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}
}
