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
package org.catrobat.catroid.test.utils;

import android.app.Activity;
import android.test.ActivityUnitTestCase;
import android.util.Log;

import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public abstract class BaseActivityUnitTestCase<T extends Activity> extends ActivityUnitTestCase<T> {

	public static final String TAG = BaseActivityUnitTestCase.class.getSimpleName();

	public BaseActivityUnitTestCase(Class<T> activityClass) {
		super(activityClass);
	}

	@Override
	protected void setUp() throws Exception {
		Log.v(TAG, "setUp");
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		Reflection.setPrivateField(StageListener.class, "checkIfAutomaticScreenshotShouldBeTaken", false);
	}

	@Override
	protected void tearDown() throws Exception {
		Log.v(TAG, "tearDown");
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}
}
