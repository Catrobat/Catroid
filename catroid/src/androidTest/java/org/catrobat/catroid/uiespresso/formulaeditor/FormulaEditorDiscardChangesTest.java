/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
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
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.onToast;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorDiscardChangesTest {

	private int brickPosition;
	private String no = getResourcesString(R.string.no);
	private String yes = getResourcesString(R.string.yes);

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(FormulaEditorDiscardChangesTest.class.getName());
	}

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		Script script = BrickTestUtils.createProjectAndGetStartScript(FormulaEditorDiscardChangesTest.class.getName());
		script.addBrick(new PlaceAtBrick());
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testGoBackToDiscardXChanges() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);
		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("1234");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(no))
				.perform(click());
		onToast(withText(R.string.formula_editor_changes_discarded))
				.check(matches(isDisplayed()));
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(0);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testGoBackToDiscardYChanges() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);
		onView(withId(R.id.brick_place_at_edit_text_y))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("5678");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(no))
				.perform(click());
		onToast(withText(R.string.formula_editor_changes_discarded))
				.check(matches(isDisplayed()));
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(0);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testGoBackToDiscardXYChanges() {
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
				.performEnterFormula("5678");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(no))
				.perform(click());
		onToast(withText(R.string.formula_editor_changes_discarded))
				.check(matches(isDisplayed()));
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(0);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(0);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testSaveXYChanges() {
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
				.performEnterFormula("5678");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(yes))
				.perform(click());
		onToast(withText(R.string.formula_editor_changes_saved))
				.check(matches(isDisplayed()));
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_x)
				.checkShowsNumber(1234);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_place_at_edit_text_y)
				.checkShowsNumber(5678);
	}
}
