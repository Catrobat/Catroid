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
package org.catrobat.catroid.uitest.content.interaction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class BrickEditFormulaTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public BrickEditFormulaTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProjectWithEveryBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void checkBrick(int brickName) {
		solo.clickOnText(solo.getString(brickName));
		solo.clickOnMenuItem(solo.getString(R.string.brick_context_dialog_formula_edit_brick));
		assertTrue("Formula Editor did not open!", solo.waitForView(solo.getView(R.id.formula_editor_brick_space)));
		solo.goBack();
	}

	@Device
	public void testClickOnBrickItemEditFormula() {
		checkBrick(R.string.brick_change_brightness);
		checkBrick(R.string.brick_change_ghost_effect);
		checkBrick(R.string.brick_change_size_by);
		checkBrick(R.string.brick_change_volume_by);
		checkBrick(R.string.brick_change_variable);
		checkBrick(R.string.brick_change_x_by);
		checkBrick(R.string.brick_change_y_by);
		checkBrick(R.string.brick_glide);
		checkBrick(R.string.brick_go_back);
		checkBrick(R.string.brick_move);
		checkBrick(R.string.brick_place_at);
		checkBrick(R.string.brick_point_in_direction);
		checkBrick(R.string.brick_set_brightness);
		checkBrick(R.string.brick_set_transparency);
		checkBrick(R.string.brick_set_size_to);
		checkBrick(R.string.brick_set_variable);
		checkBrick(R.string.brick_set_volume_to);
		checkBrick(R.string.brick_set_x);
		checkBrick(R.string.brick_set_y);
		checkBrick(R.string.brick_turn_left);
		checkBrick(R.string.brick_turn_right);
	}
}
