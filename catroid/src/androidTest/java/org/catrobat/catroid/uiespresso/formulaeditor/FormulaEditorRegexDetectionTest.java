/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.formulaeditor;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorRegexDetectionTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {
		Script script = UiTestUtils.createProjectAndGetStartScript(
				"FormulaEditorRegExDetectionTest");
		script.addBrick(new SetVariableBrick(0));
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testNonRegexFunctionChangeText() {

		String editorFunction = getFunctionEntryName(R.string.formula_editor_function_join,
				R.string.formula_editor_function_join_parameter);
		prepareUntilButton(editorFunction);

		onView(withText(R.string.formula_editor_dialog_change_text)).check(matches(isDisplayed()));
	}

	@Test (expected = NoMatchingViewException.class)
	public void testNonRegexFunctionNoAssistantButton() {
		String editorFunction = getFunctionEntryName(R.string.formula_editor_function_join,
				R.string.formula_editor_function_join_parameter);
		prepareUntilButton(editorFunction);
		onView(withText(R.string.assistant)).check(matches(isDisplayed()));
	}

	@Test
	public void testRegexFunctionFirstParamChangeRegexText() {

		String editorFunction = getFunctionEntryName(R.string.formula_editor_function_regex,
				R.string.formula_editor_function_regex_parameter);
		prepareUntilButton(editorFunction);

		onView(withText(R.string.formula_editor_dialog_change_regular_expression)).check(matches(isDisplayed()));
	}
	@Test
	public void testRegexFunctionFirstParamAssistantButton() {

		String editorFunction = getFunctionEntryName(R.string.formula_editor_function_regex,
				R.string.formula_editor_function_regex_parameter);
		prepareUntilButton(editorFunction);

		onView(withText(R.string.assistant)).check(matches(isDisplayed()));
	}
	@Test
	public void testRegexFunctionAssistantButtonOpensAssistantWindowOnClick() {
		String editorFunction = getFunctionEntryName(R.string.formula_editor_function_regex,
				R.string.formula_editor_function_regex_parameter);
		prepareUntilButton(editorFunction);

		onView(withText(R.string.assistant)).perform(click());
		onView(withText(R.string.formula_editor_dialog_regular_expression_assistant_title)).check(matches(isDisplayed()));
	}

	// Cant implement ui selection of second param.
	// Functionality is still covered by unit test
	//@Test
	public void testRegexFunctionSecondParamChangeText() {

		String editorFunction = getFunctionEntryName(R.string.formula_editor_function_regex,
				R.string.formula_editor_function_regex_parameter);

		prepareUntilButton(editorFunction);
		//select 2nd param.
		onView(withText(R.string.formula_editor_dialog_change_text)).check(matches(isDisplayed()));
	}

	// Cant implement ui selection of second param.
	// Functionality is still covered by unit test
	//@Test (expected = NoMatchingViewException.class)
	public void testRegexFunctionSecondParamNoAssistantButton() {

		String editorFunction = getFunctionEntryName(R.string.formula_editor_function_regex,
				R.string.formula_editor_function_regex_parameter);

		prepareUntilButton(editorFunction);
		//select 2nd param.
		onView(withText(R.string.assistant)).check(matches(isDisplayed()));
	}

	private void prepareUntilButton(String nameOfFunction) {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.FUNCTIONS).performSelect(nameOfFunction);
		onFormulaEditor().performClickOn(FormulaEditorWrapper.Control.TEXT);
	}

	private String getFunctionEntryName(int functionName, int paramName) {
		String functionString = UiTestUtils.getResourcesString(functionName);
		String paramString = UiTestUtils.getResourcesString(paramName);
		return functionString + paramString;
	}
}
