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
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import androidx.annotation.StringRes;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class FormulaEditorFunctionListTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Parameters(name = "{2}" + "-Test")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.string.formula_editor_function_sin, R.string.formula_editor_function_sin_parameter, "sine"},
				{R.string.formula_editor_function_cos, R.string.formula_editor_function_cos_parameter, "cosine"},
				{R.string.formula_editor_function_tan, R.string.formula_editor_function_tan_parameter, "tangent"},
				{R.string.formula_editor_function_ln, R.string.formula_editor_function_ln_parameter, "natural logarithm"},
				{R.string.formula_editor_function_log, R.string.formula_editor_function_log_parameter, "decimal logarithm"},
				{R.string.formula_editor_function_pi, R.string.formula_editor_function_pi_parameter, "pi"},
				{R.string.formula_editor_function_sqrt, R.string.formula_editor_function_sqrt_parameter, "square root"},
				{R.string.formula_editor_function_rand, R.string.formula_editor_function_rand_parameter, "random value from to"},
				{R.string.formula_editor_function_abs, R.string.formula_editor_function_abs_parameter, "absolute value"},
				{R.string.formula_editor_function_round, R.string.formula_editor_function_round_parameter, "round"},
				{R.string.formula_editor_function_mod, R.string.formula_editor_function_mod_parameter, "modulo"},
				{R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arcsin_parameter, "arcsinus"},
				{R.string.formula_editor_function_arccos, R.string.formula_editor_function_arccos_parameter,
						"arccosus"},
				{R.string.formula_editor_function_exp, R.string.formula_editor_function_exp_parameter, "exponent"},
				{R.string.formula_editor_function_arctan, R.string.formula_editor_function_arctan_parameter,
						"arctangent"},
				{R.string.formula_editor_function_arctan2, R.string.formula_editor_function_arctan2_parameter,
						"arctangent2"},
				{R.string.formula_editor_function_floor, R.string.formula_editor_function_floor_parameter, "floor"},
				{R.string.formula_editor_function_ceil, R.string.formula_editor_function_ceil_parameter, "ceil"},
				{R.string.formula_editor_function_max, R.string.formula_editor_function_max_parameter, "maximum of"},
				{R.string.formula_editor_function_min, R.string.formula_editor_function_min_parameter, "minimum of"},
				{R.string.formula_editor_function_length, R.string.formula_editor_function_length_parameter, "length"},
				{R.string.formula_editor_function_letter, R.string.formula_editor_function_letter_parameter,
						"letter"},
				{R.string.formula_editor_function_join, R.string.formula_editor_function_join_parameter, "join"},
				{R.string.formula_editor_function_regex, R.string.formula_editor_function_regex_parameter,
						"regular expression"},
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
		Script script = UiTestUtils.createProjectAndGetStartScript("FormulaEditorFunctionListTest");
		script.addBrick(new ChangeSizeByNBrick(0));
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testFunctionsListElements() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(changeSizeBrickPosition).checkShowsText(R.string.brick_change_size_by);
		onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		String formulaEditorFunctionString = UiTestUtils.getResourcesString(formulaEditorFunction);
		String formulaEditorFunctionParameterString = UiTestUtils.getResourcesString(formulaEditorFunctionParameter);
		String editorFunction = formulaEditorFunctionString + formulaEditorFunctionParameterString;
		String selectedFunctionString = getSelectedFunctionString(editorFunction);

		onFormulaEditor()
				.performOpenCategory(FormulaEditorWrapper.Category.FUNCTIONS)
				.performSelect(editorFunction);

		onFormulaEditor()
				.checkShows(selectedFunctionString);
	}

	private String getSelectedFunctionString(String functionString) {
		return functionString
				.replaceAll("^(.+?)\\(", "$1( ")
				.replace(",", " , ")
				.replace("-", "- ")
				.replaceAll("\\)$", " )")
				.concat(" ");
	}
}
