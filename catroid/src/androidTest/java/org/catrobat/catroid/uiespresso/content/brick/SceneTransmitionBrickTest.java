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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class SceneTransmitionBrickTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		String myscene = "testScene";
		BrickTestUtils.createProjectAndGetStartScript("sceneTransmitionBrickTest")
				.addBrick(new SceneTransitionBrick(myscene));
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void sceneTransmitionBrickTest() {
		String newScene = "testScene2";
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_scene_transition);
		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_scene_transition_spinner))
				.perform(click());
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());
		enterTextOnNewSceneDialog(R.id.scene_name_edittext, newScene);
		assertTrue(UiTestUtils.getCurrentActivity() instanceof ProjectActivity);
		assertEquals(newScene, ProjectManager.getInstance().getCurrentScene().getName());
		UiTestUtils.getCurrentActivity().finish();
	}

	public static void enterTextOnNewSceneDialog(int newSceneNameEditTextId, String newSceneName) {
		onView(withText(R.string.new_scene_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(newSceneNameEditTextId))
				.perform(clearText(), typeText(newSceneName), closeSoftKeyboard());
		onView(withId(android.R.id.button1))
				.perform(click());
	}
}
