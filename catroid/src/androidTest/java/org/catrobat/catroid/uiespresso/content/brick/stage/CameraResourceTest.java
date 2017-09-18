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
package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.CameraBrick;
import org.catrobat.catroid.content.bricks.ChooseCameraBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CameraResourceTest {

	private static final int BACK = 0;
	private static final int FRONT = 1;
	private static final int OFF = 0;
	private static final int ON = 1;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void cameraResourceNotUsedTest() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("cameraResourceNotUsed");
		ScriptEvaluationGateBrick lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		assertEquals(CameraManager.CameraState.notUsed, CameraManager.getInstance().getState());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void cameraOnTest() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("cameraOnTest");
		script.addBrick(new CameraBrick(ON));
		ScriptEvaluationGateBrick lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		assertEquals(CameraManager.CameraState.previewRunning, CameraManager.getInstance().getState());
		assertTrue(CameraManager.getInstance().isCurrentCameraFacingFront());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void cameraStagePausedTest() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("cameraStagePausedTest");
		script.addBrick(new CameraBrick(ON));
		ScriptEvaluationGateBrick lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		pressBack();
		assertTrue(CameraManager.getInstance().getState() == CameraManager.CameraState.previewPaused
				|| CameraManager.getInstance().getState() == CameraManager.CameraState.notUsed);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void cameraOffTest() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("cameraOffTest");
		script.addBrick(new CameraBrick(OFF));
		ScriptEvaluationGateBrick lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		assertEquals(CameraManager.CameraState.notUsed, CameraManager.getInstance().getState());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void cameraFacingFrontTest() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("cameraFacingFrontTest");
		script.addBrick(new ChooseCameraBrick(FRONT));
		script.addBrick(new CameraBrick(ON));
		ScriptEvaluationGateBrick lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		onView(isRoot()).perform(CustomActions.wait(500));
		assertEquals(CameraManager.CameraState.previewRunning, CameraManager.getInstance().getState());
		assertTrue(CameraManager.getInstance().isCurrentCameraFacingFront());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void cameraFacingBackTest() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("cameraFacingBackTest");
		script.addBrick(new ChooseCameraBrick(BACK));
		script.addBrick(new CameraBrick(ON));
		ScriptEvaluationGateBrick lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		onView(isRoot()).perform(CustomActions.wait(500));
		assertEquals(CameraManager.CameraState.previewRunning, CameraManager.getInstance().getState());
		assertTrue(CameraManager.getInstance().isCurrentCameraFacingBack());
	}
}
