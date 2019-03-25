/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Math.BRACKETCLOSE;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Math.BRACKETOPEN;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Math.DIVIDE;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Math.MINUS;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Math.MULT;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Math.PLUS;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.COMMA;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM0;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM1;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM2;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM3;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM4;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM5;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM6;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM7;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM8;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.NumPad.NUM9;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResources;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class FormulaEditorEditTextGenericTests {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorEditTextTest");
		script.addBrick(new ChangeSizeByNBrick(new Formula(10)));
		baseActivityTestRule.launchActivity();
		onView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());
	}

	@Parameterized.Parameters(name = "{2}" + "-Test")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{NUM0, "0", "button_0"},
				{NUM1, "1", "button_1"},
				{NUM2, "2", "button_2"},
				{NUM3, "3", "button_3"},
				{NUM4, "4", "button_4"},
				{NUM5, "5", "button_5"},
				{NUM6, "6", "button_6"},
				{NUM7, "7", "button_7"},
				{NUM8, "8", "button_8"},
				{NUM9, "9", "button_9"},
				{COMMA, "0" + getResources().getString(
						R.string.formula_editor_decimal_mark), "button_decimal_mark"},
				{PLUS, getResources().getString(
						R.string.formula_editor_operator_plus), "button_plus"},
				{MINUS, getResources().getString(
						R.string.formula_editor_operator_minus), "button_minus"},
				{MULT, getResources().getString(
						R.string.formula_editor_operator_mult), "button_mult"},
				{DIVIDE, getResources().getString(
						R.string.formula_editor_operator_divide), "button_divide"},
				{BRACKETOPEN, getResources().getString(
						R.string.formula_editor_bracket_open), "button_bracket_open"},
				{BRACKETCLOSE, getResources().getString(
						R.string.formula_editor_bracket_close), "button_bracket_close"}
		});
	}

	@Parameter
	@StringRes
	public Matcher<View> formulaEditorKeyboardButton;

	@Parameter(1)
	public String shouldBe;

	@Parameter(2)
	public String testName;

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testCustomKeys() {
		onView(formulaEditorKeyboardButton)
				.perform(click());
		onFormulaEditor().checkShows(shouldBe + " ");
	}
}
