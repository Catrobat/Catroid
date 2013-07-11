/**
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2013 The Catrobat Team
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.util;

import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.MainMenuActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public abstract class BaseUiTestClass extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	protected Solo solo;

	public BaseUiTestClass() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		Log.v("BaseUiTestClass", "Setup #1");
		super.setUp();
		Log.v("BaseUiTestClass", "Setup #2");
		UiTestUtils.clearAllUtilTestProjects();
		Log.v("BaseUiTestClass", "Setup #3");
		solo = new Solo(getInstrumentation(), getActivity());
		Log.v("BaseUiTestClass", "Setup #4");
		Reflection.setPrivateField(StageListener.class, "makeAutomaticScreenshot", false);
	}

	@Override
	protected void tearDown() throws Exception {
		Log.v("BaseUiTestClass", "Teardown #1");
		solo.finishOpenedActivities();
		Log.v("BaseUiTestClass", "Setup #2");
		UiTestUtils.clearAllUtilTestProjects();
		Log.v("BaseUiTestClass", "Setup #3");
		super.tearDown();
		Log.v("BaseUiTestClass", "Setup #4");
		solo = null;
	}

}