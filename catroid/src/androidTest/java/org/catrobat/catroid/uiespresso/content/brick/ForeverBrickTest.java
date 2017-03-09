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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.matchers.ScriptListMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.onScriptList;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class ForeverBrickTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void foreverBrickTest() {
		//multiple ways to check this, full verbose espresso way of checking:
		onData(instanceOf(Brick.class)).inAdapterView(ScriptListMatchers.isScriptListView()).atPosition(1)
				.onChildView(withText(R.string.brick_forever))
				.check(matches(isDisplayed()));

		//shortened with utility function to get scriptlist datainteraction object:
		onScriptList().atPosition(1).onChildView(withText(R.string.brick_forever))
				.check(matches(isDisplayed()));

		//shortened even more with utility function
		checkIfBrickAtPositionShowsString(1, R.string.brick_forever);

		//ok, now for the real test, check if all bricks are there in right order and displayed:
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(1, R.string.brick_forever);
		checkIfBrickAtPositionShowsString(2, R.string.brick_loop_end);
	}

	@After
	public void tearDown() throws Exception {
	}

	public void createProject() {
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("foreverBrickTest1");
		ForeverBrick foreverBrick = new ForeverBrick();
		startScript.addBrick(foreverBrick);
		startScript.addBrick(new LoopEndlessBrick(foreverBrick));
	}
}

