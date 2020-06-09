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

import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.VisualPlacementBrick;
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
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.catrobat.catroid.uiespresso.content.brick.app.VisualPlacementBrickTest.isFormulaEditorShownImmediatelyWithTapOnEditText;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(JUnit4.class)
public class GlideToBrickTest {
	private static final String TAG = VisualPlacementBrickTest.class.getSimpleName();

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	public int brickString = R.string.brick_glide;
	public VisualPlacementBrick brick = new GlideToBrick();
	int brickPosition = 1;

	@After
	public void tearDown() {
		baseActivityTestRule.finishActivity();
		try {
			TestUtils.deleteProjects(VisualPlacementBrickTest.class.getSimpleName());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Before
	public void setUp() throws Exception {
		Script script =
				BrickTestUtils.createProjectAndGetStartScript(VisualPlacementBrickTest.class.getSimpleName());
		script.addBrick(brick);
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testVisualPlacementNotShownForGlideSecondsOption() {
		onBrickAtPosition(brickPosition).checkShowsText(brickString);
		isFormulaEditorShownImmediatelyWithTapOnEditText(R.id.brick_glide_to_edit_text_duration);
	}

	@Test
	public void testVisualPlacementInFormulaFragmentNotShownForGlideSecondsOption() {
		onBrickAtPosition(brickPosition).checkShowsText(brickString);
		onView(withId(brick.getXEditTextId()))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		isFormulaEditorShownImmediatelyWithTapOnEditText(R.id.brick_glide_to_edit_text_duration);
	}
}
