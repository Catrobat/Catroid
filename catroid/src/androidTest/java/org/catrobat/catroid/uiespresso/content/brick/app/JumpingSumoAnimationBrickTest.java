/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class JumpingSumoAnimationBrickTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;

		BrickTestUtils.createProjectAndGetStartScript("JumpingSumoAnimationBrickTest").addBrick(new
				JumpingSumoAnimationsBrick(JumpingSumoAnimationsBrick.Animation.SPIN));
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	@Flaky
	public void testJumpingSumoAnimationBrick() {

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_jumping_sumo_animation);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_jumping_sumo_animation_spinner)
				.checkShowsText(R.string.animation_spin);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_jumping_sumo_animation_spinner)
				.performSelect(R.string.animation_tab)
				.checkShowsText(R.string.animation_tab);

		List<Integer> spinnerValuesResourceIds = Arrays.asList(
				R.string.animation_spin,
				R.string.animation_tab,
				R.string.animation_slowshake,
				R.string.animation_metronome,
				R.string.animation_ondulation,
				R.string.animation_spinjump,
				R.string.animation_spiral,
				R.string.animation_slalom);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_jumping_sumo_animation_spinner)
				.checkValuesAvailable(spinnerValuesResourceIds);
	}
}
