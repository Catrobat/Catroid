/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickCoordinatesProvider;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
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
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ForeverBrickTest {

	private Script script;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testAddBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_y);

		onView(withId(R.id.button_add))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(R.string.category_control))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
		onData(is(instanceOf(ForeverBrick.class))).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.perform(click());
		onBrickAtPosition(0).perform(click());

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(2).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(5).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(6).checkShowsText(R.string.brick_set_y);

		ForeverBrick foreverBrick = (ForeverBrick) script.getBrick(0);
		LoopEndBrick endBrick = (LoopEndBrick) script.getBrick(4);

		assertEquals(foreverBrick, endBrick.getLoopBeginBrick());
		assertEquals(endBrick, foreverBrick.getLoopEndBrick());

		foreverBrick = (ForeverBrick) script.getBrick(1);
		endBrick = (LoopEndBrick) script.getBrick(2);

		assertEquals(foreverBrick, endBrick.getLoopBeginBrick());
		assertEquals(endBrick, foreverBrick.getLoopEndBrick());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCopyBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_y);

		onBrickAtPosition(1)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_copy_brick))
				.perform(click());
		onBrickAtPosition(2)
				.perform(click());

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(2).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(3).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(5).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(6).checkShowsText(R.string.brick_set_y);

		ForeverBrick foreverBrick = (ForeverBrick) script.getBrick(0);
		LoopEndBrick endBrick = (LoopEndBrick) script.getBrick(1);

		assertEquals(foreverBrick, endBrick.getLoopBeginBrick());
		assertEquals(endBrick, foreverBrick.getLoopEndBrick());

		foreverBrick = (ForeverBrick) script.getBrick(2);
		endBrick = (LoopEndBrick) script.getBrick(4);

		assertEquals(foreverBrick, endBrick.getLoopBeginBrick());
		assertEquals(endBrick, foreverBrick.getLoopEndBrick());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testDeleteBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_y);

		onBrickAtPosition(1)
				.performDeleteBrick();

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_y);

		onView(withText(R.string.brick_loop_end))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testDeleteBrickFromEndBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_y);

		onBrickAtPosition(3)
				.performDeleteBrick();

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_y);

		onView(withText(R.string.brick_loop_end))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testDragAndDrop() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_y);

		onBrickAtPosition(1)
				.performDragNDrop(BrickCoordinatesProvider.DOWN_TO_BOTTOM);

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_set_y);
		onBrickAtPosition(2).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(3).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(4).checkShowsText(R.string.brick_loop_end);
	}

	public void createProject() {
		script = BrickTestUtils.createProjectAndGetStartScript(getClass().getSimpleName());

		ForeverBrick foreverBrick = new ForeverBrick();
		script.addBrick(foreverBrick);
		script.addBrick(new SetXBrick());
		script.addBrick(new LoopEndBrick(foreverBrick));
		script.addBrick(new SetYBrick());

		new BrickController().setControlBrickReferences(script.getBrickList());
	}
}

