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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorSeekbarWrapper.MAX_COLOR_SEEKBAR_VALUE;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorSeekbarWrapper.MIN_SEEKBAR_VALUE;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorSeekbarWrapper.onColorSeekbar;

@RunWith(AndroidJUnit4.class)
public class ColorPickerTest {

	private static final Integer INIT_COLOR_VALUE = 128;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testPenColorBrickUndoOnce() {
		int positionPenColorBrick = 1;
		float setRgbValueBlue = 44;

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.perform(click());

		onColorSeekbar().performSwipeBlueSeekbar(setRgbValueBlue / MAX_COLOR_SEEKBAR_VALUE);

		FormulaEditorWrapper.performUndo();

		onColorSeekbar().closeAndSaveChanges();

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.checkShowsNumber((int) MAX_COLOR_SEEKBAR_VALUE);
	}

	@Test
	public void testPenColorBrickUndoTwiceRedo() {
		int positionPenColorBrick = 1;
		float setRgbValuesRed = 36;
		float setRgbValuesBlue = 44;

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.perform(click());

		onColorSeekbar().performSwipeBlueSeekbar(setRgbValuesBlue / MAX_COLOR_SEEKBAR_VALUE);

		onColorSeekbar().performSwipeRedSeekbar(setRgbValuesRed / MAX_COLOR_SEEKBAR_VALUE);

		FormulaEditorWrapper.performUndo();
		FormulaEditorWrapper.performUndo();

		FormulaEditorWrapper.performRedo();

		onColorSeekbar().closeAndSaveChanges();

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.checkShowsNumber((int) MAX_COLOR_SEEKBAR_VALUE);
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_blue_edit_text)
				.checkShowsNumber((int) setRgbValuesBlue);
	}

	@Test
	public void testPenColorBrickDiscardChanges() {
		int positionPenColorBrick = 1;
		float[] newRgbValues = {55, 122, 99};
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.perform(click());
		onColorSeekbar().performSwipeRedSeekbar(newRgbValues[0] / MAX_COLOR_SEEKBAR_VALUE);
		onColorSeekbar().performSwipeGreenSeekbar(newRgbValues[1] / MAX_COLOR_SEEKBAR_VALUE);
		onColorSeekbar().performSwipeBlueSeekbar(newRgbValues[2] / MAX_COLOR_SEEKBAR_VALUE);

		onColorSeekbar().closeAndDiscardChanges();

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.checkShowsNumber((int) MAX_COLOR_SEEKBAR_VALUE);
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_green_edit_text)
				.checkShowsNumber((int) MIN_SEEKBAR_VALUE);
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_blue_edit_text)
				.checkShowsNumber(INIT_COLOR_VALUE);
	}

	public static void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "penColorBrickTest");
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("penColorBrickTest");
		Sprite sprite = new Sprite("testSprite");
		sprite.addScript(startScript);
		startScript.addBrick(new SetPenColorBrick((int) MAX_COLOR_SEEKBAR_VALUE,
				(int) MIN_SEEKBAR_VALUE, INIT_COLOR_VALUE));
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
