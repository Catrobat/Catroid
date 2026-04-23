/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrickWithVisualPlacement;
import org.catrobat.catroid.content.bricks.VisualPlacementBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.catrobat.catroid.uiespresso.content.brick.app.VisualPlacementBrickTest.isFormulaEditorShownImmediatelyWithTapOnEditText;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class VisualPlacementConditionTest {
	private static final String TAG = VisualPlacementBrickTest.class.getSimpleName();

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"GlideToBrick", R.string.brick_glide, new GlideToBrick(), R.id.brick_glide_to_edit_text_duration, -1},
				{"ShowTextColorSizeAlignment", R.string.brick_show_variable_size, new ShowTextColorSizeAlignmentBrick(0, 0, 100, "#FFFF00"), R.id.brick_show_variable_color_size_edit_relative_size, R.id.brick_show_variable_color_size_edit_color},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public int brickString;

	@Parameterized.Parameter(2)
	public VisualPlacementBrick brick;

	@Parameterized.Parameter(3)
	public int firstEditTextId;

	@Parameterized.Parameter(4)
	public int secondEditTextId;

	private int brickPosition = 1;
	private UserVariable userVariable;

	@After
	public void tearDown() {
		baseActivityTestRule.finishActivity();
		try {
			TestUtils.deleteProjects(VisualPlacementBrickTest.class.getSimpleName());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Before
	public void setUp() throws Exception {
		Script script = createProject();
		if (brick instanceof UserVariableBrickWithVisualPlacement) {
			((UserVariableBrickWithVisualPlacement) brick).setUserVariable(userVariable);
		}
		script.addBrick(brick);
		baseActivityTestRule.launchActivity();
	}

	public Script createProject() {
		Project project =
				UiTestUtils.createDefaultTestProject(TestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = UiTestUtils.getDefaultTestSprite(project);

		userVariable = new UserVariable("userVariable");
		sprite.addUserVariable(userVariable);

		return UiTestUtils.getDefaultTestScript(project);
	}

	@Test
	public void testVisualPlacementNotShownForWrongTextField() {
		onBrickAtPosition(brickPosition).checkShowsText(brickString);
		isFormulaEditorShownImmediatelyWithTapOnEditText(firstEditTextId);
		if (secondEditTextId != -1) {
			isFormulaEditorShownImmediatelyWithTapOnEditText(firstEditTextId);
		}
	}

	@Test
	public void testVisualPlacementInFormulaFragmentNotShownForWrongTextField() {
		onBrickAtPosition(brickPosition).checkShowsText(brickString);
		onView(withId(brick.getXEditTextId()))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		isFormulaEditorShownImmediatelyWithTapOnEditText(firstEditTextId);
		if (secondEditTextId != -1) {
			isFormulaEditorShownImmediatelyWithTapOnEditText(firstEditTextId);
		}
	}
}
