/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.app.Activity;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper.onColorPickerPresetButton;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorKeyboardTest {

	public static final String PROJECT_NAME = "formulaEditorKeyboardTest";

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void numericKeysTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());

		onFormulaEditor()
				.performEnterNumber(1234567890.1);

		pressBack();

		onView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText("1234567890"
						+ UiTestUtils.getResourcesString(R.string.formula_editor_decimal_mark) + "1 ")));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void basicMathOperatorKeysTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());

		onFormulaEditor()
				.performEnterFormula("(1)+1-1*1/1")
				.performOpenCategory(FormulaEditorWrapper.Category.LOGIC);

		onView(withText(R.string.formula_editor_logic_equal)).perform(click());

		onFormulaEditor().performEnterFormula("1");

		pressBack();

		onView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText("( 1 ) + 1 - 1 × 1 ÷ 1 = 1 ")));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void enterStringTest() {
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());

		onFormulaEditor()
				.performEnterString("Foo");

		pressBack();

		onView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText("'Foo' ")));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void toggleFunctionalButtonsTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_functional_button_toggle))
				.perform(click());
		onView(withId(R.id.tableRow11)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
		onView(withId(R.id.tableRow12)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

		onView(withId(R.id.formula_editor_keyboard_functional_button_toggle))
				.perform(click());
		onView(withId(R.id.tableRow11)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withId(R.id.tableRow12)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void sensorFormulaTest() {
		Activity activity = baseActivityTestRule.getActivity();
		if (activity == null || activity.isFinishing()) {
			return;
		}
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_sensors)).perform(click());
		onView(withText(activity.getString(R.string.formula_editor_sensor_x_acceleration))).perform(click());
		onFormulaEditor()
				.performEnterFormula("+2");
		pressBack();
		onView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText(activity.getString(R.string.formula_editor_sensor_x_acceleration) + " + 2 ")));
	}

	@Test
	public void addColorTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());

		onView(withId(R.id.formula_editor_keyboard_color_picker)).perform(click());

		onColorPickerPresetButton(0, 0)
				.perform(click());

		closeSoftKeyboard();

		onView(withText(R.string.color_picker_apply))
				.perform(click());

		onView(withId(R.id.brick_set_variable_edit_text)).check(matches(withText("'#0074CD' ")));
	}

	@After
	public void tearDown() throws IOException {
		baseActivityTestRule.finishActivity();
		TestUtils.deleteProjects(PROJECT_NAME);
	}

	public Project createProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		SetVariableBrick setVariableBrick = new SetVariableBrick();
		UserVariable userVariable = new UserVariable("Global1");
		project.addUserVariable(userVariable);
		setVariableBrick.setUserVariable(userVariable);

		script.addBrick(setVariableBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		return project;
	}
}
