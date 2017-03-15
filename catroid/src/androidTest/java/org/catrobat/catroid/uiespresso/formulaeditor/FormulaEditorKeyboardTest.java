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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorKeyboardTest {

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("formulaEditorKeyboardTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void numericKeysTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());

		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_2)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_3)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_4)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_5)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_6)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_7)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_8)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_9)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_0)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_decimal_mark)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_ok)).perform(click());

		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText("1234567890"
						+ UiTestUtils.getResourcesString(R.string.formula_editor_decimal_mark) + "1 ")));
	}

	@Test
	public void basicMathOperatorKeysTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());

		onView(withId(R.id.formula_editor_keyboard_bracket_open)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_bracket_close)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_plus)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_minus)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_mult)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_divide)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_equal)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_1)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_ok)).perform(click());

		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText("( 1 ) + 1 - 1 × 1 ÷ 1 = 1 ")));
	}

	@Test
	public void enterStringTest() {
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_string))
				.perform(click());

		onView(withText(R.string.formula_editor_new_string_name))
				.check(matches(isDisplayed()));
		onView(withId(R.id.formula_editor_string_name_edit_text))
				.perform(typeText("Foo"));
		onView(withText(R.string.ok))
				.perform(click());

		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText("'Foo' ")));
	}

	@After
	public void tearDown() throws Exception {
	}

	public Project createProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		SetVariableBrick setVariableBrick = new SetVariableBrick();
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		UserVariable userVariable = dataContainer.addProjectUserVariable("Global1");
		setVariableBrick.setUserVariable(userVariable);

		script.addBrick(setVariableBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		return project;
	}
}
