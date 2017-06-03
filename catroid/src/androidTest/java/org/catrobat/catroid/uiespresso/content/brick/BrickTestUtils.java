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

import android.support.test.espresso.DataInteraction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.matchers.ScriptListMatchers;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public final class BrickTestUtils {
	private BrickTestUtils() {
		throw new AssertionError();
	}

	public static DataInteraction onScriptList() {
		return onData(instanceOf(Brick.class)).inAdapterView(ScriptListMatchers.isScriptListView());
	}

	public static void checkIfBrickAtPositionShowsString(int position, int stringResourceId) {
		checkIfBrickAtPositionShowsString(position, UiTestUtils.getResourcesString(stringResourceId));
	}

	public static void checkIfBrickAtPositionShowsString(int position, String string) {
		onScriptList().atPosition(position).onChildView(withText(string))
				.check(matches(isDisplayed()));
	}

	public static void checkIfSpinnerOnBrickAtPositionShowsString(int spinnerResourceId, int position, int
			stringResourceId) {
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position,
				UiTestUtils.getResourcesString(stringResourceId));
	}

	public static void checkIfSpinnerOnBrickAtPositionShowsString(int spinnerResourceId, int position, String
			expectedString) {
		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.onChildView(withText(expectedString))
				.check(matches(isDisplayed()));
	}

	public static void clickSelectCheckSpinnerValueOnBrick(int spinnerResourceId, int position, int stringResourceId) {
		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(stringResourceId))))
				.perform(click());
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position, stringResourceId);
	}

	public static void checkIfValuesAvailableInSpinnerOnBrick(List<Integer> stringResourceIdValues,
			int spinnerResourceId, int brickPosition) {
		onScriptList().atPosition(brickPosition).onChildView(withId(spinnerResourceId))
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

		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.onChildView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		enterTextOnDialogue(R.id.dialog_formula_editor_data_name_edit_text, variableName);
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position, variableName);
	}

	public static void createNewVariableOnSpinner(int spinnerResourceId, int position, String variableName) {
		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
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

	public static Script createProjectAndGetStartScript(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return script;
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

		onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.perform(CustomActions.typeInValue(valueToSet));
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());

		// When using double or float, but value is an integer, the textField will show it as an integer
		// e.g 12.0 -> 12
		onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.check(matches(withText(valueToSet + " ")));
	}

	public static void createUserListFromDataFragment(String userListName, boolean forAllSprites) {
		onView(withId(R.id.data_user_variables_headline))
				.check(matches(isDisplayed()));
		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
				.perform(typeText(userListName), closeSoftKeyboard());

		onView(withId(R.id.dialog_formula_editor_data_is_list_checkbox))
				.perform(scrollTo(), click());

		onView(withId(R.id.dialog_formula_editor_data_is_list_checkbox))
				.check(matches(isChecked()));

		if (forAllSprites) {
			onView(withId(R.id.dialog_formula_editor_data_name_global_variable_radio_button))
					.perform(click());
		} else {
			onView(withId(R.id.dialog_formula_editor_data_name_local_variable_radio_button))
					.perform(click());
		}
		onView(withId(android.R.id.button1)).perform(click());
		onView(withText(userListName))
				.check(matches(isDisplayed()));
	}

	public static void enterStringInFormulaTextFieldOnBrickAtPosition(String stringToBeEntered,
			int editTextResourceId, int position) {
		onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_string))
				.perform(click());
		onView(withId(R.id.formula_editor_string_name_edit_text))
				.perform(typeText(stringToBeEntered));
		onView(withText(R.string.ok))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.check(matches(withText("'" + stringToBeEntered + "' ")));
	}
}
