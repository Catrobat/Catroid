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
package org.catrobat.catroid.uitest.stage;

import android.view.WindowManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class StageTestSimple extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public StageTestSimple() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		ScreenValues.SCREEN_HEIGHT = 20;
		ScreenValues.SCREEN_WIDTH = 20;

		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
	}

	public void testSimple() {
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };

		byte[] result = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		result = StageActivity.stageListener.getPixels(19, 19, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);
	}

	public void testScreenAlwaysOn() {
		final int windowFlags = solo.getCurrentActivity().getWindow().getAttributes().flags;

		assertTrue("Window flags do not contain FLAG_KEEP_SCREEN_ON!",
				(windowFlags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0);
	}
}
