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
package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class SetVariableTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		SetVariableBrick setVariableTestBrick1 = new SetVariableBrick(10);
		SetVariableBrick setVariableTestBrick2 = new SetVariableBrick(0);
		Script script = BrickTestUtils.createProjectAndGetStartScript("setVariableTest");
		script.addBrick(setVariableTestBrick1);
		script.addBrick(setVariableTestBrick2);
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCreateNewUserVariableAndDeletion() {
		String userVariableName = "testVariable1";
		String secondUserVariableName = "testVariable2";

		onBrickAtPosition(0)
				.checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1)
				.checkShowsText(R.string.brick_set_variable);
		onBrickAtPosition(1).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(userVariableName);
		onBrickAtPosition(1).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(secondUserVariableName);
		onBrickAtPosition(1).onFormulaTextField(R.id.brick_set_variable_edit_text)
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();
		onDataList().onVariableAtPosition(0)
				.performDelete();
		onDataList()
				.performClose();
		pressBack();

		onView(withText(userVariableName))
				.check(doesNotExist());
		onBrickAtPosition(1).onVariableSpinner(R.id.set_variable_spinner).onChildView(withText(userVariableName))
				.check(doesNotExist());
		onBrickAtPosition(1).onVariableSpinner(R.id.set_variable_spinner).onChildView(withText(secondUserVariableName))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testIfSetVariableSpinnerCanHoldMultipleVariables() {
		String userVariableName = "testVariable1";
		String userVariableNameTwo = "testVariable2";

		addNewVariableViaFormulaEditor(1, userVariableName);

		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		onBrickAtPosition(1).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(userVariableNameTwo);

		onBrickAtPosition(2).onVariableSpinner(R.id.set_variable_spinner)
				.checkNameableValuesAvailable(Arrays.asList(userVariableName, userVariableNameTwo));
	}

	private void addNewVariableViaFormulaEditor(int brickPosition, String userVariableName) {
		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(withId(R.id.button_add))
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.perform(typeText(userVariableName), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());
		onView(allOf(withChild(withText(userVariableName)), withChild(withText("0"))))
				.check(matches(isDisplayed()));
		pressBack();
	}
}
