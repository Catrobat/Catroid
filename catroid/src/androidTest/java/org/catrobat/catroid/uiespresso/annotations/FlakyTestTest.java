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

package org.catrobat.catroid.uiespresso.annotations;

import org.catrobat.catroid.R;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class FlakyTestTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	private static int flakyTestCounter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		flakyTestCounter = 0;
	}

	@Before
	public void setUp() throws Exception {
		UiTestUtils.createProjectAndGetStartScript("flakyTestTest");
		baseActivityTestRule.launchActivity();
	}

	@Test
	@Flaky(5)
	public void flakyTestTest() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		flakyTestCounter++;
		assertEquals(5, flakyTestCounter);
	}
}
