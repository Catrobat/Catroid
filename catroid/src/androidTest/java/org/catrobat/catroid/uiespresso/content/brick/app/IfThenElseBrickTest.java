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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.SpriteActivity;
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

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class IfThenElseBrickTest {
	private int ifThenElseBeginBrickPosition;
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
	public void ifThenElseBrickTest() {
		checkSetUpBrickArrangement();

		onBrickAtPosition(ifThenElseBeginBrickPosition)
				.checkShowsText(R.string.brick_if_begin)
				.checkShowsText(R.string.brick_if_begin_second_part);

		onBrickAtPosition(ifThenElseBeginBrickPosition).onFormulaTextField(R.id.brick_if_begin_edit_text)
				.checkShowsNumber(condition);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void ifThenElseBrickDeleteTest() {
		checkSetUpBrickArrangement();

		onBrickAtPosition(ifThenElseBeginBrickPosition).performDeleteBrick();

		onBrickAtPosition(1).checkShowsText(R.string.brick_set_x);

		onView(withText(R.string.brick_if_end))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCopyIfThenElseBrickTest() throws InterpretationException {
		onBrickAtPosition(1).perform(click());

		onView(withText(R.string.brick_context_dialog_copy_brick))
				.perform(click());

		onBrickAtPosition(ifThenElseBeginBrickPosition).perform(click());

		onBrickAtPosition(1).checkShowsText(R.string.brick_if_begin);
		onBrickAtPosition(2).checkShowsText(R.string.brick_if_else);
		onBrickAtPosition(3).checkShowsText(R.string.brick_if_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_if_begin);
		onBrickAtPosition(5).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(6).checkShowsText(R.string.brick_if_else);
		onBrickAtPosition(7).checkShowsText(R.string.brick_set_y);
		onBrickAtPosition(8).checkShowsText(R.string.brick_if_end);

		onBrickAtPosition(ifThenElseBeginBrickPosition).performDeleteBrick();

		checkSetUpBrickArrangement();
	}

	public void checkSetUpBrickArrangement() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_if_begin);
		onBrickAtPosition(2).checkShowsText(R.string.brick_set_x);
		onBrickAtPosition(3).checkShowsText(R.string.brick_if_else);
		onBrickAtPosition(4).checkShowsText(R.string.brick_set_y);
		onBrickAtPosition(5).checkShowsText(R.string.brick_if_end);
	}

	public void createProject() {
		ifThenElseBeginBrickPosition = 1;
		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick(condition);
		IfLogicElseBrick elseBrick = new IfLogicElseBrick(ifBrick);
		IfLogicEndBrick endBrick = new IfLogicEndBrick(elseBrick, ifBrick);

		Script script = BrickTestUtils.createProjectAndGetStartScript("IfThenElseBrickTest");
		script.addBrick(ifBrick);
		script.addBrick(new SetXBrick());
		script.addBrick(elseBrick);
		script.addBrick(new SetYBrick());
		script.addBrick(endBrick);

		ProjectManager.getInstance().checkNestingBrickReferences(true, false);
	}
}
