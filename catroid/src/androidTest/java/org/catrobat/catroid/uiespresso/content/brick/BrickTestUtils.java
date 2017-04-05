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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
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
		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.check(matches(withSpinnerText(stringResourceId)));
	}

	public static void clickAndSelectFromSpinnerOnBrickAtPosition(int spinnerResourceId, int position, int
			stringResourceId) {
		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.perform(click());

		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(stringResourceId))))
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

	public static void enterValueInFormulaTextFieldOnBrickAtPosition(int valueToBeEntered,
			int editTextResourceId, int position) {
		onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.perform(CustomActions.typeInValue(Integer.toString(valueToBeEntered)));
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		onScriptList().atPosition(position).onChildView(withId(editTextResourceId))
				.check(matches(withText(Integer.toString(valueToBeEntered) + " ")));
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
