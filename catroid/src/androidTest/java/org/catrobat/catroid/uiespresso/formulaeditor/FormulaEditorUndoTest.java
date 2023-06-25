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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
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
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper.onColorPickerPresetButton;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorUndoTest {

	private int brickPosition;

	private static final String VARIABLE_NAME = "TestVariable";
	private static final String NEW_VARIABLE_NAME = "NewVariable";
	private static final int VARIABLE_VALUE = 5;
	private static final String NEW_VARIABLE_VALUE = "10";
	UserVariable userVariable;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(FormulaEditorUndoTest.class.getName());
	}

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		Script script = UiTestUtils.createProjectAndGetStartScript(FormulaEditorUndoTest.class.getName());
		script.addBrick(new PlaceAtBrick());
		userVariable = new UserVariable(VARIABLE_NAME, VARIABLE_VALUE);
		ProjectManager.getInstance().getCurrentProject().addUserVariable(userVariable);
		script.addBrick(new SetVariableBrick(new Formula(0), userVariable));
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaChangesOnPressBack() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);
		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("1234");

		onView(withId(R.id.brick_place_at_edit_text_y))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("745");

		pressBack();

		onView(withId(R.id.menu_undo))
				.check(matches(isDisplayed()));
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(1234);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(745);

		onView(withId(R.id.menu_undo))
				.perform(click());
		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(0);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(0);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaWithNoChanges() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		pressBack();

		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(0);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(0);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaWithRevertedChanges() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);
		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("1234");

		onView(withId(R.id.brick_place_at_edit_text_y))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick)).perform(click());
		onFormulaEditor().performEnterFormula("0");

		pressBack();

		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(0);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(0);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaAddVariable() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.performAdd(NEW_VARIABLE_NAME);

		onDataList()
				.performClose();

		pressBack();

		onView(withId(R.id.menu_undo))
				.check(matches(isDisplayed()));

		onView(withId(R.id.menu_undo))
				.perform(click());
		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		assertNull(ProjectManager.getInstance().getCurrentProject().getUserVariable(NEW_VARIABLE_NAME));

		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.performAdd(NEW_VARIABLE_NAME);

		onDataList()
				.performClose();

		pressBack();

		onView(withId(R.id.menu_undo))
				.check(matches(isDisplayed()));

		assertNotNull(ProjectManager.getInstance().getCurrentProject().getUserVariable(NEW_VARIABLE_NAME));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaDeleteVariable() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList().onVariableAtPosition(0)
				.performDelete();

		onDataList()
				.performClose();

		pressBack();

		onView(withId(R.id.menu_undo))
				.check(matches(isDisplayed()));

		onView(withId(R.id.menu_undo))
				.perform(click());
		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		assertNotNull(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME));

		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME), userVariable);
		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME).getValue(), VARIABLE_VALUE);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaRenameVariable() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);
		onView(withId(R.id.brick_place_at_edit_text_x)).perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick)).perform(click());
		onFormulaEditor().performOpenDataFragment();
		onDataList().onVariableAtPosition(0).performRename(NEW_VARIABLE_NAME);
		onDataList().performClose();
		pressBack();

		onView(withId(R.id.menu_undo)).check(matches(isDisplayed()));

		assertNull(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME));
		assertNotNull(ProjectManager.getInstance().getCurrentProject().getUserVariable(NEW_VARIABLE_NAME));
		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(NEW_VARIABLE_NAME), userVariable);
		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(NEW_VARIABLE_NAME).getValue(), VARIABLE_VALUE);

		onView(withId(R.id.menu_undo))
				.perform(click());
		userVariable.setName(VARIABLE_NAME);
		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		assertNull(ProjectManager.getInstance().getCurrentProject().getUserVariable(NEW_VARIABLE_NAME));
		assertNotNull(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME));
		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME), userVariable);
		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME).getValue(), VARIABLE_VALUE);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaEditVariable() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList().onVariableAtPosition(0)
				.performEdit(NEW_VARIABLE_VALUE);

		onDataList()
				.performClose();

		pressBack();

		onView(withId(R.id.menu_undo))
				.check(matches(isDisplayed()));

		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME), userVariable);
		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME).getValue(), NEW_VARIABLE_VALUE);

		onView(withId(R.id.menu_undo))
				.perform(click());
		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME), userVariable);
		assertEquals(ProjectManager.getInstance().getCurrentProject().getUserVariable(VARIABLE_NAME).getValue(), VARIABLE_VALUE);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaWithSpinnerVariable() {
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_variable);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);

		onBrickAtPosition(2)
				.onSpinner(R.id.set_variable_spinner)
				.checkShowsText(VARIABLE_NAME);

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList().onVariableAtPosition(0)
				.performDelete();

		onDataList()
				.performClose();

		pressBack();

		onBrickAtPosition(2)
				.onSpinner(R.id.set_variable_spinner)
				.checkShowsText(R.string.new_option);

		onView(withId(R.id.menu_undo))
				.check(matches(isDisplayed()));

		onView(withId(R.id.menu_undo))
				.perform(click());
		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		onBrickAtPosition(2)
				.onSpinner(R.id.set_variable_spinner)
				.checkShowsText(VARIABLE_NAME);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testUndoFormulaWithColorPicker() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());

		onView(withId(R.id.formula_editor_keyboard_color_picker)).perform(click());

		onColorPickerPresetButton(0, 0)
				.perform(click());

		closeSoftKeyboard();

		onView(withText(R.string.color_picker_apply))
				.perform(click());

		pressBack();

		onView(withId(R.id.brick_place_at_edit_text_x))
				.check(matches(withText("'#0074CD' ")));
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(0);

		onView(withId(R.id.menu_undo))
				.check(matches(isDisplayed()));

		onView(withId(R.id.menu_undo))
				.perform(click());
		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(0);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(0);
	}
}
