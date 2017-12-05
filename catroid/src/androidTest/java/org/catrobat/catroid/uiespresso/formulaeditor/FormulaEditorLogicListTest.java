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
import org.catrobat.catroid.ui.ScriptActivity;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class FormulaEditorLogicListTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Parameters(name = "{1}" + "-Test")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.string.formula_editor_logic_and, "and"},
				{R.string.formula_editor_logic_or, "or"},
				{R.string.formula_editor_logic_not, "not"},
				{R.string.formula_editor_function_true, "true"},
				{R.string.formula_editor_function_false, "false"},
				{R.string.formula_editor_logic_equal, "equal"},
				{R.string.formula_editor_logic_notequal, "not_equal"},
				{R.string.formula_editor_logic_lesserthan, "lesser_than"},
				{R.string.formula_editor_logic_leserequal, "lesser_equal"},
				{R.string.formula_editor_logic_greaterthan, "greater_than"},
				{R.string.formula_editor_logic_greaterequal, "greater_equal"}
		});
	}

	@Parameter
	public @StringRes
	int formulaEditorLogicFunction;

	@Parameter(1)
	public String testName;

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorLogicTest");
		script.addBrick(new ChangeSizeByNBrick(0));
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testLogicListElements() {
		onBrickAtPosition(1)
				.onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onView(withText(R.string.formula_editor_logic))
				.perform(click());
		String editorFunction = UiTestUtils.getResourcesString(formulaEditorLogicFunction);

		onData(allOf(is(instanceOf(String.class)), is(editorFunction)))
				.inAdapterView(FormulaEditorFunctionListMatchers.isFunctionListView())
				.onChildView(withId(R.id.fragment_formula_editor_list_item))
				.perform(click());

		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(editorFunction + " ")));

		onView(withId(R.id.formula_editor_keyboard_delete))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(" ")));
	}
}
