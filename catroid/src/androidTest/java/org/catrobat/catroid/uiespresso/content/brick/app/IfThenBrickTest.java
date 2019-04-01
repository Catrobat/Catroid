/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class IfThenBrickTest {
	private int ifThenBeginBrickPosition;

	private int condition = 42;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void ifThenBrickDeleteTest() {
		checkSetUpBrickArrangement();

		onBrickAtPosition(ifThenBeginBrickPosition).performDeleteBrick();

		onBrickAtPosition(1).checkShowsText(R.string.brick_set_x);

		onView(withText(R.string.brick_if_else))
				.check(doesNotExist());

		onView(withText(R.string.brick_if_end))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCopyIfThenBrickTest() {
		onBrickAtPosition(1).perform(click());

		onView(withText(R.string.brick_context_dialog_copy_brick))
				.perform(click());

		onBrickAtPosition(ifThenBeginBrickPosition + 1).perform(click());

		onBrickAtPosition(1).checkShowsText(R.string.brick_if_begin);
		onBrickAtPosition(2).checkShowsText(R.string.brick_if_end);
		onBrickAtPosition(3).checkShowsText(R.string.brick_if_begin);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(5).checkShowsText(R.string.brick_if_end);

		onBrickAtPosition(ifThenBeginBrickPosition).performDeleteBrick();

		checkSetUpBrickArrangement();
	}

	public void checkSetUpBrickArrangement() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_if_begin);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(3).checkShowsText(R.string.brick_if_end);
	}

	public void createProject() {
		ifThenBeginBrickPosition = 1;

		IfThenLogicBeginBrick ifThenLogicBeginBrick = new IfThenLogicBeginBrick(condition);
		IfThenLogicEndBrick ifThenLogicEndBrick = new IfThenLogicEndBrick(ifThenLogicBeginBrick);

		Script script = BrickTestUtils.createProjectAndGetStartScript("IfThenBrickTest");
		script.addBrick(ifThenLogicBeginBrick);
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		script.addBrick(ifThenLogicEndBrick);

		assertTrue(new BrickController().setControlBrickReferences(script.getBrickList()));
	}
}
