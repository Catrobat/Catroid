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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorFragmentTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	@Before
	public void setUp() throws Exception {
		Project project = UiTestUtils.createDefaultTestProject("formulaEditorFragmentTest");
		Sprite sprite = project.getDefaultScene().getSprite(TestUtils.DEFAULT_TEST_SPRITE_NAME);
		Script script = sprite.getScript(TestUtils.DEFAULT_TEST_SCRIPT_INDEX);

		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(1), new UserVariable("var"));
		UserVariable userVariable = new UserVariable("Global1");
		project.addUserVariable(userVariable);
		setVariableBrick.setUserVariable(userVariable);

		script.addBrick(setVariableBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testSaveChanges() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(0);
		pressBack();
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.checkValue("0");
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Flaky
	@Test
	public void testFailParse() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("3+");
		pressBack();
		UiTestUtils.onToast(withText(R.string.formula_editor_parse_fail))
				.check(matches(isDisplayed()));
		onView(withText(R.string.formula_editor_title))
				.check(matches(isDisplayed()));
		onFormulaEditor()
				.performBackspace();
		pressBack();
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText("3 ")));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndo() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("123");
		onFormulaEditor()
				.performUndo();
		pressBack();
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText("12 ")));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testDeleteButton() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performBackspace();
		onFormulaEditor()
				.performEnterFormula("123");
		onFormulaEditor()
				.performBackspace();
		pressBack();
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText("12 ")));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testRedo() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("12");
		onFormulaEditor()
				.performUndo();
		onFormulaEditor()
				.performRedo();
		pressBack();
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.check(matches(withText("12 ")));
	}

	@After
	public void tearDown() throws Exception {
	}
}
