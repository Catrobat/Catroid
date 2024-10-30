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
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Category.FUNCTIONS;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Control.BACKSPACE;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Control.COMPUTE;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.FORMULA_EDITOR_TEXT_FIELD_MATCHER;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.onToast;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorEditTextTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Script script = UiTestUtils.createProjectAndGetStartScript("FormulaEditorEditTextTest");
		script.addBrick(new ChangeSizeByNBrick(new Formula(10)));
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onFormulaTextField(R.id.brick_change_size_by_edit_text)
				.perform(click());
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testDoubleTapSelection() {
		onFormulaEditor()
				.performEnterFormula("1234");
		onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER)
				.perform(doubleClick());
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.checkShows(" ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testLongClickDeletion() {
		onFormulaEditor()
				.performEnterFormula("1234");
		onView(BACKSPACE)
				.perform(longClick());
		onFormulaEditor()
				.checkShows(" ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testFunctionDeletion() {
		String random = getResourcesString(R.string.formula_editor_function_rand) + getResourcesString(R.string.formula_editor_function_rand_parameter);
		onFormulaEditor()
				.performOpenCategory(FUNCTIONS)
				.performSelect(random);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performOpenCategory(FUNCTIONS)
				.performSelect(random);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.checkShows(" ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testFormulaIsNotValidToast1() {
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		pressBack();
		onToast(withText(R.string.formula_editor_parse_fail))
				.check(matches(isDisplayed()));
		onView(isRoot()).perform(CustomActions.wait(3000));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testFormulaIsNotValidToast2() {
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performEnterFormula("1+1+");
		pressBack();
		onToast(withText(R.string.formula_editor_parse_fail))
				.check(matches(isDisplayed()));
		onView(isRoot()).perform(CustomActions.wait(3000));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testFormulaIsNotValidToast3() {
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performEnterFormula("+");
		pressBack();
		onToast(withText(R.string.formula_editor_parse_fail))
				.check(matches(isDisplayed()));
		onView(isRoot()).perform(CustomActions.wait(3000));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testComputeDialog() {
		onFormulaEditor()
				.performEnterFormula("-2");
		onFormulaEditor()
				.performClickOn(COMPUTE);
		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(matches(withText("-2")));
		pressBack();

		onFormulaEditor()
				.performEnterFormula("-6.111-");
		onFormulaEditor()
				.performClickOn(COMPUTE);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(COMPUTE);
		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(matches(withText("-8.111")));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testTextViewInBrickIsUpdatedCorrectly() {
		onFormulaEditor()
				.performEnterFormula("1+");

		onFormulaEditor()
				.performClickOn(FUNCTIONS);

		onView(withText("pi"))
				.perform(click());

		onView(withId(R.id.brick_change_size_by_edit_text))
				.check(matches(withText("1 + pi ")));
	}
}
