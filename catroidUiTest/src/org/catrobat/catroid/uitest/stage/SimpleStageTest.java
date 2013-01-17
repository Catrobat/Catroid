/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;

import com.jayway.android.robotium.solo.Solo;

public class SimpleStageTest extends ActivityInstrumentationTestCase2<StageActivity> {

	private Solo solo;

	public SimpleStageTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.createEmptyProject();
		Values.SCREEN_HEIGHT = 20;
		Values.SCREEN_WIDTH = 20;
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testSimple() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };

		byte[] result = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		result = StageActivity.stageListener.getPixels(19, 19, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		result = StageActivity.stageListener.getPixels(-1, -1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);
	}

	public void testScreenAlwaysOn() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		final int windowFlags = getActivity().getWindow().getAttributes().flags;

		assertTrue("Window flags do not contain FLAG_KEEP_SCREEN_ON!",
				(windowFlags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0);
	}

}
