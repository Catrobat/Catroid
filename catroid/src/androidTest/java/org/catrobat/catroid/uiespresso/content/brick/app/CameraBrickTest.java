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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.CameraBrick;
import org.catrobat.catroid.content.bricks.ChooseCameraBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript;

@RunWith(AndroidJUnit4.class)
public class CameraBrickTest {
	private int cameraBrickPosition;
	private int chooseCameraBrickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		cameraBrickPosition = 1;
		chooseCameraBrickPosition = 2;
		Script script = createProjectAndGetStartScript("cameraBrickTest");
		script.addBrick(new CameraBrick());
		script.addBrick(new ChooseCameraBrick());
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCameraBrick() {
		onBrickAtPosition(cameraBrickPosition).checkShowsText(R.string.brick_video);
		onBrickAtPosition(cameraBrickPosition).onSpinner(R.id.brick_video_spinner)
				.checkShowsText(R.string.video_brick_camera_on);

		List<Integer> spinnerValues = new ArrayList<>();
		spinnerValues.add(R.string.video_brick_camera_on);
		spinnerValues.add(R.string.video_brick_camera_off);
		onBrickAtPosition(cameraBrickPosition).onSpinner(R.id.brick_video_spinner)
			.checkValuesAvailable(spinnerValues);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testChooseCameraBrick() {
		onBrickAtPosition(chooseCameraBrickPosition).checkShowsText(R.string.brick_choose_camera);
		onBrickAtPosition(chooseCameraBrickPosition).onSpinner(R.id.brick_choose_camera_spinner)
				.checkShowsText(R.string.choose_camera_front);

		List<Integer> spinnerValues = new ArrayList<>();
		spinnerValues.add(R.string.choose_camera_front);
		spinnerValues.add(R.string.choose_camera_back);
		onBrickAtPosition(chooseCameraBrickPosition).onSpinner(R.id.brick_choose_camera_spinner)
				.checkValuesAvailable(spinnerValues);
	}
}
