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

package org.catrobat.catroid.uiespresso.formulaeditor;

import android.support.annotation.StringRes;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorFunctionListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class FormulaEditorFunctionListTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule =
			new BaseActivityInstrumentationRule<>(SpriteActivity.class, true, false);

	@Parameters(name = "{2}" + "-Test")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.string.formula_editor_function_sin, R.string.formula_editor_function_sin_parameter, "sin"},
				{R.string.formula_editor_function_cos, R.string.formula_editor_function_cos_parameter, "cos"},
				{R.string.formula_editor_function_tan, R.string.formula_editor_function_tan_parameter, "tan"},
				{R.string.formula_editor_function_ln, R.string.formula_editor_function_ln_parameter, "ln"},
				{R.string.formula_editor_function_log, R.string.formula_editor_function_log_parameter, "log"},
				{R.string.formula_editor_function_pi, R.string.formula_editor_function_pi_parameter, "pi"},
				{R.string.formula_editor_function_sqrt, R.string.formula_editor_function_sqrt_parameter, "sqrt"},
				{R.string.formula_editor_function_rand, R.string.formula_editor_function_rand_parameter, "rand"},
				{R.string.formula_editor_function_abs, R.string.formula_editor_function_abs_parameter, "abs"},
				{R.string.formula_editor_function_round, R.string.formula_editor_function_round_parameter, "round"},
				{R.string.formula_editor_function_mod, R.string.formula_editor_function_mod_parameter, "mod"},
				{R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arcsin_parameter, "arcsin"},
				{R.string.formula_editor_function_arccos, R.string.formula_editor_function_arccos_parameter, "arccos"},
				{R.string.formula_editor_function_exp, R.string.formula_editor_function_exp_parameter, "exp"},
				{R.string.formula_editor_function_arctan, R.string.formula_editor_function_arctan_parameter, "arctan"},
				{R.string.formula_editor_function_floor, R.string.formula_editor_function_floor_parameter, "floor"},
				{R.string.formula_editor_function_ceil, R.string.formula_editor_function_ceil_parameter, "ceil"},
				{R.string.formula_editor_function_max, R.string.formula_editor_function_max_parameter, "max"},
				{R.string.formula_editor_function_min, R.string.formula_editor_function_min_parameter, "min"},
				{R.string.formula_editor_function_length, R.string.formula_editor_function_length_parameter, "length"},
				{R.string.formula_editor_function_letter, R.string.formula_editor_function_letter_parameter,
						"letter"},
				{R.string.formula_editor_function_join, R.string.formula_editor_function_join_parameter, "join"},
				{R.string.formula_editor_function_number_of_items, R.string
						.formula_editor_function_number_of_items_parameter, "number of items"},
				{R.string.formula_editor_function_list_item, R.string.formula_editor_function_list_item_parameter,
						"list item"},
				{R.string.formula_editor_function_contains, R.string.formula_editor_function_contains_parameter,
						"contains"}
		});
	}

	@Parameter
	public @StringRes int formulaEditorFunction;

	@Parameter(1)
	public @StringRes int formulaEditorFunctionParameter;

	@Parameter(2)
	public String testName;

	private static Integer whenBrickPosition = 0;
	private static Integer changeSizeBrickPosition = 1;

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorFunctionListTest");
		script.addBrick(new ChangeSizeByNBrick(0));
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testFunctionsListElements() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(changeSizeBrickPosition).checkShowsText(R.string.brick_change_size_by);
		onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onView(withText(R.string.formula_editor_functions)).perform(click());

		String formulaEditorFunctionString = UiTestUtils.getResourcesString(formulaEditorFunction);
		String formulaEditorFunctionParameterString = UiTestUtils.getResourcesString(formulaEditorFunctionParameter);
		String editorFunction = formulaEditorFunctionString + formulaEditorFunctionParameterString;
		String selectedFunctionString = getSelectedFunctionString(editorFunction);

		onData(allOf(is(instanceOf(String.class)), is(editorFunction)))
				.inAdapterView(FormulaEditorFunctionListMatchers.isFunctionListView())
				.onChildView(withId(R.id.fragment_formula_editor_list_item))
				.check(matches(isDisplayed()))
				.perform(click());

		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(selectedFunctionString)));
	}

	private String getSelectedFunctionString(String functionString) {
		return functionString.replace("(", "( ")
				.replace(")", " )")
				.replace(",", " , ")
				.concat(" ");
	}
}
