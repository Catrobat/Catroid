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
package org.catrobat.catroid.uiespresso.formulaeditor;

import android.support.annotation.IdRes;
import android.support.test.InstrumentationRegistry;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.content.brick.utils.CustomSwipeAction;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorSeekbarWrapper.MAX_COLOR_SEEKBAR_VALUE;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorSeekbarWrapper.onColorSeekbar;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public final class ColorPickerParameterizedTest {
	private static final Integer INIT_COLOR_VALUE = 0;
	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Parameterized.Parameters(name = "{3}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.id.color_rgb_seekbar_red,
						R.id.brick_set_pen_color_action_red_edit_text,
						INIT_COLOR_VALUE,
						"SeekbarRed"},
				{R.id.color_rgb_seekbar_green,
						R.id.brick_set_pen_color_action_green_edit_text,
						INIT_COLOR_VALUE,
						"SeekbarGreen"},
				{R.id.color_rgb_seekbar_blue,
						R.id.brick_set_pen_color_action_blue_edit_text,
						INIT_COLOR_VALUE,
						"SeekbarBlue"},
		});
	}

	@Parameterized.Parameter
	public @IdRes int colorRgbSeekbarId;

	@Parameterized.Parameter(1)
	public @IdRes int brickActionEditTextId;

	@Parameterized.Parameter(2)
	public int initValue;

	@Parameterized.Parameter(3)
	public String testName;

	@Test
	public void testColorSeekBar() {
		int whenBrickPosition = 0;
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		int penColorBrickPosition = 1;

		onBrickAtPosition(penColorBrickPosition).onFormulaTextField(brickActionEditTextId)
				.checkShowsNumber(initValue)
				.perform(click());

		onView(withId(colorRgbSeekbarId))
				.perform(CustomSwipeAction.swipeRightSlow());

		onColorSeekbar().closeAndSaveChanges();
		onBrickAtPosition(penColorBrickPosition).onFormulaTextField(brickActionEditTextId)
				.checkShowsNumber((int) MAX_COLOR_SEEKBAR_VALUE);
	}

	public static void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "penColorBrickTest");
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("penColorBrickTest");
		Sprite sprite = new Sprite("testSprite");
		sprite.addScript(startScript);
		startScript.addBrick(new SetPenColorBrick(INIT_COLOR_VALUE, INIT_COLOR_VALUE, INIT_COLOR_VALUE));
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
