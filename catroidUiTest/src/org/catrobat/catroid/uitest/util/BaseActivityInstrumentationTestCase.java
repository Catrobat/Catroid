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
package org.catrobat.catroid.uitest.util;

import org.catrobat.catroid.stage.StageListener;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public abstract class BaseActivityInstrumentationTestCase<T extends Activity> extends
		ActivityInstrumentationTestCase2<T> {

	protected Solo solo;

	private static final String TAG = "BaseActivityInstrumentationTestCase";

	public BaseActivityInstrumentationTestCase(Class<T> clazz) {
		super(clazz);
	}

	@Override
	protected void setUp() throws Exception {
		Log.v(TAG, "Setup #1");
		super.setUp();
		Log.v(TAG, "Setup #2");
		UiTestUtils.clearAllUtilTestProjects();
		Log.v(TAG, "Setup #3");
		solo = new Solo(getInstrumentation(), getActivity());
		Log.v(TAG, "Setup #4");
		Reflection.setPrivateField(StageListener.class, "makeAutomaticScreenshot", false);
	}

	@Override
	protected void tearDown() throws Exception {
		Log.v(TAG, "Teardown #1");
		solo.finishOpenedActivities();
		Log.v(TAG, "Teardown #2");
		UiTestUtils.clearAllUtilTestProjects();
		Log.v(TAG, "Teardown #3");
		super.tearDown();
		Log.v(TAG, "Teardown #4");
		solo = null;
	}

}