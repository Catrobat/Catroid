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

import android.content.Intent;

import org.catrobat.catroid.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.visualplacement.VisualPlacementActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.X_COORDINATE_BUNDLE_ARGUMENT;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.Y_COORDINATE_BUNDLE_ARGUMENT;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class VisualPlacementActivityTest {
	private static final int XPOS = -200;
	private static final int YPOS = 500;
	private static final int XRETURNPOS = 42;
	private static final int YRETURNPOS = 666;

	@Rule
	public final IntentsTestRule<VisualPlacementActivity> baseActivityTestRule =
			new IntentsTestRule<>(VisualPlacementActivity.class, false, false);

	@Before
	public void setUp() {
		UiTestUtils.createProjectAndGetStartScript(VisualPlacementActivity.class.getSimpleName());
		Intent intent = new Intent();
		intent.putExtra(EXTRA_X_TRANSFORM, XPOS);
		intent.putExtra(EXTRA_Y_TRANSFORM, YPOS);
		baseActivityTestRule.launchActivity(intent);
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(VisualPlacementActivityTest.class.getSimpleName());
	}

	@Test
	public void testResultWhenConfirmClicked() {
		onView(withId(R.id.confirm)).perform(click());

		assertTrue(baseActivityTestRule.getActivity().isFinishing());
		Intent resultIntent = baseActivityTestRule.getActivityResult().getResultData();
		assertEquals(XPOS, resultIntent.getExtras().get(X_COORDINATE_BUNDLE_ARGUMENT));
		assertEquals(YPOS, resultIntent.getExtras().get(Y_COORDINATE_BUNDLE_ARGUMENT));
	}

	@Test
	public void testResultWithChangedCoordinates() {
		baseActivityTestRule.getActivity().setXCoordinate(XRETURNPOS);
		baseActivityTestRule.getActivity().setYCoordinate(YRETURNPOS);

		onView(withId(R.id.confirm)).perform(click());

		assertTrue(baseActivityTestRule.getActivity().isFinishing());
		Intent resultIntent = baseActivityTestRule.getActivityResult().getResultData();
		assertEquals(XRETURNPOS, resultIntent.getExtras().get(X_COORDINATE_BUNDLE_ARGUMENT));
		assertEquals(YRETURNPOS, resultIntent.getExtras().get(Y_COORDINATE_BUNDLE_ARGUMENT));
	}

	@Test
	public void testResultWhenBackAndSaveClicked() {
		baseActivityTestRule.getActivity().setXCoordinate(XRETURNPOS);
		baseActivityTestRule.getActivity().setYCoordinate(YRETURNPOS);

		Espresso.pressBack();
		onView(withText(R.string.save))
				.perform(click());

		assertTrue(baseActivityTestRule.getActivity().isFinishing());
		Intent resultIntent = baseActivityTestRule.getActivityResult().getResultData();
		assertEquals(XRETURNPOS, resultIntent.getExtras().get(X_COORDINATE_BUNDLE_ARGUMENT));
		assertEquals(YRETURNPOS, resultIntent.getExtras().get(Y_COORDINATE_BUNDLE_ARGUMENT));
	}

	@Test
	public void testResultWhenDiscarded() {
		baseActivityTestRule.getActivity().setXCoordinate(XRETURNPOS);
		baseActivityTestRule.getActivity().setYCoordinate(YRETURNPOS);

		Espresso.pressBack();
		onView(withText(R.string.discard))
				.perform(click());

		assertTrue(baseActivityTestRule.getActivity().isFinishing());
		Intent resultIntent = baseActivityTestRule.getActivityResult().getResultData();
		assertNull(resultIntent);
	}
}
