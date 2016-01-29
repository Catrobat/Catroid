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

import android.widget.EditText;
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

	private static final double SEEK_BAR_VALUE = 126;
	// Clicking on a seek bar sets the value to 126 or 127 depending on the screen size.
	// A threshold of 1 is needed to work on jenkins (126) and other devices,
	// e.g. Samsung Galaxy S4 or Google Nexus 4 (127).
	private static final double SEEK_BAR_VALUE_THRESHOLD = 1;

	public PhiroSeekBarColorBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	public void testRedSeekBar() {
		testSeekBar(R.id.brick_phiro_rgb_led_action_red_edit_text, R.id.color_rgb_seekbar_red, R.id.rgb_red_value);
	}

	public void testGreenSeekBar() {
		testSeekBar(R.id.brick_phiro_rgb_led_action_green_edit_text, R.id.color_rgb_seekbar_green, R.id.rgb_green_value);
	}

	public void testBlueSeekBar() {
		testSeekBar(R.id.brick_phiro_rgb_led_action_blue_edit_text, R.id.color_rgb_seekbar_blue, R.id.rgb_blue_value);
	}

	private void testSeekBar(int phiroLedColorEditText, int phiroLedColorSeekBar, int phiroLedColorTextView) {
		solo.clickOnView(solo.getView(phiroLedColorEditText));
		solo.clickOnView(solo.getView(phiroLedColorSeekBar));
		solo.clickOnView(solo.getView(phiroLedColorTextView));
		assertEquals("Text not updated within FormulaEditor", SEEK_BAR_VALUE,
				Double.parseDouble(((EditText) solo.getView(R.id.formula_editor_edit_field)).getText().toString()
						.replace(',', '.')), SEEK_BAR_VALUE_THRESHOLD);
	}

	public void testRedFormulaEditorInput() {
		testFormulaEditorInput(R.id.brick_phiro_rgb_led_action_red_edit_text, R.id.rgb_red_value);
	}

	public void testGreenFormulaEditorInput() {
		testFormulaEditorInput(R.id.brick_phiro_rgb_led_action_green_edit_text, R.id.rgb_green_value);
	}

	public void testBlueFormulaEditorInput() {
		testFormulaEditorInput(R.id.brick_phiro_rgb_led_action_blue_edit_text, R.id.rgb_blue_value);
	}

	private void testFormulaEditorInput(int phiroLedColorEditText, int phiroLedColorTextView) {
		solo.clickOnView(solo.getView(phiroLedColorEditText));
		solo.clickOnView(solo.getView(phiroLedColorTextView));

		UiTestUtils.insertDoubleIntoEditText(solo, 100.0);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		solo.waitForView(phiroLedColorEditText);
		TextView textView = ((TextView) solo.getView(phiroLedColorEditText));
		assertEquals("Text not updated", 100.0, Double.parseDouble(textView.getText().toString().replace(',', '.')));
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();

		script.addBrick(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, 0, 10, 20));
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
