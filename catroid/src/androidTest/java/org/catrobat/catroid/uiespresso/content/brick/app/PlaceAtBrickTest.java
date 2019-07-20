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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class PlaceAtBrickTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(PlaceAtBrickTest.class.getSimpleName());
	}

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		createProjectAndGetStartScript(PlaceAtBrickTest.class.getSimpleName())
				.addBrick(new PlaceAtBrick());
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPlaceAtBrick() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_place_at);
		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onView(withText(R.string.brick_place_at_option_place_visually))
				.check(matches(isDisplayed()));
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("1+2")
				.performCloseAndSave();
		onView(withId(R.id.brick_place_at_edit_text_x))
				.check(matches(withText("1 + 2 ")));

		onView(withId(R.id.brick_place_at_edit_text_y))
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(42)
				.performCloseAndSave();
		onView(withId(R.id.brick_place_at_edit_text_y))
				.check(matches(withText("42 ")));

		onView(withId(R.id.brick_place_at_edit_text_x))
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(42)
				.performCloseAndSave();
		onView(withId(R.id.brick_place_at_edit_text_x))
				.check(matches(withText("42 ")));

		onView(withId(R.id.brick_place_at_edit_text_y))
				.perform(click());
		onView(withText(R.string.brick_place_at_option_place_visually))
				.check(matches(isDisplayed()));
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()));
		pressBack();

		onBrickAtPosition(brickPosition).performClick();
		onView(withText(R.string.brick_place_at_option_place_visually))
				.check(matches(isDisplayed()));
	}
}
