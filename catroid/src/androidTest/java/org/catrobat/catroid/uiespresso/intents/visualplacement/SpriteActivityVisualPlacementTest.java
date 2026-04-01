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

package org.catrobat.catroid.uiespresso.intents.visualplacement;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.catrobat.catroid.visualplacement.VisualPlacementActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.content.bricks.Brick.BrickField.X_POSITION;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.CHANGED_COORDINATES;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.X_COORDINATE_BUNDLE_ARGUMENT;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.Y_COORDINATE_BUNDLE_ARGUMENT;
import static org.hamcrest.core.AllOf.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SpriteActivityVisualPlacementTest {
	private static final int XPOS = -200;
	private static final int YPOS = 500;
	private static final int XRETURN = 42;
	private static final int YRETURN = 666;
	private static final String INVALID_TEST_STRING = "test";
	private PlaceAtBrick bricktoAdd;

	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		bricktoAdd = new PlaceAtBrick();
		UiTestUtils.createProjectAndGetStartScript(SpriteActivityVisualPlacementTest.class.getSimpleName())
				.addBrick(bricktoAdd);
		Intents.init();
	}

	@After
	public void tearDown() throws Exception {
		Intents.release();
		baseActivityTestRule.finishActivity();
		TestUtils.deleteProjects(SpriteActivityVisualPlacementTest.class.getSimpleName());
	}

	@Test
	public void testCorrectIntentsSend() {
		bricktoAdd.setCoordinates(XPOS, YPOS);
		Intent intent = new Intent();
		Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

		baseActivityTestRule.launchActivity();
		intending(anyIntent()).respondWith(intentResult);

		onView(withId(R.id.brick_place_at_edit_text_x)).perform(click());
		onView(withText(R.string.brick_option_place_visually)).perform(click());

		intended(allOf(hasComponent(VisualPlacementActivity.class.getName()), hasExtra(EXTRA_X_TRANSFORM, XPOS)));
		intended(allOf(hasComponent(VisualPlacementActivity.class.getName()), hasExtra(EXTRA_Y_TRANSFORM, YPOS)));
	}

	@Test
	public void testCoordinatesSetAfterActivity() {
		bricktoAdd.setCoordinates(XPOS, YPOS);
		Intent intent = new Intent();
		intent.putExtra(X_COORDINATE_BUNDLE_ARGUMENT, XRETURN);
		intent.putExtra(Y_COORDINATE_BUNDLE_ARGUMENT, YRETURN);
		intent.putExtra(CHANGED_COORDINATES, true);
		intent.putExtra(EXTRA_BRICK_HASH, bricktoAdd.hashCode());
		Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

		baseActivityTestRule.launchActivity();
		intending(anyIntent()).respondWith(intentResult);

		onView(withId(R.id.brick_place_at_edit_text_x)).perform(click());
		onView(withText(R.string.brick_option_place_visually)).perform(click());

		onView(withId(R.id.brick_place_at_edit_text_x)).check(matches(withText(XRETURN + " ")));
		onView(withId(R.id.brick_place_at_edit_text_y)).check(matches(withText(YRETURN + " ")));
	}

	@Test
	public void testNotInterpretableInput() {
		Formula invalidFormula = new Formula(INVALID_TEST_STRING);
		bricktoAdd.setFormulaWithBrickField(X_POSITION, invalidFormula);

		Intent intent = new Intent();
		intent.putExtra(EXTRA_BRICK_HASH, bricktoAdd.hashCode());
		Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

		baseActivityTestRule.launchActivity();
		intending(anyIntent()).respondWith(intentResult);

		onView(withText(R.string.brick_place_at)).perform(click());
		onView(withId(R.string.brick_option_place_visually)).check(doesNotExist());
	}
}
