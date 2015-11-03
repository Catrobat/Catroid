/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.uitest.content.brick;

import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class PhiroSeekBarColorBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {
	private static final int SET_RED_COLOR_INITIALLY = 0;
	private static final int SET_GREEN_COLOR_INITIALLY = 10;
	private static final int SET_BLUE_COLOR_INITIALLY = 20;
	private Project project;
	private PhiroRGBLightBrick colorBrick;

	public PhiroSeekBarColorBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		super.setUp();
	}

	public void testRedSeekBar() {
		solo.clickOnView(solo.getView(R.id.brick_phiro_rgb_led_action_red_edit_text));
		solo.clickOnView(solo.getView(R.id.color_rgb_seekbar_red));
		solo.clickOnView(solo.getView(R.id.rgb_red_value));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
		TextView textView = ((TextView) solo.getView(R.id.brick_phiro_rgb_led_action_red_edit_text));
		assertNotSame("Text not updated", SET_RED_COLOR_INITIALLY,
				Double.parseDouble(textView.getText().toString().replace(',', '.')));
	}

	public void testGreenSeekbar() {
		solo.clickOnView(solo.getView(R.id.brick_phiro_rgb_led_action_green_edit_text));
		solo.clickOnView(solo.getView(R.id.color_rgb_seekbar_green));
		solo.clickOnView(solo.getView(R.id.rgb_green_value));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
		TextView textView = ((TextView) solo.getView(R.id.brick_phiro_rgb_led_action_green_edit_text));
		assertNotSame("Text not updated", SET_GREEN_COLOR_INITIALLY,
				Double.parseDouble(textView.getText().toString().replace(',', '.')));
	}

	public void testBlueSeekbar() {
		solo.clickOnView(solo.getView(R.id.brick_phiro_rgb_led_action_blue_edit_text));
		solo.clickOnView(solo.getView(R.id.color_rgb_seekbar_blue));
		solo.clickOnView(solo.getView(R.id.rgb_blue_value));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
		TextView textView = ((TextView) solo.getView(R.id.brick_phiro_rgb_led_action_blue_edit_text));
		assertNotSame("Text not updated", SET_BLUE_COLOR_INITIALLY,
				Double.parseDouble(textView.getText().toString().replace(',', '.')));
	}

	public void testRedFormulaEditorInput() {
		solo.clickOnView(solo.getView(R.id.brick_phiro_rgb_led_action_red_edit_text));
		solo.clickOnView(solo.getView(R.id.rgb_red_value));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
		TextView textView = ((TextView) solo.getView(R.id.brick_phiro_rgb_led_action_red_edit_text));
		assertEquals("Text not updated", 100.0,
				Double.parseDouble(textView.getText().toString().replace(',', '.')));
	}

	public void testGreenFormulaEditorInput() {
		solo.clickOnView(solo.getView(R.id.brick_phiro_rgb_led_action_green_edit_text));
		solo.clickOnView(solo.getView(R.id.rgb_green_value));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
		TextView textView = ((TextView) solo.getView(R.id.brick_phiro_rgb_led_action_green_edit_text));
		assertEquals("Text not updated", 200.0,
				Double.parseDouble(textView.getText().toString().replace(',', '.')));
	}

	public void testBlueFormulaEditorInput() {
		solo.clickOnView(solo.getView(R.id.brick_phiro_rgb_led_action_blue_edit_text));
		solo.clickOnView(solo.getView(R.id.rgb_blue_value));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
		TextView textView = ((TextView) solo.getView(R.id.brick_phiro_rgb_led_action_blue_edit_text));
		assertEquals("Text not updated", 300.0,
				Double.parseDouble(textView.getText().toString().replace(',', '.')));
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		colorBrick = new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, SET_RED_COLOR_INITIALLY, SET_GREEN_COLOR_INITIALLY, SET_BLUE_COLOR_INITIALLY);

		script.addBrick(colorBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
