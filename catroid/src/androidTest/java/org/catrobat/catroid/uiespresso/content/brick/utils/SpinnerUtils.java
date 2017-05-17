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
import org.catrobat.catroid.uiespresso.util.UiTestUtils;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public final class SpinnerUtils {
	private SpinnerUtils() {
		throw new AssertionError();
	}

	public static void checkIfSpinnerOnBrickAtPositionShowsString(int spinnerResourceId, int position, int
			stringResourceId) {
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position,
				UiTestUtils.getResourcesString(stringResourceId));
	}

	public static void checkIfSpinnerOnBrickAtPositionShowsString(int spinnerResourceId, int position, String
			expectedString) {
		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.onChildView(withText(expectedString))
				.check(matches(isDisplayed()));
	}

	public static void clickSelectCheckSpinnerValueOnBrick(int spinnerResourceId, int position, int stringResourceId) {
		clickSelectCheckSpinnerValueOnBrick(spinnerResourceId, position,
				UiTestUtils.getResourcesString(stringResourceId));
	}

	public static void clickSelectCheckSpinnerValueOnBrick(int spinnerResourceId, int position, String expectedString) {
		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(expectedString)))
				.perform(click());
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position, expectedString);
	}

	public static void checkIfValuesAvailableInSpinnerOnBrick(List<Integer> stringResourceIdValues,
			int spinnerResourceId, int brickPosition) {
		BrickTestUtils.onScriptList().atPosition(brickPosition).onChildView(withId(spinnerResourceId))
				.perform(click());

		for (Integer stringResourceId : stringResourceIdValues) {
			onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(stringResourceId))))
					.check(matches(isDisplayed()));
		}
		pressBack();
	}

	public static void createNewVariableOnSpinnerInitial(int spinnerResourceId, int position,
			String variableName) {
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position,
				R.string.brick_variable_spinner_create_new_variable);

		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.onChildView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		enterTextOnDialogue(R.id.dialog_formula_editor_data_name_edit_text, variableName);
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position, variableName);
	}

	public static void createNewVariableOnSpinner(int spinnerResourceId, int position, String variableName) {
		BrickTestUtils.onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.perform(click());

		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		enterTextOnDialogue(R.id.dialog_formula_editor_data_name_edit_text, variableName);
		// todo: CAT-2359 to fix this:
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position, variableName);
	}

	public static void enterTextOnDialogue(int dialogueId, String textToEnter) {
		onView(withId(dialogueId))
				.check(matches(isDisplayed()));
		onView(withId(dialogueId))
				.perform(typeText(textToEnter));
		onView(withId(android.R.id.button1))
				.perform(click());
	}
}
