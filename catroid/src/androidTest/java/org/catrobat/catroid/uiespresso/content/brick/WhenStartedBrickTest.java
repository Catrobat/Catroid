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
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

//TODO incomplete Test! ks

@RunWith(AndroidJUnit4.class)
public class WhenStartedBrickTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("WhenStartedBrickTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void whenStartedBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		deleteStartBrick();
		onView(withId(R.string.brick_when_started)).check(doesNotExist());

		addBrickAtPosition(WaitBrick.class, 1, R.string.category_control);
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		deleteStartBrick();
		onView(withId(R.string.brick_when_started)).check(doesNotExist());
		onView(withId(R.string.brick_wait)).check(doesNotExist());
	}

	public void addBrickAtPosition(Class<?> brickHeaderClass, int insertPosition, int brickCategoryId) {
		onView(withId(R.id.button_add))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(brickCategoryId))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
		onData(is(instanceOf(brickHeaderClass))).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.perform(click());
		onBrickAtPosition(insertPosition)
				.perform(click());
	}

	void deleteStartBrick() {
		onBrickAtPosition(0).perform(click());
		onView(withText(R.string.brick_context_dialog_delete_script))
				.perform(click());
		onView(withText(R.string.yes))
				.perform(click());
	}
}
