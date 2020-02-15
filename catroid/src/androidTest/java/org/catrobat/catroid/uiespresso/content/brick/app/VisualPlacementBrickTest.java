/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.VisualPlacementBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.catrobat.catroid.visualplacement.VisualPlacementActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import androidx.test.espresso.intent.Intents;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class VisualPlacementBrickTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"GlideToBrick", R.string.brick_glide, new GlideToBrick()},
				{"PlaceAtBrick", R.string.brick_place_at, new PlaceAtBrick()}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public int brickString;

	@Parameterized.Parameter(2)
	public VisualPlacementBrick brick;

	@After
	public void tearDown() throws Exception {
		Intents.release();
		resetFormulaFields();
		baseActivityTestRule.finishActivity();
		TestUtils.deleteProjects(VisualPlacementBrickTest.class.getSimpleName());
	}

	private void resetFormulaFields() {
		brick.setFormulaWithBrickField(brick.getXBrickField(), new Formula(0.0));
		brick.setFormulaWithBrickField(brick.getYBrickField(), new Formula(0.0));
	}

	@Before
	public void setUp() throws Exception {
		Script script =
				BrickTestUtils.createProjectAndGetStartScript(VisualPlacementBrickTest.class.getSimpleName());
		script.addBrick(brick);
		baseActivityTestRule.launchActivity();
		Intents.init();
	}

	@Test
	public void testIsVisualPlacementShownForEditTextX() {
		onBrickAtPosition(1).checkShowsText(brickString);
		onView(withId(brick.getXEditTextId()))
				.perform(click());
		onView(withText(R.string.brick_place_at_option_place_visually))
				.check(matches(isDisplayed()));
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testIsVisualPlacementShownForEditTextY() {
		onView(withId(brick.getYEditTextId()))
				.perform(click());
		onView(withText(R.string.brick_place_at_option_place_visually))
				.check(matches(isDisplayed()));
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testIsVisualPlacementShownOnBrickClick() {
		onBrickAtPosition(1).performClick();
		onView(withText(R.string.brick_place_at_option_place_visually))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testVisualPlacementAfterFormulaNotANumber() {
		onView(withId(brick.getXEditTextId()))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("1+2")
				.performCloseAndSave();
		onView(withId(brick.getXEditTextId()))
				.perform(click());
		onFormulaEditor()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testVisualPlacementAfterNumberEntered() {
		onView(withId(brick.getXEditTextId()))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(42)
				.performCloseAndSave();
		onView(withId(brick.getXEditTextId()))
				.check(matches(withText("42 ")))
				.perform(click());
		onView(withText(R.string.brick_place_at_option_place_visually))
				.check(matches(isDisplayed()));
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testIsVisualPlacementActivityShown() {
		onView(withId(brick.getXEditTextId()))
				.perform(click());
		onView(withText(R.string.brick_place_at_option_place_visually))
				.perform(click());
		intended(hasComponent(VisualPlacementActivity.class.getName()));
	}
}
