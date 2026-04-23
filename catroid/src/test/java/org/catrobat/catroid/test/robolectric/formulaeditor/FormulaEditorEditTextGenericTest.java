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

package org.catrobat.catroid.test.robolectric.formulaeditor;

import android.app.Activity;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import androidx.annotation.IdRes;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class FormulaEditorEditTextGenericTest {

	@ParameterizedRobolectricTestRunner.Parameters(name = "{0}" + "-Test")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"button_0", R.id.formula_editor_keyboard_0, "0"},
				{"button_1", R.id.formula_editor_keyboard_1, "1"},
				{"button_2", R.id.formula_editor_keyboard_2, "2"},
				{"button_3", R.id.formula_editor_keyboard_3, "3"},
				{"button_4", R.id.formula_editor_keyboard_4, "4"},
				{"button_5", R.id.formula_editor_keyboard_5, "5"},
				{"button_6", R.id.formula_editor_keyboard_6, "6"},
				{"button_7", R.id.formula_editor_keyboard_7, "7"},
				{"button_8", R.id.formula_editor_keyboard_8, "8"},
				{"button_9", R.id.formula_editor_keyboard_9, "9"},
				{"button_decimal_mark", R.id.formula_editor_keyboard_decimal_mark, "0."},
				{"button_plus", R.id.formula_editor_keyboard_plus, "+"},
				{"button_minus", R.id.formula_editor_keyboard_minus, "-"},
				{"button_mult", R.id.formula_editor_keyboard_mult, "×"},
				{"button_divide", R.id.formula_editor_keyboard_divide, "÷"},
				{"button_bracket_open", R.id.formula_editor_keyboard_bracket_open, "("},
				{"button_bracket_close", R.id.formula_editor_keyboard_bracket_close, ")"}
		});
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	private String name;

	@IdRes
	private int formulaKeyboardItemId;
	private String expectedFormulaText;

	private SpriteActivity activity;

	public FormulaEditorEditTextGenericTest(String name, @IdRes int formulaKeyboardItemId, String expectedFormulaText) {
		this.name = name;
		this.expectedFormulaText = expectedFormulaText;
		this.formulaKeyboardItemId = formulaKeyboardItemId;
	}

	@Before
	public void setUp() throws Exception {
		ActivityController<SpriteActivity> activityController = Robolectric.buildActivity(SpriteActivity.class);
		activity = activityController.get();
		FormulaBrick brick = new SetXBrick();
		createProject(activity, brick);
		activityController.create().resume();
		View brickView = brick.getView(activity);
		TextView brickFormulaTextView = brickView.findViewById(R.id.brick_set_x_edit_text);
		brick.onClick(brickFormulaTextView);

		shadowOf(Looper.getMainLooper()).idle();
	}

	@After
	public void tearDown() {
		ProjectManager.getInstance().resetProjectManager();
	}

	@Test
	public void openFormulaEditorTest() {
		MotionEvent touchDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_DOWN, 1, 1, 0);
		View formulaKeyboardItemView = activity.findViewById(formulaKeyboardItemId);
		formulaKeyboardItemView.dispatchTouchEvent(touchDown);
		EditText formulaEditField = activity.findViewById(R.id.formula_editor_edit_field);
		assertEquals(expectedFormulaText, formulaEditField.getText().toString().trim());
	}

	private void createProject(Activity activity, Brick brick) {
		Project project = new Project(activity, getClass().getSimpleName());
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(brick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}
}
